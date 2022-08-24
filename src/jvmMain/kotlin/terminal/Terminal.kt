package terminal

import parser.Parser
import shell.Shell
import java.io.IOException
import java.io.InputStreamReader

class Terminal(shell: Shell) {
    private val channelInputStreamReader = shell.getChannelInputStreamReader()
    private val channelOutputStreamWriter = shell.getChannelOutputStreamWriter()
    private val parser: Parser = Parser(this)

    fun start() {
        startReadFromChannel()
        startWriteToChannel()
    }

    fun stop() {
        channelInputStreamReader.close()
        channelOutputStreamWriter.close()
    }

    private fun startReadFromChannel() {
        Thread {
            val buf = CharArray(1024)
            var length: Int
            while (channelInputStreamReader.read(buf).also { length = it } != -1) {
                parser.onCharArray(buf.copyOfRange(0, length))
                print(String(buf, 0, length))
            }
        }.start()
    }

    private fun startWriteToChannel() {
        Thread {
            val inputStreamReader = InputStreamReader(System.`in`)
            val buf = CharArray(1024)
            var length: Int
            try {
                while (inputStreamReader.read(buf).also { length = it } != -1) {
                    val s = String(buf, 0, length)
                    if ("\n" == s) {
                        channelOutputStreamWriter.write(13)
                    } else {
                        channelOutputStreamWriter.write(buf, 0, length)
                    }
                    channelOutputStreamWriter.flush()
                }
            } catch (ignore: IOException) {
            }
        }.start()
    }
}