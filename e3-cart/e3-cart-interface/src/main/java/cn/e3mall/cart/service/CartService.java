package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.pojo.TbItem;
import cn.e3mall.utils.E3Result;

public interface CartService {
	/**
	 * redis添加购物车
	 */
	public E3Result addCart(long userId,long itemid,int num);

	public TbItem findItemById(Long itemId);

	public void updateNum(Long id, Long itemId, Integer num);

	public void deleteCartFromRedis(Long userId, Long itemId);

	public List<TbItem> getCartFromRedis(Long userId, List<TbItem> cartList);
	

}
