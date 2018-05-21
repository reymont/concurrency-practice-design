package chap5.nio5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Created by ljy on 2017/9/6.
 * https://github.com/ljyss9/Java-NIO/blob/master/src/Client.java
 */
public class Client {

    private static final Logger log = Logger.getLogger(Client.class.getName());
    private InetSocketAddress inetSocketAddress;

    public Client(String hostname, int port) {
        inetSocketAddress = new InetSocketAddress(hostname, port);
    }

    public void send(String requestData){
        try{
            SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
            socketChannel.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
            socketChannel.write(ByteBuffer.wrap(requestData.getBytes()));
            while(true){
                byteBuffer.clear();
                int readBytes = socketChannel.read(byteBuffer);
                if(readBytes > 0){
                    byteBuffer.flip();
                    log.info("Client: readBytes = " + readBytes);
                    log.info("Client: data = " + new String(byteBuffer.array(), 0, readBytes));
                    //socketChannel.close();
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        String requestData = "Actions speak louder than words!";
        int port = 1000;
        new Client(hostname, port).send(requestData);
    }
}