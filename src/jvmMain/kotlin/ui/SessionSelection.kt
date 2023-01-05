package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import config.Session
import platform.pointerMoveFilter

@Composable
fun SessionSelection(sessions: List<Session>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        sessions.forEach {
            it.host?.let { it1 ->
                Session(it1)
            }
        }
    }
}

@Composable
fun Session(name: String) {
    var active by remember { mutableStateOf(false) }

    Row() {
        SessionIcon(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = name,
            color = if (active) LocalContentColor.current.copy(alpha = 0.60f) else LocalContentColor.current,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically).pointerMoveFilter(
                    onEnter = {
                        active = true
                        true
                    },
                    onExit = {
                        active = false
                        true
                    }
                )
        )
    }
}