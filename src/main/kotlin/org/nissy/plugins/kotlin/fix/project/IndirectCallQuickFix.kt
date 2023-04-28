package org.nissy.plugins.kotlin.fix.project


import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.smartPointers.SmartPointerManagerImpl
import org.nissy.plugins.kotlin.dataBean.KtExpr
import org.nissy.plugins.kotlin.dataBean.MethodCallStatute
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPsiFactory


/**
 * 方法内间接调用场景的quick fix
 * @see org.nissy.plugins.kotlin.inspection.project.method.IndirectCallInspection
 */
class IndirectCallQuickFix(
    importList: KtImportList,
    statute: MethodCallStatute,
    expression: KtCallExpression,
    private val errorExpr: KtExpr,
    isNeedFixMethod:Boolean = true,
    isNeedFixImport:Boolean = true
) : MethodCallQuickFix(importList, statute,isNeedFixMethod = isNeedFixMethod,isNeedFixImport = isNeedFixImport) {

    private var callExpressionPointer: SmartPsiElementPointer<KtCallExpression>? = null

    init {
        callExpressionPointer = SmartPointerManagerImpl.createPointer(expression)
    }


    override fun methodFix(project: Project, descriptor: ProblemDescriptor, statute: MethodCallStatute) {
        val newProperty = KtPsiFactory(project).createProperty(
            descriptor.psiElement.text.replace(
                errorExpr.firstCall,
                statute.fixExpr.className
            )
        )
        descriptor.psiElement.replace(newProperty)

        if (errorExpr.lastCall != statute.fixExpr.lastCall ){
            callExpressionPointer?.element?.text?.replace(errorExpr.lastCall, statute.fixExpr.lastCall)?.let {
                val newExpr = KtPsiFactory(project).createExpression(it)
                callExpressionPointer?.element?.replace(newExpr)
            }
        }
    }
}

