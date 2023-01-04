// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.singleWindowApplication
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.pty4j.PtyProcessBuilder
import config.Session
import config.readConfigFromFile
import org.slf4j.LoggerFactory
import shell.JschShell
import shell.LocalPty
import shell.Shell
import terminal.Terminal
import terminal.TerminalConfig
import ui.TerminalViews
import ui.Terminals

class CoCoTerminalAppState(val terminals: Terminals) {}

val sessions = readConfigFromFile().sessions

@Composable
fun rememberCocoTerminalAppState(terminals: Terminals): CoCoTerminalAppState = remember(terminals) {
    CoCoTerminalAppState(terminals)
}

val terminalConfig = TerminalConfig()

//
//val localShell: Shell = LocalPty(
//    PtyProcessBuilder(arrayOf("powershell.exe")).setInitialColumns(terminalConfig.columns)
//        .setInitialRows(terminalConfig.rows).start()
//)
val localShell1: Shell = LocalPty(
    PtyProcessBuilder(arrayOf("powershell.exe")).setInitialColumns(terminalConfig.columns)
        .setInitialRows(terminalConfig.rows).start()
)


val jschShell = JschShell("192.168.130.134", 38322, "app", "wingtech")

val terminal = Terminal(jschShell, terminalConfig)
val terminal1 = Terminal(localShell1, terminalConfig)

@Composable
@Preview
fun App(appState: CoCoTerminalAppState) {

    Row {
        TerminalViews(appState)
        SessionSelection(sessions)
    }

}

@Composable
@Preview
fun SessionSelection(sessions: List<Session>) {
    Column {
        sessions.forEach {
            it.host?.let { it1 -> Text(it1) }
        }
    }
}

@Composable
fun AddSessionModal(open: Boolean, onCloseRequest: () -> Unit) {
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf<Number>(22) }
    var tag by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    if (open) {
        Dialog(onCloseRequest = onCloseRequest, title = "New Session") {
            Column {
                Column {
                    TextField(label = { Text("Host") }, value = host, onValueChange = { host = it })
                    TextField(label = { Text("Port") }, value = port.toString(), onValueChange = { port = it.toInt() })
                    TextField(label = { Text("User") }, value = user, onValueChange = { user = it })
                    TextField(label = { Text("Password") }, value = password, onValueChange = { password = it })
                }
                Row {
                    Button(onClick = {}) {
                        Text("Confirm")
                    }
                    Button(onClick = onCloseRequest) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

val terminals = Terminals()

fun main() = singleWindowApplication(title = "CoCoTerminal", onKeyEvent = { key ->
    terminals.activeTerminal?.onKeyEvent(key) != null
}) {
    val appState = rememberCocoTerminalAppState(terminals)

    var newSessionModalOpen by remember { mutableStateOf(false) }
    terminals.addNewTerminal(terminal)
    terminal.start()
    MaterialTheme {
        MenuBar {
            Menu(text = "Sessions(F)", mnemonic = 'F') {
                Item(text = "New(N)", onClick = { newSessionModalOpen = true })
                Item(text = "Open(O)", onClick = {})
                Separator()
                Item(text = "Logs(L)", onClick = {})
                Separator()
                Menu(text = "Setting(S)") {
                    Item(text = "Default(S)", onClick = {})
                    Item(text = "New Session(S)", onClick = {})
                }
                Separator()
                Item(text = "Exit(X)", onClick = {})
            }
            Menu(text = "Edit(E)", mnemonic = 'E') {
                Item(text = "Copy(C)", onClick = {})
                Item(text = "Past(V)", onClick = {})
            }
        }
        App(appState)
        AddSessionModal(newSessionModalOpen) { newSessionModalOpen = false }
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
