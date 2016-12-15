package com.hpsvse.handler;

/**
 * ��������������
 * @author tgkj
 *
 */
public class ThreadTest {
	
	//��Ʒ
	static class ProducerObject{
		//�̲߳����ɱ����
		private volatile static String value;
	}
	
	//�������߳�
	static class Producer extends Thread{
		
		Object lock;
		public Producer(Object lock) {
			this.lock = lock;
		}
		
		@Override
		public void run() {
			//��	�ϵ����ɲ�Ʒ
			while(true){
				synchronized (lock) {
					//�����Ʒû������
					if(ProducerObject.value != null){
						try {
							//�̵߳ȴ�
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//���ɲ�Ʒ
					ProducerObject.value = "producer��"+System.currentTimeMillis();
					System.out.println("������Ʒ��"+ProducerObject.value);
					//֪ͨ����������
					lock.notify();
				}
			}
		}
	}
	
	//������
	static class Consumer extends Thread{
		
		Object lock;
		public Consumer(Object lock){
			this.lock = lock;
		}
		@Override
		public void run() {
			//���ϵ����Ѳ�Ʒ
			while(true){
				synchronized (lock) {
					//�����Ʒ������
					if(ProducerObject.value == null){
						try {
							//����ȴ�
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//���Ѳ�Ʒ
					System.out.println("���Ѳ�Ʒ��"+ProducerObject.value);
					ProducerObject.value = null;
					//֪ͨ��Ʒ����
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
