package chap5.work.aio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Random;

/**
 * https://www.cnblogs.com/niejunlei/p/5980478.html
 * AsynchronousSocketChannel
 */
public class AIOClient implements Runnable {

    private AsynchronousSocketChannel client;
    private String host;
    private int port;

    public AIOClient(String host, int port) throws IOException {
        this.client = AsynchronousSocketChannel.open();
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            new Thread(new AIOClient("127.0.0.1", 8989)).start();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        client.connect(new InetSocketAddress(host, port), null, new CompletionHandler<Void, Object>() {
            public void completed(Void result, Object attachment) {
                char operators[] = {'+', '-', '*', '/'};
                Random random = new Random(System.currentTimeMillis());
                String expression = random.nextInt(10) + "" + operators[random.nextInt(4)] + (random.nextInt(10) + 1);
                client.write(ByteBuffer.wrap(expression.getBytes()));
                System.out.println("客户端发送消息：" + expression);
            }

            public void failed(Throwable exc, Object attachment) {
                System.out.println("client send field...");
            }
        });

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        client.read(byteBuffer, this, new CompletionHandler<Integer, Object>() {
            public void completed(Integer result, Object attachment) {
                // System.out.println(result);
                System.out.println("客户端收到结果: " + new String(byteBuffer.array()));
            }

            public void failed(Throwable exc, Object attachment) {
                System.out.println("read faield");
            }
        });
    }
}