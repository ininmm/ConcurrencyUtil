package com.ininmm.concurrencyutil

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by Michael.Lien
 * on 2021/1/8
 */
class SingleRunner {
    private val mutex = Mutex()

    suspend fun <T> afterPrevious(block: suspend () -> T): T {
        mutex.withLock { return block() }
    }
}