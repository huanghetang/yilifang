package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.search.service.IndexService;
import cn.e3mall.utils.E3Result;

@Controller
public class SolrManager {
	
	@Autowired
	private IndexService indexServiceImpl;
	
	/**
	 * 后台创建索引
	 * <p>Title: addDocuments</p>
	 * <p>Description: </p>
	 * @return
	 */
	@RequestMapping("/index/item/import")
	@ResponseBody
	public E3Result addDocuments(){
		boolean b = indexServiceImpl.createItemIndex();
		if(b){
			return E3Result.ok();
		}
		return new E3Result(500,"创建索引失败",null);
	}

}
