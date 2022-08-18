package buffer

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*

internal class CircleCharBufferTest {

    @org.junit.jupiter.api.Test
    fun readNextChar() {

    }

    @org.junit.jupiter.api.Test
    fun mark() {
    }

    @org.junit.jupiter.api.Test
    fun reset() {
    }

    @org.junit.jupiter.api.Test
    fun pushChars() {
        val circleCharBuffer = CircularList(3 )
        runBlocking {
            launch {
                circleCharBuffer.pushChars(charArrayOf('1', '2', '3'))
                assertEquals(circleCharBuffer.readNextChar(), '1')
                assertEquals(circleCharBuffer.readNextChar(), '2')
                assertEquals(circleCharBuffer.readNextChar(), '3')
            }
        }
    }
}