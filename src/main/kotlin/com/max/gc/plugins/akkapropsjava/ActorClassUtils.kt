package com.max.gc.plugins.akkapropsjava

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil

fun isValidAkkaActor(editor: Editor, file: PsiJavaFile): Boolean {
    val abstractActorClass = getAbstractActorClass(editor, file) ?: return false
    val topLevelClass = getTopLevelClass(editor, file) ?: return false

    if (!topLevelClass.isInheritor(abstractActorClass, true)) {
        return false
    }

    return getActorConstructor(topLevelClass) != null
}

private fun getAbstractActorClass(editor: Editor, file: PsiJavaFile): PsiClass? {
    val module = ModuleUtil.findModuleForFile(file) ?: return null
    val scope: GlobalSearchScope = GlobalSearchScope.moduleWithLibrariesScope(module)
    return JavaPsiFacade.getInstance(editor.project).findClass("akka.actor.AbstractActor", scope)
}

fun getActorConstructor(actorClass: PsiClass): PsiMethod? {
    return actorClass.constructors.let { if (it.size == 1) it[0] else null }
}

fun getTopLevelClass(editor: Editor, file: PsiJavaFile): PsiClass? {
    val offset = editor.caretModel.offset
    val element = file.findElementAt(offset) ?: return null

    val topLevelClass = PsiUtil.getTopLevelClass(element)
    val psiClass = PsiTreeUtil.getParentOfType(element, PsiClass::class.java) ?: return null
    if (!psiClass.manager.areElementsEquivalent(psiClass, topLevelClass)) {
        return null
    }

    return psiClass
}