package parser

import terminal.TerminalInputHandler

data class CsiCommand(val finalChar: Char, val prefix: Char?, val intermediate: Char?)

class CsiHandler {

    private val commandExecutorMap = HashMap<CsiCommand, CsiHandlerFun>()

    private lateinit var terminalInputHandler: TerminalInputHandler

    init {
        with(commandExecutorMap) {
            put(CsiCommand('@', null, null)) { params -> terminalInputHandler.insertChars(params) }
            put(CsiCommand('@', null, ' ')) { params -> terminalInputHandler.shiftLeft(params) }
            put(CsiCommand('A', null, null)) { params -> terminalInputHandler.cursorUp(params) }
            put(CsiCommand('A', null, ' ')) { params -> terminalInputHandler.cursorRight(params) }
            put(CsiCommand('B', null, null)) { params -> terminalInputHandler.cursorDown(params) }
            put(CsiCommand('C', null, null)) { params -> terminalInputHandler.cursorForward(params) }
            put(CsiCommand('D', null, null)) { params -> terminalInputHandler.cursorBackward(params) }
            put(CsiCommand('E', null, null)) { params -> terminalInputHandler.cursorNextLine(params) }
            put(CsiCommand('F', null, null)) { params -> terminalInputHandler.cursorPrecedingLine(params) }
            put(CsiCommand('G', null, null)) { params -> terminalInputHandler.cursorCharacterAbsolute(params) }
            put(CsiCommand('H', null, null)) { params -> terminalInputHandler.cursorPosition(params) }
            put(CsiCommand('I', null, null)) { params -> terminalInputHandler.cursorForwardTabulation(params) }
            put(CsiCommand('J', null, null)) { params -> terminalInputHandler.eraseInDisplay(params) }
            put(CsiCommand('J', '?', null)) { params -> terminalInputHandler.eraseInDisplaySelective(params) }
            put(CsiCommand('K', null, null)) { params -> terminalInputHandler.eraseInLine(params) }
            put(CsiCommand('K', '?', null)) { params -> terminalInputHandler.eraseInLineSelective(params) }
            put(CsiCommand('L', null, null)) { params -> terminalInputHandler.insertLines(params) }
            put(CsiCommand('M', null, null)) { params -> terminalInputHandler.deleteLines(params) }
            put(CsiCommand('P', null, null)) { params -> terminalInputHandler.deleteCharacters(params) }
        }
    }
}

typealias CsiHandlerFun = (params: Array<Int>) -> Unit