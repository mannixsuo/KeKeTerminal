package parser

class Params {

    private var params: Array<Int> = Array(10) { 0 }
    private var length: Int = 0
    private var buffer = StringBuffer()

    fun reset() {
        this.params = Array(10) { 0 }
        this.length = 0
    }

    fun put(code: Int) {
        if (code == 0x3b) {
            length++
        } else {
            params[length] = (params[length] * 10) + (code - 0x30)
        }
    }

    fun get(index: Int): Int {
        if (index > length) {
            return 0
        }
        return params[index]
    }

    fun toIntArray(): Array<Int> {
        if (length == 0) {
            return emptyArray()
        }
        return params.sliceArray(IntRange(0, length))
    }

    fun toParamString(): String {
        val s = StringBuilder()
        var index = 0
        params.sliceArray(IntRange(0, length)).forEach {
            s.append(it)
            if (index != length) {
                s.append(";")
            }
            index++
        }

        return s.toString()
    }
}