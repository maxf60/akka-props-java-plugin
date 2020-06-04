package com.max.gc.plugins.akkapropsjava

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.BaseCodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class AkkaPropsJavaAction : BaseCodeInsightAction() {
    private val handler = AkkaPropsJavaHandler()

    override fun getHandler(): CodeInsightActionHandler = handler

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return handler.isValidFor(editor, file)
    }
}