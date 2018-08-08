package vip.hht.solrj;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ4Cloud {
	//zookeeper地址列表
	private String zkHost = "192.168.25.129:2182,192.168.25.129:2183,192.168.25.129:2184";
	@Test
	public void test1() throws SolrServerException, IOException{
		//创建ColudSolrServer对象
		CloudSolrServer solrServer = new CloudSolrServer(zkHost) ;
		//设置defaultCollection
		solrServer.setDefaultCollection("collection2");
		//创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		//设置域
		document.setField("id", "001");
		document.setField("item_title", "测试");
		document.setField("item_sell_point", "便宜");
		document.setField("item_price", "11");
		document.setField("item_image", "www.baidu.com");
		document.setField("item_category_name", "测试");
		//保存文档对象
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	

}
