package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

public class LoginServlet extends HttpServlet {
	public void service(HttpRequest request,HttpResponse response) {
		try(
				RandomAccessFile raf = new RandomAccessFile("user.dat", "r");
				) {
			String username = request.getPrameter("username");
			System.out.println("username:"+username);
			String password = request.getPrameter("password");
			System.out.println("password:"+password);
			if(username==null||password==null) {
				forward("/myweb/login_fail.html",request,response);
				return;
			}
			boolean haveURL = false;
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)) {
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)) {
						forward("/myweb/login_success.html",request,response);
						return;
					}
					break;
				}
			}
			forward("/myweb/login_fail.html",request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
