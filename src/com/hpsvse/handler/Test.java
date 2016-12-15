
package com.hpsvse.handler;

import java.util.UUID;

public class Test {
	
	
	public static void main(String[] args) {
		
		//��ѯ����ʼ��
		Looper.prepare();
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 123){
					System.out.println("1handler������Ϣ��"+msg.obj.toString());
				}
			}
		};
		
		for (int i = 0; i < 10; i++) {
			new Thread(){
				public void run() {
					while(true){
						Message msg = new Message();
						msg.what = 123;
						synchronized (UUID.class) {
							msg.obj = UUID.randomUUID();
						}
						//msg.obj = "���߳�"+System.currentTimeMillis();
						System.out.println("���̷߳���Ϣ��"+msg.obj);
						handler.sendMessage(msg);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				
			}.start();
		}
		
		//��ʼ��ѯ
		Looper.loop();
	}
}
