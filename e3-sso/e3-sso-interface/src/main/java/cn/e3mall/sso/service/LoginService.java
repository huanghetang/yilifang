package cn.e3mall.sso.service;

import cn.e3mall.pojo.TbUser;
import cn.e3mall.utils.E3Result;

public interface LoginService {

	/**
	 * 单点登陆
	 */
	E3Result doLogin(String username,String password);

	/**
	 * 校验用户会话状态并返回用户信息
	 * <p>Title: checkLogin</p>
	 * <p>Description: </p>
	 * @param token
	 * @return
	 */
	E3Result checkLogin(String token);

	/**
	 * 用户退出
	 * <p>Title: logout</p>
	 * <p>Description: </p>
	 * @param _ticket
	 */
	void logout(String _ticket);
}
