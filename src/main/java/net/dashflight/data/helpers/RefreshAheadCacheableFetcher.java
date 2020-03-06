package net.dashflight.data.helpers;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class RefreshAheadCacheableFetcher<K,V> extends CacheableFetcher<K,V> {

    private static final BlockingQueue<RefreshCacheTask> taskQueue = new ArrayBlockingQueue<>(1024);


    public RefreshAheadCacheableFetcher(String keyPrefix) {
        super(keyPrefix);

        this.initialize();

        Thread refreshThread = new Thread(new TaskScheduler());
        refreshThread.setDaemon(false);
        refreshThread.start();
    }


    /**
     * Used to push tasks to the scheduler to run on startup.
     */
    protected void initialize() {}


    @Override
    protected void cacheResult(K key, CacheableResult<V> result) {
        long offset = (long) ((result.getCacheTTL() - (Instant.now().getEpochSecond() - result.getLastUpdated().toEpochSecond()) ) * 0.75);

        taskQueue.offer(
                RefreshCacheTask.of(
                        Instant.now().plusSeconds(offset),
                        () -> {
                            try {
                                this.cacheResult(key, this.fetchResult(key));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                )
        );
        super.cacheResult(key, result);
    }


    private static final class TaskScheduler implements Runnable {

        @Override
        public void run() {
            while (true) {

                Set<RefreshCacheTaskDefinition> tasks = new HashSet<>();

                if (taskQueue.size() > 0) {
                    Instant now = Instant.now();
                    try {
                        RefreshCacheTask task;

                        do {
                            task = taskQueue.take();
                            tasks.add(task.getTask());
                        } while (taskQueue.size() > 0 && taskQueue.peek().getRefreshTime().compareTo(now) <= 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                tasks.forEach(RefreshCacheTaskDefinition::execute);

                try {
                    Thread.sleep(10_000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final class RefreshCacheTask {
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
    }

    private interface RefreshCacheTaskDefinition {

        void execute();

    }
}
