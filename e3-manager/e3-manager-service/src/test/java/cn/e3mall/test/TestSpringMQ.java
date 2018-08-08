package cn.e3mall.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestSpringMQ {
	
	@Test
	public void testSpringQueue(){
		//获取容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-active.xml");
		//获取spring JMS模板对象
		JmsTemplate jmsTemplate = ac.getBean(JmsTemplate.class);
		//获取queue
		Queue queue = (Queue)ac.getBean("queueDestination");
		//发送消息
		jmsTemplate.send(queue, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage("this is springMQ222");
				return message;
			}
		});
		
	}
	

}
