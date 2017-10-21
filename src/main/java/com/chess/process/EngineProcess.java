package com.chess.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EngineProcess {
	private static Process p;
	private static InputStream inputStream;
	private static OutputStream outputStream;
	static {
		init();
	}

	private static void init() {
		final String cmd = "chessEngine/BugCChess.exe";
		try {
			p = Runtime.getRuntime().exec(cmd);
			 inputStream = p.getInputStream();
			 outputStream = p.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  static InputStream getInputStream() {
		return inputStream;
	}

	public  static OutputStream getOutputStream() {
		return outputStream;
	}
}
