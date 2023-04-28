package org.nissy.plugins.kotlin.view

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import org.nissy.plugins.kotlin.Config
import org.nissy.plugins.kotlin.SingleRecord
import org.nissy.plugins.kotlin.dataBean.ConfigBean
import org.nissy.plugins.kotlin.notification.KtNotification
import org.nissy.plugins.kotlin.services.KtCodeSytleProjectService
import java.awt.Dimension
import javax.swing.JDialog

class ConfigView constructor(val project: Project) : JDialog() {
    private var content: javax.swing.JPanel? = null
    private var configPath: javax.swing.JTextField? = null
    private var btn: javax.swing.JButton? = null
    private var useDefault: javax.swing.JCheckBox? = null

    init {
        contentPane = content
        isModal = true
        getRootPane().defaultButton = btn
        configPath?.text =
            ServiceManager.getService(project, KtCodeSytleProjectService::class.java).config.configData.statutesPath
        btn?.addActionListener { onOK() }
    }

    private fun onOK() {
        configPath?.text?.let { path ->
            val data = ConfigBean()
            val rPath = if (useDefault?.isSelected == true) org.nissy.plugins.kotlin.Config.METHOD_CALL_STATUTE else path
            data.statutesPath = rPath
            //更新内存中的规约
            SingleRecord.getSingleRecord()?.let {
                if (it.init(rPath)) {
                    KtNotification.showInfoNotification("规约更新成功", "规约已更新，改改代码试试吧～")
                    //更新文件中的规约文件路径
                    ServiceManager.getService(project, KtCodeSytleProjectService::class.java).config.updateConfigFile(data)
                }
            }
        }
        dispose()
    }

    fun open() {
        pack()
        title = "规约配置"
        minimumSize = Dimension(450, 200)
        //两个屏幕处理出现问题，跳到主屏幕去了
        setLocationRelativeTo(WindowManager.getInstance().getFrame(project))
        isVisible = true
    }
}