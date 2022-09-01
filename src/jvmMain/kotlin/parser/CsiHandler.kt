package parser

import terminal.TerminalInputHandler
import java.util.Stack

data class CsiCommand(val finalChar: Char, val prefix: Char?, val intermediate: Char?)

class CsiHandler(private val terminalInputHandler: TerminalInputHandler) {

    // TODO
    // | finalChar | intermediate | prefix
    // CSI Ps * x ------- | x | * | 0 |
    // CSI Ps SP t ------ | t | SP| 0 |
    // CSI ? Ps $ p ----- | p | $ | ? |
    fun csiDispatch(collect: Stack<Char>, params: Params, finalCharCode: Int) {
        var prefix: Char? = null
        var intermediate: Char? = null

        if (collect.size == 2) {
            intermediate = collect.pop()
            prefix = collect.pop()
        } else {

        }
        for (command in commandExecutorMap) {
            val key = command.key
            if (key.finalChar == finalCharCode.toChar()) {
                command.value.invoke(params.toIntArray())
            }
        }
    }

    private val commandExecutorMap = HashMap<CsiCommand, CsiHandlerFun>()

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