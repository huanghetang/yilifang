package cn.e3mall.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.LoginService;
import cn.e3mall.utils.E3Result;
import cn.e3mall.utils.JsonUtils;

@Service
public class LoginServiceImpl implements LoginService {
	@Autowired
	private TbUserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	@Value("${SESSION_EXPIRE}")
	private int SESSION_EXPIRE;
	
	@Override
	public E3Result doLogin(String username, String password) {
		//查询用户名是否存在
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			return E3Result.build(400, "用户信息不完整,登陆失败!");
		}
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> userList = userMapper.selectByExample(example );
		//不存在返回登陆失败
		if(userList==null || userList.size()==0){
			return E3Result.build(400, "用户名或密码错误,登陆失败");
		}
		//存在查询密码
		TbUser user = userList.get(0);
		String dbPassword = user.getPassword();
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
		//对password用MD5加密后比对密码
		if(!dbPassword.equals(md5Password)){
			//错误返回
			return E3Result.build(400, "用户名或密码错误,登陆失败");
		}
		//成功后,创建SESSION+UUID模拟sessionid并作为redis的key
		String key = "SESSION:"+UUID.randomUUID();
		//将用户密码置为null后作为redis的value
		user.setPassword(null);
		jedisClient.set(key, JsonUtils.objectToJson(user));
		//将用户信息保存到redis,设置过期时间
		jedisClient.expire(key, SESSION_EXPIRE);
		//返回reids的key
		return E3Result.ok(key);
	}

	@Override
	public E3Result checkLogin(String token) {
		if(StringUtils.isBlank(token)){
			return E3Result.build(400, "没有token,当前用户没有登陆!");
		}
		//到redis中查询
		String jsonStr = jedisClient.get(token);
		//查不到,返回错误信息
		if(StringUtils.isBlank(jsonStr)){
			return E3Result.build(400, "系统超时,请重新登陆!");
		}
		//命中后,刷新过期时间
		jedisClient.expire(token, SESSION_EXPIRE);
		TbUser user = JsonUtils.jsonToPojo(jsonStr, TbUser.class);
		return E3Result.ok(user);
	}

	@Override
	public void logout(String _ticket) {
		if(StringUtils.isBlank(_ticket)){
			return;
		}
		jedisClient.expire(_ticket, 0);
	}

}
