package com.chess.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.chess.StartChessEngine;
import com.chess.config.ChessEngineConfig;
import com.chess.process.EngineProcess;

public class ChessEngineUtilities {
	private static Logger log = Logger.getLogger(ChessEngineUtilities.class);

	public static String getBastMove(String fen) throws IOException {
		OutputStream outputStream = EngineProcess.getOutputStream();
		outputStream.write(fen.getBytes());
		outputStream.write(ChessEngineConfig.goCommand.getBytes());
		outputStream.flush();
		StartChessEngine.requestTime = System.currentTimeMillis();
		StartChessEngine.hasReturn = false;
		// 取得命令结果的输出流
		InputStream fis = EngineProcess.getInputStream();
		// 用一个读输出流类去读
		InputStreamReader isr = new InputStreamReader(fis);
		// 用缓冲器读行
		BufferedReader br = new BufferedReader(isr);
		String bestmove = null;
		while (true) {
			bestmove = br.readLine();
			if (bestmove == null)
				continue;
			if (bestmove.startsWith("bestmove")) {
				log.debug(bestmove);
				StartChessEngine.hasReturn = true;
				return bestmove.substring(9, 13);
			} else if (bestmove.startsWith("nobestmove")
					   ||(System.currentTimeMillis()-StartChessEngine.requestTime)>1000*60*5) {
				log.debug(bestmove);
				StartChessEngine.hasReturn = true;
				return "null";
			}
			log.debug(bestmove);
		}
	}
}
