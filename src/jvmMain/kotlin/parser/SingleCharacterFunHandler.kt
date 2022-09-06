package parser

import org.slf4j.LoggerFactory
import terminal.TerminalInputHandler

class SingleCharacterFunHandler(private val terminalInputHandler: TerminalInputHandler) {
    private val logger = LoggerFactory.getLogger(SingleCharacterFunHandler::class.java)
    private val commandExecutorMap = HashMap<Int, SingleCharacterFun>()

    init {
        commandExecutorMap[13] = { terminalInputHandler.carriageReturn() }
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