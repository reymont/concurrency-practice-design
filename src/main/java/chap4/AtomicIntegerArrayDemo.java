package chap4;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by hjy on 18-1-18.
 * https://blog.csdn.net/xinaij/article/details/50220265
 */
public class AtomicIntegerArrayDemo {

    // 申明了一个内含10个元素的数组
    static AtomicIntegerArray array = new AtomicIntegerArray(10);

    // 定义的线程对数组内10个元素进行累加操作，每个元素各加1000次
    public static class AddThread implements Runnable {
        @Override
        public void run() {
            for (int k = 0; k < 10000; k++) {
                array.getAndIncrement(k % array.length());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Thread[] ts = new Thread[10];
        // 开启10个这样的线程
        for (int k = 0; k < 10; k++) {
            ts[k] = new Thread(new AddThread());
        }
        for (int k = 0; k < 10; k++) {
            ts[k].start();
        }
        for (int k = 0; k < 10; k++) {
            ts[k].join();
        }
        System.out.println(array);
    }
}
// 如果线程安全，数组内10个元素的值必然都是10000。反之，如果线程不安全，则部分或者全部数值会小于10000
// 程序的输出结果如下
// [10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000,10000, 10000]
