package org.nissy.plugins.kotlin.fix.project

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.ide.BrowserUtil.browse
import com.intellij.openapi.project.Project

/**
 * 打开网页action
 */
open class OpenHtmlAction(
    private val url: String
) : LocalQuickFix {
    companion object {
        const val OpenHtmlActionName = "查看相关文档"
    }

    override fun getFamilyName(): String {
        return OpenHtmlActionName
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        browse(url)
    }
}