package terminal

import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * container of lines reads from a terminal
 */
interface ILineBuffer {

    val lock: Lock

    /**
     * get last line
     *
     * return null if it's empty
     */
    fun getLastLine(): ILine?

    /**
     * get line at index of the buffer
     */
    fun getLine(index: Int): ILine?

    /**
     * get lines that index in range
     */
    fun getLines(range: IntRange): List<ILine>

    /**
     * append line to the buffer
     */
    fun appendLine(line: ILine)

    /**
     * insert line at index
     */
    fun insertLine(index: Int, line: ILine)

    /**
     * delete line at index
     *
     * line behind the index will move up
     */
    fun deleteLine(index: Int)


    fun lineCount(): Int

}


class LineBuffer : ILineBuffer {

    override val lock: Lock = ReentrantLock()

    private val _buffer = LinkedList<ILine>()

    override fun getLastLine(): ILine? {
        return if (_buffer.size == 0) {
            null
        } else {
            _buffer.last
        }
    }

    override fun getLine(index: Int): ILine? {
        return if (index !in 0.._buffer.size) {
            null
        } else {
            _buffer[index]
        }
    }

    override fun getLines(range: IntRange): List<ILine> {
        val start = 0.coerceAtLeast(range.first)
        val end = _buffer.size.coerceAtMost(range.last)
        return _buffer.slice(IntRange(start, end))
    }

    override fun appendLine(line: ILine) {
        _buffer.add(line)
    }

    override fun insertLine(index: Int, line: ILine) {
        _buffer.add(index, line)
    }

    override fun deleteLine(index: Int) {
        if (index in 0 until _buffer.size) {
            _buffer.removeAt(index)
        }
    }

    override fun lineCount(): Int {
        return _buffer.size
    }
}

