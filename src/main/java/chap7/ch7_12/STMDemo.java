package chap7.ch7_12;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.transactor.Coordinated;
import akka.util.Timeout;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

/**
 * Created by hjy on 18-2-24.
 */
public class STMDemo {

    public static ActorRef company = null;
    public static ActorRef employee = null;

    public static void main(String[] args) throws Exception{
        final ActorSystem system = ActorSystem.create("transactionDemo", ConfigFactory.load("samplehello.conf"));
        company = system.actorOf(Props.create(CompanyActor.class),"company");
        employee = system.actorOf(Props.create(EmployeeActor.class),"employee");

        Timeout timeout = new Timeout(1, TimeUnit.SECONDS);

        // 尝试进行19次汇款，第1次汇款额度为1元，第二次为2元，最后一次为19元
        for (int i = 1; i < 20; i++) {
            company.tell(new Coordinated(i,timeout),ActorRef.noSender());
            Thread.sleep(200);
            // 询问公司账户和雇员的当前余额
            Integer companyCount = (Integer) Await.result(ask(company,"GetCount",timeout),timeout.duration());
            Integer employeeCount = (Integer) Await.result(ask(employee,"GetCount",timeout),timeout.duration());

            System.out.println("compony count="+companyCount);
            System.out.println("employee count="+employeeCount);
            System.out.println("=================");

        }
    }

}
