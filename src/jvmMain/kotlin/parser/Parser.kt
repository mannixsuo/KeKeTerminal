package parser

import character.TransitionTable
import terminal.Terminal

class Parser {

    var currentState = ParserState.GROUND
    var currentAction = ParserAction.PRINT
    private var params = Params()
    private var collect: Char = Char(0)
    private val transitionTable = TransitionTable(4096)
    private val terminal = Terminal()
    private val c0c1ControlFunctionExecutors: Map<Int, Executor> = HashMap()
    private val oscHandler = OSCHandler()
    private var dcsHandler = DCSHandler()

    init {
        initTransitionTable()
    }

    private fun initTransitionTable() {
        val blueprint = Array(256) { it }
        // executable characters
        var executable = blueprint.sliceArray(IntRange(0x00, 0x1a - 1))
        executable = executable.plus(blueprint.sliceArray(IntRange(0x1c, 0x20 - 1)))
        with(transitionTable) {
            // default any error action will turn to ground state
            setDefault(ParserAction.ERROR, ParserState.GROUND)
            // printable character
            addRange(0x20, 0x7f, ParserState.GROUND, ParserAction.PRINT, ParserState.GROUND)
            // global anywhere rules
            for (parserState in ParserState.values()) {
                // anywhere -> GROUND
                // 18,1A / execute
                // 80-8F,91-97,99,9A / execute
                addMany(arrayOf(0x18, 0x1a, 0x99, 0x9a), parserState, ParserAction.EXECUTE, ParserState.GROUND)
                addRange(0x80, 0x8f, parserState, ParserAction.EXECUTE, ParserState.GROUND)
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
            // csi
            add(0x5b, ParserState.ESCAPE, ParserAction.CLEAR, ParserState.CSI_ENTRY)
            add(0x3b, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x30, 0x39, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x3c, 0x3f, ParserState.CSI_ENTRY, ParserAction.COLLECT, ParserState.CSI_PARAM)

            // escape -> escape intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.ESCAPE, ParserAction.COLLECT, ParserState.ESCAPE_INTERMEDIATE)
            // event 00-17,19,1C-1F / execute
            addRange(0x00, 0x17, ParserState.ESCAPE_INTERMEDIATE, ParserAction.EXECUTE, ParserState.ESCAPE_INTERMEDIATE)
            addRange(0x1c, 0x1f, ParserState.ESCAPE_INTERMEDIATE, ParserAction.EXECUTE, ParserState.ESCAPE_INTERMEDIATE)
            add(0x19, ParserState.ESCAPE_INTERMEDIATE, ParserAction.EXECUTE, ParserState.ESCAPE_INTERMEDIATE)
            // event 20-2F / collect
            addRange(0x20, 0x2f, ParserState.ESCAPE_INTERMEDIATE, ParserAction.COLLECT, ParserState.ESCAPE_INTERMEDIATE)
            // event 7F / ignore
            add(0x7f, ParserState.ESCAPE_INTERMEDIATE, ParserAction.IGNORE, ParserState.ESCAPE_INTERMEDIATE)
            // 30-7E / esc_dispatch
            addRange(0x30, 0x7e, ParserState.ESCAPE_INTERMEDIATE, ParserAction.ESC_DISPATCH, ParserState.GROUND)

            // escape -> ground
            // 30-4F,51-57,59,5A, 5C,60-7E / esc_dispatch
            addRange(0x30, 0x4f, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            addRange(0x51, 0x57, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            add(0x59, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            add(0x5a, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            add(0x5c, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            addRange(0x60, 0x7e, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)

            // escape -> sos/pm/apc string
            // 58,5E,5F
            add(0x58, ParserState.ESCAPE, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            // event 00-17,19,1C-1F,20-7F / ignore
            addRange(0x00, 0x17, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            add(0x19, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            addRange(0x1c, 0x1f, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            addRange(0x20, 0x7f, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)

            // escape -> csi entry
            // 5B
            add(0x5b, ParserState.ESCAPE, ParserAction.CLEAR, ParserState.CSI_ENTRY)
            // event 00-17,19,1C-1F / execute
            add(0x19, ParserState.CSI_ENTRY, ParserAction.EXECUTE, ParserState.CSI_ENTRY)
            addRange(0x00, 0x17, ParserState.CSI_ENTRY, ParserAction.EXECUTE, ParserState.CSI_ENTRY)
            addRange(0x1c, 0x1f, ParserState.CSI_ENTRY, ParserAction.EXECUTE, ParserState.CSI_ENTRY)
            add(0x7f, ParserState.CSI_ENTRY, ParserAction.IGNORE, ParserState.CSI_ENTRY)

            // csi entry -> csi param
            // 30-39,3B / param
            // 3C-3F / collect
            add(0x3b, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x30, 0x39, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x3c, 0x3f, ParserState.CSI_ENTRY, ParserAction.COLLECT, ParserState.CSI_PARAM)
            // csi param
            // event 00-17,19,1C-1F / execute
            add(0x19, ParserState.CSI_PARAM, ParserAction.EXECUTE, ParserState.CSI_PARAM)
            addRange(0x00, 0x17, ParserState.CSI_PARAM, ParserAction.EXECUTE, ParserState.CSI_PARAM)
            addRange(0x1c, 0x1f, ParserState.CSI_PARAM, ParserAction.EXECUTE, ParserState.CSI_PARAM)
            // event 30-39,3B / param
            add(0x3b, ParserState.CSI_PARAM, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x30, 0x39, ParserState.CSI_PARAM, ParserAction.PARAM, ParserState.CSI_PARAM)
            // event 7F / ignore
            add(0x7f, ParserState.CSI_PARAM, ParserAction.IGNORE, ParserState.CSI_PARAM)

            // csi param -> ground
            // 40-7E / csi_dispatch
            addRange(0x40, 0x7e, ParserState.CSI_PARAM, ParserAction.CSI_DISPATCH, ParserState.GROUND)

            // csi param -> csi intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.CSI_PARAM, ParserAction.COLLECT, ParserState.CSI_INTERMEDIATE)
            // event 00-17,19,1C-1F / execute
            // event 20-2F / collect
            // event 7F / ignore
            addRange(0x00, 0x17, ParserState.CSI_INTERMEDIATE, ParserAction.EXECUTE, ParserState.CSI_INTERMEDIATE)
            addRange(0x1c, 0x1f, ParserState.CSI_INTERMEDIATE, ParserAction.EXECUTE, ParserState.CSI_INTERMEDIATE)
            add(0x19, ParserState.CSI_INTERMEDIATE, ParserAction.EXECUTE, ParserState.CSI_INTERMEDIATE)
            add(0x7f, ParserState.CSI_INTERMEDIATE, ParserAction.IGNORE, ParserState.CSI_INTERMEDIATE)

            // csi intermediate -> ground
            // 40-7E / csi_dispatch
            addRange(0x40, 0x7e, ParserState.CSI_INTERMEDIATE, ParserAction.CSI_DISPATCH, ParserState.GROUND)


            // csi param -> csi ignore
            // 3A,3C-3F
            addRange(0x3c, 0x3f, ParserState.CSI_PARAM, ParserAction.IGNORE, ParserState.CSI_IGNORE)
            // event 00-17,19,1C-1F / execute
            addRange(0x00, 0x17, ParserState.CSI_IGNORE, ParserAction.EXECUTE, ParserState.CSI_IGNORE)
            addRange(0x1c, 0x1f, ParserState.CSI_IGNORE, ParserAction.EXECUTE, ParserState.CSI_IGNORE)
            add(0x19, ParserState.CSI_IGNORE, ParserAction.EXECUTE, ParserState.CSI_IGNORE)
            // event 20-3F,7F / ignore
            addRange(0x20, 0x3f, ParserState.CSI_IGNORE, ParserAction.IGNORE, ParserState.CSI_IGNORE)
            add(0x7f, ParserState.CSI_IGNORE, ParserAction.IGNORE, ParserState.CSI_IGNORE)

            // csi ignore -> ground
            addRange(0x40, 0x7e, ParserState.CSI_IGNORE, ParserAction.IGNORE, ParserState.GROUND)

            // csi intermediate -> csi ignore
            // 30-3F
            addRange(0x30, 0x3f, ParserState.CSI_INTERMEDIATE, ParserAction.IGNORE, ParserState.CSI_IGNORE)

            // csi entry -> csi intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.CSI_ENTRY, ParserAction.COLLECT, ParserState.CSI_INTERMEDIATE)

            // csi entry -> ground
            // 40-7E / csi_dispatch
            addRange(0x40, 0x7e, ParserState.CSI_ENTRY, ParserAction.CSI_DISPATCH, ParserState.GROUND)
            // TODO
        }
    }

    fun onIntArray(intArray: Array<Int>) {
        intArray.forEach { onChar(it) }
    }

    fun onCharArray(charArray: Array<Char>) {
        charArray.forEach { onChar(it.code) }
    }

    private fun onChar(code: Int) {
        val (nextAction, nextState) = transitionTable.queryTable(code, currentState)
        when (nextAction) {
            ParserAction.IGNORE, ParserAction.ERROR -> {}
            ParserAction.PRINT -> {
                println("ParserAction.PRINT")
                print(Char(code))
            }

            ParserAction.EXECUTE -> {
                val executor = c0c1ControlFunctionExecutors[code]
                if (executor != null) {
                    executor.execute(terminal)
                } else {
                    print("no executor found for $code")
                }
            }

            ParserAction.CLEAR -> {
                params.reset()
                collect = Char(0)
            }

            ParserAction.OSC_START -> {
                oscHandler.reset()
            }

            ParserAction.OSC_PUT -> {
                oscHandler.put(code)
            }

            ParserAction.OSC_END -> {
                oscHandler.finish()
            }

            ParserAction.CSI_DISPATCH -> {
                csiDispatch(collect, params, code).execute(terminal)
            }

            ParserAction.PARAM -> {
                params.put(code)
            }

            ParserAction.COLLECT -> {
                collect = code.toChar()
            }

            ParserAction.ESC_DISPATCH -> {
                escDispatch(collect, code).execute(terminal)
            }

            ParserAction.DCS_HOOK -> {
                dcsHandler = dcsHook(collect, params, code)
            }

            ParserAction.DCS_PUT -> {
                dcsHandler.put(code)
            }

            ParserAction.DCS_UNHOOK -> {
                dcsHandler.unHook()
            }
        }
        currentState = nextState
        currentAction = nextAction
    }

    private fun dcsHook(collect: Char, params: Params, code: Int): DCSHandler {
        TODO("Not yet implemented")
    }

    private fun escDispatch(collect: Char, code: Int): Executor {
        TODO("Not yet implemented")
    }

    private fun csiDispatch(collect: Char, params: Params, code: Int): Executor {
        TODO("Not yet implemented")
    }

}