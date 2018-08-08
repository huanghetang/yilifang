package cn.e3mall.order.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.order.beans.OrderVO;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.utils.E3Result;

@Controller
public class OrderController {
	
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	
	
	/**
	 * 用户点击去结算,展示用户收货地址,订单详情,需要用户登陆
	 */
	@RequestMapping("/order/order-cart")
	public String orderCart(HttpServletRequest request){
		//用户已登陆
		TbUser user = (TbUser) request.getAttribute("user");
		//获取用户id
		Long userId = user.getId();
		//查询redis中该用户所有的订单数据
		List<TbItem> cartList = new ArrayList<TbItem>();
		cartList = cartService.getCartFromRedis(userId, cartList);
		//查询默认收货信息,已写死
		//查询所有付款方式列表,已写死
		//放入request中
		request.setAttribute("cartList", cartList);
		//返回视图 
		return "order-cart";
	}
	
	/**
	 * 用户提交订单
	 * <p>Title: submitOrder</p>
	 * <p>Description: </p>
	 * @param orderVO 包涵订单表,订单商品表,订单物流表三张表数据
	 * @param model
	 * @return
	 */
	@RequestMapping("/order/create")
	public String submitOrder(OrderVO orderVO,Model model,HttpServletRequest request){
		TbUser user = (TbUser) request.getAttribute("user");
		//调用服务插入订单对应的信息,返回E3Result包涵订单号
		E3Result e3Result = orderService.addOrder(user.getId(),orderVO);
		//给页面赋值订单号和应付金额
		model.addAttribute("orderId", e3Result.getData());
		model.addAttribute("payment", orderVO.getPayment());
		return "success";
	}
	
	public static void main(String[] args) {
		DateTime dt = new DateTime();
		String string = dt.toString("yyyy-MM-dd");
		System.out.println(string);
	}
	
}
