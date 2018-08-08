package cn.e3mall.order.service;

import cn.e3mall.order.beans.OrderVO;
import cn.e3mall.utils.E3Result;

public interface OrderService {
	
	E3Result addOrder(long userId,OrderVO orderVO);

}
