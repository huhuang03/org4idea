package tk.skuro.idea.orgmode.table

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.VisibleForTesting
import tk.skuro.idea.orgmode.common.endOffset
import tk.skuro.idea.orgmode.common.isElementType
import tk.skuro.idea.orgmode.common.startOffset
import tk.skuro.idea.orgmode.parser.OrgTokenTypes

data class Pos(val row: Int, val col: Int)

/**
 * @author huhuang03
 */
class Table {
    val hasHeader: Boolean
    val cells: MutableList<Cell>

    constructor(cells: List<Cell>, hasHeader: Boolean) {
        this.cells = cells.toMutableList()
        this.hasHeader = hasHeader
    }

    fun calcText(): String {
        val lines = mutableListOf<String>()
        lines.add(calcTextForRow(0))
        if (this.hasHeader) {
            lines.add(calcTextForDivider())
        }
        for (i in 1 until calcRow()) {
            lines.add(calcTextForRow(i))
        }
        return lines.joinToString("\n")
    }

    /**
     * not contains the end of line
     */
    private fun calcTextForRow(row: Int): String {
        val cells = getCellsForRow(row)
        val content = cells.joinToString(" | ") { it.text }
        return "| $content |"
    }

    private fun calcTextForDivider(): String {
        val firstRow = getCellsForRow(0)
        return firstRow.map { it.text.count() }.joinToString(" | ") { "*".repeat(it) }
    }

    private fun getCellsForRow(row: Int): List<Cell> {
        return this.cells.filter { it.row == row }
    }

    private fun getCellsForCol(col: Int): List<Cell> {
        return this.cells.filter { it.col == col }
    }

    fun calcRow(): Int {
        val count = this.cells.maxOfOrNull { it.row }
        if (count == null) {
            return 0
        }
        return count + 1
    }

    fun calcCol(): Int {
        val count = this.cells.maxOfOrNull { it.col }
        if (count == null) {
            return 0
        }
        return count + 1
    }

    fun calcOffset(pos: Pos): Int? {
        return cells.find { it.row == pos.row && it.col == pos.col }?.offset
    }

    /**
     * @param offset relative to table
     */
    fun doTabAtOffset(offset: Int): Pos {
        if (this.empty) {
            return Pos(0, 0)
        }

        val cell = getCellByOffset(offset) ?: return Pos(0, 0)
        val last = this.cells.last()
        val inLast = cell.pos == last.pos
        if (inLast) {
            doExpandNewLine()
        }

        doExpand()

        if (cell.col + 1 == calcCol()) {
            return Pos(cell.row + 1, 0)
        }
        return Pos(cell.row, cell.col + 1)
    }

    private fun doExpand() {
        for (r in (0 until calcRow())) {
            for (c in (0 until calcCol())) {
                var cell = cells.find { it.row == r && it.col == c }
                if (cell == null) {
                    cell = Cell(r, c, -1, "")
                    this.cells.add(cell)
                }
            }
        }
        this.cells.sortBy { it.row * calcCol() + it.col }
        val newCells = this.cells.map {
            val text = it.text.trim()
            val cellWidth = calcWidthFor(it.col)
            val tmp = if (text.length < cellWidth) {
                it.copy(
                    text = text + " ".repeat(cellWidth - text.length),
                )
            } else {
                it.copy(text = text)
            }
            tmp.copy(offset = calcOffsetInWellTable(Pos(it.row, it.col)))
        }
        this.cells.clear()
        this.cells.addAll(newCells)
    }

    private fun doExpandNewLine() {
        val row = calcRow()
        val newLineCells = (0.until(calcCol())).map {
            Cell(row, it, -1, "")
        }
        this.cells.addAll(newLineCells)
    }

    val empty: Boolean
        get() = cells.isEmpty()

    object Parser {
        fun fromTablePsi(editor: Editor, tablePsi: PsiElement): Table {
            val document = editor.document
            val hasHeader = tablePsi.firstChild.isElementType(OrgTokenTypes.TABLE_HEADER)
            val cells = mutableListOf<Cell>()
            // can I get line number of offset?
            val beginLine = document.getLineNumber(tablePsi.startOffset)
            val endLine = document.getLineNumber(tablePsi.endOffset)
            for (line in beginLine..endLine) {
                val row = line - beginLine
                if (row == 1 && hasHeader) {
                    continue
                }

                // parse a line
                val startPos = document.getLineStartOffset(line)
                val parsedLine = parseLine(document.getText(TextRange(startPos, document.getLineEndOffset(line))))
                cells.addAll(parsedLine.mapIndexed { index, item ->
                    Cell(
                        line,
                        index,
                        item.offset + startPos,
                        item.text
                    )
                })
            }
            tablePsi.startOffset
            return Table(cells, hasHeader)
        }
    }

    private fun calcOffsetInWellTable(pos: Pos): Int {
        val width = calcWidth()
        val addHeader = hasHeader && pos.row > 0
        // + 1 means the line end
        val preRowsOffset = (pos.row + (if (addHeader) 1 else 0)) * (width + 1)
        val colOffset = if (pos.col == 0) {
            2
        } else {
            // + 2 is the lead/tail space, + 1 is the lead divider '|'
            0.until(pos.col).sumOf { calcWidthFor(it) + 2 + 1 } + 2
        }
        return preRowsOffset + colOffset
    }

    private fun calcWidth(): Int {
        // calcCol + 1 is the '|' divider
        return (0.until(calcCol())).sumOf { calcWidthFor(it) + 2 } + (calcCol() + 1)
    }

    /**
     * do not contain the "|" divider, and the leading/tail space
     */
    fun calcWidthFor(col: Int): Int {
        val cells = getCellsForCol(col)
        return cells.maxOfOrNull { it.text.trim().length } ?: 0
    }

    fun getCellByOffset(offset: Int): Cell? {
        var rst = this.cells.firstOrNull()
        for (cell in this.cells) {
            if (cell.offset > offset) {
                return rst
            } else {
                rst = cell
            }
        }
        return rst
    }
}

data class ParseLineCell(val text: String, val offset: Int)


@VisibleForTesting
fun parseLine(text: String): List<ParseLineCell> {
    val rst = mutableListOf<ParseLineCell>()
    var itemBeginIndex = -1
    val itemTextBuilder = StringBuilder()
    var itemHasBegin = false

    fun itemBegin(index: Int) {
        itemHasBegin = true
        itemBeginIndex = index
        itemTextBuilder.clear()
    }

    fun itemEnd() {
        rst.add(ParseLineCell(itemTextBuilder.toString(), itemBeginIndex))
        itemHasBegin = false
    }

    val trimmedText = text.trimEnd { it == '\n' || it == '\r' }
    fun safeIndex(src: Int): Int {
        if (src >= trimmedText.length) {
            return -1
        }
        return src
    }

    val lastDividerIndex = trimmedText.lastIndexOf('|')

    for ((index, ch) in trimmedText.withIndex()) {
        if (ch == '|') {
            if (!itemHasBegin) {
                itemBegin(safeIndex(index + 1))
                continue
            } else {
                itemEnd()
                if (index != lastDividerIndex) {
                    itemBegin(safeIndex(index + 1))
                }
            }
        } else {
            if (itemHasBegin) {
                itemTextBuilder.append(ch)
            }
        }
    }

    if (itemHasBegin) {
        itemEnd()
    }
    return rst
}

/**
 * @param offset, the offset in table, not contains the start |
 */
data class Cell(val row: Int, val col: Int, val offset: Int, val text: String) {
    val pos: Pos
        get() = Pos(col, row)
}