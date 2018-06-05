package chap7.work;
  
import akka.actor.ActorSystem;  
import akka.actor.Props;  
  
public class PSOMain {  
  
    public static final int BIRD_COUNT = 1000000;  
      
    public static void main(String args[]) {  
          
        // 创建Actor的管理和维护系统  
        ActorSystem system = ActorSystem.create("psoSystem");  
        // 创建Master粒子  
        system.actorOf(Props.create(MasterBird.class), "masterbird");  
        // 创建Bird粒子群  
        for(int i=0; i<BIRD_COUNT; i++) {  
            system.actorOf(Props.create(Bird.class), "bird_" + i);  
        }         
    }     
}
//42.87516464146814
//42.97675704225391
//42.98876923688671
//42.997649698046445
//43.015936055049
//43.01939997043229