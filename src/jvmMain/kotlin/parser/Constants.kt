package parser

// 状态机 代表当前状态
enum class ParserState(val int: Int) {
    GROUND(0),
    ESCAPE(1),
    ESCAPE_INTERMEDIATE(2),
    CSI_ENTRY(3),
    CSI_PARAM(4),
    CSI_INTERMEDIATE(5),
    CSI_IGNORE(6),
    SOS_PM_APC_STRING(7),
    OSC_STRING(8),
    DCS_ENTRY(9),
    DCS_PARAM(10),
    DCS_IGNORE(11),
    DCS_INTERMEDIATE(12),
    DCS_PASS_THROUGH(13);
}


/**
 * Internal actions of EscapeSequenceParser.
 */
enum class ParserAction(val code:Int) {
    IGNORE(0),
    ERROR(1),
    PRINT(2),
    EXECUTE(3),
    OSC_START(4),
    OSC_PUT(5),
    OSC_END(6),
    CSI_DISPATCH(7),
    PARAM(8),
    COLLECT(9),
    ESC_DISPATCH(10),
    CLEAR(11),
    DCS_HOOK(12),
    DCS_PUT(13),
    DCS_UNHOOK(14),
}

enum class OscState(val code:Int) {
    START(0),
    ID(1),
    PAYLOAD(2),
    ABORT(3),
}

// payload limit for OSC and DCS
const val PAYLOAD_LIMIT = 10000000