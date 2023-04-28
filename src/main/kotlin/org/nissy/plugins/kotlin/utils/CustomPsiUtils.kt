package org.nissy.plugins.kotlin.utils

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

class CustomPsiUtils {
    companion object{
        /**
         * 找到表达式的命名方法节点/类节点
         * 抽象语法中要保证T一定比K先到达 即T为K的子/孙节点
         */
        inline fun <reified T : PsiElement, reified K : PsiElement> findParentUntil(expression: PsiElement): List<PsiElement?>? {
            var element = expression
            val result: MutableList<PsiElement?> = arrayListOf(null, null)
            while (element !is KtFile) {
                element = element.parent
                if (element is T && result[0] == null) {
                    result[0] = element
                }
                if (element is K && result[1] == null) {
                    result[1] = element
                    break
                }
            }
            if (result[0] == null && result[1] == null) {
                return null
            }
            return result
        }
    }
}