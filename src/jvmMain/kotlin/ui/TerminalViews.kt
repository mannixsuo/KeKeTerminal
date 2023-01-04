package ui

import CoCoTerminalAppState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TerminalViews(model: CoCoTerminalAppState) {

    LaunchedEffect(true) {
        model.terminals.initialized = true
        model.terminals.activeTerminal?.onLineChange?.invoke()
    }

    Box {
        Column {
            TerminalTablesView(model.terminals)
            Box(Modifier.weight(1f).background(Color.Black)) {
                TerminalView(
                    model.terminals.activeTerminalScreen.cursorX,
                    model.terminals.activeTerminalScreen.cursorY,
                    model.terminals.activeTerminalScreen.screenLines,
                )
            }
        }
    }
}