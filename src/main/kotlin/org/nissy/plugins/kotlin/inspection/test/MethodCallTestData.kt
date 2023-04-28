package org.nissy.plugins.kotlin.inspection.test

import org.nissy.plugins.kotlin.dataBean.KtExpr
import org.nissy.plugins.kotlin.dataBean.MethodCallStatute

class MethodCallTestData : MethodCallStatute() {
    init {
        errorExprs = HashSet()

        val ktErrorExpr1 = KtExpr()
        ktErrorExpr1.expression = "NavProviderProxyTest.toUri222()"
        ktErrorExpr1.classPkgName = "cn.emp.base.NavProviderProxyTest"
        errorExprs.add(ktErrorExpr1)

        val ktErrorExpr2 = KtExpr()
        ktErrorExpr2.expression = "NavProviderProxy.toUri()"
        errorExprs.add(ktErrorExpr2)

        val ktFixExpr = KtExpr()
        ktFixExpr.expression = "NavProviderProxy.toUri(context,action)"
        ktFixExpr.classPkgName = "com.emp.oneservice.nav.NavProviderProxy"
        fixExpr = ktFixExpr
        highlightLevel = 1

        fixDesc = "请使用com.emp.oneservice.nav.NavProviderProxy中的toUri(context,action)方法"
        fixTips = "使用Oneservice的方法"
        documentUrlString = "https://yuque.antfin-inc.com/kveiy9/qpp4vh/miigcx"
        whiteList.add("com.emp.middlewareservice.provider.info.NetworkInfoProviderProxy")
    }
}

