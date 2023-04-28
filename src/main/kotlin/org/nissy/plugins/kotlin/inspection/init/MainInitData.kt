package org.nissy.plugins.kotlin.inspection.init

import org.nissy.plugins.kotlin.dataBean.MethodCallStatutes

class MainInitData {
    var initData: MethodCallStatutes = MethodCallStatutes()

    init {
        //添加初始化数据
        initData.statutes.add(DefaultInit())
    }
}