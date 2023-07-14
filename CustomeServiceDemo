import openai, os, backoff, faiss
from flask import Flask
from flask import request
import pandas as pd
import numpy as np
from openai.embeddings_utils import get_embeddings, get_embedding, cosine_similarity

openai.api_key = os.environ.get("OPENAI_API_KEY")
COMPLETION_MODEL = "text-davinci-003"
embedding_model = "text-embedding-ada-002"
app = Flask(__name__)

class Conversation:
    def __init__(self, prompt, num_of_round):
        self.prompt = prompt
        self.num_of_round = num_of_round
        self.messages = []
        self.messages.append({"role": "system", "content": self.prompt})

    def ask(self, question):
        try:
            self.messages.append({"role": "user", "content": question})
            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=self.messages,
                temperature=0,
                max_tokens=2048,
                top_p=1,
            )
        except Exception as e:
            print(e)
            return e

        message = response["choices"][0]["message"]["content"]
        num_of_tokens = response['usage']['total_tokens']
        self.messages.append({"role": "assistant", "content": message})

        if len(self.messages) > self.num_of_round*2 + 1:
            del self.messages[1:3]
        print("num_of_tokens : %s\n" % num_of_tokens)
        return message.replace("```", " ")


#embedding
batch_size = 20
dfFileName = "Template_ Abbreviations_Describe.parquet"
#embedding 添加
@backoff.on_exception(backoff.expo, openai.error.RateLimitError)
def get_embeddings_with_backoff(prompts, engine):
    embeddings = []
    for i in range(0, len(prompts), batch_size):
        batch = prompts[i:i+batch_size]
        embeddings += get_embeddings(list_of_text=batch, engine=engine)
    return embeddings

def add_embeddings_data_tofile(newdf,force):
    df = None
    if os.path.exists(dfFileName):
        df = pd.read_parquet(dfFileName)
        #判断之前是否有这个缩写
        abbreviations = df.abbreviation.tolist()
        for abbreviation in newdf.abbreviation.tolist():
            if abbreviation in abbreviations:
                if force == True:
                    df = df[df.abbreviation != abbreviation]
                    df.reset_index(drop=True, inplace=True)
                else:
                    newdf = newdf[newdf.abbreviation != abbreviation]
                    newdf.reset_index(drop=True, inplace=True)
    return save_to_file(df,newdf)

def save_to_file(df,newdf):
    embeddings = []
    prompts = newdf.description.tolist()
    prompt_batches = [prompts[i:i+batch_size] for i in range(0, len(prompts), batch_size)]
    for batch in prompt_batches:
        batch_embeddings = get_embeddings_with_backoff(prompts=batch, engine=embedding_model)
        embeddings += batch_embeddings
    newdf["embedding"] = embeddings
    if df is None:
        newdf.to_parquet(dfFileName, index=False)
    else:
        df = pd.concat([df, newdf], axis=0)
        resultdf = df.reset_index(drop=True)
        resultdf.to_parquet(dfFileName, index=False)
    return "success"

#embedding
def load_embeddings_to_faiss(df):
    embeddings = np.array(df['embedding'].tolist()).astype('float32')
    index = faiss.IndexFlatL2(embeddings.shape[1])
    index.add(embeddings)
    print("load_embeddings_to_faiss index : %s\n" % index)
    return index

def search_index(index, df, query, k=1):
    query_vector = np.array(get_embedding(query, engine=embedding_model)).reshape(1, -1).astype('float32')
    distances, indexes = index.search(query_vector, k)
    results = []
    for i in range(len(indexes)):
        abbreviation = df.iloc[indexes[i]]['abbreviation'].values.tolist()
        description = df.iloc[indexes[i]]['description'].values.tolist()
        results.append((distances[i], abbreviation))
    print("description : %s\n" % description[0])
    return abbreviation[0]

def set_openai_api_key(apikey):
    if isinstance(apikey, str):
        openai.api_key = apikey
    else:
        openai.api_key = os.environ.get("OPENAI_API_KEY")

def get_response(prompt):
    completions = openai.Completion.create (
        engine=COMPLETION_MODEL,
        prompt=prompt,
        max_tokens=3000,
        n=1,
        stop=None,
        temperature=0.0,
    )
    message = completions.choices[0].text
    print(message)
    return message

requestTime = 0


@app.route('/question')
def gen():
    prompt = request.args.get('prompt')
    question = request.args.get('question')
    apiKey = request.args.get('apiKey')
    set_openai_api_key(apiKey)
    conv1 = Conversation(prompt, 1)
    result = conv1.ask(question)
    print("Assistant : %s\n" % result)
    return result

@app.route('/fembedding')
def findTmpByEmbedding():
    if not os.path.exists(dfFileName):
        return '不存在任何模版数据'
    prompt = request.args.get('question')
    apiKey = request.args.get('apiKey')
    set_openai_api_key(apiKey)
    df = pd.read_parquet(dfFileName)
    index = load_embeddings_to_faiss(df)
    result = search_index(index,df,prompt,k=1)

    return result


@app.route('/embedding', methods=["POST"])
def embedding():
    data = request.json
    print("request.json : %s\n" % data)
    headers = request.headers
    apiKey = headers.get("apiKey")
    set_openai_api_key(apiKey)
    df = pd.DataFrame(data)
    print(df.head())
    return add_embeddings_data_tofile(df,False)

@app.route('/force/embedding', methods=["POST"])
def forceEmbeddings():
    data = request.json
    print("request.json : %s\n" % data)
    headers = request.headers
    apiKey = headers.get("apiKey")
    print("request.apikey : %s\n" % apiKey)
    set_openai_api_key(apiKey)
    df = pd.DataFrame(data)
    print(df.head())
    return add_embeddings_data_tofile(df,True)