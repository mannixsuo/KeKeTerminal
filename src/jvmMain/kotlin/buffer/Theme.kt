package buffer

import androidx.compose.ui.graphics.Color
import java.awt.Font

val defaultTheme = Theme()

class Theme {

    val colors = Colors()
    val fonts = Fonts()

    class Colors {

        val black = Color.Black
        val red = Color.Red
        val green = Color.Green
        val yellow = Color.Yellow
        val blue = Color.Blue
        val magenta = Color.Magenta
        val cyan = Color.Cyan
        val white = Color.White
        val defaultForeground = black
        val defaultBackground = white

    }

    class Fonts {
        val size = 12
        val fonts = arrayOf(Font.getFont("consoles"))
    }
}