package com.webserver.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 将user.dat文件中每个用户都输出到控制台
 * @author soft01
 *
 */
public class ShowAllUserDemo {

	public static void main(String[] args) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("user.dat", "r");
		for(int i = 0;i<raf.length()/100;i++) {
			//读取用户名
			byte[] data = new byte[32];
			raf.read(data);
			String username = new String(data,"UTF-8").trim();
			System.out.println(username);
			raf.read(data);
			String password = new String(data,"UTF-8").trim();
			System.out.println(password);
			raf.read(data);
			String nickname = new String(data,"UTF-8").trim();
			System.out.println(nickname);
			int a =raf.readInt();
			System.out.println(a);
			System.out.println(raf.getFilePointer());
		}
		System.out.println("读取完毕");
		raf.close();
	}

}
