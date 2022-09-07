package buffer

import parser.Direction
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

    constructor(cell: ICellData, char: Char) : this(char, cell.getFg(), cell.getBg()) {
        this.bold = cell.isBold()
        this.italic = cell.isItalic()
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

    override fun toString(): String {
        return char.toString()
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
    fun putCell(index: Int, cell: ICellData)
    fun insertChar(startPosition: Int, count: Int, characterToInsert: Char)
    fun shift(position: Direction, count: Int)
    fun eraseLine(start: Int, end: Int)
    fun toLineString(): String?
    fun getCells(): Array<ICellData>?

}

val defaultCellData = CellData(
    ' ', defaultTheme.colors.defaultForeground, defaultTheme.colors.defaultBackground, bold = false, italic = false
)

class BufferLine : IBufferLine {
    private var cells: Array<ICellData> = Array(120) { defaultCellData }

    override var length: Int = 0

    override var isWrapped: Boolean = false

    override fun get(index: Int): ICellData {
        return cells[index]
    }

    override fun putChar(char: Char) {
        TODO("Not yet implemented")
    }

    override fun putCell(index: Int, cell: ICellData) {
        cells[index] = cell
        length++
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        for (cell in cells) {
            buffer.append(cell.getChar())
        }
        return buffer.toString()
    }

    override fun toLineString(): String? {
        return if (length > 0) {
            toString()
        } else {
            null
        }
    }

    override fun insertChar(startPosition: Int, count: Int, characterToInsert: Char) {
        for (index in startPosition until (startPosition + count).coerceAtMost(length)) {
            cells[index] = CellData(cells[index], characterToInsert)
        }
    }

    override fun shift(position: Direction, count: Int) {
        when (position) {
            Direction.LEFT -> {
                for (index in cells.indices) {
                    cells[index] = cells[(index + count).coerceAtMost(cells.size)]
                }
            }

            Direction.RIGHT -> {
                for (index in cells.size downTo 0) {
                    cells[index] = cells[(index - count).coerceAtMost(0)]
                }
            }

            else -> {}
        }
    }

    override fun eraseLine(start: Int, end: Int) {
        for (index in start..end.coerceAtMost(length)) {
            cells[index] = defaultCellData
        }
    }

    override fun getCells(): Array<ICellData>? {
        if (length==0){
            return null
        }
        return cells.copyOfRange(0, length)
    }
}

