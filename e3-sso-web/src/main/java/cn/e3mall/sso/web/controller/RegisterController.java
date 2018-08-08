package cn.e3mall.sso.web.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.RegisterService;
import cn.e3mall.utils.E3Result;

@Controller
public class RegisterController {
	@Autowired
	private RegisterService registerService;

	//跳转注册
	@RequestMapping("/page/register")
	public String toRegister(){
		return "register";
	}
	
	//注册验证
	@RequestMapping("/user/check/{checkValue}/{type}")
	@ResponseBody
	public E3Result check(@PathVariable String checkValue,@PathVariable Integer type){
		return registerService.check(checkValue, type);
	}
	
	//注册
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public E3Result register(TbUser user){
		E3Result e3Result = registerService.register(user);
		return e3Result;
	}
	
	public static void main(String[] args) {
		String a = "sdf";
		String[] split = a.split(",");
		System.out.println(Arrays.toString(split));
	}
}
