package chap7.work;
  
import java.util.List;  
  
/** 
 * 投资方案适应度类，计算出投资方案的收益，收益越大，我们认为适应度越高 
 */  
public class Fitness {  
      
    public static double fitness(List<Double> x) {  
        double sum = 0;
        // 取值sqrt(x1)+sqrt(x2)+sqrt(x3)+sqrt(x4)
        // 忽略x0
        for(int i=1; i<x.size(); i++) {  
            // 获取对于年投资的收益  
            sum += Math.sqrt(x.get(i));  
        }  
        return sum;  
    }     
} 