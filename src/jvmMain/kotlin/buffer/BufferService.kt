package buffer

interface BufferService {

    fun getColumns(): Int

    fun getRows(): Int

    fun scroll()

    fun scrollToBottom()

    fun scrollToTop()

    fun scrollToLine(line: Int)

    fun resize(columns: Int, rows: Int)
}