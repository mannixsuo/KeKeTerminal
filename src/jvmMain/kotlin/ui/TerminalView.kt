package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import buffer.IBufferLine
import terminal.Terminal
import ui.Fonts.jetbrainsMono

@Composable
fun TerminalView(terminal: Terminal) {

    var lines: List<IBufferLine> by remember { mutableStateOf(ArrayList<IBufferLine>()) }

    var coursorX by remember { mutableStateOf(0) }

    var coursorY by remember { mutableStateOf(0) }

    Surface {
        if (terminal.repaint.value) {
            val activeBuffer = terminal.bufferService.getActiveBuffer()
            lines = activeBuffer.getLine(0, 30)
            coursorX = activeBuffer.x
            coursorY = activeBuffer.y
            terminal.repaint.value = false
        }
        Column {
            Text("cursor ($coursorX,$coursorY)")
            Lines(lines)
        }
    }
}

@Composable
fun Lines(lines: List<IBufferLine>) {
    Column {
        for (line in lines) {
            Line(line)
        }
    }
}

@Composable
fun Line(line: IBufferLine) {
    Row {
        LineContent(line)
    }
}

@Composable
fun LineContent(line: IBufferLine) {
    line.getCells()?.let {
        for (cell in it) {
            Text(text = cell.getChar().toString(), softWrap = false, fontFamily = jetbrainsMono())
        }
    }
}
