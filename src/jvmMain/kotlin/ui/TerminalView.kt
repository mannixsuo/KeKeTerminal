package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import buffer.IBufferLine
import kotlinx.coroutines.launch
import terminal.Terminal
import ui.Fonts.jetbrainsMono
import java.util.*

data class CursorState(var x: Int = 0, var y: Int = 0, var blink: Boolean = false)


@Composable
fun TerminalView(terminal: Terminal) {

    var lines: List<IBufferLine> by remember { mutableStateOf(ArrayList<IBufferLine>()) }
    val repaintTimer: Timer? by remember { mutableStateOf(null) }

    val cursorState by remember { mutableStateOf(CursorState()) }
    val coroutineScope = rememberCoroutineScope()

    Surface {
        coroutineScope.launch {
//TODO
            while (true) {
                val maxFPS = 50
                repaintTimer?.cancel()
                repaintTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        val activeBuffer = terminal.bufferService.getActiveBuffer()
                        lines = activeBuffer.getLine(0, 30)
                        cursorState.x = activeBuffer.x
                        cursorState.y = activeBuffer.y
                    }
                }, 1000L / maxFPS)

            }
        }

        Column {
            Text("cursor (${cursorState.x},${cursorState.y})")
            Lines(lines, cursorState)
        }
    }
}

@Composable
fun Lines(lines: List<IBufferLine>, cursorState: CursorState) {

    Column {
        for (index in lines.indices) {
            Line(lines[index], index == cursorState.y, cursorState)
        }
    }
}

@Composable
fun Line(line: IBufferLine, cursorOnThisLine: Boolean, cursorState: CursorState) {

    Row {
        LineContent(line, cursorOnThisLine, cursorState)
    }
}

@Composable
fun LineContent(line: IBufferLine, cursorOnThisLine: Boolean, cursorState: CursorState) {
    if (cursorOnThisLine) {
        line.getCells()?.let {
            for (index in it.indices) {
                if (cursorState.x == index) {
                    Text(
                        modifier = Modifier.background(Color.Black),
                        text = it[index].getChar().toString(),
                        softWrap = false,
                        color = Color.White,
                        fontFamily = jetbrainsMono()
                    )
                } else {
                    Text(text = it[index].getChar().toString(), softWrap = false, fontFamily = jetbrainsMono())
                }
            }
        }
    } else {
        line.getCells()?.let {
            for (index in it.indices) {
                Text(text = it[index].getChar().toString(), softWrap = false, fontFamily = jetbrainsMono())
            }
        }
    }

}
