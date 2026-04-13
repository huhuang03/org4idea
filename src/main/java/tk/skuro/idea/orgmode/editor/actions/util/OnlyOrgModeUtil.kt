package tk.skuro.idea.orgmode.editor.actions.util

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import tk.skuro.idea.orgmode.OrgFileType


/**
 * @author huhuang03
 */
fun hideIfNotOrgMode(e: AnActionEvent) {
    val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
    val isOrg = file?.fileType?.name == OrgFileType.INSTANCE.name
    e.presentation.isEnabledAndVisible = isOrg
}