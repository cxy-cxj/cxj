package com.webserver.http;
/**
 * Http协议规定内容
 * @author soft01
 *
 */

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HttpContext {
	/*
	 * 资源类型与Content-Type的对一个关系
	 * key:资源的后缀名
	 * value:Content-Type消息头对应的值
	 */
	private static final Map<String,String> mimeMapping=new HashMap<>();
	static {
		//初始化
		initMimeMapping();
	}
	/*
	 * 初始化资源类型与Content-Type的值的对应关系
	 */
	private static void initMimeMapping() {
		/*
		 * 实现:
		 * 解析web.xml文件,将根标签下所有
		 * 名为<mime-mapping>的子标签获取到,并
		 * 且将其子标签:
		 * <extension>中间的文本作为key
		 * <mime-type>中间的文本作为value
		 * 存入到mimeMapping这个Map中完成初始化
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new FileInputStream("./conf/web.xml"));//读取xml文件内容
			Element root=doc.getRootElement();//得到根标签
			List<Element> list = root.elements("mime-mapping");//根标签下的名为mime-mapping的子标签
			for(Element ele:list) {
				String key = ele.elementText("extension");//mime-mapping标签下的<extension>标签里的文本
				String value = ele.elementText("mime-type");//mime-mapping标签下的<mime-type>标签里的文本
				mimeMapping.put(key, value);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/*
	 * 根据资源类型获取对应的Content-Type的值
	 */
	public static String getMimeType(String ext) {
		return mimeMapping.get(ext);
	}
	
}
