package chap4;

import org.junit.Test;
import org.junit.Before;  
import org.junit.After;

import java.util.concurrent.CountDownLatch;  
import java.util.concurrent.atomic.AtomicInteger;  
  
import static org.junit.Assert.fail;  
  
/** 
 * LockFreeStack Tester. 
 * 
 * @author <Authors name> 
 * @version 1.0 
 * @since <pre>һ�� 14, 2014</pre> 
 */  
public class LockFreeStackTest {  
  
    private LockFreeStack<Integer> stack = new LockFreeStack<Integer>();  
    private CountDownLatch start;  
    private CountDownLatch end;  
  
    static class Poper extends Thread {  
        private LockFreeStack<Integer> stack;  
        CountDownLatch start;  
        CountDownLatch end;  
        AtomicInteger count;  
  
        public Poper(LockFreeStack<Integer> stack, AtomicInteger count,  
                     CountDownLatch start, CountDownLatch end) {  
            this.start = start;  
            this.end = end;  
            this.count = count;  
            this.stack = stack;  
        }  
  
        @Override  
        public void run() {  
            try {  
                start.await();  
            } catch (InterruptedException e) {  
            }  
  
            while (stack.pop() != null) {  
                count.getAndIncrement();  
            }  
            end.countDown();  
        }  
    }  
  
    static class Pusher extends Thread {  
        private LockFreeStack<Integer> stack;  
        private int nProduct;  
        private CountDownLatch start;  
        private CountDownLatch end;  
  
        public Pusher(LockFreeStack<Integer> stack, int n,  
                      CountDownLatch start, CountDownLatch end) {  
            this.stack = stack;  
            this.nProduct = n;  
            this.start = start;  
            this.end = end;  
        }  
  
        @Override  
        public void run() {  
            try {  
                start.await();  
            } catch (InterruptedException e) {  
            }  
  
            for (int i = 0; i < nProduct; i++) {  
                stack.push(i);  
            }  
            end.countDown();  
        }  
    }  
  
    @Before  
    public void before() throws Exception {  
    }  
  
    @After  
    public void after() throws Exception {  
    }  
  
    /** 
     * Method: pop() 
     */  
    @Test  
    public void testPop() throws Exception {  
        AtomicInteger count = new AtomicInteger(0);  
        final int testTimes = 10000;  
        final int stackSize = 10000;  
        final int nThread = 10;  
  
        for (int i = 0; i < testTimes; i++) {  
            //init the stack  
            int j = stack.size();  
            while (j < stackSize) {  
                stack.push(j++);  
            }  
            start = new CountDownLatch(1);  
            end = new CountDownLatch(nThread);  
            count.set(0);  
            for(int t = 0; t < nThread ; t ++){  
                new Poper(stack,count,start,end).start();  
            }  
            start.countDown();  
            end.await();  
  
            if(stackSize != count.get()){  
                fail("times : " + i +"  stackSize = " + stackSize +"  pop count " + count.get());  
            }  
        }  
    }  
  
    /** 
     * Method: push(V value) 
     */  
    @Test  
    public void testPush() throws Exception {  
        final int nThread = 20;  
        final int testTime = 10000;  
        final int nProducePerThread = 100;  
  
        for(int i = 0; i < testTime ; i++){  
            start = new CountDownLatch(1);  
            end = new CountDownLatch(nThread);  
            while(stack.pop() != null);         //clear ths stack  
  
            for(int t = 0 ; t < nThread ; t++){  
                new Pusher(stack,nProducePerThread,start,end).start();  
            }  
  
            start.countDown();  
            end.await();  
  
            if(stack.size() != nProducePerThread * nThread){  
                fail("stack.size = " + stack.size() + " should be " + nProducePerThread * nThread);  
            }  
        }  
    }  
  
    @Test
    public void testPopPush() throws Exception {  
        final int testTimes = 10000;  
        final int nPoper = 20;  
        final int nPusher = 20;  
        final int nProduct = 100;  
        AtomicInteger count = new AtomicInteger(0);  
  
        for(int i = 0 ; i < testTimes ; i++){  
            count.set(0);  
            while (stack.pop() != null);          //clear the stack  
            start = new CountDownLatch(1);  
            end = new CountDownLatch(nPoper + nPusher);  
  
            for(int t = 0 ; t < nPusher ; t ++){  
                new Pusher(stack,nProduct,start,end).start();  
            }  
  
            for(int t = 0 ; t < nPoper ; t ++){  
                new Poper(stack,count,start,end).start();  
            }  
            start.countDown();  
            end.await();  
  
            if(count.get() + stack.size() != nProduct * nPusher){  
                fail("times " + i + " count " + count.get() +" stack.size " + stack.size() +" total should be " + nProduct * nPusher);  
            }  
        }  
    }  
  
  
}   