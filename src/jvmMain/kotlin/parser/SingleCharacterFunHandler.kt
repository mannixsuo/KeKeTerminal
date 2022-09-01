package parser

import terminal.TerminalInputHandler

class SingleCharacterFunHandler(private val terminalInputHandler: TerminalInputHandler) {

    private val commandExecutorMap = HashMap<Int, SingleCharacterFun>()

    init {
        commandExecutorMap[13] = { terminalInputHandler.carriageReturn() }
    }
}

typealias SingleCharacterFun = () -> Unit