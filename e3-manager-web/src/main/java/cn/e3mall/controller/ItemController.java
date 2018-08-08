package cn.e3mall.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mal.common.EasyUIDataGridResult;
import cn.e3mal.common.EasyUIDateTreeResutl;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;
import cn.e3mall.utils.E3Result;
import cn.e3mall.utils.FastDFSClient;
import cn.e3mall.utils.JsonUtils;

@Controller
public class ItemController {
	@Autowired
	private ItemService itemService;
	
	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	
	
	@RequestMapping(value="/item/{itemId}")
	public @ResponseBody  TbItem  findItemById(@PathVariable Long itemId){
		TbItem tbItem = itemService.findItemById(itemId);
		return tbItem;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getPageData(Integer page,Integer rows){
		EasyUIDataGridResult pageData = itemService.getPageData(page, rows);
		return pageData;
	}
	
	/**
	 * 获取异步dataTree组件子节点
	 */
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUIDateTreeResutl> treeResult(@RequestParam(name="id",defaultValue="0")long parentId){
		List<EasyUIDateTreeResutl> cataList = itemService.getCataList(parentId);
		return cataList;
	}
	/**
	 * 图片上传
	 */
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String uploadImage(MultipartFile uploadFile){
		Map map = new HashMap();
		try{
			String filename = uploadFile.getOriginalFilename();
			String extName = filename.substring(filename.lastIndexOf(".")+1);
			//使用fastDFSClient工具类上传图片
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
			//没有ip地址
			String storagePath = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			String url = IMAGE_SERVER_URL+storagePath;
			System.out.println(url);
			//返回Map,包涵error 0    "url" : "http://www.example.com/path/to/file.ext"
			map.put("error", 0);
			map.put("url", url);
		}catch(Exception e){
			map.put("error", 0);
			map.put("message", "错误信息");
			e.printStackTrace();
		}
		String json = JsonUtils.objectToJson(map);
		return json;
	}
	
	/** 
	 * 添加商品
	 */
	@RequestMapping(value="/item/save",method = RequestMethod.POST)
	@ResponseBody
	public E3Result addItem(TbItem item,String desc){
		E3Result e3Result = itemService.addItem(item, desc);
		return e3Result;
	}
}
