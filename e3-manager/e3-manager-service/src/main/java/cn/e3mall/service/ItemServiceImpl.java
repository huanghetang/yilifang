package cn.e3mall.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.json.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mal.common.EasyUIDataGridResult;
import cn.e3mal.common.EasyUIDateTreeResutl;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemCatExample;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.utils.E3Result;
import cn.e3mall.utils.IDUtils;
import cn.e3mall.utils.JsonUtils;

@Service
public class ItemServiceImpl implements ItemService {
	private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
	@Autowired
	private TbItemMapper mapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;//注入JMS模板
	@Resource
	private Destination topicDestination;//注入topic
	@Autowired
	private JedisClient jedisClient;//缓存实例
	@Value("${ITEM_INFO_PRE}")
	private String ITEM_INFO_PRE;//商品明细前缀
	@Value("${ITEM_INFO_EXPIRE}")
	private Integer ITEM_INFO_EXPIRE;//商品明细过期时间
	
	@Override
	public TbItem findItemById(Long id) {
		try{
			//从缓存中查询
			String jsonValue = jedisClient.get(ITEM_INFO_PRE+":"+id+":BASE");
			if(StringUtils.isNotBlank(jsonValue)){
				 TbItem tbItem = JsonUtils.jsonToPojo(jsonValue, TbItem.class);
				 return tbItem;
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("查询缓存失败", e);
		}
		//如果没有,查询数据库
		TbItem item = mapper.selectByPrimaryKey(id);
		try{
			//添加到缓存
			jedisClient.set(ITEM_INFO_PRE+":"+id+":BASE", JsonUtils.objectToJson(item));
			//设置过期时间
			jedisClient.expire(ITEM_INFO_PRE+":"+id+":BASE", ITEM_INFO_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("添加 缓存失败",e);
		}
		return item;
	}

	@Override
	public EasyUIDataGridResult getPageData(int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		TbItemExample example = new TbItemExample();
		//执行查询
		List<TbItem> list = mapper.selectByExample(example);
		//
		PageInfo pageInfo = new PageInfo(list);
		//设置easyUi-datagrid数据
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}
	
	
	public List<EasyUIDateTreeResutl> getCataList(long parentId){
		TbItemCatExample example = new TbItemCatExample();
		cn.e3mall.pojo.TbItemCatExample.Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		//创建easyUiDataTreeResult
		ArrayList<EasyUIDateTreeResutl> retList = new ArrayList<EasyUIDateTreeResutl>();
		for (TbItemCat tbItemCat : list) {
			EasyUIDateTreeResutl treeData = new EasyUIDateTreeResutl();
			treeData.setId(tbItemCat.getId());
			treeData.setText(tbItemCat.getName());
			treeData.setState(tbItemCat.getIsParent()?"closed":"open");
			retList.add(treeData);
		}
		return retList;
	}

	@Override
	public E3Result addItem(TbItem item, String desc) {
		//获取商品的id
		final long id = IDUtils.genItemId();
		item.setId(id);
		//给商品属性赋值
		item.setStatus((byte)1);//商品状态，1-正常，2-下架，3-删除
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		//添加商品
		mapper.insert(item);
		//创建TbItemDesc商品描述对象
		TbItemDesc itemDesc = new TbItemDesc();
		//给商品描述对象赋值
		itemDesc.setItemId(id);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		itemDesc.setItemDesc(desc);
		//添加商品描述
		itemDescMapper.insert(itemDesc);
		//向mq发送商品添加成功的消息,携带新增的商品id
		jmsTemplate.send(topicDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(id+"");
				return textMessage;
			}
		});
		//创建E3Resutl对象并返回结果
		return E3Result.ok();
	}

	@Override
	public TbItemDesc findItemDesc(Long itemId) {
		try{
			//查询缓存,如果有返回
			String jsonValue = jedisClient.get(ITEM_INFO_PRE+":"+itemId+":DESC");
			if(StringUtils.isNotBlank(jsonValue)){
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(jsonValue, TbItemDesc.class);
				return tbItemDesc;
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("查询缓存失败", e);
		}
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		//添加到缓存
		try{
			jedisClient.set(ITEM_INFO_PRE+":"+itemId+":DESC",JsonUtils.objectToJson(tbItemDesc));
			jedisClient.expire(ITEM_INFO_PRE+":"+itemId+":DESC", ITEM_INFO_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("添加 缓存失败",e);
		}
		return tbItemDesc;
	}

}
