package net.dashflight.data.caching

/**
 * Thrown when a CachedFetcher fails to fetch a result
 */
class DataFetchException constructor(message: String) : Exception(message)