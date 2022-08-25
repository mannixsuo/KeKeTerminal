package buffer

import java.awt.Color

interface IAttributeData {
    fun getFg(): Color
    fun getBg(): Color
    fun isBold(): Boolean
    fun isItalic(): Boolean
}

/**
 * represent a character
 */
interface ICellData : IAttributeData {
    fun isCombined(): Boolean
    fun getWidth(): Int
    fun getChars(): List<Char>
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

class BufferLine:IBufferLine{
    override var length: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isWrapped: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun get(index: Int): ICellData {
        TODO("Not yet implemented")
    }

    override fun putChar(char: Char) {
        TODO("Not yet implemented")
    }

    override fun putCell(cell: ICellData) {
        TODO("Not yet implemented")
    }
}