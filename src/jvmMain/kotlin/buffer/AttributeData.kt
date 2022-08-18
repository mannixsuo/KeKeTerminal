package buffer

import java.awt.Color

interface AttributeData {
    fun getFg(): Color
    fun getBg(): Color
    fun isBold(): Boolean
    fun isItalic(): Boolean
}

interface CellData : AttributeData {
    fun isCombined(): Boolean
    fun getWidth(): Int
    fun getChars(): String
}

interface BufferLine {
    var length: Int
    var isWrapped: Boolean

    fun get(index: Int): CellData
    fun putCell(cell: CellData)


}