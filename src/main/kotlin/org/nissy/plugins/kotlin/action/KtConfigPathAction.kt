package org.nissy.plugins.kotlin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.nissy.plugins.kotlin.KtCodeIcons
import org.nissy.plugins.kotlin.view.ConfigView

class KtConfigPathAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { ConfigView(it).open() }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.icon = KtCodeIcons.PROJECT_CONFIG
    }
}