package org.nissy.plugins.kotlin.inspection.project.method

import com.intellij.codeInspection.ProblemsHolder
import org.nissy.plugins.kotlin.dataBean.KtExpr
import org.nissy.plugins.kotlin.dataBean.MethodCallStatute
import org.nissy.plugins.kotlin.fix.project.DirectCallQuickFix
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * 直接调用
 * 例如：
 * NavProviderProxy.getProxy(context).toUri("ttttt")
 */
class DirectCallInspection(
    // 表达式第一项为主体 最末一项为目标方法调用
    private val expression: KtCallExpression,
    private val holder: ProblemsHolder,
    private val cache: CacheRecord,
    private val statute: MethodCallStatute,
    private val errorExpr: KtExpr,
) : IHandler {
    override fun handle() {
        if (checkCallNameEqs(expression.parent.children[0].text, errorExpr.firstCall)) {
            DirectCallQuickFix(
                cache.importList,
                statute,
                isNeedFixImport = !cache.imports.contains(statute.fixExpr.classPkgName)
            ).registerProblem(holder, expression)
        }
    }
}
