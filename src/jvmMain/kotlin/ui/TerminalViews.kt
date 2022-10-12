package ui

import CoCoTerminal
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TerminalViews(model: CoCoTerminal) {
    Box() {
        Column {
            TerminalTablesView(model.terminals)
            Box(Modifier.weight(1f)) {
                TerminalView(model.terminals.terminals[model.terminals.active])
            }
        }
    }
}