package com.chess;

import java.io.IOException;
import java.io.OutputStream;

public class StartChessEngine implements Runnable {
	private Process p;

	public StartChessEngine(Process p) {
		this.p = p;
	}

	public static void main(String[] args) throws IOException {
		// 使引擎在空闲状态
		final String cmd = "chessEngine\\cyclone.exe";
		Process p = Runtime.getRuntime().exec(cmd);
		OutputStream outputStream = p.getOutputStream();
		outputStream.write("ucci\r\n".getBytes());
		outputStream.flush();
		String bestmove = ChessEngineUtilities
				.getBastMove("position fen rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w\r\n", p);
		System.out.println(bestmove);
		// new Thread(new StartChessEngine(p)).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				// 处理准备开始，悔棋，求和。保证当前局面已到我方开始走棋,返回当前棋盘局面。
				String fen = RobotUtilities.prepareToPlay();
				// 通过fen获取最佳下法
				String bestmove = ChessEngineUtilities.getBastMove(fen, p);
				// 通过最佳下法操作鼠标点击
				RobotUtilities.playByBestmove(bestmove);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
