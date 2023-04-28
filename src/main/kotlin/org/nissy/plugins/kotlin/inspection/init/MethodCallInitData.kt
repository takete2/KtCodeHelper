package org.nissy.plugins.kotlin.inspection.init

import org.nissy.plugins.kotlin.dataBean.KtExpr
import org.nissy.plugins.kotlin.dataBean.MethodCallStatute

class DefaultInit : MethodCallStatute() {
    init {
        errorExprs = HashSet()

        val ktErrorExpr0 = KtExpr()
        ktErrorExpr0.expression = "System.out.println()"
        ktErrorExpr0.classPkgName = ""
        errorExprs.add(ktErrorExpr0)

        val ktFixExpr = KtExpr()
        ktFixExpr.expression = "println(\"ktCodeHelperFix\")"
        ktFixExpr.classPkgName = ""
        fixExpr = ktFixExpr
        highlightLevel = 1
        documentUrlString = "https://plugins.jetbrains.com/plugin/20414-ktcodehelper"

        fixDesc = "ktCodeHelper默认提示"
        fixTips = "ktCodeHelper默认修复"
    }
}