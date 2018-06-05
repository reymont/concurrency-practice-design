package chap7.work;
  
/** 
 * 个体最优方案类，记录个体最优的方案； 
 */  
public final class PBestMsg {  
      
    final PsoValue value;  
      
    public PBestMsg(PsoValue v) {  
        value = v;  
    }  
      
    public PsoValue getValue() {  
        return value;  
    }     
}  