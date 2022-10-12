// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.formdev.flatlaf.FlatLightLaf
import com.pty4j.PtyProcessBuilder
import org.slf4j.LoggerFactory
import shell.LocalPty
import shell.Shell
import terminal.Terminal
import terminal.TerminalConfig
import ui.TerminalViews
import ui.Terminals

class CoCoTerminal(val terminals: Terminals)

@Composable
@Preview
fun App() {
    val terminals = remember {
        val terminals = Terminals()
        terminals.addNewTerminal(Terminal(localShell, terminalConfig))
        terminals.addNewTerminal(Terminal(localShell, terminalConfig))
        CoCoTerminal(terminals)
    }
    TerminalViews(terminals)
}

val terminalConfig = TerminalConfig()

val localShell: Shell = LocalPty(
    PtyProcessBuilder(arrayOf("powershell.exe")).setInitialColumns(terminalConfig.columns)
        .setInitialRows(terminalConfig.rows).start()
)

//val jschShell = JschShell("192.168.130.134", 38322, "app", "wingtech")

val terminal = Terminal(localShell, terminalConfig)

fun main() = application {
//    FlatLightLaf.setup();
    terminal.start()
    Window(onCloseRequest = {
        terminal.stop()
        exitApplication()
    },
        title = "CoCoTerminal",
        onKeyEvent = { terminal.onKeyEvent(it) }) {
        MenuBar {
            Menu(text = "File", mnemonic = 'F') {
                Item(text = "Copy", onClick = {})
                Item(text = "Past", onClick = {})
                Item(text = "Save", onClick = {})
            }
            Menu(text = "Actions", mnemonic = 'A') {
                Menu("Settings") {
                    Item(text = "Setting1", onClick = {})
                    Item(text = "Setting2", onClick = {})
                }
                Separator()
                Item(text = "About", onClick = {})
                Item(text = "Exit", onClick = {})
            }
        }
        App()
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
