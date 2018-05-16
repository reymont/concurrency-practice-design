package chap2.piped;/*
管道流: 
PipedInputStream 
void connect(PipedOutputStream src)  
使此管道输入流连接到管道输出流 src 
 
PipedOutputStream 
void connect(PipedInputStream snk)   
 
在JDK我们看到PipedInputStream中有管道缓冲区,用来接收数据 
 
管道流内部在实现时还有大量的对同步数据的处理 
管道输出流和管道输入流执行时不能互相阻塞,所以一般要开启独立线程分别执行 
顺便复习了多线程操作 
 
[示例]:管道流 
*/  
import java.io.*;  
  
class PipedStreamDemo
{  
  public static void main(String[] args)  throws Exception  
  {  
    PipedInputStream pin = new PipedInputStream();  
    PipedOutputStream pout = new PipedOutputStream();  
    pin.connect(pout);  //输入流与输出流连接  
      
    ReadThread readTh   = new ReadThread(pin);  
    WriteThread writeTh = new WriteThread(pout);  
    new Thread(readTh).start();  
    new Thread(writeTh).start();  
  }  
    
  public static void sop(Object obj) //打印  
  {  
    System.out.println(obj);  
  }  
}  
  
class ReadThread implements Runnable  
{  
  private PipedInputStream pin;  
  ReadThread(PipedInputStream pin)   //  
  {  
    this.pin=pin;  
  }  
    
  public void run() //由于必须要覆盖run方法,所以这里不能抛,只能try  
  {  
    try  
    {  
      sop("R:读取前没有数据,阻塞中...等待数据传过来再输出到控制台...");  
      byte[] buf = new byte[1024];  
      int len = pin.read(buf);  //read阻塞  
      sop("R:读取数据成功,阻塞解除...");  
        
      String s= new String(buf,0,len);  
      sop(s);    //将读取的数据流用字符串以字符串打印出来  
      pin.close();       
    }  
    catch(Exception e)  
    {  
      throw new RuntimeException("R:管道读取流失败!");  
    }     
  }  
    
  public static void sop(Object obj) //打印  
  {  
    System.out.println(obj);  
  }  
}  
  
class WriteThread implements Runnable  
{  
  private PipedOutputStream pout;  
  WriteThread(PipedOutputStream pout)  
  {  
    this.pout=  pout;  
  }  
    
  public void run()  
  {  
    try  
    {  
      sop("W:开始将数据写入:但等个5秒让我们观察...");  
      Thread.sleep(5000);  //释放cpu执行权5秒  
      pout.write("W: writePiped 数据...".getBytes());  //管道输出流  
      pout.close();  
    }  
    catch(Exception e)  
    {  
      throw new RuntimeException("W:WriteThread写入失败...");  
    }  
  }  
    
  public static void sop(Object obj) //打印  
  {  
    System.out.println(obj);  
  }  
}  