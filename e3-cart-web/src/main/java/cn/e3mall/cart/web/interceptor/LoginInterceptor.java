package cn.e3mall.cart.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;

/**
 * 用户登陆状态拦截器,如果用户登陆,将用户的信息从redis中取出保存到request中
 * <p>Title: LoginInterceptor</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
	@Autowired
	private JedisClient jedisClient;
	//在handler方法调用之前执行,false拦截,true放行
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//获取cookie在sso系统保存的token
		String token = CookieUtils.getCookieValue(request, "token");
		//token不存在,放行
		if(StringUtils.isBlank(token)){
			return true;
		}
		//token存在,到redis中查询该key的值
		String userJson = jedisClient.get(token);
		//不存在,放行
		if(StringUtils.isBlank(userJson)){
			return true;
		}
		//存在,将user的信息放入到request中,放行
		TbUser user = JsonUtils.jsonToPojo(userJson, TbUser.class);
		request.setAttribute("user", user);
		return true;
	}

	//在handler调用之后,modelAndView返回之执行
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	//在handler调用之后执行,一般用来处理异常等
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
