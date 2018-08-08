package cn.e3mall.order.beans;

import java.io.Serializable;
import java.util.List;

import cn.e3mall.pojo.TbOrder;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

public class OrderVO extends TbOrder implements Serializable{
	/** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	//继承的订单信息
	
	//订单对应的商品信息
	private List<TbOrderItem> orderItems;
	//订单对应物流信息
	private TbOrderShipping orderShipping;

	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}
	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
}
