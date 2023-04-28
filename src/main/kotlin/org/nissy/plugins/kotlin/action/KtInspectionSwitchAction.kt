package org.nissy.plugins.kotlin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.nissy.plugins.kotlin.KtCodeIcons
import org.nissy.plugins.kotlin.SingleRecord
import org.nissy.plugins.kotlin.services.KtCodeSytleProjectService
import org.nissy.plugins.kotlin.utils.MyBundle

class KtInspectionSwitchAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val switch = project.getService(KtCodeSytleProjectService::class.java).config.realTimeScan
        project.getService(KtCodeSytleProjectService::class.java).config.realTimeScan = !switch
    }
    override fun update(e: AnActionEvent) {
        val project = e.project ?: return
        val smartFoxConfig = project.getService(KtCodeSytleProjectService::class.java).config
        e.presentation.isVisible = SingleRecord.currentFileIsKt
        e.presentation.text = if (!smartFoxConfig.realTimeScan) {
            e.presentation.icon = KtCodeIcons.PROJECT_INSPECTION_ON
            MyBundle.getMessage("openText")
        } else {
            e.presentation.icon = KtCodeIcons.PROJECT_INSPECTION_OFF
            MyBundle.getMessage("closeText")
        }
    }
}