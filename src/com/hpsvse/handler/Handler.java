package com.hpsvse.handler;


public class Handler {
	
	private MessageQueue mQueue;
	private Looper mLooper;
	
	//Handle的初始化，在主线程中完成
	public Handler(){
		//获取主线程的Looper对象
		mLooper = Looper.myLooper();
		this.mQueue = mLooper.mQueue;
	}
	
	/**
	 * 发送消息，添加队列
	 * @param msg
	 */
	public void sendMessage(Message msg){
		msg.target = this;
		mQueue.enqueueMessage(msg);
	}
	
	public void handleMessage(Message msg) {
    }
	
	public void dispatchMessage(Message msg){
		handleMessage(msg);
	}
	
}
