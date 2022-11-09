// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.MenuBar
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
import ui.TerminalViews
import ui.Terminals

class CoCoTerminalAppState(val terminals: Terminals)

@Composable
fun rememberCocoTerminalAppState(terminals: Terminals) = remember(terminals) {
    CoCoTerminalAppState(terminals)
}


val terminalConfig = TerminalConfig()

val localShell: Shell = LocalPty(
    PtyProcessBuilder(arrayOf("powershell.exe")).setInitialColumns(terminalConfig.columns)
        .setInitialRows(terminalConfig.rows).start()
)
val localShell1: Shell = LocalPty(
    PtyProcessBuilder(arrayOf("powershell.exe")).setInitialColumns(terminalConfig.columns)
        .setInitialRows(terminalConfig.rows).start()
)


//val jschShell = JschShell("192.168.130.134", 38322, "app", "wingtech")

val terminal = Terminal(localShell, terminalConfig)
val terminal1 = Terminal(localShell1, terminalConfig)

@Composable
@Preview
fun App(appState: CoCoTerminalAppState) {
    TerminalViews(appState)
}


fun main() = application {
//    FlatLightLaf.setup();
    val terminals = Terminals()
    terminals.addNewTerminal(terminal)
    terminals.addNewTerminal(terminal1)
    terminal.start()
    terminal1.start()
    val appState = rememberCocoTerminalAppState(terminals)

    Window(onCloseRequest = {
        exitApplication()
    }, title = "CoCoTerminal", onKeyEvent = { key ->
        terminals.activeTerminal?.onKeyEvent(key) != null
    }) {
        MenuBar {
            Menu(text = "会话(F)", mnemonic = 'F') {
                Item(text = "新建会话(N)", onClick = {})
                Item(text = "打开会话(O)", onClick = {})
                Separator()
                Item(text = "日志(L)", onClick = {})
                Separator()
                Menu(text = "设置(S)") {
                    Item(text = "默认设置(S)", onClick = {})
                    Item(text = "会话设置(S)", onClick = {})
                }
                Separator()
                Item(text = "退出(X)", onClick = {})
            }
            Menu(text = "编辑(E)", mnemonic = 'E') {
                Item(text = "复制(C)", onClick = {})
                Item(text = "黏贴(V)", onClick = {})
            }
        }
        App(appState)
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
