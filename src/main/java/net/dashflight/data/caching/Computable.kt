package net.dashflight.data.caching

/**
 * Computes a result based on an input
 * @param <K> The input type
 * @param <V> The result type
</V></K> */
interface Computable<K, V> {

    @Throws(DataFetchException::class, InterruptedException::class)
    fun compute(key: K): V

    /**
     * Thrown when fetching a result fails.
     */
    class DataFetchException(message: String) : Exception(message)
}