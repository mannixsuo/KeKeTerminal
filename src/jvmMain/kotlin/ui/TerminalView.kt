package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import buffer.IBufferLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import terminal.Terminal

@Composable
fun TerminalView(terminal: Terminal) {

    var lines: List<IBufferLine> = terminal.bufferService.getActiveBuffer().getLine(0, 30)

    Surface {
        Lines(readLines(terminal))
    }
}

fun readLines(terminal: Terminal): (backgroundScope: CoroutineScope) -> List<IBufferLine> {
    return fun(scope: CoroutineScope): List<IBufferLine> {
        var list: List<IBufferLine> by mutableStateOf(ArrayList())

        val refreshJob = scope.launch {
            while (true) {
                delay(1000)
                if (terminal.repaint) {
                    list = terminal.bufferService.getActiveBuffer().getLine(0, 30)
                }
            }
        }
        return list
    }
}

@Composable
fun Lines(linesCoroutine: (backgroundScope: CoroutineScope) -> List<IBufferLine>) {
    val scrollState = rememberLazyListState()
    val lines by loadableScoped(linesCoroutine)


    LazyColumn(state = scrollState) {
        if (lines != null) {
            items(lines!!.size) {
                Line(lines!![it])
            }
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
fun LineContent(line: IBufferLine) = Text(line.toLineString() ?: "")