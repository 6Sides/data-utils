package net.dashflight.data.helpers;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


public class Memoizer<A, V> implements Computable<A, V> {

    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();

    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        while (true) {

            Future<V> f = cache.get(arg);
            // computation not started
            if (f == null) {
                System.out.println("Computation not started");
                Callable<V> eval = () -> c.compute(arg);

                FutureTask<V> ft = new FutureTask<>(eval);
                f = cache.putIfAbsent(arg, ft);
                // start computation if it's not started in the meantime
                if (f == null) {
                    System.out.println("Computation beginning...");

                    f = ft;
                    ft.run();
                }
            } else {
                System.out.println("Computation already started!");
            }

            // get result if ready, otherwise block and wait
            try {
                System.out.println("Waiting for result...");
                return f.get();
            } catch (CancellationException e) {
                cache.remove(arg, f);
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getCause());
            }
        }
    }
}