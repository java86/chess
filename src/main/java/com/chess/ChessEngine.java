package com.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ChessEngine {
	static Process p;

	public static void main(String[] args) throws IOException, InterruptedException {
		// test.bat中的命令是ipconfig/all
		System.out.println("dd\r\n" + "ss");
		final String cmd = "D:\\Users\\86\\chessEngine\\cyclone.exe";
		// 执行命令
		p = Runtime.getRuntime().exec(cmd);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 取得命令结果的输出流
					InputStream fis = p.getInputStream();
					// 用一个读输出流类去读
					InputStreamReader isr = new InputStreamReader(fis);
					// 用缓冲器读行
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					// 直到读完为止
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		OutputStream outputStream = p.getOutputStream();
		outputStream.write("ucci\r\n".getBytes());
		outputStream.flush();
		// outputStream.write("setoption batch true".getBytes());
		// outputStream.write("position fen
		// 5k1c1/4a4/5P3/4p4/cCC3eC1/2pp2r2/9/4C4/1r2p4/5K3 w".getBytes());
		// outputStream.write("go depth 10".getBytes());
	}
}
