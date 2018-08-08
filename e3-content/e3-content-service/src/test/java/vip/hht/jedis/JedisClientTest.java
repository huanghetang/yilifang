package vip.hht.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.jedis.JedisClient;

public class JedisClientTest {
	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-jedis.xml");
		JedisClient jedisClient = ac.getBean(JedisClient.class);
		jedisClient.set("test1", "景甜");
		System.out.println(jedisClient.get("test1"));
	}

}
