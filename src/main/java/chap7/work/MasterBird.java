package chap7.work;
  
import akka.actor.ActorSelection;  
import akka.actor.UntypedActor;  
  
/** 
 * 主粒子类，用户管理和通知全局最优方案 
 */  
public class MasterBird extends UntypedActor {  
    // 全局最优方案  
    private PsoValue gBest = null;  
      
    @Override  
    public void onReceive(Object msg) throws Exception {  
        if(msg instanceof PBestMsg) {  
            PsoValue pBest = ((PBestMsg) msg).getValue();  
            if((gBest == null) || (gBest.getValue() < pBest.getValue())) {  
                // 更新全局最优方案，并通知所有粒子  
                gBest = pBest;  
                ActorSelection selection = getContext().actorSelection("/user/bird_*");  
                selection.tell(new GBestMsg(gBest), getSelf());  
                // 打印最优方案  
                System.out.println(gBest.getValue());  
            }  
        }  
    }     
}  