package com.hpsvse.handler;


public class Handler {
	
	private MessageQueue mQueue;
	private Looper mLooper;
	
	//Handle�ĳ�ʼ���������߳������
	public Handler(){
		//��ȡ���̵߳�Looper����
		mLooper = Looper.myLooper();
		this.mQueue = mLooper.mQueue;
	}
	
	/**
	 * ������Ϣ����Ӷ���
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
