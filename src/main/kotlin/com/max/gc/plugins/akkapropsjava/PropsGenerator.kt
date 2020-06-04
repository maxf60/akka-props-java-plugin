package com.max.gc.plugins.akkapropsjava

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiUtil

class PropsGenerator constructor(
    private val project: Project,
    private val editor: Editor,
    private val file: PsiFile
) : Runnable {

    private val psiElementFactory = JavaPsiFacade.getElementFactory(project)

    override fun run() {
        val actorClass = getTopLevelClass(editor, file as PsiJavaFile) ?: return
        val actorConstructor = getActorConstructor(actorClass) ?: return

        val propsType = ModuleUtil.findModuleForFile(file)?.let {
            PsiType.getTypeByName("akka.actor.Props", project, GlobalSearchScope.moduleWithLibrariesScope(it))
        } ?: return

        val propsMethod = generatePropsMethod(actorClass, actorConstructor, propsType)
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(file)
        CodeStyleManager.getInstance(project).reformat(propsMethod)

        val existingPropsMethod = findExistingPropsMethod(actorClass)
        if (existingPropsMethod != null) {
            existingPropsMethod.replace(propsMethod)
        } else {
            actorClass.addAfter(propsMethod, actorConstructor)
        }
    }

    private fun generatePropsMethod(
        actorClass: PsiClass,
        actorConstructor: PsiMethod,
        propsType: PsiClassType
    ): PsiMethod {

        val propsMethod = psiElementFactory.createMethod(propsMethodName, propsType)
        PsiUtil.setModifierProperty(propsMethod, PsiModifier.PUBLIC, true)
        PsiUtil.setModifierProperty(propsMethod, PsiModifier.STATIC, true)

        val constructorParams = actorConstructor.parameterList.parameters
        constructorParams.forEach(propsMethod.parameterList::add)

        val returnStatement = psiElementFactory.createStatementFromText(
            makeReturnStatementText(constructorParams, actorClass), propsMethod
        )
        propsMethod.body?.add(returnStatement)

        return propsMethod
    }

    private fun makeReturnStatementText(constructorParams: Array<PsiParameter>, actorClass: PsiClass): String {
        if (constructorParams.isEmpty()) {
            return "return Props.create(${actorClass.name}.class);"
        }
        val paramNames = constructorParams.map { it.name }.joinToString(", ")
        return "return Props.create(${actorClass.name}.class, ${paramNames});"
    }

    private fun findExistingPropsMethod(actorClass: PsiClass): PsiMethod? {
        return actorClass.findMethodsByName(propsMethodName, false)
            .let { if (it.size == 1) it[0] else null }
    }

    private companion object {
        const val propsMethodName = "props"
    }
}