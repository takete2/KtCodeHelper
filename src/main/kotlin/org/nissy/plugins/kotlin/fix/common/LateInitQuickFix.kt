package org.nissy.plugins.kotlin.fix.common

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.nissy.plugins.kotlin.notification.KtNotification
import org.jetbrains.kotlin.psi.*

/**
 * 修复lateinit
 */
open class LateInitQuickFix : LocalQuickFix {
    companion object {
        const val ActionName = "add judgment"
    }

    override fun getFamilyName(): String {
        return ActionName
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val text = descriptor.psiElement.text
        val topExp = findTopExpr(descriptor.psiElement)

        val newExpression = KtPsiFactory(project).createExpression("::$text.isInitialized")
        if (topExp != null) {
            val newIdf = KtPsiFactory(project).createIf(
                newExpression,
                topExp,
                null
            )
            topExp.replace(newIdf)
        } else {
            KtNotification.showErrorNotification("无法修复","无法针对这种情况快速修复，请手动修复")
        }
    }

    private fun findTopExpr(psiElement: PsiElement?): KtExpression? {
        if (psiElement == null) {
            return null
        }
        var temp = psiElement
        while (temp !is KtFile) {
            temp = temp?.parent
            if (temp is KtExpression && (temp.parent is KtBlockExpression || temp.parent is KtFile)) {
                return temp
            }
        }
        return null
    }
}