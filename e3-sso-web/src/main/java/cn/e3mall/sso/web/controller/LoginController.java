package cn.e3mall.sso.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.sso.service.LoginService;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.E3Result;
import cn.e3mall.utils.JsonUtils;

@Controller
public class LoginController {
	@Autowired
	private LoginService loginService;

	@RequestMapping("/page/login")
	public String toLogin(@RequestParam(name="redirect",required=false) String redirect,Model model){
		//handler中只跳转到登陆页面,重定向的地址存入到login页面中,当页面登陆成功后,由页面location.href=redirect解决用户登陆后跳回原来的页面
		model.addAttribute("redirect", redirect);
		return "login";
	}
	
	@RequestMapping("/user/login")
	@ResponseBody
	public E3Result doLogin(String username,String password,
			HttpServletRequest request,HttpServletResponse response){
		//调用服务登陆
		E3Result e3Result = loginService.doLogin(username, password);
		//判断返回值状态,status=200时,获取key并保存到cookie
		if(e3Result.getStatus()==200){
			String sessionId = (String)e3Result.getData();
			//不设置cookie的过期时间,默认关闭浏览器,token失效
			CookieUtils.setCookie(request, response, "token", sessionId);
		}
		return e3Result;
	}
	
	//每次刷新页面shotcut.jsp中验证用户登陆状态,如果登陆则刷新redis的过期时间
	@RequestMapping(value="/user/token/{token}",
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)//"application/json;charset=utf-8"
	@ResponseBody
	public String checkLogin(@PathVariable String token,String callback){
		//查询用户
		E3Result e3Result  = loginService.checkLogin(token);
		//配和浏览器解决跨域问题
		//跨域请求时,会携带callback参数
		if(StringUtils.isNoneBlank(callback)){
			String retVal = callback+"("+JsonUtils.objectToJson(e3Result)+");";
			return retVal;
		}
		return JsonUtils.objectToJson(e3Result);
	}
	

	//jsonp跨域请求第二种方式
/*	@RequestMapping(value="/user/token/{token}")
	@ResponseBody
	public Object checkLogin(@PathVariable String token,String callback){
		//查询用户
		E3Result e3Result  = loginService.checkLogin(token);
		//配和浏览器解决跨域问题
		//跨域请求时,会携带callback参数
		if(StringUtils.isNoneBlank(callback)){
			//spring4.1以后可以使用
			MappingJacksonValue jacksonValue = new MappingJacksonValue(e3Result);
			jacksonValue.setJsonpFunction(callback);
			return jacksonValue;
		}
		return e3Result;
	}*/
	
	//用户退出
	@RequestMapping("/user/logout/{token}")
	public String loginout(@PathVariable String token){
		//根据token清除缓存中的数据
		loginService.logout(token);
		return "redirect:/page/login";
	}
}
