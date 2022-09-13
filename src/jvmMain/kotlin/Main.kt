// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.pty4j.PtyProcessBuilder
import org.slf4j.LoggerFactory
import shell.LocalPty
import shell.Shell
import terminal.Terminal
import terminal.TerminalConfig
import ui.TerminalView
import java.util.*

@Composable
@Preview
fun App() {
    MaterialTheme {
        TerminalView(terminal)
    }
}

val terminalConfig = TerminalConfig()
val localShell: Shell = LocalPty(
    PtyProcessBuilder(arrayOf("cmd.exe"))
        .setInitialColumns(terminalConfig.columns)
        .setInitialRows(terminalConfig.rows)
        .start()
)
val terminal = Terminal(localShell, terminalConfig)

fun main() = application {
    terminal.start()

    Window(onCloseRequest = {
        terminal.stop()
        exitApplication()
    }, title = "CoCoTerminal") {
        App()
//        Line()
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}

@Composable
@Preview
fun Line() {
    var blink by remember { mutableStateOf(false) }
    var timerInitialed by remember { mutableStateOf(false) }
    val timer by remember { mutableStateOf(Timer()) }
    if (!timerInitialed) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                blink = !blink
            }
        }, 1000, 500)
        timerInitialed = true
    }
    val text = "Line Line Line Line Line Line Line Line Line Line Line Line1"
    val builder = AnnotatedString.Builder()
    var index = 0
    for (t in text) {
        builder.append(t)
        if (index++ % 3 == 0) {
            if (blink) {
                builder.addStyle(SpanStyle(color = Color.White, background = Color.Black), index, index + 1)
            }
        }
    }
    Text(builder.toAnnotatedString(), softWrap = false)
}
