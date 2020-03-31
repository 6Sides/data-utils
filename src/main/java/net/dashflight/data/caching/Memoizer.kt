package net.dashflight.data.caching

import java.util.concurrent.*

/**
 * Prevents many concurrent calls from independently fetching the same data.
 *
 * If computation is not running, begin and wrap result in a Future for other callers.
 * If computation is running, caller will wait for computation to finish and collect result.
 */
class Memoizer<K, V>(private val computeFunction: Computable<K, V>) : Computable<K, V?> {
    private val cache: ConcurrentMap<K, Future<V?>?> = ConcurrentHashMap()

    @Throws(InterruptedException::class)
    override fun compute(key: K): V? {
        while (true) {
            var startedComputation = false
            var future = cache[key]

            // Computation not started
            if (future == null) {
                val eval = Callable { computeFunction.compute(key) }
                val futureTask = FutureTask(eval)
                future = cache.putIfAbsent(key, futureTask)

                // Start computation if it's not started in the meantime
                if (future == null) {
                    future = futureTask
                    futureTask.run()
                    startedComputation = true
                }
            }

            // Get result if ready, otherwise block and wait
            try {
                val result = future.get()

                // Remove future from map to prevent caching value forever
                if (startedComputation) {
                    cache.remove(key)
                }
                return result
            } catch (e: CancellationException) {
                cache.remove(key, future)
            } catch (e: ExecutionException) {
                e.printStackTrace()
                throw RuntimeException(e.cause)
            }
        }
    }

}