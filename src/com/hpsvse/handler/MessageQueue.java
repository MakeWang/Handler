package com.hpsvse.handler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue {
	
	//ͨ������Ľṹ�洢Message����
	Message[] itmes;
	
	//�������е�����
	int putIndex;
	int takeIndex;
	//������
	int count;
	
	//�������������൱�ڴ�������
	private Lock lock;
	//��������
	private Condition notEmpty;
	private Condition notFull;
	
	public MessageQueue(){
		//��Ϣ���еĴ�С����
		this.itmes = new Message[50];
		this.lock = new ReentrantLock();
		this.notEmpty = lock.newCondition();
		this.notFull = lock.newCondition();
	}
	
	
	/**
	 * ������У����߳����У�
	 * @param msg
	 */
	public void enqueueMessage(Message msg){
		try{
			lock.lock();
			//��Ϣ�������ˣ����߳�ֹͣ����Ϣ������
			while(count == itmes.length){
				try {
					notFull.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			itmes[putIndex] = msg;
			//ѭ��ȡֵ
			putIndex = (++putIndex == itmes.length) ? 0 : putIndex;
			count++;
			
			//���µ�Message����֪ͨ���߳�
			notEmpty.signal();
		}finally{
			lock.unlock();
		}
		
	}
	
	/**
	 * �����У����߳����У�
	 * @return
	 */
	public Message next(){
		Message msg = null;
		try{
			//��Ϣ�������ˣ����߳�ֹͣ����Ϣ������
			lock.lock();
			while(count == 0){
				try {
					notEmpty.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			msg = itmes[takeIndex];//ȡ��
			itmes[takeIndex] = null;//Ԫ���ƿ�
			takeIndex = (++takeIndex == itmes.length) ? 0 : takeIndex;
			count--;
			
			//ʹ����һ��Message����֪ͨ���̣߳����Լ̳�����
			notFull.signal();
		}finally{
			lock.unlock();
		}
		return msg;
	}
	
}
