package chap7.ch7_5;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * Created by hjy on 18-2-24.
 */
public class ExceptionMain {

    public static void customStrategy(ActorSystem system){
        ActorRef a = system.actorOf(Props.create(Supervisor.class),"Supervisor");
        // 对Supervisor发送一个RestartActor的Props，使得Supervisor创建RestartActor
        a.tell(Props.create(RestartActor.class),ActorRef.noSender());

        ActorSelection sel = system.actorSelection("akka://lifecycle/user/Supervisor/restartActor");

        for (int i = 0; i < 100; i++) {
            // 向RestartActor发送100条RESTART消息
            sel.tell(RestartActor.Msg.RESTART,ActorRef.noSender());
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("lifecycle", ConfigFactory.load("lifecycle.conf"));
        customStrategy(system);
    }


}
