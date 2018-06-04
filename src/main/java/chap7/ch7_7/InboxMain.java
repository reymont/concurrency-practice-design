package chap7.ch7_7;

import akka.actor.*;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by hjy on 18-2-24.
 */
public class InboxMain {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("inboxmain", ConfigFactory.load("samplehello.conf"));

        ActorRef worker = system.actorOf(Props.create(MyWorker.class),"worker");

        // 根据ActorSystem构造了一个与之绑定的邮箱Inbox
        final Inbox inbox = Inbox.create(system);
        // 接着使用邮箱监视MyWorker
        inbox.watch(worker);
        inbox.send(worker,MyWorker.Msg.WORKING);
        inbox.send(worker,MyWorker.Msg.DONE);
        inbox.send(worker,MyWorker.Msg.CLOSE);

        while (true){
            Object msg = inbox.receive(Duration.create(1, TimeUnit.SECONDS));

            if (msg==MyWorker.Msg.CLOSE){
                System.out.println("My worker is Closing");
            }else if (msg instanceof Terminated){
                System.out.println("My worker is dead");
                system.shutdown();
                break;
            }else {
                System.out.println(msg);
            }

        }



    }



}
