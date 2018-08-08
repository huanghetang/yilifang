package vip.hht.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	
	/**
	 * 测试单机版
	 * <p>Title: test1</p>
	 * <p>Description: </p>
	 */
	@Test
	public void test1(){
		//创建jedis连接池
		JedisPool pool = new JedisPool("192.168.25.129",7001);
		//获取连接
		Jedis jedis = pool.getResource();
		//操作
		jedis.set("aa", "景甜");
		System.out.println(jedis.get("aa"));
		//关闭资源
		jedis.close();
		pool.close();
	}

	/**
	 * 测试集群版
	 */
	@Test
	public void test2(){
		//创建JedisCluster对象需要子节点集合
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		HostAndPort hostAndPort1 = new HostAndPort("192.168.25.129", 7001);
		HostAndPort hostAndPort2 = new HostAndPort("192.168.25.129", 7002);
		HostAndPort hostAndPort3 = new HostAndPort("192.168.25.129", 7003);
		HostAndPort hostAndPort4 = new HostAndPort("192.168.25.129", 7004);
		HostAndPort hostAndPort5 = new HostAndPort("192.168.25.129", 7005);
		HostAndPort hostAndPort6 = new HostAndPort("192.168.25.129", 7006);
		nodes.add(hostAndPort1);
		nodes.add(hostAndPort2);
		nodes.add(hostAndPort3);
		nodes.add(hostAndPort4);
		nodes.add(hostAndPort5);
		nodes.add(hostAndPort6);
		//创建jedisCluster对象
		JedisCluster jedisCluster = new JedisCluster(nodes);
		//操作redis
		jedisCluster.hset("hash1", "name", "景甜");
		System.out.println(jedisCluster.hget("hash1", "name"));
		//关闭资源
		jedisCluster.close();
	}
}
