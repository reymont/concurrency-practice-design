package chap7.ch7_4;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * Created by hjy on 18-2-24.
 */
public class DeadMain {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("deadwatch", ConfigFactory.load("samplehello.conf"));
        ActorRef worker = system.actorOf(Props.create(MyWorker.class),"worker");
        // Props.create()方法，第1个参数为要创建的Actor类型，第2个参数为这个Actor的构造函数的参数
        system.actorOf(Props.create(WatchActor.class,worker),"watcher");
        worker.tell(MyWorker.Msg.WORKING,ActorRef.noSender());
        worker.tell(MyWorker.Msg.DONE,ActorRef.noSender());
        // 直接毒死接收方，让其终止
        worker.tell(PoisonPill.getInstance(),ActorRef.noSender());



    }

}
