package terminal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

/**
 * 字符最小单元
 */
interface ICell {
    var char: Char
    val bg: Color
    val fg: Color
    val bold: Boolean
    val italic: Boolean
}

class Cell(override var char: Char,
           override val bg: Color,
           override val fg: Color,
           override val bold: Boolean,
           override val italic: Boolean) : ICell

/**
 * TODO do we need this?
 * Cells
 * 字符最小单元的集合
 * 每个cells中的cell共享 样式
 */
interface ICells {
    var cells: Array<ICell>
    var length: Int
    val bg: Color
    val fg: Color
    val fontFamily: FontFamily
    val bold: Boolean
    val italic: Boolean
}

class Cells(
    override var cells: Array<ICell>,
    override val bg: Color,
    override val fg: Color,
    override val fontFamily: FontFamily,
    override val bold: Boolean,
    override val italic: Boolean
) : ICells {
    override var length: Int = cells.size
}