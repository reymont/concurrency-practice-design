package chap3.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Fork(1)
public class MillionByIncrementBenchmark {

    public static final int MILLION = 1_000_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MillionByIncrementBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @State(Scope.Benchmark)
    public static class CounterState {
        public long counter = 0;
        public long sum = 0;

        public AtomicLong atomicCounter = new AtomicLong(0);
    }

    @Benchmark
    public void timeOfIncrementBySingleThread(CounterState state, Blackhole bh) {
        while (state.counter < MILLION) {
            state.counter += 1;
            state.sum += state.counter;
        }
        bh.consume(state.sum);
    }

    @Benchmark
    public void timeOfIncrementByMultipleThreadsWithExtends(CounterState state, Blackhole bh) throws InterruptedException {
        Thread[] threads = new Thread[4];
        Object lock = new Object();

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (true) {
                        synchronized (lock) {
                            // Condition check must be synchronized
                            if (state.counter < MILLION) {
                                state.counter += 1;
                                state.sum += state.counter;
                            } else {
                                break;
                            }
                        }
                    }
                }
            };
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        bh.consume(state.sum);
    }

    @Benchmark
    public void timeOfIncrementByMultipleThreadsWithRunnable(CounterState state, Blackhole bh) throws InterruptedException {
        Thread[] threads = new Thread[4];
        Object lock = new Object();

        Runnable runnable = () -> {
            while (true) {
                synchronized (lock) {
                    // Condition check must be synchronized
                    if (state.counter < MILLION) {
                        state.counter += 1;
                        state.sum += state.counter;
                    } else {
                        break;
                    }
                }
            }
        };

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(runnable);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        bh.consume(state.sum);
    }

    @Benchmark
    public void timeOfIncrementWithReentrantLock(CounterState state, Blackhole bh) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Lock lock = new ReentrantLock();

        Runnable task = () -> {
            while (true) {
                lock.lock();
                // Condition check must be synchronized
                if (state.counter < MILLION) {
                    state.counter += 1;
                    state.sum += state.counter;
                } else {
                    lock.unlock();
                    break;
                }
                lock.unlock();
            }
        };

        for (int i = 0; i < 4; ++i) {
            executor.submit(task);
        }

        executor.shutdown();

        bh.consume(state.sum);
    }

    @Benchmark
    public void timeOfIncrementWithReadWriteLock(CounterState state, Blackhole bh) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ReadWriteLock lock = new ReentrantReadWriteLock();

        Runnable task = () -> {
            while (state.counter < MILLION) {
                lock.writeLock().lock();
                state.counter += 1;
                lock.writeLock().unlock();
            }
        };

        for (int i = 0; i < 4; ++i) {
            executor.submit(task);
        }

        executor.shutdown();

        bh.consume(state.counter);
    }

    @Benchmark
    public void timeOfIncrementWithAtomicLong(CounterState state, Blackhole bh) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Runnable task = () -> {
            // !!! Impossible to make atomic compareAndIncrement with AtomicLong only. Result will exceed MILLION
            while (state.atomicCounter.get() < MILLION) {
                state.atomicCounter.incrementAndGet();
            }
        };

        for (int i = 0; i < 4; ++i) {
            executor.submit(task);
        }

        executor.shutdown();

        bh.consume(state.counter);
    }
}