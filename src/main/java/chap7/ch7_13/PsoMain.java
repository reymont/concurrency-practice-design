package chap7.ch7_13;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * Created by hjy on 18-2-26.
 */
public class PsoMain {

    public static final int BIRD_COUNT = 100000;

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("psoSystem", ConfigFactory.load("samplehello.conf"));
        system.actorOf(Props.create(MasterBird.class),"masterbird");
        for (int i = 0; i < BIRD_COUNT; i++) {
            system.actorOf(Props.create(Bird.class),"bird_"+i);
        }
    }




}
