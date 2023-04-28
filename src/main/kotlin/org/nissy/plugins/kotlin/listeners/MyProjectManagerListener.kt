package org.nissy.plugins.kotlin.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import org.nissy.plugins.kotlin.SingleRecord
import org.nissy.plugins.kotlin.services.KtCodeSytleProjectService

internal class MyProjectManagerListener : ProjectManagerListener {


    override fun projectOpened(project: Project) {
        project.service<KtCodeSytleProjectService>()
    }
}

internal class MyProjectFileListener : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {
        event.newFile?.name?.let {
            SingleRecord.currentFileName = it
        }
        super.selectionChanged(event)
    }
}
