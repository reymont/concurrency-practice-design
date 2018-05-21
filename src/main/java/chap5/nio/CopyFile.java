package chap5.nio;

import java.io.*;
import java.nio.*;  
import java.nio.channels.*;

/**
 * https://blog.csdn.net/qq_18860653/article/details/53406723
 */
public class CopyFile {  
    static public void main(String args[]) throws Exception {  
  
        String infile = "c://test/nio_copy.txt";  
        String outfile = "c://test/result.txt";  
  
        FileInputStream fin = new FileInputStream(infile);  
        FileOutputStream fout = new FileOutputStream(outfile);  
        // 获取读的通道  
        FileChannel fcin = fin.getChannel();  
        // 获取写的通道  
        FileChannel fcout = fout.getChannel();  
        // 定义缓冲区，并指定大小  
        ByteBuffer buffer = ByteBuffer.allocate(1024);  
  
        while (true) {  
            // 清空缓冲区  
            buffer.clear();  
            //从通道读取一个数据到缓冲区  
            int r = fcin.read(buffer);  
            //判断是否有从通道读到数据  
            if (r == -1) {  
                break;  
            }  
            //将buffer指针指向头部  
            buffer.flip();  
            //把缓冲区数据写入通道  
            fcout.write(buffer);  
        }  
    }  
}  