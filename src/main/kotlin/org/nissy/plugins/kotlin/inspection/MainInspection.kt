package org.nissy.plugins.kotlin.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.nissy.plugins.kotlin.Config
import org.nissy.plugins.kotlin.SingleRecord
import org.nissy.plugins.kotlin.inspection.common.LateInitInspection
import org.nissy.plugins.kotlin.inspection.common.OSIntNestingInspection
import org.nissy.plugins.kotlin.inspection.project.method.MethodCallInspection
import org.nissy.plugins.kotlin.services.KtCodeSytleProjectService
import org.intellij.markdown.flavours.gfm.table.GitHubTableMarkerProvider.Companion.contains
import org.jetbrains.kotlin.j2k.ast.LBrace
import org.jetbrains.kotlin.j2k.ast.LambdaExpression
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * 项目规约代码检查开始的地方
 */
class MainInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return MainInspectionVisitorVoid(holder)
    }

}

class MainInspectionVisitorVoid(holder: ProblemsHolder) : KtVisitorVoid() {
    private var holder: ProblemsHolder

    init {
        this.holder = holder
    }


    override fun visitKtElement(element: KtElement) {
        super.visitKtElement(element)
        if (org.nissy.plugins.kotlin.Config.TEST_MODE) {
            println("value:" + element.text + "     type" + element.toString())
        }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (ServiceManager.getService(
                holder.project,
                KtCodeSytleProjectService::class.java
            ).config.realTimeScan
        ) {
            LateInitInspection.getLateInitInsp(property, SingleRecord.currentFileName)
        }

    }


    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        if (ServiceManager.getService(
                holder.project,
                KtCodeSytleProjectService::class.java
            ).config.realTimeScan
        ) {
            LateInitInspection.getLateInitInsp(null, SingleRecord.currentFileName)?.recordCheck(expression)
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        if (ServiceManager.getService(
                holder.project,
                KtCodeSytleProjectService::class.java
            ).config.realTimeScan
        ) {
            LateInitInspection.getLateInitInsp(null, SingleRecord.currentFileName)?.report(expression, holder)
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (ServiceManager.getService(
                holder.project,
                KtCodeSytleProjectService::class.java
            ).config.realTimeScan
        ) {
            MethodCallInspection(expression, holder)
        }
    }

    override fun visitBlockExpression(expression: KtBlockExpression) {
        super.visitBlockExpression(expression)
        if (ServiceManager.getService(
                holder.project,
                KtCodeSytleProjectService::class.java
            ).config.realTimeScan
        ) {
            OSIntNestingInspection(expression, holder)
        }
    }

    fun judgeScan(holder: ProblemsHolder,element: KtElement): Boolean {
        return ServiceManager.getService(
            holder.project,
            KtCodeSytleProjectService::class.java
        ).config.realTimeScan && element.containingFile.name.contains("kts")
    }
}


