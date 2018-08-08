package cn.e3mall.pojo;

import java.io.Serializable;
import java.util.List;

import cn.e3mal.common.Item;

public class SearchItem implements Serializable{
	//总页数
	private int totalPages;
	//总记录数
	private long recourdCount;
	//结果集
	private List<Item> itemList;
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public long getRecourdCount() {
		return recourdCount;
	}
	public void setRecourdCount(long recourdCount) {
		this.recourdCount = recourdCount;
	}
	public List<Item> getItemList() {
		return itemList;
	}
	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
	

	
}
