package buffer

interface TerminalDataInputStream {

    fun readNextChar(): Char?

    fun mark()

    fun reset()

}