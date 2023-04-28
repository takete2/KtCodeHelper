package org.nissy.plugins.kotlin.services

import com.intellij.openapi.project.Project
import org.nissy.plugins.kotlin.Config
import org.nissy.plugins.kotlin.SingleRecord
import org.nissy.plugins.kotlin.notification.KtNotification

class KtCodeSytleProjectService(project: Project) {
    val project: Project
    val config: org.nissy.plugins.kotlin.Config
    init {
//        val service = ServiceManager.getService(project, MyApplicationService::class.java)
//        println(MyBundle.message("projectService", project.name))
        this.project = project
        KtNotification.init(project)
        config = org.nissy.plugins.kotlin.Config()
        config.configData.let {
            SingleRecord.getSingleRecord()?.init(it.statutesPath)
        }

    }

}
