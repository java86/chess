package com.chess.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EngineProcess {
	private static Process p;
	static {
		init();
	}

	private static void init() {
		final String cmd = "chessEngine\\BugCChess.exe";
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static InputStream getInputStream() {
		do {
			InputStream inputStream = p.getInputStream();
			if (inputStream == null)
				init();
			else
				return inputStream;
		} while (true);
	}

	public synchronized static OutputStream getOutputStream() {
		do {
			OutputStream outputStream = p.getOutputStream();
			if (outputStream == null)
				init();
			else
				return outputStream;
		} while (true);
	}
}
