package parser

// 0000000000 0000000000 0000000000 00
class StateTable {

}


class Parser {
    private var initialState = ParserState.GROUND
    private var parserAction = ParserAction.ERROR
    private var params = Params()
    private var collect: Char = Char(0)

    fun consumeChar(c: Char) {

    }

}