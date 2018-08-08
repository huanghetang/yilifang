package cn.e3mall.search.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mal.common.Item;
import cn.e3mall.pojo.SearchItem;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.mapper.IndexMapper;
import cn.e3mall.search.service.IndexService;

@Service
public class IndexServiceImpl implements IndexService {

	@Autowired
	private IndexMapper itemMapper;
	
	@Autowired
	private SolrServer solrServer;
	@Autowired
	private SearchDao searchDao;
	
	@Override
	public boolean createItemIndex() {
		//获取所有商品结果
		try {
			List<Item> itemList = itemMapper.selectAllItem();
			for (Item item : itemList) {
				//创建SolrInputDocument
				SolrInputDocument document = new SolrInputDocument();
				document.setField("id", item.getId());
				document.setField("item_title", item.getTitle());
				document.setField("item_sell_point", item.getSell_point());
				document.setField("item_price", item.getPrice());
				document.setField("item_image", item.getImage());
				document.setField("item_category_name", item.getName());
				//写入索引库
				solrServer.add(document);
			}
			//提交
			solrServer.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public SearchItem FindItemByKeyword(String keyword,int page,int size) throws Exception  {
		//创建solrQuery
		SolrQuery solrQuery = new SolrQuery();
		//设置查询条件
		solrQuery.setQuery(keyword);
		solrQuery.set("df","item_title");
		//设置分页条件
		solrQuery.setStart(size*(page-1));
		solrQuery.setRows(size);
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em style=\"color:red\">");
		solrQuery.setHighlightSimplePost("</em>");
		//searchDao查询
		SearchItem searchItem = searchDao.searchItem(solrQuery);
		//封装searchItem,返回结果
		long recourdCount = searchItem.getRecourdCount();
		int totalPages = (int) (recourdCount/size);
		if(recourdCount%size>0)
			totalPages++;
		searchItem.setTotalPages(totalPages);
		return searchItem;
	}

}
