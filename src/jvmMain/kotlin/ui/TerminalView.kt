package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import terminal.ILine
import terminal.Terminal
import ui.Fonts.jetbrainsMono
import java.util.*

data class CursorState(var x: Int = 0, var y: Int = 0, var blink: Boolean = false)
data class ScrollState(var x: Int = 0, var y: Int = 0)

@Composable
fun TerminalView(terminal: Terminal) {

    var lines: List<ILine> by remember { mutableStateOf(ArrayList<ILine>()) }
    var timerInitialed by remember { mutableStateOf(false) }
    val cursorState by remember { mutableStateOf(CursorState()) }
    val scrollState by remember { mutableStateOf(ScrollState(y = terminal.scrollY)) }
    var cursorBlink by remember { mutableStateOf(false) }
    var cursorX by remember { mutableStateOf(0) }
    var cursorY by remember { mutableStateOf(0) }
    val timer by remember { mutableStateOf(Timer()) }
    val lineTimer by remember { mutableStateOf(Timer()) }
    var text by remember { mutableStateOf("") }
//    val textInputService = LocalTextInputService.current
    LocalInputModeManager.current.inputMode

    if (!timerInitialed) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                cursorBlink = !cursorBlink
            }
        }, 1000, 200)
        timerInitialed = true
        lineTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                lines = terminal.bufferService.getActiveBuffer()
                    .getLines(IntRange(terminal.scrollY, terminal.scrollY + terminal.terminalConfig.rows))
                cursorX = terminal.cursorX
                cursorY = terminal.cursorY
            }
        }, 1000, 200)
    }


    Surface {
        Column {
            Text("cursor (${cursorX},${cursorY})")
            Lines(lines, cursorX, cursorY) { cursorBlink }
        }
    }
}

@Composable
fun Lines(lines: List<ILine>, cursorX: Int, cursorY: Int, cursorBlink: () -> Boolean) {

    Column {
        for (index in lines.indices) {
            Line(index, lines[index], index == cursorY, cursorBlink, cursorX)
        }
    }
}

// cursorBlink: () -> Boolean : use function so only rows that cursor affects repaint every time cursor blink
@Composable
fun Line(index: Int, line: ILine, cursorOnThisLine: Boolean, cursorBlink: () -> Boolean, cursorX: Int) {
    Row {
        Text(text = "$index  ", fontFamily = jetbrainsMono(), color = Color.LightGray)
        LineContent(line, cursorOnThisLine, cursorBlink, cursorX)
    }
//    println("Line $index PAINT")
}

@Composable
fun LineContent(line: ILine, cursorOnThisLine: Boolean, cursorBlink: () -> Boolean, cursorX: Int) {

    val builder = AnnotatedString.Builder()
    val cells = line.getCells()
    for (index in 0 until line.length()) {
        val cell = line.getCell(index)
        cell?.let {
            builder.append(it.char)
            val style = SpanStyle(
                background = it.bg,
                color = it.fg,
                fontWeight = if (it.bold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (it.italic) FontStyle.Italic else FontStyle.Normal
            )
            builder.pushStyle(style)
        }
    }
    if (cursorOnThisLine && cursorBlink.invoke()) {
        if (cursorX >= line.length()) {
            builder.append('_')
            builder.pushStyle(SpanStyle(background = Color.Black, color = Color.Black))
        } else {
            val cellAtCursor = cells[cursorX]
            cellAtCursor?.let {
                builder.addStyle(SpanStyle(color = it.bg, background = it.fg), cursorX, cursorX + 1)
            }
        }
    }
    Text(
        text = builder.toAnnotatedString(), fontFamily = jetbrainsMono()
    )
}
