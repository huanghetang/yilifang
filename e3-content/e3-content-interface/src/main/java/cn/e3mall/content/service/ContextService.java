package cn.e3mall.content.service;

import java.util.List;

import cn.e3mal.common.EasyUIDataGridResult;
import cn.e3mal.common.EasyUIDateTreeResutl;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.utils.E3Result;

public interface ContextService {

	/**
	 * 查询内容分类列表
	 * <p>Title: contentCategoryList</p>
	 * <p>Description: </p>
	 * @param parentId
	 * @return
	 */
	List<EasyUIDateTreeResutl> contentCategoryList(long parentId); 
	
	/**
	 * 增加内容分类
	 */
	E3Result addContentCategory(long parentId,String name);
	
	/**
	 * 修改内容分类
	 */
	void editContentCategoryById(long id,String name);
	
	/**
	 * 删除内容分类
	 */
	void deleteContentCategoryById(long id);
	
	 void deleteContentCategoryById1(long id);

	EasyUIDataGridResult getContentPageList(Long categoryId, int page, int rows);

	E3Result addContent(TbContent tbContent);

	List<TbContent> getBigAd(String bIG_AD);
}
