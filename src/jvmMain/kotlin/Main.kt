// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
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

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    val terminalConfig = TerminalConfig()
    val localShell: Shell = LocalPty(
        PtyProcessBuilder(arrayOf("cmd.exe"))
            .setInitialColumns(terminalConfig.columns)
            .setInitialRows(terminalConfig.rows)
            .start()
    )
    val terminal = Terminal(localShell, terminalConfig)
    terminal.start()
    Window(onCloseRequest = {
        exitApplication()
        terminal.stop()
    }) {
        App()
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
