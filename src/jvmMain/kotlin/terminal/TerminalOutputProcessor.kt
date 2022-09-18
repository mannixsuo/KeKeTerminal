package terminal

class TerminalOutputProcessor(private val terminal: Terminal) {

    fun process(code: Int) {
        val activeBuffer = terminal.bufferService.getActiveBuffer()
        with(activeBuffer) {
            try {
                lock.lock()
                var lastLine = getLastLine()
                if (lastLine == null) {
                    lastLine = Line(terminal.terminalConfig.columns)
                    appendLine(lastLine)
                }
                lastLine.appendCell(
                    buildCell(code.toChar(), terminal)
                )
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