package tk.skuro.idea.orgmode.util

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import tk.skuro.idea.orgmode.OrgFileType
import tk.skuro.idea.orgmode.psi.OrgPsiElementImpl


/**
 * @author huhuang03
 */
fun hideIfNotOrgMode(e: AnActionEvent) {
    val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
    val isOrg = file?.fileType?.name == OrgFileType.INSTANCE.name
    e.presentation.isEnabledAndVisible = isOrg
}

object OrgModeUtil {
    fun getTopmostParentOfType(element: PsiElement, type: IElementType): PsiElement? {
        val ele = PsiTreeUtil.getTopmostParentOfType(element, OrgPsiElementImpl::class.java) ?: return null
        if (ele.elementType != type) return null
        return ele
    }
}