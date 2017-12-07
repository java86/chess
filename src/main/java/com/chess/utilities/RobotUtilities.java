package com.chess.utilities;

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openimaj.image.MBFImage;

import com.chess.constant.ChessFlag;
import com.chess.model.Move;
import com.chess.model.Point;

public class RobotUtilities {
	private static String flag;// 标明我方是红还是黑
	private static String preFen = "/9/9/9/9/9/9/9/9/9/9";// 上一个局面，用于检测对方是否已经下棋。
	private static String prePlay = "/9/9/9/9/9/9/9/9/9/9";// 上一个局面，用于检测我方是否已经下棋。
	private static String eatFen;// 上一个吃子局面，用于发送给引擎
	private static List<Move> moves = new ArrayList<>();
	private static final String RED = "w";// 红方
	private static final String BLACK = "b";// 黑方
	private static final List<Character> moveflag = Arrays
			.asList(new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
					'i' });
	private static final Map<Character, Character> transMap = new HashMap<>();
	private static final String[] checkStrings = new String[] { "b", "r" };
	private static Logger log = Logger.getLogger(RobotUtilities.class);
	static {
		transMap.put('a', 'i');
		transMap.put('b', 'h');
		transMap.put('c', 'g');
		transMap.put('d', 'f');
		transMap.put('i', 'a');
		transMap.put('h', 'b');
		transMap.put('g', 'c');
		transMap.put('f', 'd');
		transMap.put('e', 'e');
		transMap.put('0', '9');
		transMap.put('1', '8');
		transMap.put('2', '7');
		transMap.put('3', '6');
		transMap.put('4', '5');
		transMap.put('5', '4');
		transMap.put('6', '3');
		transMap.put('7', '2');
		transMap.put('8', '1');
		transMap.put('9', '0');
	}

	public static void main(String[] args) throws AWTException {
		refreshFen("h0g2");
		System.out.println(preFen);
	}

	public static void playByBestmove(String bestmove) throws AWTException {
		// String transBestmove = bestmove;
		try {
			// if ("b".equals(flag)) {
			// transBestmove = transBestmove(bestmove);
			// log.debug("trans: " + bestmove);
			// }
			// 根据bestmove拿到屏幕坐标
			Point[] clickPoints = ScreenUtilities.getPointsByBestmove("b"
					.equals(flag) ? transBestmove(bestmove) : bestmove);
			// 获取当前页面
			List<MBFImage> grapChessPiecesByScreen = ScreenUtilities
					.grapChessPiecesByScreen();
			// 获取当前局面的fen
			String fenByImages = FenUtilities
					.getFenByImages(grapChessPiecesByScreen);
			fenByImages = "b".equals(flag) ? transFen(fenByImages)
					: fenByImages;
			if (!fenByImages.equals(preFen))// 对方重新走棋。
				return;
			// 依次点击屏幕
			MouseClickUtilities.clickInOrder(clickPoints);
			// 更新当前局面
			String refreshFen = refreshFen(bestmove);
			// 更新move局面
			refreshMoveFen(refreshFen, bestmove);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void refreshMoveFen(String refreshFen, String bestmove) {
		if (isEat(refreshFen)) {
			// if ("b".equals(flag))
			// eatFen = transFen(preFen);
			// else
			eatFen = preFen;
			moves.clear();
		} else {
			moves.add(new Move(flag, bestmove));
		}
	}

	private static String transBestmove(String bestmove) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			char charAt = bestmove.charAt(i);
			Character character = transMap.get(charAt);
			sb.append(character);
		}
		return sb.toString();
	}

	private static String refreshFen(String bestmove) {
		String[] lineFens = preFen.split("/");
		int length = lineFens.length;
		// 处理起点坐标
		String startPoint = bestmove.substring(0, 2);
		Integer startLineNum = length
				- (Integer.valueOf(startPoint.charAt(1)) - 48) - 1;
		int startIndexOfLine = moveflag.indexOf(startPoint.charAt(0));
		String startlineFen = lineFens[startLineNum];// 找出起点坐标所在行
		// 找出位于起点坐标的棋子，移除
		char[] startChar = startlineFen.toCharArray();
		char[] startCharCopy = charCopy(startChar);
		char find = startCharCopy[startIndexOfLine];
		startCharCopy[startIndexOfLine] = 0;
		// 更新起点所在行的fen
		String startReplace = sumSpace(startCharCopy);
		lineFens[startLineNum] = startReplace;
		// 处理终点坐标
		String endPoint = bestmove.substring(2, 4);
		Integer endLineNum = length
				- (Integer.valueOf(endPoint.charAt(1)) - 48) - 1;
		int endIndexOfLine = moveflag.indexOf(endPoint.charAt(0));
		String endLineFen = lineFens[endLineNum];// 找出终点坐标所在行
		// 找出终点坐标的格子，替换成走棋后的棋子
		char[] endChar = endLineFen.toCharArray();
		char[] endCharCopy = charCopy(endChar);
		endCharCopy[endIndexOfLine] = find;
		// 更新终点所在行的fen
		String endReplace = sumSpace(endCharCopy);
		lineFens[endLineNum] = endReplace;
		// 更新fen
		StringBuilder sb = new StringBuilder();
		for (String s : lineFens) {
			sb.append(s).append("/");
		}
		String preFenR = preFen;
		preFen = sb.substring(0, sb.length() - 1);
		log.info("playFen:" + preFen);
		return preFenR;

	}

	private static String sumSpace(char[] startCharCopy) {
		int countSpace = 0;
		char[] starCharReplace = new char[9];
		for (int i = 0; i < 9; i++) {
			char c = startCharCopy[i];
			if (c == 0) {
				countSpace++;
				continue;
			}
			if (countSpace != 0) {
				starCharReplace[i - 1] = (char) (countSpace + 48);
				countSpace = 0;
			}
			starCharReplace[i] = c;
		}
		if (countSpace != 0)
			starCharReplace[8] = (char) (countSpace + 48);
		String startReplace = new String(starCharReplace).replaceAll("\0+", "");// 去除空格，char默认值为\0
		return startReplace;
	}

	private static char[] charCopy(char[] startChar) {
		char[] startCharCopy = new char[9];
		int startCharLength = startChar.length;
		int setIndex = 0;
		for (int i = 0; i < startCharLength; i++) {
			char c = startChar[i];
			boolean digit = Character.isDigit(c);
			if (digit) {
				setIndex += Integer.valueOf(c) - 48;
			} else {
				startCharCopy[setIndex] = c;
				setIndex++;
			}
		}
		return startCharCopy;
	}

	public static String prepareToPlay() throws AWTException,
			InterruptedException {
		while (true) {
			Thread.sleep(3000);
			// 检查当前界面
			Point p = ScreenUtilities.needClick();
			if (p != null) {
				// 点击当前屏幕
				MouseClickUtilities.clickByXY(p.x, p.y);
				preFen = "/9/9/9/9/9/9/9/9/9/9";
				eatFen = null;
				continue;
			}
			// 满50步没吃子新中国象棋会弹出提示框，影响棋盘抓取。坑爹，不想去对比画面了，每次抓取的时候都点一下确定的坐标吧，我的是（679,411）
			MouseClickUtilities.clickByXY(679, 411);
			MouseClickUtilities.clickByXY(679, 411);// 点两次棋子就是等于没点。
			// 获取当前页面
			List<MBFImage> grapChessPiecesByScreen = ScreenUtilities
					.grapChessPiecesByScreen();
			// 获取当前局面的fen
			String fenByImages = FenUtilities
					.getFenByImages(grapChessPiecesByScreen);
			initTag(fenByImages);
			fenByImages = "b".equals(flag) ? transFen(fenByImages)
					: fenByImages;
			// 对方还没走棋，继续下一个循环
			// check
			if (check(fenByImages)) {
				continue;
			}
			// 我方没成功走棋
			if (fenByImages.equals(prePlay)) {
				preFen = "/9/9/9/9/9/9/9/9/9/9";
			}
			log.debug("oldFen:" + preFen);
			log.info("newFen:" + fenByImages);
			// 检查有没有吃子
			if (isEat(fenByImages)) {
				eatFen = fenByImages;
				moves.clear();
				preFen = fenByImages;
				prePlay = fenByImages;
				fenByImages = "position fen " + fenByImages + " " + flag
						+ "\r\n";
			} else {
				if (eatFen == null)
					eatFen = flag.equals("w") ? "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR"
							: "RNBAKABNR/9/1C5C1/P1P1P1P1P/9/9/p1p1p1p1p/1c5c1/9/rnbakabnr";
				String move = analyzeMove(fenByImages);// 分析对方走了什么棋
				String f = flag.equals("w") ? "b" : "w";
				moves.add(new Move(f, move));
				Move firstMove = moves.get(0);
				preFen = fenByImages;
				prePlay = fenByImages;
				fenByImages = "position fen " + eatFen + " " + firstMove.flag
						+ " moves " + movesString() + "\r\n";
			}
			log.debug(fenByImages);
			return fenByImages;
		}
	}

	private static String analyzeMove(String fenByImages) {
		String[] eatfens = preFen.split("/");
		String[] movefens = fenByImages.split("/");
		int[] startPoint = new int[2];
		int[] endPoint = new int[2];
		int length = movefens.length;
		for (int y = 0; y < length; y++) {
			String movefen = movefens[y];
			String eatfen = eatfens[y];
			if (movefen.equals(eatfen))
				continue;
			char[] movefenchar = charCopy(movefen.toCharArray());
			char[] eatfenchar = charCopy(eatfen.toCharArray());
			int length2 = movefenchar.length;
			for (int x = 0; x < length2; x++) {
				char moveX = movefenchar[x];
				char eatX = eatfenchar[x];
				if (moveX == eatX)
					continue;
				if (moveX == 0) {
					startPoint[0] = x;
					startPoint[1] = y;
				} else {
					endPoint[0] = x;
					endPoint[1] = y;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(moveflag.get(startPoint[0])).append(9 - startPoint[1])
				.append(moveflag.get(endPoint[0])).append(9 - endPoint[1]);
		return sb.toString();
	}

	private static String movesString() {
		StringBuilder sb = new StringBuilder();
		for (Move s : moves) {
			sb.append(s.bestmove).append(" ");
		}
		return sb.toString().trim();
	}

	private static boolean isEat(String fenByImages) {
		int chessCount = getChessCount(fenByImages);
		int chessCount2 = getChessCount(preFen);
		return chessCount != chessCount2;

	}

	private static int getChessCount(String fen) {
		int count = 0;
		int length = fen.length();
		for (int i = 0; i < length; i++) {
			char charAt = fen.charAt(i);
			if (!Character.isDigit(charAt))
				count++;
		}
		return count;
	}

	private static boolean check(String fenByImages) {
		if (fenByImages.equals(preFen)
				|| !fenByImages.contains("k")
				|| !fenByImages.contains("K")
				|| ("rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR"
						.equals(fenByImages) && "b".equals(flag)))
			return true;
		for (String check : checkStrings) {
			int count = 0;
			Pattern p = Pattern.compile(check);
			Matcher m = p.matcher(fenByImages);
			while (m.find()) {
				count++;
			}
			if (count > 4)
				return true;
		}
		return false;
	}

	private static String transFen(String fenByImages) {
		StringBuffer sb = new StringBuffer();
		int length = fenByImages.length();
		for (int i = length - 1; i >= 0; i--) {
			sb.append(fenByImages.charAt(i));// 使用StringBuffer从右往左拼接字符
		}
		return sb.toString();
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
