package chap3.LockSupport;

import java.util.concurrent.locks.LockSupport;

/**
 * https://www.cnblogs.com/qingquanzi/p/8228422.html
 */
public class LockSupportNotSleep {

    public static void main(String[] args)throws Exception {
        final Object obj = new Object();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for(int i=0;i<10;i++){
                    sum+=i;
                }
                LockSupport.park();
                System.out.println(sum);
            }
        });
        A.start();
        //睡眠一秒钟，保证线程A已经计算完成，阻塞在wait方法
        //Thread.sleep(1000);
        // unpark函数可以先于park调用，所以不需要担心线程间的执行的先后顺序
        LockSupport.unpark(A);
    }
}