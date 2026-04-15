package tk.skuro.idea.orgmode.table

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import tk.skuro.idea.orgmode.common.endOffset
import tk.skuro.idea.orgmode.common.isEOF
import tk.skuro.idea.orgmode.common.startOffset
import tk.skuro.idea.orgmode.logger
import tk.skuro.idea.orgmode.parser.OrgTokenTypes
import tk.skuro.idea.orgmode.util.OrgModeUtil
import tk.skuro.idea.orgmode.util.hideIfNotOrgMode


open class TabInTable : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        try {
            val file = e.getData(LangDataKeys.PSI_FILE)
            val editor = e.getData(LangDataKeys.EDITOR)
            if (file == null || editor == null) {
                logger.warn("file: ${file}, editor; $editor")
                return
            }
            val document = editor.document
            var offset = editor.caretModel.currentCaret.offset
            if (document.isEOF(offset)) {
                offset -= 1
            }
            if (offset < 0) {
                return
            }
            val element = file.findElementAt(offset)
            if (element == null) {
                logger.warn("element is null")
                return
            }
            val tablePsi = OrgModeUtil.getTopmostParentOfType(element, OrgTokenTypes.TABLE)
            if (tablePsi == null) {
                logger.warn("tablePsi is null element: $element")
                return
            }
            val table = Table.Parser.fromTablePsi(editor, tablePsi)
            val newPos = table.doTabAtOffset(offset - tablePsi.startOffset)
            val newText = table.calcText()
            if (newText != tablePsi.text) {
                WriteCommandAction.runWriteCommandAction(tablePsi.project) {
                    document.replaceString(tablePsi.startOffset, tablePsi.endOffset, newText)
                }
            }
            val newOffset = table.calcOffset(newPos) ?: return
            editor.caretModel.moveToOffset(newOffset)
        } catch (t: Throwable) {
            logger.error(t)
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        hideIfNotOrgMode(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}