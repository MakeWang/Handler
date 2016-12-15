package com.hpsvse.handler;

public final class Looper {
	
	//每个主线程都会有一个Loople对象
	//Loople对象保存在ThreadLocal,保证了线程数据的隔离（安全性）
	static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
	
	//一个Loople对象，对应一个消息队列
	MessageQueue mQueue;
	
	private Looper(){
		mQueue = new MessageQueue();
	}
	
	
	/**
	 * Looper对像的初始化方法
	 */
	public static void prepare(){
		if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
		sThreadLocal.set(new Looper());
	}
	
	/**
	 * 得到当前的Looper对象
	 * @return
	 */
	public static Looper myLooper(){
		return sThreadLocal.get();
	}
	
	/**
	 * 队列轮询器
	 */
	public static void loop(){
		Looper me = myLooper();
		if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
		MessageQueue queue = me.mQueue;
		for(;;){
			Message msg = queue.next();
			if(msg == null){
				continue;
			}
			msg.target.dispatchMessage(msg);
		}
	}
	
}
