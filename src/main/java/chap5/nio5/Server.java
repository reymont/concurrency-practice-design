package chap5.nio5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by ljy on 2017/9/6.
 */
public class Server extends Thread{
    private static final Logger log = Logger.getLogger(Server.class.getName());
    private InetSocketAddress inetSocketAddress;
    private ServerHandler serverHandler = new ServerHandler();

    public Server(String hostName,int port){
        inetSocketAddress = new InetSocketAddress(hostName,port);
    }

    public void run(){
        try{
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(inetSocketAddress);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.info("Server :socket server started");
            while(true){
                int nKeys = selector.select();
                if(nKeys > 0 ){
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeySet.iterator();
                    while(it.hasNext()){
                        SelectionKey key = it.next();
                        if(key.isAcceptable()){
                            log.info("Server : is acceptable");
                            serverHandler.handleAccept(key);
                        } else if(key.isReadable()) {
                            log.info("Server: SelectionKey is readable.");
                            serverHandler.handleRead(key);
                        } else if(key.isWritable()) {
                            log.info("Server: SelectionKey is writable.");
                            serverHandler.handleWriter(key);
                    }
                    it.remove();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static  void main(String args[]){
        String hostname = "localhost";
        int port = 1000;
        Server s = new Server(hostname,port);
        s.start();
    }
}