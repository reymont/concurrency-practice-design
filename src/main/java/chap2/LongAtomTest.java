package chap2;

public class LongAtomTest implements Runnable {

    private static long field = 0;

    private volatile long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public LongAtomTest(long value) {
        this.setValue(value);
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 100000) {
            LongAtomTest.field = this.getValue();
            i++;
            long temp = LongAtomTest.field;
            if (temp != 1L && temp != -1L) {
                System.out.println("Iterate " + i + " is error and the result is "
                        + Long.toBinaryString(temp) + "("+temp+")" );
                System.exit(0);
            }
        }
        System.out.println("Iterate "+i+" success");
    }

    public static void main(String[] args) throws InterruptedException {
        // 获取并打印当前JVM是32位还是64位的
        String arch = System.getProperty("sun.arch.data.model");
        System.out.println(Long.toBinaryString(1));
        System.out.println(Long.toBinaryString(-1));
        System.out.println(arch+"-bit");
        LongAtomTest t1 = new LongAtomTest(1);
        LongAtomTest t2 = new LongAtomTest(-1);
        Thread T1 = new Thread(t1);
        Thread T2 = new Thread(t2);
        // t1,t2各自不停的给long类型的静态变量field赋值为1，-1；
        // t1,t2每次赋值后，会读取field的值，若field值既不是1又不是-1，就将field的值打印出来
        T1.start();
        T2.start();
        T1.join();
        T2.join();
    }

}