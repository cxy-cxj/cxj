package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类没一个实例用于表示客户端发送过来的请求内容.
 * 
 * 一个请求包含三部分:请求行,消息头,消息正文
 * @author soft01
 *
 */
public class HttpRequest {
	/*
	 *	请求行相关信息
	 */
	//请求方式
	private String method;
	//请求资源的抽象路径
	private String url;
	//请求使用的协议版本
	private String protocol;
	//抽象路径中的请求部分
	private String requestURI;
	//抽象路径中的参数部分
	private String queryString;
	//保存具体每一个参数
	private Map<String,String> parameters = new HashMap<>();
	
	/*
	 * 请求头相关信息
	 */
	private Map<String, String> headers = new HashMap<>();
	/*
	 * 请求正文相关信息
	 */
	/*
	 * 与客户端连接相关的属性消息
	 */
	private Socket socket;
	//通过Socket获取的输入流,用于读取客户端消息
	private InputStream in;
	/*
	 * 构造方法,用于初始化请求对象
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/*
			 * 实例化请求对象要分为三部分解析:
			 * 1:解析请求行
			 * 2:解析消息头
			 * 3:解析消息正文
			 */
			System.out.println("HttpRequest:开始解析请求...");
			parseRequestLine();
			parseHeaders();
			parseContent();
			System.out.println("HttpRequest:解析请求完毕!");
		} catch(EmptyRequestException e) {
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 解析请求行
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("开始解析请求行...");
		/*
		 * 1:通过输入流读取第一行字符串(请求行内容)
		 * 2:将请求行内容按照空格拆分为三部分
		 * 3:将这三部分内容设置到请求行对应属性
		 *  	method,url,protocol上完成解析请求行
		 *  		的工作.
		 */
		try {	
			//读取第一行字符串,请求行的内容
			String line = readLine();
			//判断是否为空请求
			if("".equals(line)) {
				//抛出空请求异常
				throw new EmptyRequestException();
			}
			String[] arr = line.split("\\s");
			method = arr[0];
			url = arr[1];
			protocol = arr[2];
			//进一步解析抽象路径
			parseURL();
			System.out.println("method:"+method);
			System.out.println("url:"+url);
			System.out.println("protocol:"+protocol);
			System.out.println("解析请求行完毕!");
		}catch(EmptyRequestException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 进一步解析抽象路径部分
	 */
	private void parseURL() {
		System.out.println("进一步解析抽象路径...");
		/*
		 * 抽象路径部分有两种情况
		 * 1:带参数 2:不带参数
		 * 是否带参数可以通过判断url中是否含有"?"
		 * 来决定.
		 * 
		 * 如果不含有参数,那么直接将url的值赋值
		 * 给requestURI即可.
		 * 
		 * 如果含有参数,那么首先将url的值按照"?"
		 * 拆分为两部分
		 * 第一部分赋值给requestURI
		 * 第二部分赋值个queryString
		 * 
		 * 然后在对queryString进行拆分,首先按照
		 * "&"拆分出每哟个参数.接着每个参数在按照
		 * "="拆分为参数名和参数值,并大恩别作为key
		 * 和value保存到parameters中即可.
		 */
		try {
			if(url.indexOf("?")==-1) {//若不存在"?"
				requestURI=url;
			}else {
				String[] arr = url.split("\\?");//正则表达式里的?有特殊含义
				requestURI = arr[0];
				if(arr.length>1) {
					queryString = arr[1];
					/*
					 * 首先对参数部分解码(将"%XX"内容还原对应字符)
					 * username = %E8%8C%83&password=...
					 * 经过解码得到的字符串为:
					 * username = 范&password=...
					 */
					queryString = URLDecoder.decode(queryString, "UTF-8");
					String[] lines = queryString.split("&");
					for(String str :lines) {
						String[] as = str.split("=");
						if(as.length>1) {
							parameters.put(as[0], as[1]);
						}else {
							parameters.put(as[0], null);
						}
					}
				}
			}
			System.out.println("requestURI:"+requestURI);
			System.out.println("queryString:"+queryString);
			System.out.println("parameters:"+parameters);
			System.out.println("解析抽象路径完成!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 解析消息头
	 */
	private void parseHeaders() {
		System.out.println("开始解析消息头...");
		try {
			/*
			 * 循环调用readLine方法读取每一个
			 * 消息头,如果readLine方法返回的
			 * 字符串是一个空字符串时,说明这次
			 * 单独读取到了CRLF,那么循环就应当
			 * 停止了/
			 * 每当我们读取到一个消息头后,就可以
			 * 按照"冒号空格"即": "进行拆分,
			 * 拆分出的内容就是消息头的名字和对应
			 * 的值,然后将他们分别以key,value形式
			 * 存入到headers这个Map中完成消息头
			 */
			while(true) {
			String line = readLine();
			if("".equals(line)) {
				break;
			}
			String[] arr=line.split(":\\s");
			headers.put(arr[0], arr[1]);
			System.out.println(line);
			}
			System.out.println("headers:"+headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("解析消息头完毕!");
	}
	/*
	 * 解析消息正文
	 */
	private void parseContent() {
		System.out.println("开始解析消息正文...");

		System.out.println("解析消息正文完毕!");
	}
	/*
	 * 通过客户端输入流读取一行字符串(以CRLF结尾的)
	 */
	private String readLine() throws Exception {
		try {
			StringBuilder builder = new StringBuilder();
			int d =-1;//记录每次读取到的字节
			//c1表示上次读取的字符,c2表示本次读取到的字符
			char c1 = 'a',c2='a';
			while((d=in.read())!=-1) {
				c2=(char)d;
				//是否上次读取到的是CR,本次读取到的是LF
				if(c1==13&&c2==10) {
					break;
				}
				builder.append(c2);
				c1=c2;
			}
			return builder.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public String getProtocol() {
		return protocol;
	}
	/*
	 * 根据名字获取值
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	public Socket getSocket() {
		return socket;
	}
	public InputStream getIn() {
		return in;
	}
	public String getRequestURI() {
		return requestURI;
	}
	public String getQueryString() {
		return queryString;
	}
	/*
	 * 根据参数名获取对应的参数值,若给定的参数不存在
	 * 则返回值为null
	 */
	public String getPrameter(String name) {
		return this.parameters.get(name);
	}
}
