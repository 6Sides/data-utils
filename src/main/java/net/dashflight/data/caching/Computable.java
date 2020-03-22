package net.dashflight.data.caching;

/**
 * Computes a result based on an input
 * @param <K> The input type
 * @param <V> The result type
 */
public interface Computable<K, V> {

    V compute(K key) throws DataFetchException, InterruptedException;


    /**
     * Thrown when fetching a result fails.
     */
    class DataFetchException extends Exception {

        public DataFetchException(String message) {
            super(message);
        }

    }
}