package cn.e3mall.item.web.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Student;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

@Controller
public class ItemHtmlProcessController {
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	
	@RequestMapping("/process")
	@ResponseBody
	public String processDetialHtml() throws Exception{
		//用freeMarkerConfigurer得到实现类freeMarker配置对象
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
		//获取模板
		Template template = configuration.getTemplate("student.ftl");
		//创建hash数据
		HashMap hashMap = new HashMap();
		ArrayList<Student> list = new ArrayList<Student>();
		list.add(new Student("1","景甜1",18,"航头镇"));
		list.add(new Student("2","景甜2",18,"航头镇"));
		list.add(new Student("3","景甜3",18,"航头镇"));
		list.add(new Student("4","景甜4",18,"航头镇"));
		list.add(new Student("5","景甜5",18,"航头镇"));
		list.add(new Student("6","景甜6",18,"航头镇"));
		list.add(new Student("7","景甜7",18,"航头镇"));
		list.add(new Student("8","景甜8",18,"航头镇"));
		hashMap.put("stuList", list);
		hashMap.put("date", new Date());
		hashMap.put("testnull", null);
		hashMap.put("hello", "Hello");
		hashMap.put("message", "hello world");
		//创建流
		Writer writer = new FileWriter(new File("E:/hm/Hello2.html"));
		//使用模板写文件
		template.process(hashMap, writer);
		//关流
		writer.close();
		return "OK";
	}
}
