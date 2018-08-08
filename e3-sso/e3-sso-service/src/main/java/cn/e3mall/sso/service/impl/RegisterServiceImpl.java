package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.RegisterService;
import cn.e3mall.utils.E3Result;

@Service
public class RegisterServiceImpl implements RegisterService {
	@Autowired
	private TbUserMapper userMapper;
	
	@Override
	public E3Result check(String checkValue, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//type:1检查用户名,type:2检查手机号
		if(type==1){
			criteria.andUsernameEqualTo(checkValue);
		}else if(type==2){
			criteria.andPhoneEqualTo(checkValue);
		}else{
			return E3Result.ok(false);
		}
		List<TbUser> userList = userMapper.selectByExample(example);
		//该用户名或者手机号已被占用
		if(userList!=null && userList.size()>0){
			return E3Result.ok(false);
		}
		
		return  E3Result.ok(true);
	}

	@Override
	public E3Result register(TbUser user) {
		//校验用户名密码手机号
		String username = user.getUsername();
		String password = user.getPassword();
		String phone = user.getPhone();
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)
				|| StringUtils.isBlank(phone)){
			return E3Result.build(400, "用户数据不完整,注册失败!");
		}
		E3Result checkUsername = check(username,1);
		if(!(boolean) checkUsername.getData()){
			return E3Result.build(400, "用户名或密码已被占用");
		}
		E3Result checkPassword = check(password,2);
		if(!(boolean) checkPassword.getData()){
			return E3Result.build(400, "用户名或密码已被占用");
		}
		//对密码MD5加密
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
		user.setPassword(md5Password);
		//补全用户属性
		Date date = new Date();
		user.setCreated(date);
		user.setUpdated(date);
		//插入tb_user表
		userMapper.insert(user);
		//返回
		return E3Result.ok();
	}

}
