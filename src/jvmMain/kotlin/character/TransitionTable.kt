package character

import character.TableShift.INDEX_STATE_SHIFT
import character.TableShift.TRANSITION_ACTION_SHIFT
import character.TableShift.TRANSITION_STATE_MASK
import parser.ParserAction
import parser.ParserState

object TableShift {
    const val TRANSITION_ACTION_SHIFT = 4
    const val TRANSITION_STATE_MASK = 15
    const val INDEX_STATE_SHIFT = 8
}

val blueprint = Array(256) { it }

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

    fun addMany(codes: Array<Int>, state: ParserState, action: ParserAction, next: ParserState) {
        codes.forEach {
            this.table[state.state shl INDEX_STATE_SHIFT or it] =
                action.action shl TRANSITION_ACTION_SHIFT or next.state
        }
    }

    fun addRange(start: Int, endInclusive: Int, state: ParserState, action: ParserAction, next: ParserState) {
        addMany(blueprint.sliceArray(IntRange(start, endInclusive)), state, action, next)
    }

    fun queryTable(code: Int, currentState: ParserState): Pair<ParserAction, ParserState> {
        val value = table[currentState.state shl INDEX_STATE_SHIFT or code]
        val action: ParserAction = ParserAction.of(value shr TRANSITION_ACTION_SHIFT)
        val state: ParserState = ParserState.of(value and TRANSITION_STATE_MASK)
        return Pair(action, state)
    }

}