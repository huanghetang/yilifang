package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mal.common.EasyUIDataGridResult;
import cn.e3mal.common.EasyUIDateTreeResutl;
import cn.e3mall.content.service.ContextService;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.utils.E3Result;

@Controller
public class ContextController {
	@Autowired
	private ContextService contextServiceImpl;
	
	/**
	 * 显示内容分类
	 * <p>Title: ContextCategoryList</p>
	 * <p>Description: </p>
	 * @param parentID
	 * @return
	 */
	@RequestMapping(value="/content/category/list",method=RequestMethod.GET)
	@ResponseBody
	public List<EasyUIDateTreeResutl> ContextCategoryList(@RequestParam(name="id",defaultValue="0") Long parentID){
		List<EasyUIDateTreeResutl> treeNodeList = contextServiceImpl.contentCategoryList(parentID);
		return treeNodeList;
	}
	
	/**
	 * 添加内容分类
	 * <p>Title: addContentCategory</p>
	 * <p>Description: </p>
	 * @param parentId
	 * @param name
	 * @return
	 */
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3Result addContentCategory(Long parentId,String name){
		E3Result retVal = contextServiceImpl.addContentCategory(parentId,name);
		return retVal;
	}
	/**
	 * 修改内容分类
	 */
	@RequestMapping(value="/content/category/update",method=RequestMethod.POST)
	public void editContentCategory(Long id,String name){
		contextServiceImpl.editContentCategoryById(id,name);
	}
	
	/**
	 * 删除内容分类
	 */
	@RequestMapping(value="/content/category/delete",method=RequestMethod.POST)
	@ResponseBody
	public String deleteContentCategory(Long id){
		contextServiceImpl.deleteContentCategoryById1(id);
		return "1";
	}
	
	/**
	 * 查询内容
	 * <p>Title: contentPageList</p>
	 * <p>Description: </p>
	 * @param categoryId
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value="/content/query/list",method=RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult contentPageList(Long categoryId,Integer page,Integer rows){
		//查询分页数据
		EasyUIDataGridResult easyUIDataGridResult = contextServiceImpl.getContentPageList(categoryId,page,rows);
		return easyUIDataGridResult;
	}
	
	/**
	 * 添加内容
	 */
	@RequestMapping(value="/content/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result addContent(TbContent tbContent){
		E3Result e3Result = contextServiceImpl.addContent(tbContent);
		return e3Result;
		
	}
}
