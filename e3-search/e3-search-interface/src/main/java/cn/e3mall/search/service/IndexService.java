package cn.e3mall.search.service;

import java.util.List;


import cn.e3mal.common.Item;
import cn.e3mall.pojo.SearchItem;

public interface IndexService {
	/**
	 * manager-web后台创建所有索引
	 * <p>Title: createItemIndex</p>
	 * <p>Description: </p>
	 * @return
	 */
	boolean createItemIndex();
	
	/**
	 * search-web 从索引库查询商品
	 * <p>Title: FindItemByKeyword</p>
	 * <p>Description: </p>
	 * @param keyword
	 * @return
	 */
	SearchItem FindItemByKeyword(String keyword,int page,int size) throws Exception ;
}
