package net.dashflight.data.helpers.caching;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dashflight.data.helpers.caching.Computable.DataFetchException;
import net.dashflight.data.redis.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Implementation of the refresh-ahead caching strategy.
 */
public abstract class RefreshAheadCachedFetcher<K, V> extends CachedFetcher<K, V> {

    // Should be in (0, 1). The higher the value, the longer the value waits to be refreshed.
    private static final float REFRESH_AHEAD_FACTOR = 0.5f;

    private static final JedisPool redisPool = RedisFactory.withDefaults().getPool();
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();


    @Override
    protected final CacheableResult<V> memoizedGet(K input) throws DataFetchException {
        boolean needsRefresh;
        try (Jedis client = redisPool.getResource()) {
            needsRefresh = !client.exists(this.generateHash(input) + "rac");
        }

        if (needsRefresh) {
            // Refetch result and cache it asynchronously
            threadPool.submit(() -> {
                try {
                    CacheableResult<V> result = fetchResult(input);
                    this.cacheResult(input, result);

                    redis.setWithExpiry(this.generateHash(input) + "rac", (int) (result.getTTL() * REFRESH_AHEAD_FACTOR), "");

                } catch (DataFetchException e) {
                    e.printStackTrace();
                }
            });
        }


        CacheableResult<V> result = super.getValueFromCache(input);

        if (result == null) {
            // Refetch result and cache it
            result = this.fetchResult(input);
            this.cacheResult(input, result);
        }

        return result;
    }
}
