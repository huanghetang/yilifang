package cn.e3mall.fast;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

public class TestFastDFS {
	
/*	@Test
	public void test1() throws Exception{
		//创建配置文件,初始化全局参数
		ClientGlobal.init("D:/hhtspace/e3-manager-web/client.conf");
		//创建追踪客户端TrackerClient
		TrackerClient trackerClient = new TrackerClient();
		//用trackerClient获取到TrackerServer服务端
		TrackerServer trackerServer = trackerClient.getConnection();
		//创建一个空的StorageServer引用
		StorageServer storageServer = null;
		//用storageClient和trackerServer创建一个StorageServer
		StorageClient storageClient = new StorageClient(trackerServer,storageServer);
		//使用storageClient上传文件
		String[] upload_file = storageClient.upload_file("D:/important/1527407449323.png", "png", null);
		for (String str : upload_file) {
			System.out.println(str);
		}
	}*/
	
		@Test
	public void test1() throws Exception{
		//创建配置文件,初始化全局参数
		ClientGlobal.init("D:/hhtspace/e3-manager-web/src/main/resources/conf/client.conf");
		//创建追踪客户端TrackerClient
		TrackerClient trackerClient = new TrackerClient();
		//用trackerClient获取到TrackerServer服务端
		TrackerServer trackerServer = trackerClient.getConnection();
		//创建一个空的StorageServer引用
		StorageServer storageServer = null;
		//用storageClient和trackerServer创建一个StorageServer
		StorageClient storageClient = new StorageClient(trackerServer,storageServer);
		//使用storageClient上传文件
//		String[] upload_file = storageClient.upload_file("D:/important/1527407449323.png", "png", null);
		//http://192.168.25.133/group1/M00/00/00/wKgZhVsfNDiAF7TlABRNjuzzsVI161.png
		int delete_file = storageClient.delete_file("group1", "M00/00/00/wKgZhVsfNDiAF7TlABRNjuzzsVI161.png");
		System.out.println(delete_file);
//		for (String str : upload_file) {
//			System.out.println(str);
//		}
	}

}
