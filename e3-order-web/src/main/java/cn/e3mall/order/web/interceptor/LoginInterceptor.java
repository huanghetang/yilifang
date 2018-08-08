package cn.e3mall.order.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;

/**
 * 登陆拦截器,订单中所有操作都需要用户先登陆
 * <p>Title: LoginInterceptor</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private CartService cartService;
	@Value("${COOKIE_CART}")
	private String COOKIE_CART;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		//token不存在,拦截,去登陆
		if(StringUtils.isBlank(token)){
			response.sendRedirect("http://localhost:8088/page/login?redirect="+request.getRequestURL());
			return false;
		}
		//token存在,去redis中查询用户信息
		String jsonUser = jedisClient.get(token);
		//redis中不存在,拦截,去登陆
		if(StringUtils.isBlank(jsonUser)){
			response.sendRedirect("http://localhost:8088/page/login?redirect="+request.getRequestURL());
			return false;
		}
		//存在,获取用户id
		TbUser user = JsonUtils.jsonToPojo(jsonUser, TbUser.class);
		long userId = user.getId();
		//放入到request中
		request.setAttribute("user", user);
		//去cookie中找到用户的购物车
		String cartListJson = CookieUtils.getCookieValue(request, COOKIE_CART, true);
		//把cookie中购物车合并到redis中
		if(StringUtils.isNotBlank(cartListJson)){
			List<TbItem> itemList = JsonUtils.jsonToList(cartListJson, TbItem.class);
			cartService.getCartFromRedis(userId, itemList);
			//添加到redis后删除cookie中的购物车
			CookieUtils.setCookie(request, response, COOKIE_CART, "", 0);
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
