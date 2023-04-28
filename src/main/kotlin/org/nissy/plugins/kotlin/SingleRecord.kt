package org.nissy.plugins.kotlin

import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import org.nissy.plugins.kotlin.Config.Companion.METHOD_CALL_STATUTE
import org.nissy.plugins.kotlin.dataBean.MethodCallStatutes
import org.nissy.plugins.kotlin.inspection.init.MainInitData
import org.nissy.plugins.kotlin.inspection.test.MainTestData
import org.nissy.plugins.kotlin.notification.KtNotification

import org.nissy.plugins.kotlin.utils.FileUtils
import java.io.File

class SingleRecord private constructor() {
    companion object {
        val currentFileIsKt
            get() = currentFileName.contains(".kt")
        var currentFileName = ""

        private var singleton: SingleRecord? = null
        fun getSingleRecord(): SingleRecord? {
            if (singleton == null) {
                singleton = SingleRecord()
            }
            return singleton
        }
    }

    var methodCall: MethodCallStatutes? = null
    private var statutesPath: String = METHOD_CALL_STATUTE

    fun init(path: String): Boolean {
        statutesPath = path
        initRecordFile()
        return initMethodCall()
    }

    fun get(): SingleRecord? {
        return if (methodCall != null) {
            this
        } else {
            null
        }
    }


    /**
     * 初始化方法调用规约
     */
    private fun initMethodCall(): Boolean {
        val stringBuilder = StringBuilder()
        FileUtils.readFile(statutesPath, stringBuilder)
        //远端文件失败使用本地文件
        if (stringBuilder.isEmpty()) {
            KtNotification.showErrorNotification("规约初始化失败", "规约初始化失败,规约文件内容为空")
            return false
        }
        try {
            val recordJson = JSONObject.parseObject(stringBuilder.toString())
            recordJson?.let {
                methodCall = JSONObject.parseObject(recordJson.toJSONString(), MethodCallStatutes().javaClass)
            }
            return true
        } catch (e: JSONException) {
            e.printStackTrace()
            KtNotification.showErrorNotification("规约初始化失败", "文件格式错误")
            return false
        }
    }

    /**
     * 检查文件是否存在
     */
    private fun initRecordFile() {
        FileUtils.checkDirExists(org.nissy.plugins.kotlin.Config.STATUTES_PATH)

        val methodCallFile = File(METHOD_CALL_STATUTE)
        if (!methodCallFile.exists()) {
            //创建文件
            if (!methodCallFile.createNewFile()) {
                throw NullPointerException("方法调用规约文件（methodCall.json）不存在！")
            } else {
                generateInitData()
            }
            if (org.nissy.plugins.kotlin.Config.TEST_MODE) {
                generateTestData()
            }
        }
    }

    private fun generateInitData() {
        val a = JSONObject.toJSONString(MainInitData().initData)
        FileUtils.writeFile(METHOD_CALL_STATUTE, a)
    }

    private fun generateTestData() {
        val a = JSONObject.toJSONString(MainTestData().testData)
        FileUtils.writeFile(METHOD_CALL_STATUTE, a)
    }

}

