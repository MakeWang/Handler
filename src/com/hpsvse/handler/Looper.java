package com.hpsvse.handler;

public final class Looper {
	
	//ÿ�����̶߳�����һ��Loople����
	//Loople���󱣴���ThreadLocal,��֤���߳����ݵĸ��루��ȫ�ԣ�
	static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
	
	//һ��Loople���󣬶�Ӧһ����Ϣ����
	MessageQueue mQueue;
	
	private Looper(){
		mQueue = new MessageQueue();
	}
	
	
	/**
	 * Looper����ĳ�ʼ������
	 */
	public static void prepare(){
		if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
		sThreadLocal.set(new Looper());
	}
	
	/**
	 * �õ���ǰ��Looper����
	 * @return
	 */
	public static Looper myLooper(){
		return sThreadLocal.get();
	}
	
	/**
	 * ������ѯ��
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
