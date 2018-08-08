package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.tools.doclets.internal.toolkit.Content;

import cn.e3mal.common.EasyUIDataGridResult;
import cn.e3mal.common.EasyUIDateTreeResutl;
import cn.e3mall.content.service.ContextService;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.utils.E3Result;
import cn.e3mall.utils.JsonUtils;

@Service
public class ContextServiceImpl implements ContextService {
	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	
	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	
	@Autowired
	private JedisClient jedisClient;
	@Override
	public List<EasyUIDateTreeResutl> contentCategoryList(long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		List<EasyUIDateTreeResutl> treeNodeList = new ArrayList<EasyUIDateTreeResutl>();
		for (TbContentCategory contentCategory : list) {
			EasyUIDateTreeResutl treeNode = new EasyUIDateTreeResutl();
			treeNode.setId(contentCategory.getId());
			treeNode.setText(contentCategory.getName());
			treeNode.setState(contentCategory.getIsParent()?"closed":"open");
			treeNodeList.add(treeNode);
		}
		return treeNodeList;
	}

	@Override
	public E3Result addContentCategory(long parentId, String name) {
		TbContentCategory tbContentCategory = new TbContentCategory();
		tbContentCategory.setParentId(parentId);
		tbContentCategory.setName(name);
		//该类目是否为父类目，1为true，0为false,新建子节点为false
		tbContentCategory.setIsParent(false);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		tbContentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		tbContentCategory.setStatus(1);
		Date date = new Date();
		tbContentCategory.setCreated(date);
		tbContentCategory.setUpdated(date);
		//插入分类
		 tbContentCategoryMapper.insert(tbContentCategory);
		//判断父分类is_parent如果是0更新为1 
		TbContentCategory parentCategory = tbContentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parentCategory.getIsParent()){
			parentCategory.setIsParent(true);
			tbContentCategoryMapper.updateByPrimaryKey(parentCategory);
		}
		//响应结果
		return E3Result.ok(tbContentCategory);
	}

	@Override
	public void editContentCategoryById(long id, String name) {
		TbContentCategory tbContentCategory = new TbContentCategory();
		tbContentCategory.setId(id);
		tbContentCategory.setName(name);
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		tbContentCategoryMapper.updateByExampleSelective(tbContentCategory,example);
	}

	/**
	 * 该方法只删除当前节点,子节点的垃圾数据还保留在数据库
	 * <p>Title: deleteContentCategoryById</p>
	 * <p>Description: </p>
	 * @param id
	 * @see cn.e3mall.content.service.ContextService#deleteContentCategoryById(long)
	 */
	@Deprecated
	@Override
	public void deleteContentCategoryById(long id) {
		//查询父节点
		TbContentCategory parentNode = tbContentCategoryMapper.selectByPrimaryKey(id);
		//获取到父节点id
		Long parentId = parentNode.getParentId();
		//查询子节点
		//删除
		tbContentCategoryMapper.deleteByPrimaryKey(id);
		//判断有没有子节点,如果没有子节点,把is_parent设为0;该类目是否为父类目，1为true，0为false
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		int sonNum = tbContentCategoryMapper.countByExample(example);
		//没有子节点
		if(sonNum==0){
			TbContentCategory contentCategory = new TbContentCategory();
			//设置父节点的主键
			contentCategory.setId(parentId);
			contentCategory.setIsParent(false);
			tbContentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
		}
	}
	
	/**
	 * 如果此节点是父节点,删除父节点以及所有子节点,最后可以改变该节点父节点的状态
	 * <p>Title: deleteContentCategoryById</p>
	 * <p>Description: </p>
	 * @param id
	 * @see cn.e3mall.content.service.ContextService#deleteContentCategoryById(long)
	 */
	@Override
	public void deleteContentCategoryById1(long id) {
		//查询子节点
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		List<TbContentCategory> sonNodeList = tbContentCategoryMapper.selectByExample(example);
		if(sonNodeList!=null && sonNodeList.size()>0){//有子节点
			for (TbContentCategory sonNode : sonNodeList) {
				long nodeId = sonNode.getId();
				//删除子节点,迭代
				deleteContentCategoryById1(nodeId);
			}
			//判断该节点父节点后代个数,若为0,则改变is_parent状态
			TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
			Long parentId = tbContentCategory.getParentId();
			criteria.andParentIdEqualTo(parentId);
			List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
			if(list==null || list.size()==0 ){
				TbContentCategory record = new TbContentCategory();
				record.setId(parentId);
				record.setIsParent(false);
				tbContentCategoryMapper.updateByPrimaryKeySelective(record);
			}
			//字节删除完,删除该节点
			tbContentCategoryMapper.deleteByPrimaryKey(id);
		}else{//无子节点
			//删除该节点
			tbContentCategoryMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public EasyUIDataGridResult getContentPageList(Long categoryId, int page, int rows) {
		//查询分页数据
		PageHelper.startPage(page, rows);
		//查询条件
		TbContentExample example = new TbContentExample();
		cn.e3mall.pojo.TbContentExample.Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//执行查询
		List<TbContent> list = tbContentMapper.selectByExample(example);
		//初始化结果信息
		PageInfo pageInfo = new PageInfo(list);
		//获取总条数
		long total = pageInfo.getTotal();
		//创建并初始化EasyUi分页数据
		EasyUIDataGridResult easyUIDataGridResult = new EasyUIDataGridResult();
		easyUIDataGridResult.setTotal(total);
		easyUIDataGridResult.setRows(list);
		return easyUIDataGridResult;
	}

	@Override
	public E3Result addContent(TbContent tbContent) {
		Date date = new Date();
		tbContent.setCreated(date);
		tbContent.setUpdated(date);
		tbContentMapper.insert(tbContent);
		//删除缓存,下次查询时重新查询数据库并加入缓存
		jedisClient.hdel(CONTENT_KEY, tbContent.getCategoryId().toString());
		return  E3Result.ok();
	}

	@Override
	public List<TbContent> getBigAd(String bIG_AD) {
		//先查询缓存,如果有缓存直接返回
		 try {
			String contentJsonList = jedisClient.hget(CONTENT_KEY, bIG_AD);
			 if(StringUtils.isNotBlank(contentJsonList)){
				 List<TbContent> contentList = JsonUtils.jsonToList(contentJsonList, TbContent.class);
				 return contentList;
			 }
		} catch (Exception e) {
			e.printStackTrace();
			//打印日志
		}
		TbContentExample example = new TbContentExample();
		cn.e3mall.pojo.TbContentExample.Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(Long.parseLong(bIG_AD));
		List<TbContent> list = tbContentMapper.selectByExample(example);
		//将查询的结果放入缓存
		try {
			String jsonList = JsonUtils.objectToJson(list);
			jedisClient.hset(CONTENT_KEY, bIG_AD,jsonList);
		} catch (Exception e) {
			e.printStackTrace();
			//打印日志
		}
		return list;
	}
	


}
