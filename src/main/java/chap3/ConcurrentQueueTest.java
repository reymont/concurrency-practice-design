package chap3;

import java.util.concurrent.*;

abstract class Test {
    protected String id;
    protected CyclicBarrier barrier;
    protected long count;
    protected int threadNum;
    protected ExecutorService executor;

    public Test(String id, CyclicBarrier barrier, long count, int threadNum,
                ExecutorService executor) {
        this.id = id;
        this.barrier = barrier;
        this.count = count;
        this.threadNum = threadNum;
        this.executor = executor;
    }

    public long startTest() {

        long start = System.currentTimeMillis();
        for (int j = 0; j < threadNum; j++) {
            executor.execute(new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {
                        test();
                    }
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        // 所有线程执行完成之后，才会跑到这一步
        long duration = System.currentTimeMillis() - start;
        System.out.println(id + " = " + duration);
        return duration;
    }

    protected abstract void test();
}

public class ConcurrentQueueTest {
    private static int COUNT = 100000;
    private static int THREAD_NUM = 10;
    private static CyclicBarrierThread cyclicBarrierThread = new CyclicBarrierThread();

    private static ConcurrentLinkedQueue conQueue = new ConcurrentLinkedQueue();
    private static LinkedBlockingQueue linkQueue = new LinkedBlockingQueue();

    static class ConcurrentLinkedQueueProducer extends Test {

        public ConcurrentLinkedQueueProducer(String id, CyclicBarrier barrier, long count, int threadNum, ExecutorService executor) {
            super(id, barrier, count, threadNum, executor);
        }

        @Override
        protected void test() {
            conQueue.add(1);
        }
    }

    static class LinkedBlockingQueueProducer extends Test {

        public LinkedBlockingQueueProducer(String id, CyclicBarrier barrier, long count, int threadNum, ExecutorService executor) {
            super(id, barrier, count, threadNum, executor);
        }

        @Override
        protected void test() {
            linkQueue.add(1);
        }
    }

    static class CyclicBarrierThread extends Thread {
        @Override
        public void run() {
            conQueue.clear();
            linkQueue.clear();
        }
    }

    public static void test(String id, long count, int threadNum,
                            ExecutorService executor) {

        final CyclicBarrier barrier = new CyclicBarrier(threadNum + 1, cyclicBarrierThread);

        System.out.println("==============================");
        System.out.println("count = " + count + "\t" + "Thread Count = " + threadNum);

        concurrentTotalTime += new ConcurrentLinkedQueueProducer("ConcurrentLinkedQueueProducer" + id, barrier, COUNT, threadNum, executor).startTest();
        linkedBlockingTotalTime += new LinkedBlockingQueueProducer("LinkedBlockingQueueProducer" + id, barrier, COUNT, threadNum, executor).startTest();

        totalThreadCount += threadNum;
        executor.shutdownNow();

        System.out.println("==============================");
    }

    static long concurrentTotalTime = 0;
    static long linkedBlockingTotalTime = 0;

    static long totalThreadCount = 0;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i < 10; i++) {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM * i);
            test("-thread-" + i, COUNT, 10 * i, executor);
        }

        System.out.println("ConcurrentLinkedQueue Avg Time = " + concurrentTotalTime / totalThreadCount);

        System.out.println("LinkedBlockingQueue Avg Time = " + linkedBlockingTotalTime / totalThreadCount);

    }
} 