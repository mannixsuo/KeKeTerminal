package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import terminal.buffer.IBufferLine
import terminal.buffer.defaultTheme
import terminal.Terminal
import ui.Fonts.jetbrainsMono
import java.util.*

data class CursorState(var x: Int = 0, var y: Int = 0, var blink: Boolean = false)
data class ScrollState(var x: Int = 0, var y: Int = 0)

@Composable
fun TerminalView(terminal: Terminal) {

    var lines: List<IBufferLine> by remember { mutableStateOf(ArrayList<IBufferLine>()) }
    var timerInitialed by remember { mutableStateOf(false) }
    val cursorState by remember { mutableStateOf(CursorState()) }
    val scrollState by remember { mutableStateOf(ScrollState(y = terminal.bufferService.getActiveBuffer().scrollY)) }
    var cursorBlink by remember { mutableStateOf(false) }
    var cursorX by remember { mutableStateOf(0) }
    var cursorY by remember { mutableStateOf(0) }
    val timer by remember { mutableStateOf(Timer()) }
    var text by remember { mutableStateOf("") }
//    val textInputService = LocalTextInputService.current
    LocalInputModeManager.current.inputMode

    if (!timerInitialed) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                cursorBlink = !cursorBlink
                lines = terminal.bufferService.getActiveBuffer()
                    .getLine(scrollState.y, terminal.bufferService.getActiveBuffer().y)
                cursorX = terminal.bufferService.getActiveBuffer().x
                cursorY = terminal.bufferService.getActiveBuffer().y
            }
        }, 1000, 200)
        timerInitialed = true
    }


    Surface {
        Column {
            Text("cursor (${cursorX},${cursorY})")

        }
    }
}

@Composable
fun Lines(lines: List<IBufferLine>, cursorX: Int, cursorY: Int, cursorBlink: () -> Boolean) {

    Column {
        for (index in lines.indices) {
            Line(index, lines[index], index == cursorY, cursorBlink, cursorX)
        }
    }
}

// cursorBlink: () -> Boolean : use function so only rows that cursor affects repaint every time cursor blink
@Composable
fun Line(index: Int, line: IBufferLine, cursorOnThisLine: Boolean, cursorBlink: () -> Boolean, cursorX: Int) {
    Row {
        Text(text = "$index  ", fontFamily = jetbrainsMono(), color = Color.LightGray)
        LineContent(line, cursorOnThisLine, cursorBlink, cursorX)
    }
//    println("Line $index PAINT")
}

@Composable
fun LineContent(line: IBufferLine, cursorOnThisLine: Boolean, cursorBlink: () -> Boolean, cursorX: Int) {

    val builder = AnnotatedString
        .Builder(line.toLineString() ?: "")
    if (cursorOnThisLine && cursorBlink.invoke()) {
        builder
            .addStyle(
                SpanStyle(background = Color.Black, color = Color.White),
                cursorX - 1, cursorX
            )
    }
    Text(
        text = builder.toAnnotatedString(),
        fontFamily = jetbrainsMono()
    )
}
