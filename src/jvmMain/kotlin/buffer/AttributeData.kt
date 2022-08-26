package buffer

import java.awt.Color

interface IAttributeData {
    fun getFg(): Color
    fun getBg(): Color
    fun isBold(): Boolean
    fun isItalic(): Boolean
}

/**
 * represent character
 */
interface ICellData : IAttributeData {
    fun getWidth(): Int
    fun getChar(): Char
}

class CellData(
    private val char: Char,
    private val fg: Color,
    private val bg: Color,
) : ICellData {

    constructor(
        char: Char, fg: Color, bg: Color, bold: Boolean, italic: Boolean
    ) : this(char, fg, bg) {
        this.bold = bold
        this.italic = italic
    }

    private var bold: Boolean = false
    private var italic: Boolean = false

    override fun getFg(): Color {
        return fg
    }

    override fun getBg(): Color {
        return bg
    }

    override fun isBold(): Boolean {
        return bold
    }

    override fun isItalic(): Boolean {
        return italic
    }

    override fun getWidth(): Int {
        return 1
    }

    override fun getChar(): Char {
        return char
    }
}


/**
 * represent a line in terminal
 */
interface IBufferLine {

    var length: Int
    var isWrapped: Boolean

    fun get(index: Int): ICellData
    fun putChar(char: Char)
    fun putCell(cell: ICellData)

}

class BufferLine : IBufferLine {
    private var cells: ArrayList<ICellData> = ArrayList()

    override var length: Int = 0

    override var isWrapped: Boolean = false

    override fun get(index: Int): ICellData {
        return cells[index]
    }

    override fun putChar(char: Char) {
        TODO("Not yet implemented")
    }

    override fun putCell(cell: ICellData) {
        cells.add(cell)
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        for (cell in cells) {
            buffer.append(cell.getChar())
        }
        return buffer.toString()
    }


}

