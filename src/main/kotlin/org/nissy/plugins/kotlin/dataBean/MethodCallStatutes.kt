package org.nissy.plugins.kotlin.dataBean

import com.alibaba.fastjson.annotation.JSONField
import com.intellij.codeInspection.ProblemHighlightType

/**
 * 方法调用规约合集数据bean
 *
 */
class MethodCallStatutes {
    var statutes: MutableSet<MethodCallStatute> = HashSet()
}

open class MethodCallStatute {

    /**
     * 说明文档 空字符串不展示 查看文档 提示
     */
    var documentUrlString = ""

    /**
     * 错误的表达式
     */
    var errorExprs: MutableSet<KtExpr> = HashSet()

    /**
     * 修复后正确的表达式
     */
    var fixExpr: KtExpr = KtExpr()

    /**
     * 修复提示
     */
    var fixTips: String = ""

    /**
     * 修复描述
     */
    var fixDesc: String = ""

    /**
     * 白名单
     * 例如：org.nissy.plugins.kotlin.inspection.project.MethodCallInspection
     */
    var whiteList: MutableSet<String> = HashSet()

    /**
     * 高亮级别 文件使用
     * @see com.intellij.codeInspection.ProblemHighlightType
     * 0：ERROR
     * 1:WARNING
     * 2:WEAK_WARNING
     * 3:INFORMATION
     */
    var highlightLevel: Int = 0
        set(value) {
            field = value
            when (highlightLevel) {
                0 -> {
                    highlightType = ProblemHighlightType.ERROR
                }
                1 -> {
                    highlightType = ProblemHighlightType.WARNING
                }
                2 -> {
                    highlightType = ProblemHighlightType.WEAK_WARNING
                }
                3 -> {
                    highlightType = ProblemHighlightType.INFORMATION
                }
                else -> {
                    highlightType = ProblemHighlightType.ERROR
                }
            }
        }

    /**
     * 高亮级别 程序内使用
     * 通过highlightLevel转换
     */
    @JSONField(serialize = false, deserialize = false)
    var highlightType: ProblemHighlightType = ProblemHighlightType.ERROR
}

class KtExpr {
    /**
     * 表达式
     */
    var expression: String = ""
        set(value) {
            field = value
            firstCall = expression.split(".")[0]

            val strs: List<String> = expression.split(".")
            lastCall = strs[strs.size - 1]
        }

    /**
     * 完整路径
     */
    var classPkgName: String = ""
        set(value) {
            field = value
            val strs: List<String> = classPkgName.split(".")
            className = strs[strs.size - 1]
        }

    /**
     * 主体类名
     */
    @JSONField(serialize = false, deserialize = false)
    var className: String = ""

    /**
     * 调用主体
     */
    @JSONField(serialize = false, deserialize = false)
    var firstCall: String = ""


    /**
     * 最终调用
     */
    @JSONField(serialize = false, deserialize = false)
    var lastCall: String = ""

}
