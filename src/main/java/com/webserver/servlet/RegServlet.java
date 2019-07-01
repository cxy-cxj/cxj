package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理用户注册业务
 * @author soft01
 *
 */
public class RegServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		System.out.println("RegServlet:开始注册...");
		/*
		 * 注册流程
		 * 1:获取用户输入
		 * 	通过request得到用户表单上输入的信息
		 * 	注意:
		 * 	request.getParameter()这个方法传入的
		 *		参数值应当与页面中表单里对应输入框的名字
		 *		一致!
		 * 2:将用户的注册信息写入文件user.dat中
		 * 	每个用户信息占用100字节,其中用户名,密码,
		 * 	昵称为字符串,各占32字节.年龄为整数占4字节.
		 * 
		 * 3:设置HttpResponse,响应注册结果页面	
		 */
		//1
		String username = request.getPrameter("username");
		String password = request.getPrameter("password");
		String nickname = request.getPrameter("nickname");
		int age = Integer.parseInt(request.getPrameter("age"));
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		System.out.println("nickname:"+nickname);
		System.out.println("age:"+age);
		try(
				RandomAccessFile raf = new RandomAccessFile("user.dat", "rw");
				) {
			/*
			 先读取user.dat文件中现有数据中
			是否已经有该用户名,若有则直接设置
			response响应一个提示页面,告知用户
			该用户名已经存在.若没有则将指针移动
			到文件末尾追加这条记录.
			reg_info.html
			该页面中提示:用户名已存在,请重新注册.
			 */
			boolean haveUser = false;//判断是否已存在
			for(int i=0;i<raf.length()/100;i++) {
				//先将指针移动到该条记录开始位置
				raf.seek(i*100);
				//读取32个字节得到用户名
				byte[] a = new byte[32];
				raf.read(a);
				String line = new String(a,"UTF-8").trim();
				if(line.equals(username)) {
					//已注册用户
					haveUser = true;
					break;
				}
			}
			if(haveUser){
				forward("/myweb/reg_info.html",request,response);
			}else {
				//2
				//将指针移动到文件末尾
				raf.seek(raf.length());
				//写用户名
				byte[] data = username.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);
				//写密码
				data = password.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);
				//写昵称
				data = nickname.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);
				//写年龄
				raf.writeInt(age);
				System.out.println("RegServlet:注册完毕!");
				//3响应客户端注册成功页面
				forward("/myweb/reg_success.html",request,response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
