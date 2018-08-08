package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.utils.E3Result;
import cn.e3mall.utils.JsonUtils;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${CART_REDIS_KEY}")
	private String CART_REDIS_KEY;
	
	//redis中添加购物车,hash
	@Override
	public E3Result addCart(long userId, long itemid, int num) {
		//查询redis中有没有该itemid的field值
		String key = CART_REDIS_KEY+userId;
		String field = itemid+"";
		Boolean hexists = jedisClient.hexists(key, field);
		//有
		if(hexists){
			//取出商品
			String itemStr = jedisClient.hget(key,field);
			TbItem tbItem = JsonUtils.jsonToPojo(itemStr, TbItem.class);
			//更新num
			tbItem.setNum(tbItem.getNum()+num);
			//存入redis
			jedisClient.hset(key, field, JsonUtils.objectToJson(tbItem));
			return E3Result.ok();
		}
		//无,查询数据库中该条商品信息
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemid);
		//修改商品数量
		tbItem.setNum(num);
		//设置购物车商品图片
		String image = tbItem.getImage();
		if(StringUtils.isNotBlank(image)){
			tbItem.setImage(image.split(",")[0]);
		}
		//添加到购物车中
		jedisClient.hset(key, field, JsonUtils.objectToJson(tbItem));
		//返回成功
		return E3Result.ok();
	}


	@Override
	public TbItem findItemById(Long itemId) {
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		return item;
	}
	
	/**
	 * 更新redis中购物车商品
	 * <p>Title: updateNum</p>
	 * <p>Description: </p>
	 * @param id
	 * @param itemId
	 * @param num
	 * @see cn.e3mall.cart.service.CartService#updateNum(java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public void updateNum(Long userId, Long itemId, Integer num) {
		//插叙redis中有没有该数据
		//查询redis中该itemid的field值
		String key = CART_REDIS_KEY+userId;
		String field = itemId+"";
		Boolean hexists = jedisClient.hexists(key, field);
		//有
		if(hexists){
			//取出商品
			String itemStr = jedisClient.hget(key,field);
			TbItem tbItem = JsonUtils.jsonToPojo(itemStr, TbItem.class);
			//更新num
			tbItem.setNum(num);
			//存入redis
			jedisClient.hset(key, field, JsonUtils.objectToJson(tbItem));
		}
		
	}


	/**
	 * 从redis中删除购物车商品
	 * <p>Title: deleteCartFromRedis</p>
	 * <p>Description: </p>
	 * @param userId
	 * @param itemId
	 * @see cn.e3mall.cart.service.CartService#deleteCartFromRedis(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void deleteCartFromRedis(Long userId, Long itemId) {
		String key = CART_REDIS_KEY+userId;
		String field = itemId+"";
		Boolean hexists = jedisClient.hexists(key, field);
		//有
		if(hexists){
			//删除商品
			jedisClient.hdel(key, field);
		}
		
	}


	/**
	 * 用户登陆后将cookie中的购物车合并到Redis中
	 * <p>Title: getCartFromRedis</p>
	 * <p>Description: </p>
	 * @param userId
	 * @param cartList
	 * @return
	 * @see cn.e3mall.cart.service.CartService#getCartFromRedis(java.lang.Long, java.util.List)
	 */
	@Override
	public List<TbItem> getCartFromRedis(Long userId, List<TbItem> cartList) {
		//从redis中取出所有数据
		String key = CART_REDIS_KEY+userId;
		List<String> jsonList = jedisClient.hvals(key);
		ArrayList<TbItem> retList = new ArrayList<TbItem>();
		//遍历redis用户对应的所有购物车的值
		for (String str : jsonList) {
			TbItem tbItem = JsonUtils.jsonToPojo(str, TbItem.class);
			retList.add(tbItem);
			//遍历cookie中购物车
			for(TbItem item:cartList){
				//当cookie中的商品和redis中的商品是同一个时,数量相加
				if(item.getId().longValue() == tbItem.getId()){
					//修改需要加入redis中的商品数量
					tbItem.setNum(tbItem.getNum().intValue()+item.getNum());
					//重新写回
					jedisClient.hset(key, item.getId()+"", JsonUtils.objectToJson(tbItem));
				}else{
					//将cookie中的购物车添加到redis中
					jedisClient.hset(key, item.getId()+"", JsonUtils.objectToJson(item));
					retList.add(item);
				}
			}
		}
		return retList;
	}

}
