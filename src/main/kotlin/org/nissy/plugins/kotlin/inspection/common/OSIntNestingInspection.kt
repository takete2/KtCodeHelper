package org.nissy.plugins.kotlin.inspection.common

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*

class OSIntNestingInspection(expression: KtBlockExpression, holder: ProblemsHolder) {
    companion object {
        const val REPORT_TEXT = "The nesting level is too deep, please consider simplifying the logic or extracting the method"
    }
    init {
        val deep = intArrayOf(0)
        checkBlockDeep(expression, deep)
        if (deep[0] > 3) {
            holder.registerProblem(
                expression,
                REPORT_TEXT,
                ProblemHighlightType.WARNING
            )
        }
    }

    private fun checkBlockDeep(expression: PsiElement, deep: IntArray) {
        //父亲是一个闭包 且不是一个方法
        if (expression.parent is KtNamedFunction || expression.parent is KtFile || expression.parent is KtClassInitializer) {
           //Compose方法可以额外嵌套一层
            if (expression.parent is KtNamedFunction && expression.parent.firstChild is KtModifierList){
                expression.parent.firstChild.children.forEach {
                    if (it.text.equals("@Composable")){
                        deep[0] -= 1
                    }
                }
            }
            return
        }
        if (expression is KtBlockExpression) {
            deep[0] += 1
        }
        checkBlockDeep(expression.parent, deep)
    }
}