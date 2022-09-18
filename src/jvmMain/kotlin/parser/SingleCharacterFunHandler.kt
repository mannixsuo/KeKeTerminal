package parser

import org.slf4j.LoggerFactory
import terminal.TerminalInputProcessor

class SingleCharacterFunHandler(private val terminalInputProcessor: TerminalInputProcessor) {
    private val logger = LoggerFactory.getLogger(SingleCharacterFunHandler::class.java)
    private val commandExecutorMap = HashMap<Int, SingleCharacterFun>()

    init {
        commandExecutorMap[13] = { terminalInputProcessor.carriageReturn() }
        commandExecutorMap[10] = { terminalInputProcessor.newLine() }
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
}

typealias SingleCharacterFun = () -> Unit