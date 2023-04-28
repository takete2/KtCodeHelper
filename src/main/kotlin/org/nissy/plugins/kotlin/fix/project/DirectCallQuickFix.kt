package org.nissy.plugins.kotlin.fix.project


import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.nissy.plugins.kotlin.dataBean.MethodCallStatute
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPsiFactory


/**
 * 直接调用场景的quick fix
 * 扫描见：
 * @see org.nissy.plugins.kotlin.inspection.project.method.DirectCallInspection
 */
class DirectCallQuickFix(
    importList: KtImportList,
    statute: MethodCallStatute,
    isNeedFixMethod:Boolean = true,
    isNeedFixImport:Boolean = true
) : MethodCallQuickFix(importList,statute,isNeedFixMethod = isNeedFixMethod,isNeedFixImport = isNeedFixImport) {

    override fun methodFix(project: Project, descriptor: ProblemDescriptor, statute: MethodCallStatute) {
        val newExpr = KtPsiFactory(project).createExpression(statute.fixExpr.expression)
        descriptor.psiElement.parent.replace(newExpr)
    }


}