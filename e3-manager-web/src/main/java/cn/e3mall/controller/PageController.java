package cn.e3mall.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	
	

	
	@RequestMapping("/")
	public String toHome(){
		return "index";
	}
	
	@RequestMapping("/{page}")
	public String goPage(@PathVariable String page){
		if("".equals(page.trim()))
			return "index";
		return page;
	}
	
	public void test(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/");
//		ac.getBean(TbItemMapper.class);
	}
}
