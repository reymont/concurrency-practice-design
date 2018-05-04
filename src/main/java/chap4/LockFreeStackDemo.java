package chap4;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 无锁栈
 * https://blog.csdn.net/whf584201314/article/details/78961749
 * 实现一个无锁的Stack，并写一段测试代码（多线程访问），证明这个Stack是线程安全的
 */
public class LockFreeStackDemo {
    //data
    static private AtomicInteger j = new AtomicInteger(0);
    //控制插入的指针
    static private AtomicInteger index = new AtomicInteger(0);
    static AtomicIntegerArray array = new AtomicIntegerArray(10);

    //存放集合
    @SuppressWarnings("unchecked")
    static private AtomicReference<Object>[] arr = new AtomicReference[]{
            new AtomicReference<Object>(), new AtomicReference<Object>(), new AtomicReference<Object>(),
            new AtomicReference<Object>(), new AtomicReference<Object>(), new AtomicReference<Object>(),
            new AtomicReference<Object>(), new AtomicReference<Object>(), new AtomicReference<Object>(),
            new AtomicReference<Object>()};

    static final private int MAXLENGTH = 10;

    public static Integer get() {
        return index.get();
    }

    //入栈
    public static void add() {
        Integer i = get();
        if (i < MAXLENGTH && i >= 0) {
            if (index.compareAndSet(i, i + 1)) {
                int data = j.incrementAndGet();
                if (arr[i].compareAndSet(null, data))
                    System.out.println("入队:" + i + "\t\t数据：" + data);
                else {
                    System.out.println("插入数据失败:" + data);
                    readd(data);
                }
            }
        }
    }

    //处理插入失败的数据
    public static void readd(Integer data) {
        boolean flag = true;
        while (flag) {
            Integer i = get();
            if (i < MAXLENGTH && i >= 0) {
                if (index.compareAndSet(i, i + 1)) {
                    if (arr[i].compareAndSet(null, data)) {
                        flag = false;
                        System.out.println("重入队:" + i + "\t数据:" + data);
                    }
                }
            }
        }
    }

    //出栈
    public static void remove() {
        Integer i = get();
        if (i > 0) {
            if (index.compareAndSet(i, i - 1)) {
                int index = i - 1;
                if (arr[index] != null && arr[index].get() != null) {
                    Object o = arr[index].get();
                    if (arr[index].compareAndSet(arr[index].get(), null))
                        System.out.println("出队:" + index + "\t\t元素:" + o);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 1; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        add();
                    }
                }
            }.start();
        }
        for (int i = 0; i < 3; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        remove();
                    }
                }
            }.start();
        }
    }

}
