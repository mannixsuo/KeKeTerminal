package parser

import org.slf4j.LoggerFactory
import terminal.Line
import terminal.Terminal
import terminal.TerminalInputProcessor

class SingleCharacterFunProcessor(private val terminal: Terminal) {
    private val logger = LoggerFactory.getLogger(SingleCharacterFunProcessor::class.java)
    private val commandExecutorMap = HashMap<Int, SingleCharacterFun>()
    private val bufferService = terminal.bufferService

    init {
        commandExecutorMap[13] = { carriageReturn() }
        commandExecutorMap[10] = { newLine() }
    }

    fun handleCode(code: Int) {
        with(commandExecutorMap) {
            if (containsKey(code)) {
                get(code)!!.invoke()
            } else {
                logger.info("NO C0C1CONTROLFUNCTIONEXECUTOR FOUND FOR $code")
            }
        }
    }

    /**
     * move cursor at start position in next line
     */
    private fun carriageReturn() {
        terminal.cursorY += 1
        terminal.cursorX = 0
        terminal.scrollX = 0
    }

    /**
     * make a new line at current line
     */
    private fun newLine() {
        bufferService.getActiveBuffer()
            .insertLine(terminal.scrollY + terminal.cursorY, Line(terminal.terminalConfig.columns))
    }
}

typealias SingleCharacterFun = () -> Unit