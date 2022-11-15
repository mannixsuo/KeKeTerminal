package parser

class EscHandler {
    data class EscCommand(val first: Char, val second: Char?, val final: Char) {
        fun key(): Int {
            return generateKey(first, second, final)
        }
    }
}


fun generateKey(first: Char, second: Char?, final: Char): Int {
    return (final.code shl 16) or (first.code shl 8) or (second?.code ?: 0)
}