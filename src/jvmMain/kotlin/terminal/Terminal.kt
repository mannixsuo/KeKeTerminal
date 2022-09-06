package terminal

import buffer.BufferLine
import buffer.BufferService
import buffer.CellData
import buffer.IBufferService
import org.slf4j.LoggerFactory
import parser.Parser
import shell.Shell
import java.io.IOException
import java.io.InputStreamReader

class Terminal(shell: Shell, private val terminalConfig: TerminalConfig) {
    var repaint: Boolean = true

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

        }
    }

    fun start() {
        startReadFromChannel()
        startWriteToChannel()
        bufferService.getActiveBuffer().addLine(currentLine)
    }

    fun stop() {
        channelInputStreamReader.close()
        channelOutputStreamWriter.close()
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

    private fun startWriteToChannel() {
        Thread {
            val inputStreamReader = InputStreamReader(System.`in`)
            val buf = CharArray(1024)
            var length: Int
            try {
                while (inputStreamReader.read(buf).also { length = it } != -1) {
                    val s = String(buf, 0, length)
                    if ("\n" == s) {
                        channelOutputStreamWriter.write(13)
                    } else {
                        channelOutputStreamWriter.write(buf, 0, length)
                    }
                    channelOutputStreamWriter.flush()
                }
            } catch (ignore: IOException) {
            }
        }.start()
    }
}