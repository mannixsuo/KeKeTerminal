package buffer

import java.util.concurrent.locks.ReentrantLock


class CircularList<T : Any> {
    private val lock = ReentrantLock()
    private var bufferSize = 4096
    private lateinit var buffer: Array<T>
    private var currentReadIndex = 0
    private var currentWriteIndex = 0
    private var currentMarkerIndex = 0

    constructor()

    constructor(size: Int) {
        this.bufferSize = size
    }


}