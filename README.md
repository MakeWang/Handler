# Handler
Handler机制的流程

![](https://github.com/MakeWang/Handler/blob/master/image/a1.png)


这是用java工程模拟Android中Handler机制，如上是效果图：</br>
我们知道，Handler中有几个重要的类</br>
1、Looper：这个类被我们称为消息循环器，不断的循环MessageQueue中的消息。</br>
2、MessageQueue：是来存储发送的Message消息，这个类最重要的是消息存储线程安全，这里我不是按照Handler中源码，是用线程安全生产和消费者思想来写的。</br>
3、Message：这个是我们handler.sendMessage(msg)发送消息类。</br>
4、Handler：这个故名思议就是我们加入消息和取消息的一个主线程类。</br>

# Looper：</br>
这个类在Android中入口函数中就定义了
```java
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
```

# MessageQueue：</br>
```java
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

```

# Message：</br>
```java
public class Message {
	
	Handler target;
	
	public int what;
	public Object obj;
	
	@Override
	public String toString() {
		return obj.toString();
	}
	
}
```

# Handler：</br>
```java
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
```



# Test：</br>
```java
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
		
		for (int i = 0; i < 2; i++) {
			new Thread(){
				public void run() {
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
				};
				
			}.start();
		}
		
		//开始轮询
		Looper.loop();
	}
```
