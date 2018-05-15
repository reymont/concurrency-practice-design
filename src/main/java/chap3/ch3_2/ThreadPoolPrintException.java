package chap3.ch3_2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

public class ThreadPoolPrintException {
    private static Log log = LogFactory.getLog(ThreadPoolPrintException.class);

    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(0,Integer.MAX_VALUE,0L,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>()){

            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                printException(r, t);
            }
        };

        for (int i = 0; i < 5; i++) {
            pool.execute(new DivTask(100,i));
        }
    }

    private static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone())
                    future.get();
            } catch (CancellationException ce) {
                t.printStackTrace();
            } catch (ExecutionException ee) {
                t.printStackTrace();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null){}
            log.error(t.getMessage(), t);
    }
}
