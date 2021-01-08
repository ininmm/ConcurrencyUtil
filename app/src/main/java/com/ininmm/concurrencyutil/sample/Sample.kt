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

package com.ininmm.concurrencyutil.sample

import com.ininmm.concurrencyutil.ConcurrencyRunner
import com.ininmm.concurrencyutil.SingleRunner
import kotlinx.coroutines.*

/**
 * Created by Michael.Lien
 * on 2021/1/8
 */
class Sample {

    suspend fun loadFoo() {
        delay(2000)
        println("Foo")
    }

    suspend fun loadBar() {
        delay(2000)
        println("Bar")
    }
}

fun main() {
    val sample = Sample()
    val singleRunner = SingleRunner()
    val concurrencyRunner = ConcurrencyRunner<Unit>()
    GlobalScope.launch {
        println("Show SampleRunner")
        coroutineScope {
            launch { singleRunner.afterPrevious { sample.loadFoo() } }
            launch { singleRunner.afterPrevious { sample.loadBar() } }
        }
    }
    GlobalScope.launch {
        delay(5000)
        println("Show ConcurrencyRunner.cancelPreviousThenRun")
        coroutineScope {
            launch { concurrencyRunner.cancelPreviousThenRun { sample.loadFoo() } }
            launch {
                delay(500)
                concurrencyRunner.cancelPreviousThenRun { sample.loadBar() }
            }
        }
    }
    GlobalScope.launch {
        delay(10000)
        println("Show ConcurrencyRunner.joinPreviousOrRun")
        coroutineScope {
            launch { concurrencyRunner.joinPreviousOrRun { sample.loadFoo() } }
            launch {
                delay(500)
                concurrencyRunner.joinPreviousOrRun { sample.loadBar() }
            }
        }
    }
    runBlocking {
        delay(15000L)  // ... while we delay for 2 seconds to keep JVM alive
    }
}