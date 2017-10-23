package com.chess.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.chess.config.ChessEngineConfig;

public class EngineProcess {
	private static Process p;
	private static InputStream inputStream;
	private static OutputStream outputStream;
	static {
		init();
	}

	private static void init() {
		final String cmd = "chessEngine/"+ChessEngineConfig.engine;
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
