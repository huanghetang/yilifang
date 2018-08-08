package cn.e3mall.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.content.service.ContextService;
import cn.e3mall.pojo.TbContent;

@Controller
public class PortalController {
	@Value(value="${BIG_AD}")
	private String BIG_AD;
	@Autowired
	private ContextService contextServiceImpl;
	
	@RequestMapping("/index")
	public String index(Model model){
		List<TbContent> ad1List = contextServiceImpl.getBigAd(BIG_AD);
		model.addAttribute("ad1List", ad1List);
		return "index";
	}
}
