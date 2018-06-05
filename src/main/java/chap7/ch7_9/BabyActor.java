package chap7.ch7_9;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

/**
 * Created by hjy on 18-2-24.
 */
public class BabyActor extends UntypedActor{

    private final LoggingAdapter log = Logging.getLogger(getContext().system(),this);

    public static enum Msg{
        SLEEP,PLAY,CLOSE;
    }

    // Procedure在这里可以表示一种Actor的状态，同时封装了在这种状态下的消息处理逻辑
    Procedure<Object> angry = new Procedure<Object>() {
        @Override
        public void apply(Object msg) throws Exception {
            System.out.println("angryApply:"+msg);
            if (msg==Msg.SLEEP){
                getSender().tell("I am already angry",getSelf());
                System.out.println("I am already angry");
            }else if (msg == Msg.PLAY){
                System.out.println("I like playing");
                getContext().become(happy);
            }

        }
    };

    Procedure<Object> happy = new Procedure<Object>() {
        @Override
        public void apply(Object msg) throws Exception {
            System.out.println("happyApply:"+msg);
            if (msg==Msg.PLAY){
                getSender().tell("I am already happy :-)",getSelf());
                System.out.println("I am already happy :-)");
            }else if (msg == Msg.SLEEP){
                System.out.println("I don't want to sleep");
                getContext().become(angry);
            }

        }
    };


    @Override
    public void onReceive(Object msg) throws Exception {
        System.out.println("onReceive:"+msg);
        if (msg==Msg.SLEEP){
            // 使用become()方法用于切换Actor的状态
            getContext().become(angry);
        }else if (msg == Msg.PLAY){
            getContext().become(happy);
        }else {
            unhandled(msg);
        }
    }
}
