package net.dashflight.data.helpers;

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