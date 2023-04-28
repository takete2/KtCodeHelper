package org.nissy.plugins.kotlin.inspection.common

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.nissy.plugins.kotlin.fix.common.LateInitQuickFix
import org.nissy.plugins.kotlin.utils.CustomPsiUtils
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

class LateInitInspection private constructor(
    private val lateInitProperty: KtProperty?,
    private val fileName: String
) {
    private val lateInitProperties: MutableMap<String, Boolean> = HashMap()

    companion object {
        const val JUDGE_TEXT = "isInitialized"
        const val ATTRIBUTE_KEY = "lateinit"
        const val REPORT_TEXT = "It may cause the lateinit attribute in the Public method, it is recommended to use the isInitialized method to judge or set it as a nullable variable of var"

        private var singleton: LateInitInspection? = null
        fun getLateInitInsp(
            lateInitProperty: KtProperty?,
            fileName: String
        ): LateInitInspection? {
            if (singleton == null || fileName != singleton?.fileName) {
                singleton = LateInitInspection(lateInitProperty, fileName)
            }
            singleton?.addLateInitProperties(lateInitProperty)
            return singleton
        }
    }

    init {
        lateInitProperty?.name?.let {
            if (lateInitProperty.text.replace(it, "").contains(ATTRIBUTE_KEY)) {
                lateInitProperties[it] = false
            }
        }
    }

    fun addLateInitProperties(lateInitProperty: KtProperty?) {
        lateInitProperty?.name?.let {
            if (lateInitProperty.text.replace(it, "").contains(ATTRIBUTE_KEY)) {
                lateInitProperties[it] = false
            }
        }
    }

    fun recordCheck(expression: KtReferenceExpression) {
        if (expression.text == JUDGE_TEXT) {
            expression.parent.text.split("::").forEach {
                lateInitProperties[it.split(".")[0]] = true
            }
        } else if (lateInitProperties.contains(expression.text)
            && expression.parent.text.contains("=")
            && expression.parent.text.split("=")[0].trim() == expression.text
        ) {
            lateInitProperties[expression.text] = true
        }
    }

    fun report(expression: KtDotQualifiedExpression, holder: ProblemsHolder) {
        val params = expression.receiverExpression.text.replace("::", "")
        if (!lateInitProperties.contains(params) || expression.text.contains(JUDGE_TEXT)) {
            return
        }
        val target: List<PsiElement?> =
            CustomPsiUtils.findParentUntil<KtBlockExpression, KtNamedFunction>(expression) ?: return
        val pBlock = target[0] as KtBlockExpression?
        val pFun = target[1] as KtNamedFunction?
        if (pFun != null && pFun.isPrivate()) {
            if (lateInitProperties[params] != true) {
                holder.registerProblem(
                    expression.firstChild,
                    REPORT_TEXT,
                    ProblemHighlightType.WARNING,
                    LateInitQuickFix()
                )
            }
        } else {
            if (!hasCheckOrEQ(pBlock, pFun, params)) {
                holder.registerProblem(
                    expression.firstChild,
                    REPORT_TEXT,
                    ProblemHighlightType.WARNING,
                    LateInitQuickFix()
                )
            }
        }

    }

    private fun hasCheckOrEQ(pBlock: KtBlockExpression?, pFun: KtNamedFunction?, param: String): Boolean {
        if (pBlock == null) {
            return false
        }
        if (hasCheckOrEQ(pBlock, param)) {
            return true
        } else {
            if (pFun == null) {
                return false
            }
            val block = pFun.bodyBlockExpression
            return hasCheckOrEQ(block, param)
        }
    }

    private fun hasCheckOrEQ(element: PsiElement?, param: String): Boolean {
        var result = false
        element?.children?.forEach {
            if (it is KtIfExpression) {
                result =
                    (it.condition?.text?.contains(JUDGE_TEXT) == true && it.condition?.text?.contains(param) == true)
            } else if (it is KtBinaryExpression) {
                if (it.left is KtReferenceExpression) {
                    result = (it.left as KtReferenceExpression).text == param
                }
            }
            if (result) {
                return true
            }
        }
        return false
    }

}