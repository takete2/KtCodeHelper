package org.nissy.plugins.kotlin

import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.intellij.openapi.application.PathManager
import org.nissy.plugins.kotlin.dataBean.ConfigBean
import org.nissy.plugins.kotlin.utils.FileUtils
import org.nissy.plugins.kotlin.utils.MyBundle
import java.io.File

class Config {

    companion object {
        /**
         * 规约文件夹路径
         */
        val STATUTES_PATH: String = PathManager.getPluginsPath() + "/" + MyBundle.message("name") + "/statutes"

        /**
         * 默认规约文件路径
         */
        val METHOD_CALL_STATUTE = "${org.nissy.plugins.kotlin.Config.Companion.STATUTES_PATH}/methodCall.json"

        /**
         * 规约配置文件路径
         */
        val STATUTES_CONFIG_PATH: String =
            PathManager.getPluginsPath() + "/" + MyBundle.message("name") + "/statutes/config.json"


        /**
         * 是否是测试模式
         */
        val TEST_MODE: Boolean = false
    }

    var configData: ConfigBean = ConfigBean()

    /**
     * 是否开启实时扫描
     */
    var realTimeScan = true

    init {
        initConfigFile()
        initConfigData()
    }

    private fun initConfigData() {
        val stringBuilder = StringBuilder()
        FileUtils.readFile(org.nissy.plugins.kotlin.Config.Companion.STATUTES_CONFIG_PATH, stringBuilder)
        try {
            val configJson = JSONObject.parseObject(stringBuilder.toString())
            configJson?.let {
                configData = JSONObject.parseObject(configJson.toJSONString(), ConfigBean().javaClass)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun initConfigFile() {
        FileUtils.checkDirExists(org.nissy.plugins.kotlin.Config.Companion.STATUTES_PATH)
        val methodCallFile = File(org.nissy.plugins.kotlin.Config.Companion.STATUTES_CONFIG_PATH)
        if (!methodCallFile.exists()) {
            //创建文件
            if (!methodCallFile.createNewFile()) {
                throw NullPointerException("配置文件（config.json）不存在且创建失败")
            } else {
                generateInitData()
            }
        }
    }

    private fun generateInitData() {
        val a = JSONObject.toJSONString(ConfigBean())
        FileUtils.writeFile(org.nissy.plugins.kotlin.Config.Companion.STATUTES_CONFIG_PATH, a)
    }

    fun updateConfigFile(configData: ConfigBean) {
        this.configData = configData
        val a = JSONObject.toJSONString(configData)
        FileUtils.writeFile(org.nissy.plugins.kotlin.Config.Companion.STATUTES_CONFIG_PATH, a)
    }
}

