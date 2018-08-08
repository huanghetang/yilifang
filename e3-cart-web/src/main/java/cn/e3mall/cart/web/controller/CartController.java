package cn.e3mall.cart.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;

/**
 * 购物车
 * <p>Title: CartController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class CartController {

	@Autowired
	private CartService cartService;
	//cookie中购物车的key
	@Value("${COOKIE_CART}")
	private String COOKIE_CART;
	@Value("${CART_EXPIRE}")
	private Integer CART_EXPIRE;
	/**
	 * 添加购物车
	 */
	@RequestMapping("cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,@RequestParam(defaultValue="1") Integer num,
			HttpServletRequest request,HttpServletResponse response){
		//判断用户是否登陆
		Object object = request.getAttribute("user");
		//登陆后将该商品保存到redis购物车中
		if(object!=null){
			TbUser user = (TbUser)object;
			cartService.addCart(user.getId(), itemId, num);
			return "cartSuccess";
		}
		//不登陆将商品保存到cookie购物车中
		//查询cookie中购物车
		List<TbItem> itmeList = getCartFromCookie(request);
		boolean falg = false;
		//遍历cookei中购物车,并判断购物车中有没有这条商品
		for (TbItem tbItem : itmeList) {
			//有,更新数量
			if(tbItem.getId().longValue()==itemId){
				tbItem.setNum(tbItem.getNum()+num);
				falg = true;
				//避免并发修改异常
				break;
			}
		}
		//无,查询商品信息,加入购物车
		if(!falg){
			TbItem item = cartService.findItemById(itemId);
			//修改商品数量
			item.setNum(num);
			//设置购物车商品图片
			String image = item.getImage();
			if(StringUtils.isNotBlank(image)){
				item.setImage(image.split(",")[0]);
			}
			itmeList.add(item);
		}
		//保存到cookie中
		CookieUtils.setCookie(request, response, COOKIE_CART, JsonUtils.objectToJson(itmeList), CART_EXPIRE,true);
		//返回视图
		return "cartSuccess";
	}

	/**
	 * 更新购物车商品数量
	 * <p>Title: RequestMapping</p>
	 * <p>Description: </p>
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	public void updateNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response){
		//登陆状态
		
		//判断用户是否登陆
		Object object = request.getAttribute("user");
		//登陆后将该商品保存到redis购物车中
		if(object!=null){
			TbUser user = (TbUser)object;
			//调用服务
			cartService.updateNum(user.getId(), itemId, num);
			return;
		}
		//没登陆状态
		//从cookie中获取购物车信息
		List<TbItem> itemList = getCartFromCookie(request);
		//遍历购物车,修改数量
		for (TbItem tbItem : itemList) {
			if(tbItem.getId().longValue()==itemId){
				tbItem.setNum(num);
				break;
			}
		}
		//重新写入cookie
		CookieUtils.setCookie(request, response, COOKIE_CART, JsonUtils.objectToJson(itemList),CART_EXPIRE, true);
	}
	
	/**
	 * 删除购物车中的商品
	 * <p>Title: goCart</p>
	 * <p>Description: </p>
	 * @return
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartByItemId(@PathVariable Long itemId,
			HttpServletRequest request,HttpServletResponse response){
		//没登陆时
		//判断用户是否登陆
		Object object = request.getAttribute("user");
		//登陆后将该商品保存到redis购物车中
		if(object!=null){
			TbUser user = (TbUser)object;
			//调用服务
			cartService.deleteCartFromRedis(user.getId(), itemId);
			return  "redirect:/cart/cart.html";
		}
		//从cookie中取出购物车
		List<TbItem> itemList = getCartFromCookie(request);
		//找到这条商品
		for (TbItem tbItem : itemList) {
			if(tbItem.getId().longValue()==itemId){
				//删除
				itemList.remove(tbItem);
				break;
			}
		}
		//重新写入到cookie中
		CookieUtils.setCookie(request, response, COOKIE_CART, JsonUtils.objectToJson(itemList),CART_EXPIRE,true);
		return  "redirect:/cart/cart.html";
	}
	
	/*
	 * 查询购物车页面
	 */
	@RequestMapping("/cart/cart")
	public String goCart(HttpServletRequest request,HttpServletResponse response){
		//不登陆直接查询cookie购物车
		List<TbItem> cartList = getCartFromCookie(request);
		//判断用户是否登陆
		Object object = request.getAttribute("user");
		if(object!=null){
			TbUser user = (TbUser)object;
			//将cookie购物车合并到redis购物车中
			cartList = cartService.getCartFromRedis(user.getId(),cartList);
			//清除cookie中的购物车
			CookieUtils.setCookie(request, response, COOKIE_CART,"",0);
		}
		//返回视图
		request.setAttribute("cartList", cartList);
		return "cart";
	}
	
	
	/**
	 * 从cookie中获取购物车信息
	 * <p>Title: getCartFromCookie</p>
	 * <p>Description: </p>
	 * @param request
	 * @return 如果有该用户的购物车,返回该购物车列表,否则返回一个空的ArrayList
	 */
	private List<TbItem> getCartFromCookie(HttpServletRequest request){
		List<TbItem> retList = new ArrayList<TbItem>();
		String cartList = CookieUtils.getCookieValue(request, COOKIE_CART, true);
		if(StringUtils.isNotBlank(cartList)){
			retList = JsonUtils.jsonToList(cartList, TbItem.class);
			return retList;
		}
		return retList;
	}
}
