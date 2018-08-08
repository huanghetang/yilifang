package cn.e3mall.item.pojo;

import org.apache.commons.lang3.StringUtils;

import cn.e3mall.pojo.TbItem;

/**
 * 商品详情使用,不能更改原本的TbItem
 * <p>Title: Item4Detail</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
public class Item4Detail extends TbItem{

	public Item4Detail(TbItem tbItem){
		this.setId(tbItem.getId());
		this.setTitle(tbItem.getTitle());
		this.setSellPoint(tbItem.getSellPoint());
		this.setPrice(tbItem.getPrice());
		this.setNum(tbItem.getNum());
		this.setBarcode(tbItem.getBarcode());
		this.setImage(tbItem.getImage());
		this.setCid(tbItem.getCid());
		this.setStatus(tbItem.getStatus());
		this.setCreated(tbItem.getCreated());
		this.setUpdated(tbItem.getUpdated());
	}
	
	public String[] getImages(){
		String images = this.getImage();
		if(StringUtils.isNotBlank(images)){
			String[] strings = images.split(",");
			return strings;
		}
		return null;
	}
}
