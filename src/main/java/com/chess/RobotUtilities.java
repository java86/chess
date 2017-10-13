package com.chess;

import java.awt.AWTException;
import java.util.Arrays;
import java.util.List;

import org.openimaj.image.MBFImage;

public class RobotUtilities {
	private static String flag;// 标明我方是红还是黑
	private static String preFen = "RNBAKABNR/9/1C5C1/P1P1P1P1P/9/9/p1p1p1p1p/1c5c1/9/rnbakabnr";// 上一个局面，用于检测对方是否已经下棋。
	private static final String RED = "w";// 红方
	private static final String BLACK = "b";// 黑方
	private static final List<Character> moveflag = Arrays
			.asList(new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' });

	public static void main(String[] args) throws AWTException {
		// Robot robot = new Robot();
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// System.out.println(screenSize.getHeight());
		// System.out.println(screenSize.getWidth());
		// robot.mouseMove(1920, 1080);
		// robot.mousePress(KeyEvent.BUTTON1_MASK);
		// robot.mouseRelease(KeyEvent.BUTTON1_MASK);
		// robot.mouseWheel(10000);
		refreshFen("g0e2");
		System.out.println(preFen);
	}

	public static void playByBestmove(String bestmove) throws AWTException {
		// 根据bestmove拿到屏幕坐标
		Point[] clickPoints = ScreenUtilities.getPointsByBestmove(bestmove);
		// 依次点击屏幕
		MouseClickUtilities.clickInOrder(clickPoints);
		// 更新当前局面
		refreshFen(bestmove);

	}

	private static void refreshFen(String bestmove) {
		String[] lineFens = preFen.split("/");
		int length = lineFens.length;
		// 处理起点坐标
		char find = 0;
		String startPoint = bestmove.substring(0, 2);
		Integer startLineNum = length - (Integer.valueOf(startPoint.charAt(1)) - 48) - 1;
		int startIndexOfLine = moveflag.indexOf(startPoint.charAt(0));
		String startlineFen = lineFens[startLineNum];// 找出起点坐标所在行
		// 找出位于起点坐标的棋子，移除
		char[] startChar = startlineFen.toCharArray();
		int startCount = -1;
		int startCharLength = startChar.length;
		for (int index = 0; index < startCharLength; index++) {
			char c = startChar[index];
			boolean digit = Character.isDigit(c);
			if (digit)
				startCount += Integer.valueOf(c) - 48;
			else
				startCount++;
			if (startCount == startIndexOfLine) {
				find = c;
				startChar[index] = '1';
				break;
			}
		}
		// 替换起点坐标所在行的fen
		char[] replaceFen = new char[9];
		Integer sum = 0;
		for (int index = 0; index < startCharLength; index++) {
			char c = startChar[index];
			boolean digit = Character.isDigit(c);
			if (digit) {
				sum += Integer.valueOf(c) - 48;
				continue;
			} else {
				if (sum != 0) {
					replaceFen[index] = c;
					replaceFen[index - 1] = (char) (sum + 48);
					sum = 0;
				} else {
					replaceFen[index] = c;
				}

			}
		}
		if (sum != 0)
			replaceFen[8] = (char) (sum + 48);
		String startReplace = new String(replaceFen).replaceAll("\0+", "");// 去除空格，char默认值为\0
		lineFens[startLineNum] = startReplace;
		// 处理终点坐标
		String endPoint = bestmove.substring(2, 4);
		Integer endLineNum = length - (Integer.valueOf(endPoint.charAt(1)) - 48) - 1;
		int endIndexOfLine = moveflag.indexOf(endPoint.charAt(0));
		String endLineFen = lineFens[endLineNum];// 找出终点坐标所在行
		// 找出终点坐标的索引
		char[] endChar = endLineFen.toCharArray();
		int endCharLength = endChar.length;
		int endCount = -1;
		int findIndex = 0;
		for (int index = 0; index < endCharLength; index++) {
			char c = endChar[index];
			boolean digit = Character.isDigit(c);
			if (digit)
				endCount += Integer.valueOf(c) - 48;
			else
				endCount++;

			if (endCount >= endIndexOfLine) {
				findIndex = index;
				break;
			}
		}
		// 替换终点坐标的所在行的fen
		char[] endReplaceC = new char[10];
		for (int index = 0; index < endCharLength; index++) {
			char c = endChar[index];
			if (index < findIndex) {
				endReplaceC[index] = c;
			} else if (index == findIndex) {
				boolean digit = Character.isDigit(c);
				if (digit) {
					Integer cValue = Integer.valueOf(c);
					int char2int = cValue - 48;
					int reallyIndex = index + char2int - 1;
					if (reallyIndex > endIndexOfLine) {
						// 分成三部分
						if (endIndexOfLine == index) {
							endReplaceC[index] = find;
							endReplaceC[index + 1] = (char) (cValue - 1);
						} else {
							endReplaceC[index] = (char) (endIndexOfLine - index + 48);
							endReplaceC[index + 1] = find;
							endReplaceC[index + 2] = (char) (reallyIndex - endIndexOfLine + 48);
						}
					} else {
						if (char2int > 1) {
							endReplaceC[index] = (char) (cValue - 1);
							endReplaceC[index + 1] = find;
						} else {
							endReplaceC[index] = find;
						}
					}
				} else {
					endReplaceC[index] = find;
				}

			} else {
				endReplaceC[index + 2] = c;
			}
		}
		String endReplace = new String(endReplaceC).replaceAll("\0+", "");
		lineFens[endLineNum] = endReplace;
		// 更新fen
		StringBuilder sb = new StringBuilder();
		for (String s : lineFens) {
			sb.append(s).append("/");
		}
		preFen = sb.substring(0, sb.length() - 1);

	}

	public static String prepareToPlay() throws AWTException {
		while (true) {
			// 检查当前界面
			Point p = ScreenUtilities.needClick();
			if (p != null) {
				// 点击当前屏幕
				MouseClickUtilities.clickByXY(p.x, p.y);
				continue;
			}
			// 获取当前页面
			List<MBFImage> grapChessPiecesByScreen = ScreenUtilities.grapChessPiecesByScreen();
			// 获取当前局面的fen
			String fenByImages = FenUtilities.getFenByImages(grapChessPiecesByScreen);
			// 对方还没走棋，继续下一个循环
			if (fenByImages.equals(preFen))
				continue;
			preFen = fenByImages;
			initTag(fenByImages);
			fenByImages = "position fen" + fenByImages + " " + flag + "\r\n";
			return fenByImages;

		}
	}

	private static void initTag(String fenByImages) {
		int w = fenByImages.indexOf(ChessFlag.红帅.getFlag());
		int b = fenByImages.indexOf(ChessFlag.黑将.getFlag());
		if (w > b)
			flag = RED;
		else
			flag = BLACK;
	}
}
