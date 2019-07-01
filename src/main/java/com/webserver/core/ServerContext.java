package com.webserver.core;
/**
 * 服务端相关配置信息
 * @author soft01
 *
 */

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

public class ServerContext {
	/*
	 * 请求与对应的处理类Servlet的对应关系
	 * key:请求路径
	 * value:对应的某Servlet实例
	 */
	private static final Map<String , HttpServlet> servletMapping = new HashMap<>();
	static {
		initServletMapping();
	}
	private static void initServletMapping() {
//		servletMapping.put("/myweb/reg", new RegServlet());
//		servletMapping.put("/myweb/login", new LoginServlet());
		/*
		 * 使用DOM4J解析conf/servlets.xml文件.
		 * 将根标签下的所有<servlet>标签获取到.
		 * 并且用每个<servlet>标签中的属性:
		 * path:的值作为key
		 * className:的值得到后利用反射加载并实例化对应的
		 * 					Servlet的实例作为value
		 * 存入到servletMapping这个Map中完成初始化
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/servlets.xml"));
			Element root = doc.getRootElement();
			List<Element> eles=root.elements();
			for(Element ele: eles) {
				String path=ele.attributeValue("path");
				String classNmae = ele.attributeValue("classNmae");
				Class cls = Class.forName(classNmae);
				HttpServlet servlet = (HttpServlet)cls.newInstance();
				servletMapping.put(path, servlet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 根据请求获取对应的SErvlet,如果该请求没有
	 * 对应的Servlet方法会返回null
	 */
	public static HttpServlet getServlet(String path) {
		return servletMapping.get(path);
	}
	
}
