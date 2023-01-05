// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import config.Session
import config.readConfigFromFile
import org.slf4j.LoggerFactory
import shell.JschShell
import terminal.Terminal
import terminal.TerminalConfig
import ui.*

class CoCoTerminalAppState(val terminals: Terminals, val splitterState: SplitterState) {
    var expandedSize by mutableStateOf(300.dp)
    val expandedSizeMin = 90.dp
    var isExpanded by mutableStateOf(true)
    val collapsedSize = 24.dp

}

val sessions = readConfigFromFile().sessions

@Composable
fun rememberCocoTerminalAppState(terminals: Terminals): CoCoTerminalAppState = remember(terminals) {
    CoCoTerminalAppState(terminals, SplitterState())
}

val terminalConfig = TerminalConfig()

//
//val localShell: Shell = LocalPty(
//    PtyProcessBuilder(arrayOf("/bin/bash")).setInitialColumns(terminalConfig.columns)
//        .setInitialRows(terminalConfig.rows).start()
//)
//val localShell1: Shell = LocalPty(
//    PtyProcessBuilder(arrayOf("/bin/bash")).setInitialColumns(terminalConfig.columns).setEnvironment(buildMap {
//        put("TERM", "xterm")
//    }).setInitialRows(terminalConfig.rows).start()
//)


val jschShell = JschShell("192.168.130.134", 38322, "app", "wingtech")

val terminal = Terminal(jschShell, terminalConfig)
//val terminal1 = Terminal(localShell1, terminalConfig)

@Composable
@Preview
fun App(appState: CoCoTerminalAppState) {
    val animatedSize = if (appState.splitterState.isResizing) {
        if (appState.isExpanded) appState.expandedSize else appState.collapsedSize
    } else {
        animateDpAsState(
            if (appState.isExpanded) appState.expandedSize else appState.collapsedSize,
            SpringSpec(stiffness = StiffnessLow)
        ).value
    }

    VerticalSplittable(
        Modifier.fillMaxSize(),
        appState.splitterState,
        onResize = {
            appState.expandedSize = (appState.expandedSize + it).coerceAtLeast(appState.expandedSizeMin)
        }
    ) {
        ResizablePanel(
            modifier = Modifier.width(animatedSize)
                .onPreviewKeyEvent {
                    true
                }.fillMaxHeight(), appState
        ) {
            Row {
                SessionSelection(sessions)
            }
        }
        TerminalViews(appState)
    }

}

@Composable
fun ResizablePanel(
    modifier: Modifier, state: CoCoTerminalAppState, content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(if (state.isExpanded) 1f else 0f, SpringSpec(stiffness = StiffnessLow))
    Box(modifier) {
        Box(Modifier.fillMaxSize().graphicsLayer(alpha = alpha)) {
            content()
        }
        Icon(
            if (state.isExpanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
            contentDescription = if (state.isExpanded) "Collapse" else "Expand",
            tint = LocalContentColor.current,
            modifier = Modifier
                .padding(top = 4.dp)
                .width(24.dp)
                .focusable(false)
                .clickable {
                    state.isExpanded = !state.isExpanded
                }
                .padding(4.dp)
                .align(Alignment.TopEnd)
        )
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

fun main() = singleWindowApplication(
    title = "CoCoTerminal",
    onKeyEvent = { key ->
        terminals.activeTerminal?.onKeyEvent(key)
        true
    },
    state = WindowState(width = 1280.dp, height = 768.dp),
    icon = BitmapPainter(useResource("icon/terminal.png", ::loadImageBitmap)),
) {
    val appState = rememberCocoTerminalAppState(terminals)

    var newSessionModalOpen by remember { mutableStateOf(false) }
    terminals.addNewTerminal(terminal)
    terminal.start()
    MaterialTheme(
        colors = AppTheme.colors.material
    ) {
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
        Surface {
            App(appState)
        }
        AddSessionModal(newSessionModalOpen) { newSessionModalOpen = false }
    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
