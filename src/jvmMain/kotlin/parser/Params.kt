package parser

class Params {

    private var params: Array<String> = Array(10) { "0" }
    private var length: Int = 0
    private var buffer = StringBuffer()

    fun reset() {
        this.params = emptyArray()
        this.length = 0
    }

    fun put(code: Int) {
        if (code == 0x3b) {
            params[length] = buffer.toString()
            length++
            buffer = StringBuffer()
        } else {
            buffer.append(code.toChar())
        }
    }

    fun get(index: Int): Int {
        if (index > length) {
            return 0
        }
        return params[index].toInt()
    }
}