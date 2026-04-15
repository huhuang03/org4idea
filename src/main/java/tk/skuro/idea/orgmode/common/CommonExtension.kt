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

val PsiElement.startOffset: Int
    get() = node.startOffset

val PsiElement.endOffset: Int
    get() = startOffset + textLength

fun Document.isEOF(offset: Int): Boolean {
    return this.textLength == offset
}