package com.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpContext;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

/**
 * 该线程负责处理客户端的请求
 * @author soft01
 *
 */
public class ClientHandler implements Runnable{
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	public void run() {
		try {
			//1.解析请求
			HttpRequest request = new HttpRequest(socket);
			HttpResponse response = new HttpResponse(socket);

			//2.处理请求
			//2.1获取请求中请求行里的资源抽象路径
			String path = request.getRequestURI();
			System.out.println("path:"+path);
			//2.2首先判断是否为请求一个业务
			HttpServlet servlet = ServerContext.getServlet(path);
			if(servlet!=null) {
				//请求业务
				servlet.service(request,response);
			
			}else {
				//2.3根据抽象路径找到webapps下对应的资源
				File file  = new File("webapps"+path);
				if(file.exists()) {
					System.out.println("该资源已找到!");
					/*
					 * 先获取该资源的文件类型
					 * index.html
					 * logo.png
					 * 
					 * 常见的文件类型与Content-Type对应的值
					 * html		text/html
					 * css		text/css
					 * js			application/javascript
					 * png		image/png
					 * gif		image/gif
					 * jpg		image/jpeg
					 */

					//将用户请求的资源设置到response中
					response.setEntity(file);
				}else {
					System.out.println("该资源不存在!");
					//响应404页面给客户端
					File notFoundPage = new File("webapps/root/404.html");
					response.setEntity(notFoundPage);
					//设置状态代码为404
					response.setStatusCode(404);
					response.setStatusReason("NOT FOUND");

				}
			}
			//3.响应客户端
			response.flush();

		}catch(EmptyRequestException e) {
			System.out.println("空请求...");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
