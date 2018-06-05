package chap7.work;
  
/** 
 * 全局最优方案类，记录全局最优的方案； 
 */  
public final class GBestMsg {  
      
    final PsoValue value;  
      
    public GBestMsg(PsoValue v) {  
        value = v;  
    }  
      
    public PsoValue getValue() {  
        return value;  
    }  
} 