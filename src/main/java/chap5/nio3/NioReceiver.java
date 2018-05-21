package chap5.nio3;

import java.net.InetSocketAddress;
import java.net.ServerSocket;  
import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.ServerSocketChannel;  
import java.nio.channels.SocketChannel;  
import java.util.Iterator;  
import java.util.Set;

/**
 * https://blog.csdn.net/qq_18860653/article/details/53406723
 * http://www.importnew.com/19816.html
 */
public class NioReceiver {  
    @SuppressWarnings("null")  
    public static void main(String[] args) throws Exception {
        // 创建了一个8个byte的数组的缓冲区
        ByteBuffer echoBuffer = ByteBuffer.allocate(8);
        // 要使用选择器（Selector），需要创建一个Selector实例（使用静态工厂方法open()）
        // 并将其注册（register）到想要监控的信道上
        // （注意，这要通过channel的方法实现，而不是使用selector的方法）。
        ServerSocketChannel ssc = ServerSocketChannel.open();  
        Selector selector = Selector.open();
        // 与Selector一起使用时，Channel必须处于非阻塞模式下
        ssc.configureBlocking(false);  
        ServerSocket ss = ssc.socket();  
        InetSocketAddress address = new InetSocketAddress(8080);  
        ss.bind(address);
        // 为了将Channel和Selector配合使用，必须将Channel注册到Selector上
        SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);  
        System.out.println("开始监听……");  
        while (true) {
            // 调用选择器的select()方法。该方法会阻塞等待，直到有一个或更多的信道准备好了I/O操作或等待超时。
            // select()方法将返回可进行I/O操作的信道数量
            int num = selector.select();  
            Set selectedKeys = selector.selectedKeys();  
            Iterator it = selectedKeys.iterator();  
            while (it.hasNext()) {  
                SelectionKey sKey = (SelectionKey) it.next();  
                SocketChannel channel = null;  
                if (sKey.isAcceptable()) {  
                    ServerSocketChannel sc = (ServerSocketChannel) key.channel();
                    // 监听新进来的连接
                    channel = sc.accept();// 接受连接请求
                    // ServerSocketChannel可以设置成非阻塞模式。
                    // 在非阻塞模式下，accept() 方法会立刻返回，如果还没有新进来的连接,返回的将是null
                    channel.configureBlocking(false);  
                    channel.register(selector, SelectionKey.OP_READ);
                    // 注意每次迭代末尾的keyIterator.remove()调用。
                    // Selector不会自己从已选择键集中移除SelectionKey实例。
                    // 必须在处理完通道时自己移除。下次该通道变成就绪时，Selector会再次将其放入已选择键集中。
                    it.remove();  
                } else if (sKey.isReadable()) {
                    // SelectionKey.channel()方法返回的通道需要转型成你要处理的类型，
                    // 如ServerSocketChannel或SocketChannel等。
                    channel = (SocketChannel) sKey.channel();  
                    while (true) {
                        // 调用clear()方法：position将被设回0，limit设置成capacity，
                        // 换句话说，Buffer被清空了，其实Buffer中的数据并未被清除，
                        // 只是这些标记告诉我们可以从哪里开始往Buffer里写数据。
                        // 如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，
                        // 意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。
                        echoBuffer.clear();
                        // 写入数据到Buffer
                        int r = channel.read(echoBuffer);  
                        if (r <= 0) {  
                            channel.close();  
                            System.out.println("接收完毕，断开连接");  
                            break;  
                        }  
                        System.out.println("##" + r + " " + new String(echoBuffer.array(), 0, echoBuffer.position()));
                        // position设回0，并将limit设成之前的position的值
                        echoBuffer.flip();  
                    }  
                    it.remove();  
                } else {  
                    channel.close();  
                }  
            }  
        }  
  
    }  
  
}  