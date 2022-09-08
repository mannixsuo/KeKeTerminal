package terminal

import buffer.BufferLine
import parser.Direction

class TerminalInputHandler(private val terminal: Terminal) {

    private val bufferService = terminal.bufferService

    /**
     * CSI Ps @
     * Insert Ps (Blank) Character(s) (default = 1) (ICH).
     */
    fun insertChars(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            getLine(scrollY + y)?.insertChar(scrollX + x, params.elementAtOrElse(0) { 1 }, ' ')
        }
    }

    /**
     * CSI Ps SP @
     * Shift left Ps columns(s) (default = 1) (SL), ECMA-48.
     */
    fun shiftLeft(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            getLine(scrollY + y)?.shift(Direction.LEFT, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps A
     * Cursor Up Ps Times (default = 1) (CUU).
     */
    fun cursorUp(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            moveCursor(Direction.UP, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps SP A
     * Shift right Ps columns(s) (default = 1) (SR), ECMA-48.
     */
    fun cursorRight(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            getLine(scrollY + y)?.shift(Direction.RIGHT, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps B
     * Cursor Down Ps Times (default = 1) (CUD).
     */
    fun cursorDown(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            moveCursor(Direction.DOWN, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps C
     * Cursor Forward Ps Times (default = 1) (CUF).
     */
    fun cursorForward(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            moveCursor(Direction.RIGHT, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps D
     * Cursor Backward Ps Times (default = 1) (CUB).
     */
    fun cursorBackward(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            moveCursor(Direction.LEFT, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps E
     * Cursor Next Line Ps Times (default = 1) (CNL).
     */
    fun cursorNextLine(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            moveCursor(Direction.DOWN, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps F
     * Cursor Preceding Line Ps Times (default = 1) (CPL).
     */
    fun cursorPrecedingLine(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            moveCursor(Direction.UP, params.elementAtOrElse(0) { 1 })
        }
    }

    /**
     * CSI Ps G
     * Cursor Character Absolute  [column] (default = [row,1]) (CHA).
     * Moves cursor to the Ps-th column of the active line. The default value of Ps is 1.
     */
    fun cursorCharacterAbsolute(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            x = params.elementAtOrElse(0) { 1 }
        }
    }

    /**
     * CSI Ps ; Ps H
     * Cursor Position [row;column] (default = [1,1]) (CUP).
     */
    fun cursorPosition(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            y = params.elementAtOrElse(0) { 1 }
            x = params.elementAtOrElse(1) { 1 }
        }
    }

    /**
     * CSI Ps I
     * Cursor Forward Tabulation Ps tab stops (default = 1) (CHT).
     */
    fun cursorForwardTabulation(params: Array<Int>) {
        TODO()
    }

    /**
     * CSI Ps J  Erase in Display (ED), VT100.
     * Ps = 0  ⇒  Erase Below (default).
     * Ps = 1  ⇒  Erase Above.
     * Ps = 2  ⇒  Erase All.
     * Ps = 3  ⇒  Erase Saved Lines, xterm.
     */
    fun eraseInDisplay(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            when (params.elementAtOrElse(0) { 0 }) {
                0 -> {
                    for (index in (y + 1)..screenRows) {
                        getLine(scrollY + index)?.eraseLine(0, Int.MAX_VALUE)
                    }
                }

                1 -> {
                    for (index in (y - 1) downTo 0) {
                        getLine(scrollY + index)?.eraseLine(0, Int.MAX_VALUE)
                    }
                }

                2 -> {
                    for (index in 0..y) {
                        getLine(scrollY + index)?.eraseLine(0, Int.MAX_VALUE)
                    }
                }
                //Ps = 3  ⇒  Erase Saved Lines, xterm.
                3 -> {
                    for (index in 0..scrollY + y) {
                        getLine(index)?.eraseLine(0, Int.MAX_VALUE)
                    }
                }
            }
        }
    }

    /**
     * CSI ? Ps J
     * Erase in Display (DECSED), VT220.
     * Ps = 0  ⇒  Selective Erase Below (default).
     * Ps = 1  ⇒  Selective Erase Above.
     * Ps = 2  ⇒  Selective Erase All.
     * Ps = 3  ⇒  Selective Erase Saved Lines, xterm.
     */
    fun eraseInDisplaySelective(params: Array<Int>) {
        eraseInDisplay(params)
    }

    /**
     * CSI Ps K  Erase in Line (EL), VT100.
     *  Ps = 0  ⇒  Erase to Right (default).
     *  Ps = 1  ⇒  Erase to Left.
     *  Ps = 2  ⇒  Erase All.
     */
    fun eraseInLine(params: Array<Int>) {
        val activeBuffer = bufferService.getActiveBuffer()
        with(activeBuffer) {
            when (params.elementAtOrElse(0) { 0 }) {
                0 -> {
                    getLine(scrollY + y)?.eraseLine(scrollX + x, Int.MAX_VALUE)
                }

                1 -> {
                    getLine(scrollY + y)?.eraseLine(0, scrollX + x)
                }

                2 -> {
                    getLine(scrollY + y)?.eraseLine(0, Int.MAX_VALUE)
                }

                else -> {}
            }

        }
    }

    /**
     * CSI ? Ps K
     * Erase in Line (DECSEL), VT220.
     * Ps = 0  ⇒  Selective Erase to Right (default).
     * Ps = 1  ⇒  Selective Erase to Left.
     * Ps = 2  ⇒  Selective Erase All.
     */
    fun eraseInLineSelective(params: Array<Int>) {
        eraseInLine(params)
    }

    /**
     * CSI Ps L
     * Insert Ps Line(s) (default = 1) (IL).
     */
    fun insertLines(params: Array<Int>) {
        TODO("Not yet implemented")
    }

    /**
     * CSI Ps M
     * Delete Ps Line(s) (default = 1) (DL).
     */
    fun deleteLines(params: Array<Int>) {
        TODO("Not yet implemented")
    }

    /**
     * CSI Ps P
     * Delete Ps Character(s) (default = 1) (DCH).
     */
    fun deleteCharacters(params: Array<Int>) {
        TODO("Not yet implemented")
    }

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