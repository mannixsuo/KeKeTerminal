package buffer

import parser.Direction
import terminal.TerminalConfig
import java.util.*
import java.util.concurrent.locks.ReentrantLock

interface IBuffer {

    var screenColumns: Int

    var screenRows: Int

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
    fun getLine(index: Int): IBufferLine?

    /**
     * push one line into buffer
     */
    fun addLine(line: BufferLine)

    /**
     * insert line at index, elements before index is move backward , if buffer is full first element is removed for space
     */
    fun insertLine(index: Int, line: IBufferLine)

    fun moveCursor(direction: Direction, count: Int)

    fun getLine(from: Int, to: Int): List<IBufferLine>

    fun setLine(index: Int, line: IBufferLine)

}


class Buffer : IBuffer {

    private val buffer = Collections.synchronizedList(LinkedList<IBufferLine>())

    private val lock = ReentrantLock()

    init {
        for (index in 0..30) {
            buffer.add(BufferLine())
        }
    }

    override var screenColumns = 120

    override var screenRows = 30

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

    override fun getLine(index: Int): IBufferLine? {
        try {
            lock.lock()
            return if (buffer.size > index) {
                buffer[index]
            } else {
                null
            }
        } finally {
            lock.unlock()
        }

    }

    override fun addLine(line: BufferLine) {
        try {
            lock.lock()
            buffer.add(line)
        } finally {
            lock.unlock()
        }
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

            Direction.ABSOLUTE -> {
                x = count
            }
        }
    }

    override fun getLine(from: Int, to: Int): List<IBufferLine> {
        try {
            lock.lock()
            return buffer.slice(IntRange(from, to))
        } finally {
            lock.unlock()
        }
    }

    override fun setLine(index: Int, line: IBufferLine) {
        try {
            lock.lock()
            buffer[index] = line
        } finally {
            lock.unlock()
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