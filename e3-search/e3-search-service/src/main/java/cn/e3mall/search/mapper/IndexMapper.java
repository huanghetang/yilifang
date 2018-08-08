package cn.e3mall.search.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.e3mal.common.Item;

public interface IndexMapper {
	
	List<Item> selectAllItem();
	
	Item selectItemById(@Param("id") String id);

}
