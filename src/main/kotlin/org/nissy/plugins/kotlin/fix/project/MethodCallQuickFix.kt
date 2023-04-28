package org.nissy.plugins.kotlin.fix.project

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.smartPointers.SmartPointerManagerImpl
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath
import org.nissy.plugins.kotlin.dataBean.MethodCallStatute

/**
 * 方法Quick Fix统一父类
 * 处理import
 * 子类重写methodFix方法 这样子类只需要关注自己场景下需要修复的表达式即可
 */
open class MethodCallQuickFix(
    importList: KtImportList,
    private val statute: MethodCallStatute,
    private val isNeedFixMethod:Boolean = true,
    private val isNeedFixImport:Boolean = true
) : LocalQuickFix {

    private var importListPointer: SmartPsiElementPointer<KtImportList>? = null
    private var importPointer: SmartPsiElementPointer<KtImportDirective>? = null
    private var isSameClassName: Boolean = false

    companion object {
        const val ActionName = "查看相关文档"
    }

    init {
        importListPointer = SmartPointerManagerImpl.createPointer(importList)

        for (importDirective: KtImportDirective in importList.imports) {
            if (importDirective.text.split(".").last() == statute.fixExpr.className) {
                importPointer = SmartPointerManagerImpl.createPointer(importDirective)
                isSameClassName = true
            }
        }

    }

    override fun getFamilyName(): String {
        return statute.fixTips
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        if (isNeedFixMethod) {
            //其实不改让子类持有statute引用，有修改内容的风险。后续优化（深拷贝一份再传递？）
            methodFix(project, descriptor, statute)
        }
        if (isNeedFixImport) {
            // 引用增加/替换
            val newImportDirective =
                KtPsiFactory(project).createImportDirective(ImportPath.fromString(statute.fixExpr.classPkgName))
            if (isSameClassName) {
                //类名相同 替换引包
                importPointer?.element?.replace(newImportDirective)
            } else {
                //否则增加引用
                importListPointer?.element?.add(newImportDirective)
            }
        }
    }

    open fun methodFix(project: Project, descriptor: ProblemDescriptor, statute: MethodCallStatute) {

    }


    /**
     * 修复项
     */
    fun registerProblem(holder: ProblemsHolder, expression: PsiElement) {
        if (statute.fixDesc.isNotEmpty() && statute.fixExpr.expression.isNotEmpty()){
            holder.registerProblem(
                expression,
                statute.fixDesc,
                statute.highlightType,
                this
            )
        }
        if (statute.documentUrlString.isNotEmpty()) {
            holder.registerProblem(
                expression,
                ActionName,
                statute.highlightType,
                OpenHtmlAction(statute.documentUrlString)
            )
        }

    }
}