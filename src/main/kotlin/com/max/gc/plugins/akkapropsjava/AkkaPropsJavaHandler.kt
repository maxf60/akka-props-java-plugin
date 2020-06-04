package com.max.gc.plugins.akkapropsjava

import com.intellij.lang.LanguageCodeInsightActionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*

class AkkaPropsJavaHandler : LanguageCodeInsightActionHandler {

    override fun isValidFor(editor: Editor?, file: PsiFile?): Boolean {
        if (editor?.project == null) {
            return false
        }

        if (file !is PsiJavaFile) {
            return false
        }

        return isValidAkkaActor(editor, file)
    }

    override fun startInWriteAction(): Boolean = true

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        PropsGenerator(project, editor, file).run()
    }
}