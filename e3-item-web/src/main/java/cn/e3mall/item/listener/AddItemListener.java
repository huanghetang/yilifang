package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Item4Detail;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * mq监听器,在manager-service中添加商品后会发送topic消息,在商品表现层中接受到消息,
 * 然后生成一个商品详情页面,指向nginx静态资源访问目录中,当用户点击查询商品详情时,
 * 直接访问nginx的静态页面,提高并发能力
 * <p>Title: AddItemListener</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
public class AddItemListener implements MessageListener{
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;//spring管理的freemarker configuration对象
	@Autowired
	private ItemService itemService;//商品服务
	@Value("${FREEMARKER_HTML_DIR}")
	private String FREEMARKER_HTML_DIR;//html文件输出目录
	//日志
	private static final Logger logger = LoggerFactory.getLogger(AddItemListener.class);
	
	@Override
	public void onMessage(Message message) {
		try {
			//获取商品id
			TextMessage msg = (TextMessage) message;
			String text = msg.getText();
			Long itemId = new Long(text);
			//等待商品添加事务提交
			Thread.sleep(1000);
			//查询到商品信息
			TbItem tbItem = itemService.findItemById(itemId);
			Item4Detail item4Detail = new Item4Detail(tbItem);
			//查询商品详情信息
			TbItemDesc itemDesc = itemService.findItemDesc(itemId);
			//封装到map中
			Map map = new HashMap();
			map.put("item", item4Detail);
			map.put("itemDesc", itemDesc);
			//获取到freemarker的template模板对象
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			//创建一个输出流,指定输出目录和文件名
			Writer writer = new FileWriter(new File(FREEMARKER_HTML_DIR+itemId+".html"));
			//生成文件
			template.process(map, writer);
			//关闭流
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("生成商品详情静态页面出错",e);
		}
	
		 
		
	}

}
