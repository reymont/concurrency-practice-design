package chap4;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * https://blog.csdn.net/whf584201314/article/details/78961749
 * 实现一个无锁的Stack，并写一段测试代码（多线程访问），证明这个Stack是线程安全的
 */
public class LockFreeStackDemo {
    //data
    static private AtomicInteger j = new AtomicInteger(0);
    //控制插入的指针
    static private AtomicInteger index = new AtomicInteger(0);

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
        // 栈满或栈空不进行入栈操作
        if (i < MAXLENGTH && i >= 0) {
            // 入栈前，栈指针index加1
            if (index.compareAndSet(i, i + 1)) {
                int data = j.incrementAndGet();
                // 如果当前状态值等于预期值，则以原子方式将同步状态设置为给定的更新值
                // 如果当前状态值等于null，即该位置没有值，才设定新值
                if (arr[i].compareAndSet(null, data))
                    System.out.println(Thread.currentThread().getName() + "\t入栈:" + i + "\t\t数据:" + data);
                else {
                    System.out.println(Thread.currentThread().getName() + "\t入栈:" + i + "\t\t数据:" + data + "\t失败");
                    readd(data);
                }
            }
        }
    }

    //处理插入失败的数据
    public static void readd(Integer data) {
        boolean flag = true;
        // 重复插入数据，确保该数据能够插入
        while (flag) {
            Integer i = get();
            if (i < MAXLENGTH && i >= 0) {
                if (index.compareAndSet(i, i + 1)) {
                    if (arr[i].compareAndSet(null, data)) {
                        flag = false;
                        System.out.println(Thread.currentThread().getName() + "\t重入栈:" + i + "\t数据:" + data);
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
                    if (arr[index].compareAndSet(arr[index].get(), null)){
                        System.out.println(Thread.currentThread().getName() + "\t出栈:" + index + "\t\t元素:" + o);
                    }else{
                        System.out.println(Thread.currentThread().getName() + "\t出栈:" + index + "\t\t元素:" + o +"\t失败");
                    }

                }
            }
        }
    }

    public static void main(String[] args) throws Exception {

        // 1个入栈线程
        for (int i = 0; i < 1; i++) {
            new Thread("add" + i) {
                @Override
                public void run() {
                    while (true) {
                        add();
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        // 3个出站线程
        for (int i = 0; i < 3; i++) {
            new Thread("remove" + i) {
                @Override
                public void run() {
                    while (true) {
                        remove();
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }
}

//        add0	入栈:0		数据:97
//        remove2	出栈:0		元素:97
//        remove2	出栈:0		元素:98
//        add0	入栈:0		数据:98
//        add0	入栈:0		数据:99
//        add0	入栈:0		数据:100	失败
//        add0	重入栈:1	数据:100
//        remove2	出栈:1		元素:100
//        remove1	出栈:0		元素:99
//        add0	入栈:0		数据:101
//        remove2	出栈:0		元素:101
//        add0	入栈:0		数据:102


