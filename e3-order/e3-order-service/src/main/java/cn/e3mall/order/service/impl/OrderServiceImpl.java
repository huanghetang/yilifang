package cn.e3mall.order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.beans.OrderVO;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;
import cn.e3mall.pojo.UserCartData;
import cn.e3mall.utils.E3Result;
import cn.e3mall.utils.JsonUtils;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_ORDER_START}")
	private String REDIS_ORDER_START;
	@Value("${REDIS_ORDER_KEY}")
	private String REDIS_ORDER_KEY;
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Value("${REDIS_ORDERITEM_KEY}")
	private String REDIS_ORDERITEM_KEY;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination topicDestination;
	/**
	 * 添加订单
	 * <p>Title: addOrder</p>
	 * <p>Description: </p>
	 * @param orderVO
	 * @return
	 * @see cn.e3mall.order.service.OrderService#addOrder(cn.e3mall.order.beans.OrderVO)
	 */
	@Override
	public E3Result addOrder(long userId,OrderVO orderVO) {
		//插入order表
		//使用redis生成主键 订单id
		String redisOrder = jedisClient.get(REDIS_ORDER_KEY);
		//第一次设置订单号初始值
		if(StringUtils.isBlank(redisOrder)){
			jedisClient.set(REDIS_ORDER_KEY, REDIS_ORDER_START);
		}
		//原子性加1
		Long orderId = jedisClient.incr(REDIS_ORDER_KEY);
		orderVO.setOrderId(orderId+"");
		//补全order表属性
		//status 1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderVO.setStatus(1);
		//create_time
		Date date = new Date();
		orderVO.setCreateTime(date);
		//update_time
		orderVO.setUpdateTime(date);
		orderMapper.insert(orderVO);
		//插入order_item表
		List<TbOrderItem> orderitems = orderVO.getOrderItems();
		//循环插入 
		for (TbOrderItem orderItem : orderitems) {
			//设置主键
			Long orderItemId = jedisClient.incr(REDIS_ORDERITEM_KEY);
			orderItem.setId(orderItemId+"");
			orderItem.setOrderId(orderId+"");
			orderItemMapper.insert(orderItem);
		}
		//插入order_shipping表
		TbOrderShipping orderShipping = orderVO.getOrderShipping();
		//补全属性
		orderShipping.setOrderId(orderId+"");
		orderShipping.setCreated(date);
		orderShipping.setUpdated(date);
		orderShippingMapper.insert(orderShipping);
		//删除购物车中对应的商品数据,发送mq消息让购物车服务去删除
		List<TbOrderItem> orderItems = orderVO.getOrderItems();
		List<Long> itemIdList = new ArrayList<Long>();
		//循环删除订单中的每一条商品
		for (TbOrderItem tbOrderItem : orderItems) {
			String itemId = tbOrderItem.getItemId();
			itemIdList.add(new Long(itemId));
		}
		//需要发送的消息
		UserCartData userCartData = new UserCartData();
		userCartData.setUserId(userId);
		userCartData.setItemId(itemIdList);
		final String msg = JsonUtils.objectToJson(userCartData);
		jmsTemplate.send(topicDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage(msg);
				return message;
			}
		});
		//返回订单号
		return E3Result.ok(orderId);
	}
	

}
