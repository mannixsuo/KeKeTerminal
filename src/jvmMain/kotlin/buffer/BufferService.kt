package buffer

import terminal.TerminalConfig

/**
 * save all line in terminal
 */
interface IBufferService {

    fun getColumnSize(): Int
    fun getTotalRow(): Int
    fun getRowSize(): Int
    fun getLine(index: Int): IBufferLine
    fun addLine(line: IBufferLine)
    fun insertLine(index: Int, line: IBufferLine)
    fun deleteLine(index: Int)

}

class BufferService(terminalConfig: TerminalConfig) : IBufferService {

    private val buffer = CircularList<IBufferLine>(10)

    private var totalRow = 0
    private var columnSize = 0
    private var rowSize = 0


    override fun getColumnSize(): Int {
        return columnSize
    }

    override fun getTotalRow(): Int {
        return totalRow
    }

    override fun getRowSize(): Int {
        return rowSize
    }

    override fun getLine(index: Int): IBufferLine {
        return buffer.get(index) ?: BufferLine()
    }

    override fun addLine(line: IBufferLine) {
        buffer.push(line)
    }

    override fun insertLine(index: Int, line: IBufferLine) {
        TODO("Not yet implemented")
    }

    override fun deleteLine(index: Int) {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return buffer.toString()
    }


}