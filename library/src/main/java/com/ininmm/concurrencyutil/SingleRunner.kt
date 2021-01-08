/*
 * Copyright (c) 2021.  Michael Lien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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