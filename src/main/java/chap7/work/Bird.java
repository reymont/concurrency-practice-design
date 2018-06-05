package chap7.work;
  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Random;  
  
import akka.actor.ActorSelection;  
import akka.actor.UntypedActor;  
  
/** 
 * 粒子类，也是粒子群算法中最核心的类； 
 * 在粒子群算法中，为了提高效率，选择执行方案时并不是完全随机的； 
 * 而是让粒子先随机分布在整个区域内单独查找，看谁查找的方案适配度最优， 
 * 并将这最优方案发送给大家，大家再以这个最优方案为方向， 
 */
public class Bird extends UntypedActor {  
    // Bird个体最优方案  
    private PsoValue pBest = null;  
    // 全局最优方案  
    private PsoValue gBest = null;  
    // 粒子在各个维度上的移动速度；每一年的投资认为是一个维度，一共4个维度（这里数组长度为5，是为从1-4的角标，方便代码阅读）  
    private List<Double> velocity = new ArrayList<Double>(5);  
    // 粒子的初始化位置  
    private List<Double> x = new ArrayList<Double>(5);  
    // 创建生成随机数对象  
    private Random r = new Random();  
      
    // 创建粒子时，初始化粒子的位置，每一个位置可以看做是一种方案  
    @Override  
    public void preStart() {  
        // 初始化velocity和x  
        for(int i=0; i<5; i++) {  
            velocity.add(Double.NEGATIVE_INFINITY);  
            x.add(Double.NEGATIVE_INFINITY);  
        }  
          
        // 第一年投资资金 x1<=400  
        x.set(1, (double)r.nextInt(401));  
          
        // 第二年投资资金 x2<=440-1.1*x1（x1：第一年投资资金）  
        double max = 440 - 1.1 * x.get(1);  
        if(max < 0){  
            max = 0;  
        }  
        x.set(2, r.nextDouble() * max);  
          
        // 第三年投资资金 x3<=484-1.21*x1-1.1*x2  
        max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);  
        if(max < 0) {  
            max = 0;  
        }  
        x.set(3, r.nextDouble() * max);  
          
        // 第四年投资资金 x4<=532.4-1.331*x1-1.21*x2-1.1*x3  
        max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);  
        if(max < 0) {  
            max = 0;  
        }  
        x.set(4, r.nextDouble() * max);  
          
        // 计算出该方案的适应度（收益）  
        double newFit = Fitness.fitness(x);  
        // 得到局部最优方案（因为是第一个方案，肯定是当前最优方案）  
        pBest = new PsoValue(newFit, x);  
        // 创建局部最优方案消息  
        PBestMsg pBestMsg = new PBestMsg(pBest);  
        // 通过工厂获取消息发送对象  
        ActorSelection selection = getContext().actorSelection("/user/masterbird");  
        // 将局部最优方案消息发送给Master  
        selection.tell(pBestMsg, getSelf());  
          
    }  
      
    @Override  
    public void onReceive(Object msg) throws Exception {  
        // 如果接受到的是全局最优方案消息，则记录最优方案，并根据全局最优方案更新自己的运行速度  
        if(msg instanceof GBestMsg) {  
            gBest = ((GBestMsg) msg).getValue();  
            // 更新速度  
            for(int i=1; i<velocity.size(); i++) {  
                updateVelocity(i);  
            }  
            // 更新位置  
            for(int i=1; i<x.size(); i++) {  
                updateX(i);  
            }  
            // 有效性检测，防止粒子超出了边界  
            validateX();  
            // 重新计算适应度，如果产生了新的个体最优，就发送给Master  
            double newFit = Fitness.fitness(x);  
            if(newFit > pBest.value) {  
                pBest = new PsoValue(newFit, x);  
                PBestMsg pBestMsg = new PBestMsg(pBest);  
                getSender().tell(pBestMsg, getSelf());  
            }  
        }  
        else {  
            unhandled(msg);  
        }  
    }  
  
    // 更新速度  
    public double updateVelocity(int i) {  
        double v = Math.random() * velocity.get(i)  
                + 2 * Math.random() * (pBest.getX().get(i) - x.get(i))  
                + 2 * Math.random() * (gBest.getX().get(i) - x.get(i));  
        v = v > 0 ? Math.min(v, 5) : Math.max(v, -5);  
        velocity.set(i, v);  
        return v;  
    }  
  
    // 更新位置  
    public double updateX(int i) {  
        double newX = x.get(i) + velocity.get(i);  
        x.set(i, newX);  
        return newX;  
    }  
      
    // 有效性检测，防止粒子超出了边界  
    public void validateX() {  
        // x1  
        if(x.get(1) > 400) {  
            x.set(1, (double) r.nextInt(401));  
        }  
        // x2  
        double max = 440 - 1.1 * x.get(1);  
        if((x.get(2) > max) || (x.get(2) < 0)) {  
            x.set(2, r.nextDouble() * max);  
        }  
        // x3  
        max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);;  
        if((x.get(3) > max) || (x.get(3) < 0)) {  
            x.set(3, r.nextDouble() * max);  
        }  
        // x4  
        max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);  
        if((x.get(4) > max) || (x.get(4) < 0)) {  
            x.set(4, r.nextDouble() * max);  
        }         
    }         
}  