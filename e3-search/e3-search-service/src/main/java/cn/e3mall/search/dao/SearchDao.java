package cn.e3mall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mal.common.Item;
import cn.e3mall.pojo.SearchItem;

@Repository
public class SearchDao {

	@Autowired
	private SolrServer solrServer;
	
	public SearchItem searchItem(SolrQuery query) throws SolrServerException{
		QueryResponse queryResponse = solrServer.query(query);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		long recourdCount = solrDocumentList.getNumFound();
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		List<Item> itemList = new ArrayList<Item>();
		for (SolrDocument solrDocument : solrDocumentList) {
			Map<String, List<String>> map = highlighting.get((String)solrDocument.get("id"));
			String title = "";
			List<String> list = map.get("item_title");
			if(list !=null && list.size()>0){
				 title = list.get(0);
			}else{
				title = (String) solrDocument.get("item_title");
			}
			Item item = new Item();
			item.setTitle(title);
			item.setId((String) solrDocument.get("id"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			item.setPrice((long) solrDocument.get("item_price"));
			item.setImage((String) solrDocument.get("item_image"));
			item.setName((String) solrDocument.get("item_category_name"));
			itemList.add(item);
		}
		
		SearchItem result = new SearchItem();
		result.setRecourdCount(recourdCount);
		result.setItemList(itemList);
		return result;
	}
	
}
