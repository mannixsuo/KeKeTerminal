package buffer

import java.awt.Color
import java.awt.Font

val defaultTheme = Theme()

class Theme {

    val colors = Colors()
    val fonts = Fonts()

    class Colors {

        val black = Color.BLACK
        val red = Color.RED
        val green = Color.GREEN
        val yellow = Color.YELLOW
        val blue = Color.BLUE
        val magenta = Color.MAGENTA
        val cyan = Color.CYAN
        val white = Color.WHITE
        val defaultForeground = black
        val defaultBackground = white

    }

    class Fonts {
        val size = 12
        val fonts = arrayOf(Font.getFont("consoles"))
    }
}