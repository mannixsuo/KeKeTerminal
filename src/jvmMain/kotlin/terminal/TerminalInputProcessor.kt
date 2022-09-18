package terminal

import parser.Direction

class TerminalInputProcessor(private val terminal: Terminal) {

    val csiProcessor = CSIProcessor(terminal)


    fun carriageReturn() {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            x = 0
            scrollX = 0
            y++
        }
        terminal.repaint.value = true

    }

    fun newLine() {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            setLine(scrollY + y, BufferLine(1))
        }
        terminal.repaint.value = true
    }
}