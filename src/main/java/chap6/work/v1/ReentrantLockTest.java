package chap6.work.v1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {

    public static void main(String[] args) {
        Position position = new Position();
        ReentrantLock lock = new ReentrantLock();

        ReWriter writer = new ReWriter(position, lock);
        ReReader reader = new ReReader(position, lock);

        writer.start();
        reader.start();

        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ReReader extends Thread {
    private final Position position;
    private final ReentrantLock lock;

    public ReReader(Position position,  ReentrantLock lock){
        this.position = position;
        this.lock = lock;
    }

    @Override
    public void run() {

        for (int i = 0; i < 50; i++) {
            lock.lock();
            try {
                System.out.printf("Reader: - (%d,%d)\n", position.getX(), position.getY());
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.printf("Reader: - Lock released\n");
            }
        }
    }
}

class ReWriter extends Thread {
    private final Position position;
    private final ReentrantLock lock;

    public ReWriter(Position position,  ReentrantLock lock){
        this.position = position;
        this.lock = lock;
    }

    @Override
    public void run() {

        for (int i = 0; i < 10; i++) {
            lock.lock();

            try {
                System.out.printf("Writer: Lock acquired \n");
                position.setX(position.getX()+1);
                position.setY(position.getY()+1);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.printf("Writer: Lock released \n");
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}