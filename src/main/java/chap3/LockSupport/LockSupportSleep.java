package chap3.LockSupport;

import java.util.concurrent.locks.LockSupport;

/**
 * https://www.cnblogs.com/qingquanzi/p/8228422.html
 */
public class LockSupportSleep {

    public static void main(String[] args)throws Exception {
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for(int i=0;i<10;i++){
                    sum+=i;
                }
                // 直接调用就可以了，没有说非得在同步代码块里才能用
                LockSupport.park();
                System.out.println(sum);
            }
        });
        A.start();
        //睡眠一秒钟，保证线程A已经计算完成，阻塞在wait方法
        Thread.sleep(1000);
        LockSupport.unpark(A);
    }
}