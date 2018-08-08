package item.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import cn.e3mall.item.pojo.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestFrameMarker {
	
	@Test
	public void testFrememarker() throws Exception{
		//1.创建一个模板
		//2.创建一个Configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		//3.设置路径
		configuration.setDirectoryForTemplateLoading(new File("D:/hhtspace/e3-item-web/src/main/webapp/WEB-INF/ftl"));
		//4.设置字符集
		configuration.setDefaultEncoding("utf-8");
		//5.创建一个模板对象 Template
		Template template = configuration.getTemplate("hello.java");
		//6.创建模板源数据
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("hello", "Hello");
		hashMap.put("message", "hello world");
		//7.创建一个wirter 指定输出的位置 
		FileWriter writer = new FileWriter(new File("E:/hm/Hello.java"));
		//8.使用template对象生成文件
		template.process(hashMap, writer);
		//9.关流
		writer.close();
		
	}
	
	@Test
	public void test2() throws Exception{
		//1.创建一个模板
		//2.创建一个Configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		//3.设置路径
		configuration.setDirectoryForTemplateLoading(new File("D:/hhtspace/e3-item-web/src/main/webapp/WEB-INF/ftl"));
		//4.设置字符集
		configuration.setDefaultEncoding("utf-8");
		//5.创建一个模板对象 Template
		Template template = configuration.getTemplate("student.ftl");
		//6.创建模板源数据
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
		//7.创建一个wirter 指定输出的位置 
		FileWriter writer = new FileWriter(new File("E:/hm/student.html"));
		//8.使用template对象生成文件
		template.process(hashMap, writer);
		//9.关流
		writer.close();
		
	}

}
