package chap2.ch2_8;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by hjy on 18-1-11.
 */
public class ArrayListMultiThread {

    static List<Integer> al = new ArrayList<Integer>(10);
    static List<Integer> a2 = new Vector<Integer>(10);

    public static class ListAddThread implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                al.add(i);
            }
        }
    }

    public static class VectorAddThread implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                a2.add(i);
            }
        }
    }


    public static void main(String[] args) throws Exception{
        Thread t1 = new Thread(new ListAddThread());
        Thread t2 = new Thread(new ListAddThread());
        t1.start();
        t2.start();
        t1.join(); t2.join();
        System.out.println(al.size());
        Thread t3 = new Thread(new VectorAddThread());
        Thread t4 = new Thread(new VectorAddThread());
        t3.start();
        t4.start();
        t3.join(); t4.join();
        System.out.println(a2.size());
    }



}
