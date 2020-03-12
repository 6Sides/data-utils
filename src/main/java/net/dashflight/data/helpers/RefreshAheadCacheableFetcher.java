package net.dashflight.data.helpers;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import net.dashflight.data.helpers.Computable.DataFetchException;

/**
 * Implementation of the refresh-ahead caching strategy.
 *
 * By default, lazily loads on first fetch and then auto-refreshes value based on
 * cache ttl and refresh-ahead factor.
 */
public abstract class RefreshAheadCacheableFetcher<K, V> extends CacheableFetcher<K, V> {

    private static final float REFRESH_AHEAD_FACTOR = 0.75f;

    private static final PriorityBlockingQueue<RefreshCacheTask> taskQueue = new PriorityBlockingQueue<>(1024);


    private static final Thread refreshThread;

    // Configures and starts the refresh monitor thread
    static {
        refreshThread = new Thread(new TaskScheduler());
        refreshThread.setDaemon(true);
        refreshThread.start();
    }


    /**
     * Adds the key,value pair to the refresh queue and then caches the current result value.
     *
     * @param key The key to store the result at.
     * @param result The object to serialize and store.
     */
    @Override
    protected void cacheResult(K key, CacheableResult<V> result) {
        long timeSinceLastUpdate = Instant.now().getEpochSecond() - result.getLastUpdated().toEpochSecond();

        // Time (in seconds) until the result value should be refreshed
        long delay = (long) ((result.getTTL() - timeSinceLastUpdate) * REFRESH_AHEAD_FACTOR);

        taskQueue.offer(
                RefreshCacheTask.of(
                        Instant.now().plusSeconds(delay),
                        () -> {
                            try {
                                this.cacheResult(key, this.fetchResult(key));
                            } catch (DataFetchException e) {
                                e.printStackTrace();
                            }
                        }
                )
        );

        super.cacheResult(key, result);
    }

    /**
     * Runnable to monitor and schedule refresh jobs.
     */
    private static final class TaskScheduler implements Runnable {

        @Override
        public void run() {
            while (true) {

                Set<RefreshCacheTaskDefinition> tasks = new HashSet<>();
                Instant now = Instant.now();

                // Add all tasks that have a refresh time less than the current time to the task set
                while (taskQueue.size() > 0 && taskQueue.peek().getRefreshTime().compareTo(now) <= 0) {
                    try {
                        RefreshCacheTask task = taskQueue.take();
                        tasks.add(task.getTask());
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                tasks.forEach(RefreshCacheTaskDefinition::execute);


                try {
                    Thread.sleep(5_000L);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static final class RefreshCacheTask implements Comparable<RefreshCacheTask> {
        private Instant refreshTime;
        private RefreshCacheTaskDefinition task;

        public Instant getRefreshTime() {
            return refreshTime;
        }

        public RefreshCacheTaskDefinition getTask() {
            return task;
        }

        private RefreshCacheTask(Instant refreshTime, RefreshCacheTaskDefinition task) {
            this.refreshTime = refreshTime;
            this.task = task;
        }

        static RefreshCacheTask of(Instant refreshTime, RefreshCacheTaskDefinition task) {
            return new RefreshCacheTask(refreshTime, task);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RefreshCacheTask that = (RefreshCacheTask) o;
            return refreshTime.equals(that.refreshTime) &&
                    task.equals(that.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(refreshTime, task);
        }

        @Override
        public int compareTo(RefreshCacheTask o) {
            return this.refreshTime.compareTo(o.refreshTime);
        }
    }

    private interface RefreshCacheTaskDefinition {

        void execute();

    }
}
