package parser

import org.slf4j.LoggerFactory
import terminal.TerminalInputHandler
import java.util.*

// Type  Size(bits)
// Byte    8
// Short   16
// Int     32
// Long    64
//
// | finalChar | intermediate | prefix
// 8bit + 8bit + 8bit
// CSI Ps * x ------- | x | * | 0 |
// CSI Ps SP t ------ | t | SP| 0 |
// CSI ? Ps $ p ----- | p | $ | ? |
const val prefixShift = 8

data class CsiCommand(val finalChar: Char, val prefix: Char?, val intermediate: Char?) {

    fun key(): Int {
        return generateKey(finalChar, prefix, intermediate)
    }
}

fun generateKey(finalChar: Char, prefix: Char?, intermediate: Char?): Int {
    return (if (prefix == null) 0 else prefix.code shl 14) or (if (intermediate == null) 0 else intermediate.code shl 8) or (finalChar.code)
}


fun generateKey(finalCharCode: Int, prefix: Char?, intermediate: Char?): Int {
    return (if (prefix == null) 0 else prefix.code shl 14) or (if (intermediate == null) 0 else intermediate.code shl 8) or (finalCharCode)
}


class CsiHandler(private val terminalInputHandler: TerminalInputHandler) {
    private val logger = LoggerFactory.getLogger(CsiHandler::class.java)

    fun csiDispatch(collect: Stack<Char>, params: Params, finalCharCode: Int) {

        var prefix: Char? = null
        var intermediate: Char? = null

        if (collect.size == 2) {
            intermediate = collect.pop()
            prefix = collect.pop()
        }
        if (collect.size == 1) {
            prefix = collect.pop()
        }

        val key = generateKey(finalCharCode, prefix, intermediate)
        if (commandExecutorMap.containsKey(key)) {
            commandExecutorMap[key]?.invoke(params.toIntArray())
        } else {
            logger.warn("NO CSI COMMAND HANDLER FOUND FOR $prefix $intermediate ${finalCharCode.toChar()}")
        }

    }

    private val commandExecutorMap = TreeMap<Int, CsiHandlerFun>()

    init {
        with(commandExecutorMap) {
            put(CsiCommand('@', null, null).key()) { params -> terminalInputHandler.insertChars(params) }
            put(CsiCommand('@', null, ' ').key()) { params -> terminalInputHandler.shiftLeft(params) }
            put(CsiCommand('A', null, null).key()) { params -> terminalInputHandler.cursorUp(params) }
            put(CsiCommand('A', null, ' ').key()) { params -> terminalInputHandler.cursorRight(params) }
            put(CsiCommand('B', null, null).key()) { params -> terminalInputHandler.cursorDown(params) }
            put(CsiCommand('C', null, null).key()) { params -> terminalInputHandler.cursorForward(params) }
            put(CsiCommand('D', null, null).key()) { params -> terminalInputHandler.cursorBackward(params) }
            put(CsiCommand('E', null, null).key()) { params -> terminalInputHandler.cursorNextLine(params) }
            put(CsiCommand('F', null, null).key()) { params -> terminalInputHandler.cursorPrecedingLine(params) }
            put(CsiCommand('G', null, null).key()) { params -> terminalInputHandler.cursorCharacterAbsolute(params) }
            put(CsiCommand('H', null, null).key()) { params -> terminalInputHandler.cursorPosition(params) }
            put(CsiCommand('I', null, null).key()) { params -> terminalInputHandler.cursorForwardTabulation(params) }
            put(CsiCommand('J', null, null).key()) { params -> terminalInputHandler.eraseInDisplay(params) }
            put(CsiCommand('J', '?', null).key()) { params -> terminalInputHandler.eraseInDisplaySelective(params) }
            put(CsiCommand('K', null, null).key()) { params -> terminalInputHandler.eraseInLine(params) }
            put(CsiCommand('K', '?', null).key()) { params -> terminalInputHandler.eraseInLineSelective(params) }
            put(CsiCommand('L', null, null).key()) { params -> terminalInputHandler.insertLines(params) }
            put(CsiCommand('M', null, null).key()) { params -> terminalInputHandler.deleteLines(params) }
            put(CsiCommand('P', null, null).key()) { params -> terminalInputHandler.deleteCharacters(params) }
        }
    }
}

typealias CsiHandlerFun = (params: Array<Int>) -> Unit
