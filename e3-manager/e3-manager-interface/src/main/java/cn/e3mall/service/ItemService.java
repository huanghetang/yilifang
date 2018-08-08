package cn.e3mall.service;

import java.util.List;

import cn.e3mal.common.EasyUIDataGridResult;
import cn.e3mal.common.EasyUIDateTreeResutl;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.utils.E3Result;

public interface ItemService {
/**
 * 业务逻辑：根据商品id查询商品信息。
 */
	TbItem  findItemById(Long id);
	
	/**
	 * 分页查询
	 */
	EasyUIDataGridResult getPageData(int page,int rows);
	
	/**
	 * 获取easyui-datatree组件的子节点
	 */
	List<EasyUIDateTreeResutl> getCataList(long parentId);
	
	/**
	 * 添加商品
	 */
	E3Result addItem(TbItem item,String desc);

	TbItemDesc findItemDesc(Long itemId);
		
		
		
}
