package com.chess;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.util.BlockingInputStream;

public class Channel {
	public static void main(String[] args) throws IOException, InterruptedException {
		Process exec = Runtime.getRuntime().exec("D:\\Bin\\QQScLauncher.exe");
		InputStream inputStream = exec.getInputStream();
		BlockingInputStream bin = new BlockingInputStream(inputStream);
		byte[] b = new byte[1024];
		int read = bin.read(b);
		System.out.println(read);
	}
}
