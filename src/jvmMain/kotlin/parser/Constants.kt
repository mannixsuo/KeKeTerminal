package parser

// 状态机 代表当前状态
enum class ParserState {
    /**
     *   This is the initial state of the parser, and the state used to consume all characters other than components
     *   of escape and control sequences.
     */
    GROUND,

    /**
     * This state is entered whenever the C0 control ESC is received.
     */
    ESCAPE,

    /**
     * This state is entered when an intermediate character arrives in an escape sequence.
     */
    ESCAPE_INTERMEDIATE,

    /**
     * This state is entered when the control function CSI is recognised, in 7-bit or 8-bit form.
     */
    CSI_ENTRY,

    /**
     * This state is entered when a parameter character is recognised in a control sequence.
     */
    CSI_PARAM,

    /**
     * This state is entered when an intermediate character is recognised in a control sequence.
     */
    CSI_INTERMEDIATE,

    /**
     * This state is used to consume remaining characters of a control sequence that is still being recognised,
     * but has already been disregarded as malformed.
     */
    CSI_IGNORE,
    /**
     * This state is entered when the control function DCS is recognised, in 7-bit or 8-bit form.
     */
    DCS_ENTRY,

    SOS_PM_APC_STRING,
    OSC_STRING,


    DCS_PARAM,
    DCS_IGNORE,
    DCS_INTERMEDIATE,
    DCS_PASS_THROUGH;
}


/**
 * Internal actions of EscapeSequenceParser.
 */
enum class ParserAction {
    IGNORE, ERROR, PRINT, EXECUTE, OSC_START, OSC_PUT, OSC_END, CSI_DISPATCH, PARAM, COLLECT, ESC_DISPATCH, CLEAR, DCS_HOOK, DCS_PUT, DCS_UNHOOK,
}

enum class OscState {
    START, ID, PAYLOAD, ABORT,
}

// payload limit for OSC and DCS
const val PAYLOAD_LIMIT = 10000000