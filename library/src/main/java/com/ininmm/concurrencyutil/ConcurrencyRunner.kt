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

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by Michael.Lien
 * on 2021/1/8
 */
class ConcurrencyRunner<T> {

    private val activeTask = AtomicReference<Deferred<T>?>(null)

    suspend fun cancelPreviousThenRun(block: suspend () -> T): T {
        activeTask.get()?.cancelAndJoin()

        return coroutineScope {
            val newTask = async(start = CoroutineStart.LAZY) { block() }

            newTask.invokeOnCompletion { activeTask.compareAndSet(newTask, null) }

            val result: T

            while (true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    activeTask.get()?.cancelAndJoin()
                    yield()
                } else {
                    result = newTask.await()
                    break
                }
            }

            result
        }
    }

    suspend fun joinPreviousOrRun(block: suspend () -> T): T {
        activeTask.get()?.let { return it.await() }

        return coroutineScope {
            val newTask = async(start = CoroutineStart.LAZY) { block() }

            newTask.invokeOnCompletion { activeTask.compareAndSet(newTask, null) }

            val result: T

            while (true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    val currentTask = activeTask.get()
                    if (currentTask != null) {
                        newTask.cancel()
                        result = currentTask.await()
                        break
                    } else {
                        yield()
                    }
                } else {
                    result = newTask.await()
                    break
                }
            }

            result
        }
    }
}