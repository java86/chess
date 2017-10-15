package com.chess;

import java.io.IOException;
import java.io.OutputStream;

public class StartChessEngine implements Runnable {
	private  Process p;
	public static long updateTime=System.currentTimeMillis();

	public StartChessEngine(Process p) {
		this.p = p;
	}

	public static void main(String[] args) throws IOException {
		// 使引擎在空闲状态
		final String cmd = "chessEngine\\XQSPIRIT.exe";
		Process p = Runtime.getRuntime().exec(cmd);
		final OutputStream outputStream = p.getOutputStream();
		outputStream.write("ucci\r\n".getBytes());
		outputStream.write("setoption Hash 520\r\n".getBytes());
		outputStream.flush();
		 new Thread(new StartChessEngine(p)).start();
//		 new Thread(new Runnable(){
//			@Override
//			public void run() {
//				long currentTimeMillis = System.currentTimeMillis();
//				while(true){
//					if(currentTimeMillis-updateTime>1000*20){
//						try {
//							outputStream.write("stop\r\n".getBytes());
//							outputStream.flush();
//							System.out.println("stop");
//							Thread.sleep(2000);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}}).start();
//		 position fen 3a1kb2/4a4/4b4/9/9/6np1/9/9/3p5/4K3c w
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
