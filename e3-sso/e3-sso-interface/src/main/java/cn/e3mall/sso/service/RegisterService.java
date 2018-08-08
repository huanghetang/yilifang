package cn.e3mall.sso.service;

import cn.e3mall.pojo.TbUser;
import cn.e3mall.utils.E3Result;

public interface RegisterService {

	/**
	 * 注册校验
	 */
	public abstract E3Result check(String checkValue,int type);
	
	/**
	 * 注册个人用户
	 */
	E3Result register(TbUser user);

}
