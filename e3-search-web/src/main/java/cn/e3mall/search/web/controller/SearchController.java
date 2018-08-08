package cn.e3mall.search.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.pojo.SearchItem;
import cn.e3mall.search.service.IndexService;

@Controller
public class SearchController {

	@Value("${SEARCH_PAGE_SIZE}")
	private String SEARCH_PAGE_SIZE;
	
	@Autowired
	private IndexService indexService;
	
	/**
	 *检索商品
	 * @throws Exception 
	 */
	@RequestMapping("/search")
	public String queryKeyword(String keyword,@RequestParam(defaultValue="1") Integer page,Model model) throws Exception{
		keyword = new String(keyword.getBytes("iso-8859-1"),"utf-8");
		//int a = 1/0;
		if(page<1) page=1;
		SearchItem  searchItem = indexService.FindItemByKeyword(keyword, page, Integer.parseInt(SEARCH_PAGE_SIZE));
		//关键字
		model.addAttribute("query", keyword);
		//总记录数
		model.addAttribute("totalPages", searchItem.getTotalPages());
		//总条数
		model.addAttribute("page", page);
		//总页数
		model.addAttribute("recourdCount", searchItem.getRecourdCount());
		//结果集
		model.addAttribute("itemList", searchItem.getItemList());
		return "search";
	}
	
}
