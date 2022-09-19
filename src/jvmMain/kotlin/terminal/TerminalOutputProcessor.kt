package terminal

class TerminalOutputProcessor(private val terminal: Terminal) {

    fun process(code: Int) {
        val activeBuffer = terminal.bufferService.getActiveBuffer()
        with(activeBuffer) {
            try {
                lock.lock()
                var lineAtCurrentCursor = getLine(terminal.scrollY + terminal.cursorY)
                if (lineAtCurrentCursor == null) {
                    lineAtCurrentCursor = Line(terminal.terminalConfig.columns)
                    insertLine(terminal.scrollY + terminal.cursorY, lineAtCurrentCursor)
                }
                lineAtCurrentCursor.appendCell(buildCell(code.toChar(), terminal))
                terminal.cursorX++
            } finally {
                lock.unlock()
            }
        }
    }
}

fun buildCell(char: Char, terminal: Terminal): ICell {
    return Cell(
        char,
        terminal.nextCharBgColor,
        terminal.nextCharFgColor,
        terminal.nextCharBold,
        terminal.nextCharItalic
    )
}

fun buildCells(char: Char, count: Int, terminal: Terminal): Array<ICell> {
    return Array(count) { buildCell(char, terminal) }
}