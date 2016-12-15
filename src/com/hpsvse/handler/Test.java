
package com.hpsvse.handler;

import java.util.UUID;

public class Test {
	
	
	public static void main(String[] args) {
		
		//轮询器初始化
		Looper.prepare();
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 123){
					System.out.println("1handler接收消息："+msg.obj.toString());
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
						//msg.obj = "子线程"+System.currentTimeMillis();
						System.out.println("子线程发消息："+msg.obj);
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
		
		//开始轮询
		Looper.loop();
	}
}
