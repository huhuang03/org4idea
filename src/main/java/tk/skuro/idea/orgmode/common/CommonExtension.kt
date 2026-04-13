package tk.skuro.idea.orgmode.common

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType

/**
 * @author huhuang03
 */

fun PsiElement.isElementType(type: IElementType): Boolean {
    return this.node.elementType == type
}

fun Document.isEOF(offset: Int): Boolean {
    return this.textLength == offset
}