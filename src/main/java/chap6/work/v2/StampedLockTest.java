package chap6.work.v2;

import static java.lang.Thread.sleep;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by MaXuewen on 2018/1/31.
 *
 * 1、非重入损
 */
public class StampedLockTest {

  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Map<String, String> map = new HashMap<>();
    StampedLock stampedLock = new StampedLock();

    executor.submit(() -> {
      long l = stampedLock.writeLock();
      try {
        map.put("foo", "bar");
        sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        stampedLock.unlockWrite(l);
      }
    });

    //悲观读
    Runnable readTask = () -> {
      long stamp = stampedLock.readLock();
      try {
        System.out.println(map.get("foo"));
        sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        stampedLock.unlockRead(stamp);
      }
    };

    //乐观读
    Runnable OpReadTask = () -> {
      long l = stampedLock.tryOptimisticRead();
      System.out.println(map.get("foo"));
      try {
        sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      //判断乐观读是否成功
      if (! stampedLock.validate(l)) {
        //乐观读失败，这里可以做悲观读
      }
    };

    executor.submit(readTask);
    executor.submit(readTask);
    executor.submit(OpReadTask);
    executor.shutdown();
  }

}