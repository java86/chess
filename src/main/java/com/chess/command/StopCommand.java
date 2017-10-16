package com.chess.command;

import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.chess.StartChessEngine;

public class StopCommand implements Runnable {
	private Process p;
	private static final long LIMITTIME = 1000 * 20;// 引擎最多可以考虑的时间，超过就要马上返回下法。
	private Logger log = Logger.getLogger(StopCommand.class);

	public StopCommand(Process p) {
		this.p = p;
	}

	@Override
	public void run() {
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - StartChessEngine.updateTime > LIMITTIME) {
			try {
				OutputStream outputStream = p.getOutputStream();
				outputStream.write("stop\r\n".getBytes());
				outputStream.flush();
				log.debug("stop");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
