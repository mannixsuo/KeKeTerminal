package terminal

/**
 * A line represent one line in window
 * it contains some characters
 *
 * may be empty
 */
interface ILine {
    /**
     * get cell at specific index of the line
     * may be null if index is out bound
     */
    fun getCell(index: Int): ICell?

    /**
     * get all cells in this line
     */
    fun getCells(): Array<ICell?>

    /**
     * append cell at last
     */
    fun appendCell(cell: ICell)

    /**
     * insert cell at specific index
     *
     * cells behind index will move back
     */
    fun insertCell(index: Int, cell: ICell)

    /**
     * insert cells at specific index
     *
     * cells behind index will move back
     */
    fun insertCells(index: Int, cells: Array<ICell>)

    /**
     * replace a cell
     */
    fun replaceCell(index: Int, replacement: ICell)

    /**
     * replace cell by range
     */
    fun replaceCells(range: IntRange, replacement: ICell)

    /**
     * delete cell at index
     *
     * cells behind the index will move forward on cell
     */
    fun deleteCell(index: Int): ICell?

    /**
     * delete cells by range
     *
     * cells behind the range end will move forward
     */
    fun deleteCells(range: IntRange)

    /**
     * max length of the line
     */
    fun maxLength(): Int

    /**
     * length of the line
     */
    fun length(): Int
}

class Line(private val maxLength: Int) : ILine {

    private var _length = 0

    private val _cells = Array<ICell?>(maxLength) { null }

    override fun getCell(index: Int): ICell? {
        return if (index < 0 || index > _length) {
            null
        } else {
            _cells[index]
        }
    }

    override fun getCells(): Array<ICell?> {
        return _cells
    }

    override fun insertCell(index: Int, cell: ICell) {
        if (index in 0.._length) {
            for (i in _length downTo index + 1) {
                _cells[i] = _cells[i - 1]
            }
            _cells[index] = cell
        }
    }

    override fun insertCells(index: Int, cells: Array<ICell>) {
        if (index in 0.._length) {
            for (i in _length downTo index + cells.size) {
                _cells[i] = cells[i - 1]
            }
            for (i in index until cells.size) {
                _cells[i] = cells[i]
            }
        }
    }

    override fun appendCell(cell: ICell) {
        _cells[_length++] = cell
    }

    override fun replaceCell(index: Int, replacement: ICell) {
        if (index in 0.._length) {
            _cells[index] = replacement
        }
    }

    override fun replaceCells(range: IntRange, replacement: ICell) {
        val start = 0.coerceAtLeast(range.first)
        val end = _length.coerceAtMost(range.last)
        for (index in start..end) {
            _cells[index] = replacement
        }
    }

    override fun deleteCell(index: Int): ICell? {
        if (index !in 0.._length) {
            return null
        }
        val deleted = _cells[index]
        for (i in index until _length) {
            _cells[i] = _cells[i + 1]
        }
        return deleted
    }

    override fun deleteCells(range: IntRange) {
        val start = 0.coerceAtLeast(range.first)
        val end = _length.coerceAtMost(range.last)
        for (i in start..end) {
            _cells[i] = _cells[end + 1]
        }
    }

    override fun maxLength(): Int {
        return maxLength
    }

    override fun length(): Int {
        return _length
    }
}