// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
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
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
