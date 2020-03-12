package net.dashflight.data.helpers;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Prevents many concurrent calls from independently fetching the same data.
 *
 * If computation is not running, start it and wrap in a Future for other callers.
 * If computation is running, caller will wait for computation to finish and collect result.
 */
public class Memoizer<A, V> implements Computable<A, V> {

    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();

    private final Computable<A, V> computeFunction;

    public Memoizer(Computable<A, V> computeFunction) {
        this.computeFunction = computeFunction;
    }


    public V compute(final A arg) throws InterruptedException {
        while (true) {

            Future<V> future = cache.get(arg);

            // Computation not started
            if (future == null) {
                Callable<V> eval = () -> computeFunction.compute(arg);

                FutureTask<V> futureTask = new FutureTask<>(eval);
                future = cache.putIfAbsent(arg, futureTask);

                // Start computation if it's not started in the meantime
                if (future == null) {
                    future = futureTask;
                    futureTask.run();
                }
            }

            // Get result if ready, otherwise block and wait
            try {
                return future.get();
            } catch (CancellationException e) {
                cache.remove(arg, future);
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getCause());
            }
        }
    }
}