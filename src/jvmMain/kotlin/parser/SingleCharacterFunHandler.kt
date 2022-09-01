package parser

import terminal.TerminalInputHandler

class SingleCharacterFunHandler {
    private val commandExecutorMap = HashMap<Int, SingleCharacterFun>()
    private lateinit var terminalInputHandler: TerminalInputHandler

    init {
        commandExecutorMap[13] = { terminalInputHandler.carriageReturn() }
    }
}

typealias SingleCharacterFun = () -> Unit