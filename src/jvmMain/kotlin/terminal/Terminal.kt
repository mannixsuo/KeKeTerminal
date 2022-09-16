package terminal

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalTextInputService
import buffer.BufferLine
import buffer.BufferService
import buffer.CellData
import buffer.IBufferService
import org.slf4j.LoggerFactory
import parser.Parser
import shell.Shell

class Terminal(shell: Shell, private val terminalConfig: TerminalConfig) {
    var repaint: MutableState<Boolean> = mutableStateOf(true)

    private val logger = LoggerFactory.getLogger(Terminal::class.java)
    val bufferService: IBufferService = BufferService(terminalConfig)

    /**
     * current cursor position x
     */
    var currentCursorX = 0

    /**
     * current cursor position y
     */
    var currentCursorY = 0


    private val channelInputStreamReader = shell.getChannelInputStreamReader()
    private val channelOutputStreamWriter = shell.getChannelOutputStreamWriter()
    private val parser: Parser = Parser(this)


    private var nextCharFgColor = terminalConfig.theme.colors.defaultForeground
    private var nextCharBgColor = terminalConfig.theme.colors.defaultBackground
    private var nextCharBold = false
    private var nextCharItalic = false

    private var currentLine: BufferLine = BufferLine()


    fun printChar(charCode: Int) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            val line = getLine(scrollY + y)
            if (line == null) {
                logger.error("line not found ${scrollY + y}")
            } else {
                line.putCell(
                    scrollX + x,
                    CellData(
                        Char(charCode), nextCharFgColor, nextCharBgColor, nextCharBold, nextCharItalic
                    )
                )
                x++
            }
            repaint.value = true
        }
    }

    fun start() {
        startReadFromChannel()
        bufferService.getActiveBuffer().addLine(currentLine)
    }

    fun stop() {
        channelInputStreamReader.close()
        channelOutputStreamWriter.close()
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.type == KeyEventType.KeyDown) {
            var toInt = event.utf16CodePoint
            if (toInt == 10) {
                toInt = 13
            }
            channelOutputStreamWriter.write(toInt)
            channelOutputStreamWriter.flush()
            return true
        }
        return false
    }

    private fun startReadFromChannel() {
        Thread {
            val buf = CharArray(1024)
            var length: Int
            while (channelInputStreamReader.read(buf).also { length = it } != -1) {
                parser.onCharArray(buf.copyOfRange(0, length))
                print(String(buf, 0, length))
            }
        }.start()
    }

}