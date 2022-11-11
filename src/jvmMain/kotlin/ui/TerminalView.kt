package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import terminal.ILine
import ui.Fonts.jetbrainsMono


@Composable
fun TerminalView(cursorX: Int = 0, cursorY: Int = 0, lines: List<ILine>) {
// linesGenerator: (scope: CoroutineScope) -> List<ILine>
//    val lines by loadableScoped(linesGenerator)

    println("TerminalView $cursorX $cursorY ${lines.size}")

    Surface {
        Column {
            Text("cursor (X: ${cursorX}, Y:${cursorY})")
            Lines(
                lines, cursorX, cursorY
            )
        }
    }
}

@Composable
fun Lines(lines: List<ILine>, cursorX: Int, cursorY: Int) {

    Column {
        for (index in lines.indices) {
            Line(index, lines[index], index == cursorY, cursorX)
        }
    }
}

// cursorBlink: () -> Boolean : use function so only rows that cursor affects repaint every time cursor blink
@Composable
fun Line(index: Int, line: ILine, cursorOnThisLine: Boolean, cursorX: Int) {
    Row {
        Text(text = "$index  ", fontFamily = jetbrainsMono(), color = Color.LightGray)
        LineContent(line, cursorOnThisLine, cursorX)
    }
//    println("Line $index PAINT")
}

@Composable
fun LineContent(line: ILine, cursorOnThisLine: Boolean, cursorX: Int) {

    val builder = AnnotatedString.Builder()
    val cells = line.getCells()
    for (index in 0 until line.length()) {
        val cell = line.getCell(index)
        cell?.let {
            builder.append(it.char)
            val bg = if (cursorOnThisLine && index == cursorX) it.fg else it.bg
            val fg = if (cursorOnThisLine && index == cursorX) it.bg else it.fg
            val style = SpanStyle(
                background = bg,
                color = fg,
                fontWeight = if (it.bold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (it.italic) FontStyle.Italic else FontStyle.Normal
            )
            builder.pushStyle(style)
        }
    }
    Text(
        text = builder.toAnnotatedString(),
        fontFamily = jetbrainsMono(),
        softWrap = false
    )
    if (cursorOnThisLine && cursorX > line.length()) {
        Text(
            modifier = Modifier.drawWithContent {
                drawRect(Color.Black)
            },
            text = "_",
        )
    }
}
