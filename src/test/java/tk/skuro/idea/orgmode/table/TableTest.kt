package tk.skuro.idea.orgmode.table

import org.junit.Assert
import org.junit.Test

/**
 * @author huhuang03
 */
class TableTest {
    @Test
    fun testParseLine() {
        var rst = parseLine("|")
        Assert.assertEquals(1, rst.size)
        Assert.assertEquals(ParseLineCell("", -1), rst[0])

        rst = parseLine("||")
        Assert.assertEquals(1, rst.size)
        Assert.assertEquals(ParseLineCell("", 1), rst[0])
    }
}