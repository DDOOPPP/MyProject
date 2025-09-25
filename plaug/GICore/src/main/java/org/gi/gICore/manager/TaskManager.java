package org.gi.gICore.manager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.gi.gICore.model.stat.TaskStats;
import org.gi.gICore.util.ModuleLogger;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class TaskManager {
    private final JavaPlugin plugin;
    private final ModuleLogger logger;
    private final ExecutorService executor;
    private final ScheduledExecutorService schedulerExecutor;
    private final ConcurrentHashMap<String, BukkitTask> namedTasks;

    public TaskManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = new ModuleLogger(null,"TaskManager");

        this.executor = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r,"GI Async - " + System.currentTimeMillis());
            thread.setDaemon(true);
            return thread;
        });

        this.schedulerExecutor = Executors.newScheduledThreadPool(4, r -> {
            Thread thread = new Thread(r,"GI Scheduler - " + System.currentTimeMillis());
            thread.setDaemon(true);
            return thread;
        });

        this.namedTasks = new ConcurrentHashMap<>();
    }

    /**
     * 동기 작업 메서드 (Main Thread)
     */
    public BukkitTask runSync(Runnable task) {
        return plugin.getServer().getScheduler().runTask(plugin, task);
    }
    /**
     * 동기 작업 지연 실행
     */
    public BukkitTask runSyncLater(Runnable task, long delayTicks) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, task, delayTicks);
    }
    /**
     * 동기 작업 반복 실행
     */
    public BukkitTask runSyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }
    /**
     * 비동기 작업 실행
     */
    public CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }
    /**
     * 비동기 작업 실행 (결과 반환)
     */
    public <T> CompletableFuture<T> runAsync(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    /**
     * 비동기 작업 지연 실행
     */
    public CompletableFuture<Void> runAsyncLater (Runnable task, long delayTicks) {
        return CompletableFuture.runAsync(() -> {
            try{
                Thread.sleep(delayTicks * 1000);
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        },executor);
    }

    /**
     * 스케줄된 작업 실행
     */
    public ScheduledFuture<?> schedule (Runnable task, long delayTicks, TimeUnit unit) {
        return schedulerExecutor.schedule(task, delayTicks, unit);
    }

    /**
     * 스케줄된 작업 반복 실행
     */
    public ScheduledFuture<?> scheduleAtFixedRate (Runnable task, long initialDelay, long period, TimeUnit unit) {
        return schedulerExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }
    /**
     * 명명된 작업 실행 (중복 실행 방지)
     */
    public boolean runNamedTask(String taskName, Runnable task, long delayTicks, long periodTicks) {
        if (namedTasks.containsKey(taskName)) {
            return false; // 이미 실행 중
        }

        BukkitTask bukkitTask = runSyncTimer(new NamedTaskWrapper(taskName, task), delayTicks, periodTicks);
        namedTasks.put(taskName, bukkitTask);
        return true;
    }
    /**
     * 명명된 작업 취소
     */
    public boolean cancelNamedTask(String taskName) {
        BukkitTask bukkitTask = namedTasks.remove(taskName);
        if (bukkitTask != null) {
            bukkitTask.cancel();
            return true;
        }
        return false;
    }

    /**
     * 모든 명명된 작업 취소
     */
    public void cancelAllNamedTasks() {
        namedTasks.values().forEach(BukkitTask::cancel);
        namedTasks.clear();
    }

    /**
     * 비동기에서 동기로 작업 전환
     */
    public <T> CompletableFuture<T> asyncToSync (Callable<T> asyncTask) {
        CompletableFuture<T> future = new CompletableFuture<>();

        runAsync(() -> {
            try {
                T result = asyncTask.call();
                runSync(() -> future.complete(result));
            } catch (Exception e) {
                runSync(() -> future.completeExceptionally(e));
            }
        });
        return future;
    }

    /**
     * 동기에서 비동기로 작업 전환
     */
    public <T> CompletableFuture<T> syncToAsync(Callable<T> syncTask) {
        CompletableFuture<T> future = new CompletableFuture<>();

        runSync(() -> {
            try {
                T result = syncTask.call();
                runAsync(() -> future.complete(result));
            } catch (Exception e) {
                runAsync(() -> future.completeExceptionally(e));
            }
        });

        return future;
    }

    /**
     * 배치 작업 실행
     */
    public <T> CompletableFuture<Void> runBatch(Iterable<T> items, Consumer<T> processor, int batchSize) {
        return CompletableFuture.runAsync(() -> {
            int count = 0;
            for (T item : items) {
                processor.accept(item);
                count++;

                if (count % batchSize == 0) {
                    try{
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        },executor);
    }
    /**
     * 타임아웃과 함께 작업 실행
     */
    public <T> CompletableFuture<T> runWithTimeout(Callable<T> task, long timeout, TimeUnit unit) {
        CompletableFuture<T> future = new CompletableFuture<>();
        CompletableFuture<T> timeoutFuture = new CompletableFuture<>();

        schedulerExecutor.schedule(() -> {
            if (!future.isDone()) {
                timeoutFuture.completeExceptionally(new TimeoutException("Task Timed Out"));
            }
        },timeout, unit);

        return CompletableFuture.anyOf(future,timeoutFuture).thenApply(result -> (T) result);
    }

    public TaskStats getTaskStats() {
        return TaskStats.builder()
                .activeNamedTasks(namedTasks.size())
                .asyncExecutorActive(((ThreadPoolExecutor) executor).getActiveCount())
                .asyncExecutorQueued(((ThreadPoolExecutor) executor).getQueue().size())
                .scheduledExecutorActive(((ScheduledThreadPoolExecutor) schedulerExecutor).getActiveCount())
                .scheduledExecutorQueued(((ScheduledThreadPoolExecutor) schedulerExecutor).getQueue().size())
                .build();
    }

    /**
     * 리소스 정리
     */
    public void shutdown() {
        cancelAllNamedTasks();

        executor.shutdown();
        schedulerExecutor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            if (!schedulerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                schedulerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            schedulerExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        logger.info("TaskManager shutdown completed");
    }

    private class NamedTaskWrapper implements Runnable {
        private final String taskName;
        private final Runnable task;

        public NamedTaskWrapper(String taskName, Runnable task) {
            this.taskName = taskName;
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Error in named task '%s': %s", taskName, e.getMessage());
                // 에러 발생 시 작업 제거
                namedTasks.remove(taskName);
                throw e; // BukkitScheduler가 작업을 취소하도록
            }
        }
    }
}
