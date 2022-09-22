// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.pty4j.PtyProcessBuilder
import org.slf4j.LoggerFactory
import shell.JschShell
import shell.LocalPty
import shell.Shell
import shell.UserInfoCompose
import terminal.Terminal
import terminal.TerminalConfig
import ui.TerminalView

@Composable
@Preview
fun App() {
    MaterialTheme {
//        UserInfoCompose()
        TerminalView(terminal)
    }
}

val terminalConfig = TerminalConfig()

val localShell: Shell = LocalPty(
    PtyProcessBuilder(arrayOf("powershell.exe"))
        .setInitialColumns(terminalConfig.columns)
        .setInitialRows(terminalConfig.rows)
        .start()
)

val jschShell = JschShell("192.168.130.134", 38322, "app", "wingtech")

val terminal = Terminal(jschShell, terminalConfig)

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    terminal.start()
    Window(
        onCloseRequest = {
            terminal.stop()
            exitApplication()
        },
        title = "CoCoTerminal",
        icon = loadSvgPainter(ResourceLoader.Default.load("icon/terminal.svg"), Density(10F)),
        onKeyEvent = { terminal.onKeyEvent(it) }) {
        App()
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
