
package chap4.work;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadAddTest {
    // 设置最大值
    private static int M = 1000000;

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("命令格式为 java ThreadAddTest 1000");
        }
        System.out.println("当线程数量为 ： " + args[0] + " 时：");
        atomicMethod(Integer.valueOf(args[0]));
        synMethod(Integer.valueOf(args[0]));
    }

    private static void atomicMethod(int N) throws InterruptedException {
        AtomicRun atomicRun = new AtomicRun();
        AtomicRun.endValue = M;

        ExecutorService service = Executors.newFixedThreadPool(N);
        // 开始时间
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            service.submit(atomicRun);
        }
        service.shutdown();
        service.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
        // 结束时间
        long endTime = System.currentTimeMillis();
        System.out.println("无锁线程数量为 ： " + N + " 开始时间为 ： " + startTime
                + " 结束时间为 ： " + endTime + " 耗费时间为 ： " + (endTime - startTime)
                + "ms" + " value:" + AtomicRun.atomicInteger);
    }

    private static void synMethod(int N) throws InterruptedException {
        SynRun synRun = new SynRun();
        SynRun.endValue = M;

        ExecutorService service = Executors.newFixedThreadPool(N);
        long starttime = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            service.submit(synRun);
        }
        service.shutdown();
        service.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        System.out.println("有锁线程数量为 ： " + N + " 开始时间为 ： " + starttime
                + " 结束时间为 ： " + endTime + " 耗费时间为 ： " + (endTime - starttime)
                + "ms" + " value:" + SynRun.startValue);
    }


}

class AtomicRun implements Runnable {

    protected static AtomicInteger atomicInteger = new AtomicInteger();
    protected static int endValue;

    @Override
    public void run() {
        int startValue = atomicInteger.get();
        while (startValue < endValue) {
            if(atomicInteger.compareAndSet(startValue, startValue + 1)){
                startValue++;
            }else{
                break;
            }
        }
    }
}

class SynRun implements Runnable {

    protected static int startValue;
    protected static int endValue;

    @Override
    public void run() {
        while (startValue < endValue) {
            addValue();
        }
    }

    private synchronized void addValue() {
        if (startValue < endValue)
            startValue++;
    }
}