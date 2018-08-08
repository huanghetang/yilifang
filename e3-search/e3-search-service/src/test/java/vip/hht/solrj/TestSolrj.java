package vip.hht.solrj;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.search.service.IndexService;


public class TestSolrj {

	
	@Test
	public void addDocument(){
		//创建solrj客户端
		HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.129:10000/solr");
		//创建SolorInputDocument
		SolrInputDocument document = new SolrInputDocument();
		//赋值
		document.setField("id", "001");
		document.setField("item_title", "测试");
		document.setField("item_sell_point", "便宜");
		document.setField("item_price", "11");
		document.setField("item_image", "www.baidu.com");
		document.setField("item_category_name", "测试");
		//写入文档对象
		try {
			solrServer.add(document);
			//提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void deleteDocument(){
		//创建solrj服务接口
		HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.129:10000/solr");
		//根据id删除
		try {
			UpdateResponse deleteById = solrServer.deleteById("001");
			System.out.println(	deleteById.getStatus());;
			solrServer.commit();
			System.out.println("删除成功");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test3(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext*.xml");
		IndexService itemService = ac.getBean(IndexService.class);
		boolean b = itemService.createItemIndex();
		System.out.println(b);
		
	}
	
	@Test
	public void test5() throws SolrServerException{
		//创建solrj服务接口
		HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.129:10000/solr");
		//设置查询条件
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q", "item_title:手机");
		solrQuery.set("df", "item_keywords");
//		solrQuery.setFields("")
		//执行查询
		QueryResponse queryResponse = solrServer.query(solrQuery);
		//获取结果集
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		long totals = solrDocumentList.getNumFound();
		System.out.println("共查询到"+totals+"条数据");
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_image"));
		}
		//封装结果集
		
	}
}
