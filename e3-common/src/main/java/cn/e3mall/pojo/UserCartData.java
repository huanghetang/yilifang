package cn.e3mall.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Order服务提交订单后,发送给Cart服务,删除购物车中对应数据
 * <p>Title: UserCartData</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
public class UserCartData implements Serializable{
	
	/** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private Long userId;
	private List<Long> itemId;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<Long> getItemId() {
		return itemId;
	}
	public void setItemId(List<Long> itemId) {
		this.itemId = itemId;
	}
	
	

}
