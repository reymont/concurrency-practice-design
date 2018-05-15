package chap3.ch3_2;

import java.util.concurrent.*;

/**
 * Created by hjy on 18-1-17.
 */
public class DivTask2 implements Runnable{
    int a,b;

    public DivTask2(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        double re = a/b;
        System.out.println(re);
    }

    public static void main(String[] args) throws InterruptedException,ExecutionException{

        ThreadPoolExecutor pool = new ThreadPoolExecutor(0,Integer.MAX_VALUE,0L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());

        for (int i = 0; i < 5; i++) {
            Future future = pool.submit(new DivTask2(100,i));
            future.get();
        }
    }
}
