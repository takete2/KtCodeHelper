package org.nissy.plugins.kotlin.inspection.project.method

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.nissy.plugins.kotlin.dataBean.KtExpr
import org.nissy.plugins.kotlin.dataBean.MethodCallStatute
import org.nissy.plugins.kotlin.fix.project.IndirectCallQuickFix
import org.nissy.plugins.kotlin.utils.CustomPsiUtils
import org.jetbrains.kotlin.psi.*

/**
 * 间接调用
 * 例如：
 * fun test(){
 *  val n = NavProviderProxy.getProxy(context)
 *  ...
 *  n.toUri("ttttt")
 *  }
 * 或者:
 *  class org.nissy.plugins.kotlin.Test{
 *    val n = NavProviderProxy.getProxy(context)
 *    fun test(){
 *      n.toUri("ttttt")
 *    }
 * }
 */
class IndirectCallInspection(
    private val expression: KtCallExpression,
    private val holder: ProblemsHolder,
    private val cache: CacheRecord,
    private val statute: MethodCallStatute,
    private val errorExpr: KtExpr
) : IHandler {
    override fun handle() {
        val variable = expression.parent.text.split(".")[0]
        //找到方法体
        val target: List<PsiElement?> = CustomPsiUtils.findParentUntil<KtNamedFunction, KtClass>(expression) ?: return
        //获取所有定义的属性
        target.forEach target@{
            it?.let {
                it.children.forEach { element ->
                    if (element is KtBlockExpression || element is KtClassBody) {
                        element.children.forEach { blockElement ->
                            if (blockElement is KtProperty) {
                                if (!handleProperty(":", variable, blockElement)) {
                                    if (handleProperty("=", variable, blockElement)) {
                                        return@target
                                    }
                                } else {
                                    return@target
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleProperty(splitSymbol: String, variable: String, blockElement: KtProperty): Boolean {
        if (blockElement.text.contains(splitSymbol)) {
            val propertyItems = blockElement.text.split(splitSymbol)
            if (propertyItems.size >= 2) {
                val propertyLeftItems = propertyItems[0].trim().split(" ")
                val propertyKey = propertyLeftItems[propertyLeftItems.size - 1]
                val propertyValue = propertyItems[1].split(".")[0].split("(")[0].trim()
                // 找到初始化表达式 替换类名
                if (propertyKey == variable && propertyValue == errorExpr.firstCall){
                    IndirectCallQuickFix(
                        cache.importList,
                        statute,
                        expression,
                        errorExpr,
                        isNeedFixImport = !cache.imports.contains(statute.fixExpr.classPkgName)
                    ).registerProblem(holder, blockElement)
                }
                return true
            }

        }
        return false
    }
}
