package buffer

import parser.Direction
import terminal.TerminalConfig
import java.lang.RuntimeException

interface IBuffer {
    /**
     * scroll y position
     */
    var scrollY: Int

    /**
     * cursor y position
     */
    var y: Int

    /**
     * scroll x position
     */
    var scrollX: Int

    /**
     * cursor x position
     */
    var x: Int

    /**
     * get line at index,
     * index is absolute position in buffer. we don't have to include scroll position
     */
    fun getLine(index: Int): IBufferLine

    /**
     * push one line into buffer
     */
    fun addLine(line: BufferLine)

    /**
     * insert line at index, elements before index is move backward , if buffer is full first element is removed for space
     */
    fun insertLine(index: Int, line: IBufferLine)
    fun moveCursor(direction: Direction, count: Int)

}


class Buffer : IBuffer {

    private val buffer = CircularList<IBufferLine>(1024)

    /**
     * scroll y position
     */
    override var scrollY: Int = 0

    /**
     * y position
     */
    override var y: Int = 0

    /**
     * scroll x position
     */
    override var scrollX: Int = 0

    /**
     * x position
     */
    override var x: Int = 0

    override fun getLine(index: Int): IBufferLine {
        return buffer.get(index) ?: throw RuntimeException("can't get line at $index, total size is ${buffer.length}")
    }

    override fun addLine(line: BufferLine) {
        buffer.push(line)
    }

    override fun insertLine(index: Int, line: IBufferLine) {
        TODO("Not yet implemented")
    }

    override fun moveCursor(direction: Direction, count: Int) {
        when (direction) {
            Direction.UP -> {
                y -= count
            }

            Direction.LEFT -> {
                x -= count
            }

            Direction.DOWN -> {
                y += count
            }

            Direction.RIGHT -> {
                x += count
            }
        }
    }
}


/**
 * save all line in terminal
 */
interface IBufferService {

    fun getActiveBuffer(): IBuffer

    fun switchToAltBuffer()

    fun switchToDefaultBuffer()

}

class BufferService(terminalConfig: TerminalConfig) : IBufferService {

    private val buffer = Buffer()
    private val alternativeBuffer = Buffer()
    private var activeBuffer = buffer

    override fun getActiveBuffer(): IBuffer {
        return activeBuffer
    }

    override fun toString(): String {
        return buffer.toString()
    }

    override fun switchToAltBuffer() {
        this.activeBuffer = alternativeBuffer
    }

    override fun switchToDefaultBuffer() {
        this.activeBuffer = buffer
    }
}