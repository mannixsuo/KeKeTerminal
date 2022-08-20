package character

import parser.ParserAction
import parser.ParserState

const val TRANSITION_ACTION_SHIFT = 4
const val TRANSITION_STATE_MASK = 15
const val INDEX_STATE_SHIFT = 8


// https://vt100.net/emu/dec_ansi_parser

/**
 * table[ state ... code] = action ... next_state
 */
class TransitionTable(private val size: Int) {
    private lateinit var table: Array<Int>

    fun setDefault(action: ParserAction, next: ParserState) {
        this.table = Array(size) { action.action shl TRANSITION_ACTION_SHIFT or next.state }
    }

    fun add(code: Int, state: ParserState, action: ParserAction, next: ParserState) {
        this.table[state.state shl INDEX_STATE_SHIFT or code] = action.action shl TRANSITION_ACTION_SHIFT or next.state
    }


}