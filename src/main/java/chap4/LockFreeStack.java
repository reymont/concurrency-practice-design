package chap4;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;  
  
/** 
 * <p>A lock free thread safe stack,based on linked list</p> 
 */  
public class LockFreeStack<V> {  
  
    private AtomicReference<Node<V>> head = new AtomicReference<Node<V>>(null);  
    private AtomicInteger size = new AtomicInteger(0);  
  
    /** 
     * <p>stack node,consist of value and an link to the next node</p> 
     * @param <V> type of value 
     */  
    private static class Node<V>{  
        /** the value of this node,not null*/  
        public V value;  
        /** link to next node,if it's the last ,next reference to null*/  
        AtomicReference<Node<V>> next ;  
  
        public Node(V value,Node<V> next){  
            this.value = value;  
            this.next = new AtomicReference<Node<V>>(next);  
        }  
    }  
  
    /** 
     * <p>Pop an value from the stack.</p> 
     * <p>This method is based on CAS operation, and is thread safe.</p> 
     * <p>When used in concurrency environment ,only one thread will get the  value on the top for once, 
     * the rest thread will try to get next ones,until the stack is empty</p> 
     * @return if stack is not empty,rerun an not null value,or null when the stack is null. 
     */  
    public V pop(){  
        Node<V> oldHead = null;  
        Node<V> next = null;  
  
        do{  
            oldHead = head.get();  
            if(oldHead == null){  
                return null;                       //empty stack  
            }  
            next = oldHead.next.get();  
        }while (!head.compareAndSet(oldHead,next));  
  
        size.getAndDecrement();  
        return oldHead.value;  
    }  
  
    /** 
     * <p>Push an value into the stack</p> 
     * <p>This method is based on CAP operation,and is thread safe</p> 
     * <p>When used in concurrency environment, only one thread will succeed once, 
     * the rest will try again ,until succeed</p> 
     * @param value value to put into the stack,not null 
     * @exception NullPointerException throws when value is null 
     */  
    public void push(V value){  
        if(value == null){  
            throw new NullPointerException("value is null");  
        }  
        Node<V> newNode = new Node<V>(value,null);  
        Node<V> oldHead ;  
        do{  
            oldHead = head.get();  
            newNode.next.set(oldHead);  
        }while(!head.compareAndSet(oldHead,newNode));  
        size.getAndIncrement();  
    }  
  
    /** 
     * <p>Get the size of the stack</p> 
     * <p>This method doesn't reflect timely state when used in concurrency environment</p> 
     * @return size of the stack 
     */  
    public int size(){  
        return size.get();  
    }  
    /** 
     * <p>Check is the stack is empty</p> 
     * <p>This method doesn't reflect timely state when used in concurrency environment</p> 
     * @return false unless stack is empty 
     */  
    public boolean isEmpty(){  
        return head.get() == null;  
    }  
}  