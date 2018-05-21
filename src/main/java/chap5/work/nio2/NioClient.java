package chap5.work.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * https://github.com/xuhuawei131/NioSocket
 */
public class NioClient {
    private SocketChannel client;
    private InetSocketAddress serverAddress;
    private Selector selector;

    private ByteBuffer receiverBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer senderBuffer = ByteBuffer.allocate(1024);

    public NioClient() {
        serverAddress = new InetSocketAddress("localhost", MyConstants.port);
        try {
            client = SocketChannel.open();
            client.configureBlocking(false);
            client.connect(serverAddress);

            selector = Selector.open();
            client.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void session() throws IOException {
        //先要判断是否已经建立连接
        if (client.isConnectionPending()) {
            client.finishConnect();
            System.out.println("请在控制台输入计算公式，例如：1+2");
            client.register(selector, SelectionKey.OP_WRITE);
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String expression = scanner.nextLine();
            if (expression.equals("")) {
                continue;
            }
            process(expression);
        }
    }

    private void process(String expression) throws IOException {
        boolean unFinish = true;
        while (unFinish) {
            int value = selector.select();
            if (value == 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                //来一个 处理一个
                SelectionKey key = iterator.next();
                if (key.isWritable()) {
                    senderBuffer.clear();
                    senderBuffer.put(expression.getBytes());
                    senderBuffer.flip();

                    client.write(senderBuffer);
                    client.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    unFinish = false;
                    receiverBuffer.clear();
                    int length = client.read(receiverBuffer);
                    if (length > 0) {
                        receiverBuffer.flip();
                        System.out.println("客户端收到结果: " + new String(receiverBuffer.array(),0,length));
                        client.register(selector, SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        new NioClient().session();
    }
}