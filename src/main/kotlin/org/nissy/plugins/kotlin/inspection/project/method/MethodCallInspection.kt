package org.nissy.plugins.kotlin.inspection.project.method

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.nissy.plugins.kotlin.SingleRecord
import org.jetbrains.kotlin.psi.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MethodCallInspection(expression: KtCallExpression, holder: ProblemsHolder) {
    init {
        val record = SingleRecord.getSingleRecord()?.get()
        record?.methodCall?.statutes?.forEach { statute ->
            statute.errorExprs.forEach { errorExpr ->
                if (checkMethodNameEqs(expression.text, errorExpr.lastCall)) {
                    //cache初始化
                    val cache = initCache(expression)
                    cache?.let {
                        if (!statute.whiteList.contains(it.currentName) && ((it.imports.contains(errorExpr.classPkgName)))) {
                            //添加处理
                            MethodCallInspectionHandlerChain()
                                .addHandler(DirectCallInspection(expression, holder, it, statute, errorExpr))
                                .addHandler(IndirectCallInspection(expression, holder, it, statute, errorExpr))
                                .start()
                        }
                    }
                }
            }
        }
    }

    private fun initCache(expression: KtCallExpression): CacheRecord? {
        var ktFile: KtFile? = null
        var ktClass: KtClass? = null
        var importList: KtImportList? = null

        var temp: PsiElement = expression
        while (temp !is KtFile) {
            temp = temp.parent
            if (temp is KtClass) {
                ktClass = temp
            }
            if (temp is KtFile) {
                ktFile = temp
            }
        }

        var currentName = ""
        ktFile?.let {
            importList = ktFile.importList
            val pkgName = ktFile.firstChild.text
            currentName = if (ktClass != null) {
                pkgName + "." + ktClass.name
            } else {
                pkgName + "." + ktFile.name
            }
        }

        return importList?.let { CacheRecord(it, currentName) }

    }
}

/**
 * 添加调用连
 */
class MethodCallInspectionHandlerChain {
    private val handlers: MutableList<IHandler> = ArrayList()

    fun addHandler(handler: IHandler): MethodCallInspectionHandlerChain {
        handlers.add(handler)
        return this
    }

    fun start() {
        for (handler in handlers) {
            handler.handle()
        }
    }
}

interface IHandler {
    fun handle()

}

/**
 * 判断两个CallExpression的方法名是否相同
 */
fun checkMethodNameEqs(expression1: String, expression2: String): Boolean {
    return expression1.split("(")[0] == expression2.split("(")[0]
}

/**
 * 判断CallExpression的第一项元素是否是类的直接调用
 */
fun checkCallNameEqs(expression1: String, className: String): Boolean {
    return expression1.split(".")[0].split("(")[0] == className
}

class CacheRecord(importList: KtImportList, val currentName: String) {
    var importList: KtImportList
    val imports: HashSet<String> = HashSet()

    init {
        this.importList = importList
        imports.add("")
        for (importDirective: KtImportDirective in importList.imports) {
            imports.add(importDirective.importedFqName.toString())
        }
    }
}
