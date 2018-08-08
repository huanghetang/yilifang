package cn.e3mall.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mal.common.Item;
import cn.e3mall.search.mapper.IndexMapper;

public class AddItemIndexListener implements MessageListener {
	@Autowired
	private IndexMapper indexMapper;//注入dao
	
	@Autowired
	private SolrServer solrServer;//注入solr实例

	@Override
	public void onMessage(Message message) {
		//该线程暂停1秒,让生产者结束事务,如果添加商品的事务没有提交,数据库查不到该条记录
		try {
			Thread.sleep(1000);
			//根据商品id查询该条记录
			TextMessage msg = (TextMessage) message;
			String id = msg.getText();
			Item item = indexMapper.selectItemById(id);
			//创建一个solr文档对象
			SolrInputDocument document = new SolrInputDocument();
			//设置域
			document.setField("id", item.getId());
			document.setField("item_title", item.getTitle());
			document.setField("item_sell_point", item.getSell_point());
			document.setField("item_price", item.getPrice());
			document.setField("item_image", item.getImage());
			document.setField("item_category_name", item.getName());
			//添加到索引库
			solrServer.add(document);
			//提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
