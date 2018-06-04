package chap7.ch7_5;

import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.japi.Function;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by hjy on 18-2-24.
 */
public class Supervisor extends UntypedActor{

    // OneForOneStrategy 在1分钟内进行3次重试
    private static SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES), new Function<Throwable, SupervisorStrategy.Directive>() {
        @Override
        public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
            // 遇到ArithmeticException异常时，继续指定这个Actor，不做任何处理
            if (throwable instanceof ArithmeticException){
                System.out.println("meet ArithmeticException,just resume");
                return SupervisorStrategy.resume();
            }else if (throwable instanceof NullPointerException){
                System.out.println("meet NullPointerException,restart");
                // 空指针重启
                return SupervisorStrategy.restart();
            }else if (throwable instanceof IllegalArgumentException){
                // 参数异常则直接停止
                return SupervisorStrategy.stop();
            }else {
                // 其他异常则向上抛出
                return SupervisorStrategy.escalate();
            }
        }
    });


    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Props){
            getContext().actorOf((Props) o,"restartActor");
        }else {
            unhandled(o);
        }
    }
}
