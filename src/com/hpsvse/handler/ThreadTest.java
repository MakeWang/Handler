package com.hpsvse.handler;

/**
 * 生成者与消费者
 * @author tgkj
 *
 */
public class ThreadTest {
	
	//产品
	static class ProducerObject{
		//线程操作可变的类
		private volatile static String value;
	}
	
	//生产者线程
	static class Producer extends Thread{
		
		Object lock;
		public Producer(Object lock) {
			this.lock = lock;
		}
		
		@Override
		public void run() {
			//不	断的生成产品
			while(true){
				synchronized (lock) {
					//如果产品没消费完
					if(ProducerObject.value != null){
						try {
							//线程等待
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//生成产品
					ProducerObject.value = "producer："+System.currentTimeMillis();
					System.out.println("生产产品："+ProducerObject.value);
					//通知消费者消费
					lock.notify();
				}
			}
		}
	}
	
	//消费者
	static class Consumer extends Thread{
		
		Object lock;
		public Consumer(Object lock){
			this.lock = lock;
		}
		@Override
		public void run() {
			//不断的消费产品
			while(true){
				synchronized (lock) {
					//如果产品以消费
					if(ProducerObject.value == null){
						try {
							//进入等待
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//消费产品
					System.out.println("消费产品："+ProducerObject.value);
					ProducerObject.value = null;
					//通知产品生产
					lock.notify();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Object lock = new Object();
		
		new Producer(lock).start();
		new Consumer(lock).start();
	}
	
}
