package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.*

@Composable
fun SessionIcon(modifier: Modifier) = Box(modifier.size(24.dp).padding(4.dp)) {
    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF87939A))
}