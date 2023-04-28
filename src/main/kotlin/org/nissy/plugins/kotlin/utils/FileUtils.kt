package org.nissy.plugins.kotlin.utils

import java.io.*
import java.net.URL

/**
 * 文件操作工具类
 */
object FileUtils {
    /**
     * 将数据写入指定文件中
     *
     * @param fileName 文件完整路径+名称
     * @param data     数据内容
     */
    @Synchronized
    fun writeFile(fileName: String?, data: String?) {
        if (fileName.isNullOrEmpty() || data.isNullOrEmpty()) {
            return
        }
        val file = File(fileName)
        var fileWriter: FileWriter? = null
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    run { throw IOException() }
                }
            }
            fileWriter = FileWriter(fileName)
            fileWriter.write(data)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }

    /**
     * 读取文件，逐行读取
     *
     * @param fileName      文件名称（完整路径）
     * @param stringBuilder 件字符会添加到这里
     */
    fun readFile(fileName: String, stringBuilder: StringBuilder) {
        if (fileName.isEmpty()) {
            return
        }
        try {
            if (fileName.startsWith("http")) {
                downLoadFileToJsonString(fileName, stringBuilder)
            } else {
                val file = File(fileName)
                BufferedReader(FileReader(file)).use { reader ->
                    var tempString: String?
                    while (reader.readLine().also { tempString = it } != null) {
                        stringBuilder.append(tempString)
                        println(tempString)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            stringBuilder.clear()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            stringBuilder.clear()
        }
    }

//    /**
//     * 读取文件，读到指定行时将其添加到stringBuilder中
//     * 逐行读取
//     * 指定行：此行包含readFlag
//     *
//     * @param fileName      文件名称（完整路径）
//     * @param stringBuilder 文件字符会添加到这里
//     * @param readFlag      读取标识
//     */
//    fun readFile(fileName: String?, stringBuilder: StringBuilder, readFlag: String?) {
//        if (fileName.isNullOrEmpty()) {
//            return
//        }
//        val file = File(fileName)
//        try {
//            BufferedReader(FileReader(file)).use { reader ->
//                var tempString: String
//                while (reader.readLine().also { tempString = it } != null) {
//                    //添加结束行
//                    if (tempString.contains(readFlag!!)) {
//                        stringBuilder.append(tempString)
//                        break
//                    }
//                }
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

    @Synchronized
    fun downLoadFileToJsonString(url: String, buffer: StringBuilder) {
        val inputStream: InputStream = URL(url).openStream()
        val bufferedInputStream = BufferedInputStream(inputStream)
        val bufferedReader = BufferedReader(InputStreamReader(bufferedInputStream, "utf-8"))
        try {
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.read().toChar())
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            bufferedReader.close()
            bufferedInputStream.close()
            inputStream.close()
        }
    }

    fun checkDirExists(path: String) {
        val dir = File(path)
        if (!dir.exists()) {
            //创建文件夹
            if (!dir.mkdirs()) {
                throw NullPointerException("statute文件夹不存在！")
            }
        }
    }
}