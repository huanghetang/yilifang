package cn.e3mall.test;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestActivemq {

	
	/**
	 * 测试Activemq发送点对点消息
	 * <p>Title: testQueueProducer</p>
	 * <p>Description: </p>
	 * @throws JMSException
	 */
	@Test
	public void testQueueProducer() throws JMSException{
		//1.创建工厂,传入一个坐标(brokerURL 经纪人坐标)
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.129:61616");
		//2.使用工厂创建一个Connection
		Connection connection = connectionFactory.createConnection();
		//3.开启连接,调用connection的start方法
		connection.start();
		//4.使用connection对象获取session对象
		//第一个参数是否开启事务(分布式事务),一般为false为true时第二个参数无意义.第二个参数为应答方式,分为自动应答和手动应答,
		//手动应答需要自己写代码实现,一般为自动应答
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		//5.创建一个destination对象,topic和queue,此处是queue
		Queue queue = session.createQueue("test-queue");
		//6.使用session对象创建Producer对象
		MessageProducer producer = session.createProducer(queue);
		//7.创建一个消息
		TextMessage message = session.createTextMessage("hello activemq");
		//8.使用producer发送消息
		producer.send(message);
		//9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	/**
	 * 测试activemq 点对点消息消费者
	 * <p>Title: testQueueConsumer</p>
	 * <p>Description: </p>
	 * @throws JMSException 
	 * @throws IOException 
	 */
	@Test
	public void testQueueConsumer() throws JMSException, IOException{
		//1.创建一个ConnectionFactory工厂,传入经纪人坐标
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.129:61616");
		//2.使用工厂创建一个connection对象
		Connection connection = connectionFactory.createConnection();
		//3.开启连接,调用connection对象的start方法
		connection.start();
		//4.使用connection对象创建一个session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.创建一个destination对象,此处是queue
		Queue queue = session.createQueue("spring-queue");
		//6.使用session创建一个consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		//7.获取消息(开启一个监听)
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				TextMessage msg = (TextMessage) message;
				try {
					String text = msg.getText();
					//8.打印消息
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		//阻塞当前线程
		System.in.read();
		//9.关闭资源
		consumer.close();
		session.close();
		connection.close();
		
	}
	
	/**
	 * 测试Activemq topic方式生产者
	 * <p>Title: testTopicProducer</p>
	 * <p>Description: </p>
	 * @throws JMSException
	 */
	@Test
	public void testTopicProducer() throws JMSException{
		//1.创建工厂,传入一个坐标(brokerURL 经纪人坐标)
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.129:61616");
		//2.使用工厂创建一个Connection
		Connection connection = connectionFactory.createConnection();
		//3.开启连接,调用connection的start方法
		connection.start();
		//4.使用connection对象获取session对象
		//第一个参数是否开启事务(分布式事务),一般为false为true时第二个参数无意义.第二个参数为应答方式,分为自动应答和手动应答,
		//手动应答需要自己写代码实现,一般为自动应答
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		//5.创建一个destination对象,topic和queue,此处是queue
		Topic topic = session.createTopic("test-topic");
		//6.使用session对象创建Producer对象
		MessageProducer producer = session.createProducer(topic);
		//7.创建一个消息
		TextMessage message = session.createTextMessage("hello activemq");
		//8.使用producer发送消息
		producer.send(message);
		//9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	
	/**
	 * 测试Activemq topic消费者
	 * <p>Title: testTopicConsumer</p>
	 * <p>Description: </p>
	 * @throws JMSException
	 * @throws IOException
	 */
	@Test
	public void testTopicConsumer() throws JMSException, IOException{
		//1.创建一个ConnectionFactory工厂,传入经纪人坐标
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.129:61616");
		//2.使用工厂创建一个connection对象
		Connection connection = connectionFactory.createConnection();
		//3.开启连接,调用connection对象的start方法
		connection.start();
		//4.使用connection对象创建一个session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.创建一个destination对象,此处是topic
		Topic topic = session.createTopic("test-topic");
		//6.使用session创建一个consumer对象
		MessageConsumer consumer = session.createConsumer(topic);
		//7.获取消息(开启一个监听)
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				TextMessage msg = (TextMessage) message;
				try {
					String text = msg.getText();
					//8.打印消息
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("第3个消费者正在接收消息");
		//阻塞当前线程
		System.in.read();
		//9.关闭资源
		consumer.close();
		session.close();
		connection.close();
		
	}
}
