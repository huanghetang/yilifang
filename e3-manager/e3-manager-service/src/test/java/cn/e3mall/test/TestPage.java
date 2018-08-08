package cn.e3mall.test;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemParamItemExample;

public class TestPage {
	
	@Test
	public void testPageHelper(){
		//初始化容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		//获取dao动态代理对象
		TbItemMapper itemMapper = ac.getBean(TbItemMapper.class);
		//初始化PageHelper
		PageHelper.startPage(1, 10);
		TbItemExample example = new TbItemExample();
		//查询结果
		List<TbItem> list = itemMapper.selectByExample(example );
		//创建分页对象
		PageInfo pageInfo = new PageInfo(list);
		//打印结果
		System.out.println(pageInfo.getTotal());
		System.out.println(pageInfo.getPageSize());
		System.out.println(pageInfo.getPages());
	}

}
