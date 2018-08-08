package cn.e3mal.common;

import java.io.Serializable;

public class Item implements Serializable{
	private String id;
	private String title;
	private String sell_point;
	private Long price;
	private String image;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSell_point() {
		return sell_point;
	}
	public void setSell_point(String sell_point) {
		this.sell_point = sell_point;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", title=" + title + ", sell_point=" + sell_point + ", price=" + price + ", image="
				+ image + ", name=" + name + "]";
	}
	
	
	public String[] getImages(){
		if(image!=null && !"".equals(image)){
			String[] strings = image.split(",");
			return strings;
		}
		return null;
		
	}
	
	
}
