package com.hpsvse.handler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue {
	
	//通过数组的结构存储Message对象
	Message[] itmes;
	
	//入队与出列的索引
	int putIndex;
	int takeIndex;
	//计数器
	int count;
	
	//互斥锁，就是相当于代码块加锁
	private Lock lock;
	//条件变量
	private Condition notEmpty;
	private Condition notFull;
	
	public MessageQueue(){
		//消息队列的大小限制
		this.itmes = new Message[50];
		this.lock = new ReentrantLock();
		this.notEmpty = lock.newCondition();
		this.notFull = lock.newCondition();
	}
	
	
	/**
	 * 加入队列（子线程运行）
	 * @param msg
	 */
	public void enqueueMessage(Message msg){
		try{
			lock.lock();
			//消息队列满了，子线程停止发消息，阻塞
			while(count == itmes.length){
				try {
					notFull.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			itmes[putIndex] = msg;
			//循环取值
			putIndex = (++putIndex == itmes.length) ? 0 : putIndex;
			count++;
			
			//有新的Message对象，通知主线程
			notEmpty.signal();
		}finally{
			lock.unlock();
		}
		
	}
	
	/**
	 * 出队列（主线程运行）
	 * @return
	 */
	public Message next(){
		Message msg = null;
		try{
			//消息队列满了，主线程停止发消息，阻塞
			lock.lock();
			while(count == 0){
				try {
					notEmpty.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			msg = itmes[takeIndex];//取出
			itmes[takeIndex] = null;//元素制空
			takeIndex = (++takeIndex == itmes.length) ? 0 : takeIndex;
			count--;
			
			//使用了一个Message对象，通知子线程，可以继承生产
			notFull.signal();
		}finally{
			lock.unlock();
		}
		return msg;
	}
	
}
