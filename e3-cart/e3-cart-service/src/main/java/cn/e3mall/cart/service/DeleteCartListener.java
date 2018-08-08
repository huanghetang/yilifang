package cn.e3mall.cart.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.pojo.UserCartData;
import cn.e3mall.utils.JsonUtils;

/**
 * 监听订单服务,当订单提交后,需要删除redis中对用的购物车数据
 * <p>Title: DeleteCartListener</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
public class DeleteCartListener implements  MessageListener{
	@Autowired
	private CartService cartService;

	@Override
	public void onMessage(Message message) {
		TextMessage msg = (TextMessage) message;
		try {
			String jsonStr = msg.getText();
			//获取需要删除的商品集合
			 UserCartData userCartData = JsonUtils.jsonToPojo(jsonStr, UserCartData.class);
			 //用户信息
			 long userId = userCartData.getUserId();
			 //该用户订单中已购买商品信息
			 List<Long> itemIdList = userCartData.getItemId();
			//循环删除
			for (Long itemId : itemIdList) {
				cartService.deleteCartFromRedis(userId, itemId);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
