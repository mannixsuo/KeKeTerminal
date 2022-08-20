package parser

import character.TransitionTable
import java.util.Arrays

class Parser() {
    private var initialState = 0
    private var parserAction = 0
    private var params = Params()
    private var collect: Char = Char(0)

    private val transitionTable = TransitionTable(4096)


    init {
        initTransitionTable()
    }

    private fun initTransitionTable() {
        val blueprint = Array(256) { it }
        // printable characters
        val printable = blueprint.sliceArray(IntRange(0x20, 0x7f))
        // executable characters
        var executable = blueprint.sliceArray(IntRange(0x00, 0x1a))
        executable = executable.plus(blueprint.sliceArray(IntRange(0x1c, 0x20)))

        with(transitionTable) {
            // default any error action will turn to ground state
            setDefault(ParserAction.ERROR, ParserState.GROUND)
            // printable character
            addMany(printable, ParserState.GROUND, ParserAction.PRINT, ParserState.GROUND)
            // global anywhere rules
            for (parserState in ParserState.values()) {
                // anywhere -> GROUND
                // 18,1A / execute
                // 80-8F,91-97,99,9A / execute
                addMany(arrayOf(0x18, 0x1a, 0x99, 0x9a), parserState, ParserAction.EXECUTE, ParserState.GROUND)
                addMany(
                    blueprint.sliceArray(IntRange(0x80, 0x90)),
                    parserState,
                    ParserAction.EXECUTE,
                    ParserState.GROUND
                )
                // 9C / (no action)
                add(0x9c, parserState, ParserAction.IGNORE, ParserState.GROUND)
                // anywhere -> sos/pm/apc string
                // 98,9E,9F
                addMany(arrayOf(0x98, 0x9e, 0x9f), parserState, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
                // anywhere -> escape
                // 1B
                add(0x1b, parserState, ParserAction.CLEAR, ParserState.ESCAPE)
                // anywhere -> dcs entry
                // 90
                add(0x90, parserState, ParserAction.CLEAR, ParserState.ESCAPE)
                // anywhere -> osc string
                // 9D
                add(0x9d, parserState, ParserAction.OSC_START, ParserState.OSC_STRING)
                // anywhere -> csi entry
                // 9B
                add(0x9b, parserState, ParserAction.CLEAR, ParserState.CSI_ENTRY)
            }
            // rules for executables and 7f
            addMany(executable, ParserState.GROUND, ParserAction.EXECUTE, ParserState.GROUND)
            addMany(executable, ParserState.ESCAPE, ParserAction.EXECUTE, ParserState.ESCAPE)
            add(0x7f, ParserState.ESCAPE, ParserAction.IGNORE, ParserState.ESCAPE)
            addMany(executable, ParserState.OSC_STRING, ParserAction.IGNORE, ParserState.OSC_STRING)

        }


    }

    fun consumeChar(c: Char) {

    }

}