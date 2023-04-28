package org.nissy.plugins.kotlin.inspection.test

import org.nissy.plugins.kotlin.dataBean.MethodCallStatutes

class MainTestData {
    var testData: MethodCallStatutes = MethodCallStatutes()

    init {
        //添加测试
//        testData.statutes.add(NavProviderProxytToUri())
//        testData.statutes.add(AppInfoProviderProxyGetApplication())
//        testData.statutes.add(AppInfoProviderProxyIsDebugable())
//        testData.statutes.add(DeviceInfoProviderGetStatusBarHeight())
//        testData.statutes.add(CloudConfigProviderGet())
//        testData.statutes.add(Permission())
//        testData.statutes.add(Location())

        testData.statutes.add(MethodCallTestData())


    }
}