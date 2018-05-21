package chap5.nio5;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by ljy on 2017/9/6.
 */
public interface Handler {

    void handleAccept(SelectionKey key) throws IOException;
    void handleRead(SelectionKey key) throws IOException;
    void handleWriter(SelectionKey key) throws  IOException;

}