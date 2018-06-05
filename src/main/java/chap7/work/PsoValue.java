package chap7.work;
  
import java.util.Collections;  
import java.util.List;  
  
/** 
 * 可行的解决方案类，记录每年投资的钱，和该种方案获得的收益； 
 * 因为每种方案都是固定的，不会改变，所以这里都设置成不变模式； 
 */  
public final class PsoValue {  
    // 该方案的收益  
    final double value;  
    // 该方案每年投资的钱  
    final List<Double> x;  
      
    public PsoValue(double v, List<Double> x2) {  
        this.value = v;  
        this.x = Collections.unmodifiableList(x2);  
    }  
      
    public double getValue() {  
        return value;  
    }  
      
    public List<Double> getX() {  
        return x;  
    }  
      
    public String toString() {  
        StringBuffer sb = new StringBuffer();  
        sb.append("value: ").append("-->").append(x.toString());  
        return sb.toString();  
    }  
} 