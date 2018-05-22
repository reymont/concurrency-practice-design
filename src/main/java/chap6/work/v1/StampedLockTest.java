package chap6.work.v1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * https://github.com/iklyubanov/java-personal-workbench/blob/master/java-modern-concurrency/src/main/java/ru/klyubanov/java_modern_concurrency/ch2/recipe16/Main.java
 * https://www.cnblogs.com/dennyzhangdd/p/6925473.html
 * https://blog.csdn.net/zero__007/article/details/55805789
 */
public class StampedLockTest {

    public static void main(String[] args) {
        Position position = new Position();
        StampedLock stampedLock = new StampedLock();

        Writer writer = new Writer(position, stampedLock);
        Reader reader1 = new Reader(position, stampedLock,1);
        Reader reader2 = new Reader(position, stampedLock,2);
        Reader reader3 = new Reader(position, stampedLock,3);
        Reader reader4 = new Reader(position, stampedLock,4);
        Reader reader5 = new Reader(position, stampedLock,5);
        OptimisticReader optimisticReader1 = new OptimisticReader(position, stampedLock,1);
        OptimisticReader optimisticReader2 = new OptimisticReader(position, stampedLock,2);
        OptimisticReader optimisticReader3 = new OptimisticReader(position, stampedLock,3);
        OptimisticReader optimisticReader4 = new OptimisticReader(position, stampedLock,4);
        OptimisticReader optimisticReader5 = new OptimisticReader(position, stampedLock,5);

        writer.start();
        reader1.start();
        reader2.start();
        reader3.start();
        reader4.start();
        reader5.start();
        optimisticReader1.start();
        optimisticReader2.start();
        optimisticReader3.start();
        optimisticReader4.start();
        optimisticReader5.start();

        try {
            writer.join();
            reader1.join();
            reader2.join();
            reader3.join();
            reader4.join();
            reader5.join();
            optimisticReader1.join();
            optimisticReader2.join();
            optimisticReader3.join();
            optimisticReader4.join();
            optimisticReader5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 乐观读
 */
class OptimisticReader extends Thread {
    private final Position position;
    private final StampedLock stampedLock;

    public OptimisticReader(Position position, StampedLock stampedLock,int i) {
        this.position = position;
        this.stampedLock = stampedLock;
        this.setName("OptmisticReader"+i);
    }

    @Override
    public void run() {
        long stamp;
        for (int i = 0; i < 100; i++) {
            try {
                /**
                             * tryOptimisticRead是一个乐观的读，使用这种锁的读不阻塞写
                             * 每次读的时候得到一个当前的stamp值（类似时间戳的作用）
                             */
                stamp = stampedLock.tryOptimisticRead();
                int x = position.getX();
                int y = position.getY();
                if (stampedLock.validate(stamp)) {
                    System.out.printf("%s: %d - (%d,%d)\n",this.getName(),
                            stamp, x, y);
                } else {
                    System.out.printf("%s: %d - Not Free\n",this.getName(),
                            stamp);
                }
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Position {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

/**
 * 悲观读
 */
class Reader extends Thread {
    private final Position position;
    private final StampedLock stampedLock;

    public Reader(Position position, StampedLock stampedLock, int i) {
        this.position = position;
        this.stampedLock = stampedLock;
        this.setName("Reader"+i);
    }

    @Override
    public void run() {

        for (int i = 0; i < 50; i++) {
            long stamp = stampedLock.readLock();

            try {
                System.out.printf("%s: %d - (%d,%d)\n", this.getName(), stamp,
                        position.getX(), position.getY());
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                stampedLock.unlockRead(stamp);
                System.out.printf("%s: %d - Lock released\n", this.getName(),stamp);
            }
        }
    }
}

/**
 * 排它写
 */
class Writer extends Thread {
    private final Position position;
    private final StampedLock stampedLock;

    public Writer(Position position, StampedLock stampedLock) {
        this.position = position;
        this.stampedLock = stampedLock;
    }

    @Override
    public void run() {

        for (int i = 0; i < 10; i++) {
            long stamp = stampedLock.writeLock();

            try {
                System.out.printf("Writer: Lock acquired %d\n", stamp);
                position.setX(position.getX() + 1);
                position.setY(position.getY() + 1);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                stampedLock.unlockWrite(stamp);
                System.out.printf("Writer: Lock released %d\n", stamp);
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}