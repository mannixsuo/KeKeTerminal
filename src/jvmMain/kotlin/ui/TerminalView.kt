package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
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
    Surface {
        SelectionContainer {
            Column(modifier = Modifier.background(Color.Black).fillMaxWidth()) {
                Text(color = Color.White, text = "cursor (X: ${cursorX}, Y:${cursorY})")
                Lines(
                    lines, cursorX, cursorY
                )
            }
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
    Row(modifier = Modifier.background(Color.Black)) {
        Text(text = "$index  ", fontFamily = jetbrainsMono(), color = Color.LightGray)
        LineContent(line, cursorOnThisLine, cursorX)
    }
}

@Composable
fun LineContent(line: ILine, cursorOnThisLine: Boolean, cursorX: Int) {

    val builder = AnnotatedString.Builder()
    val cells = line.getCells()
    val cursorInText: Boolean = cursorOnThisLine && line.length() != 0 && cursorX < line.length()

    if (line.length() > 0) {
        for (index in 0 until line.length()) {
            val cell = line.getCell(index)
            val cursorInThisPosition: Boolean = cursorInText && cursorX == index
            cell?.let {
                builder.append(it.char)
                var bg = it.bg
                var fg = it.fg
                if (cursorInThisPosition) {
                    bg = it.fg
                    fg = it.bg
                }
                val style = SpanStyle(
                    background = bg,
                    color = fg,
                    fontWeight = if (it.bold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (it.italic) FontStyle.Italic else FontStyle.Normal
                )
                builder.addStyle(style, index, index + 1)
            }
        }
        Text(
            text = builder.toAnnotatedString(),
            fontFamily = jetbrainsMono(),
            softWrap = false
        )
    }

    if (cursorOnThisLine) {
        if (cursorInText) {

        } else {
            Text(
                modifier = Modifier.drawWithContent {
                    drawRect(Color.White)
                },
                text = "_",
            )
        }
    }
}
