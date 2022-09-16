package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import buffer.IBufferLine
import terminal.Terminal
import java.util.*

data class CursorState(var x: Int = 0, var y: Int = 0, var blink: Boolean = false)
data class ScrollState(var x: Int = 0, var y: Int = 0)

@Composable
fun TerminalView(terminal: Terminal) {

    var lines: List<IBufferLine> by remember { mutableStateOf(ArrayList<IBufferLine>()) }
    var timerInitialed by remember { mutableStateOf(false) }
    val cursorState by remember { mutableStateOf(CursorState()) }
    var cursorBlink by remember { mutableStateOf(false) }
    var cursorX by remember { mutableStateOf(0) }
    var cursorY by remember { mutableStateOf(0) }
    val timer by remember { mutableStateOf(Timer()) }
    var text by remember { mutableStateOf("") }
//    val textInputService = LocalTextInputService.current


    if (!timerInitialed) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                cursorBlink = !cursorBlink
                lines = terminal.bufferService.getActiveBuffer().getLine(0, 100)
                cursorX = terminal.bufferService.getActiveBuffer().x
                cursorY = terminal.bufferService.getActiveBuffer().y
            }
        }, 1000, 500)
        timerInitialed = true
    }

    Surface {
        Column {
            TextField(text, onValueChange = { text = it })

            Text("cursor (${cursorX},${cursorY})")
            Lines(lines, cursorX, cursorY, cursorBlink)
        }
    }
}

@Composable
fun Lines(lines: List<IBufferLine>, cursorX: Int, cursorY: Int, cursorBlink: Boolean) {

    Column {
        for (index in lines.indices) {
            Line(lines[index], index == cursorY, cursorBlink, cursorX)
        }
    }
}

@Composable
fun Line(line: IBufferLine, cursorOnThisLine: Boolean, cursorBlink: Boolean, cursorX: Int) {

    Row {
        LineContent(line, cursorOnThisLine, cursorBlink, cursorX)
    }
}

@Composable
fun LineContent(line: IBufferLine, cursorOnThisLine: Boolean, cursorBlink: Boolean, cursorX: Int) {

    val builder = AnnotatedString
        .Builder(line.toLineString() ?: "")
    if (cursorOnThisLine && cursorBlink) {
        builder
            .addStyle(
                SpanStyle(background = Color.Black, color = Color.White),
                cursorX - 1, cursorX
            )
    }
    Text(
        text = builder.toAnnotatedString()
    )


}
