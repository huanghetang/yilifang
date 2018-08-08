package cn.e3mall.item.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.item.pojo.Item4Detail;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	
	/**
	 * 查询商品详情
	 * <p>Title: itemDetail</p>
	 * <p>Description: </p>
	 * @param itemId
	 * @param model
	 * @return
	 */
	@RequestMapping("/item/{itemId}")
	public String itemDetail(@PathVariable("itemId") Long itemId,Model model){
		//查询商品信息对象
		TbItem tbItem = itemService.findItemById(itemId);
		Item4Detail item4Detail = new Item4Detail(tbItem);
		//查询商品描述对象
		TbItemDesc itemDesc = itemService.findItemDesc(itemId);
		//绑定数据
		model.addAttribute("item", item4Detail);
		model.addAttribute("itemDesc", itemDesc);
		return "item";
	}
	


}
