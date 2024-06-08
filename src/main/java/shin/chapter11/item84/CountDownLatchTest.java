package shin.chapter11.item84;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService e = Executors.newFixedThreadPool(1000);

        System.out.println("CountDownLatch :" + time(e, 1000, () -> System.out.print("")));
        System.out.println("SlowCountDownLatch :" + time2(e, 1000, () -> System.out.print("")));
        e.shutdown();
    }

    // CountDownLatch 시간 측정
    public static long time(Executor executor, int concurrency, Runnable action)
            throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(concurrency);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(concurrency);
        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown(); // Tell timer we're ready
                try {
                    start.await(); // Wait till peers are ready
                    action.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown(); // Tell timer we're done
                }
            });
        }
        ready.await(); // Wait for all workers to be ready
        long startNanos = System.nanoTime();
        start.countDown(); // And they're off!
        done.await(); // Wait for all workers to finish
        return System.nanoTime() - startNanos;

    }

    // SlowCountDownLatch 시간 측정
    public static long time2(Executor executor, int concurrency, Runnable action)
            throws InterruptedException {
        SlowCountDownLatch ready = new SlowCountDownLatch(concurrency);
        SlowCountDownLatch start = new SlowCountDownLatch(1);
        SlowCountDownLatch done = new SlowCountDownLatch(concurrency);
        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown(); // Tell timer we're ready
                try {
                    start.await(); // Wait till peers are ready
                    action.run();
                } finally {
                    done.countDown(); // Tell timer we're done
                }
            });
        }
        ready.await(); // Wait for all workers to be ready
        long startNanos = System.nanoTime();
        start.countDown(); // And they're off!
        done.await(); // Wait for all workers to finish
        return System.nanoTime() - startNanos;
    }
}
