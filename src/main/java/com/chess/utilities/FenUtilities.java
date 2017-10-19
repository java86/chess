package com.chess.utilities;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;

/**
 * 把当前屏幕棋局局面转换成fen,方便调用象棋软件引擎
 * 
 * @author 86
 * @date 2017年10月11日 下午4:15:40
 */
public class FenUtilities {
	private static final Map<String, MBFImage> pieces = new HashMap<>();// 新中国象棋棋子图像，用来对比当前格子是什么棋子
	private static final int pieceLineNum = 9;// 新中国象棋一行有几个棋子
	// 加载棋子图像
	static {
		try {
			pieces.put("R", ImageUtilities.readMBF(new File("chessImage\\r2.png")));
			pieces.put("N", ImageUtilities.readMBF(new File("chessImage\\n2.png")));
			pieces.put("B", ImageUtilities.readMBF(new File("chessImage\\b2.png")));
			pieces.put("A", ImageUtilities.readMBF(new File("chessImage\\a2.png")));
			pieces.put("K", ImageUtilities.readMBF(new File("chessImage\\k2.png")));
			pieces.put("C", ImageUtilities.readMBF(new File("chessImage\\c2.png")));
			pieces.put("P", ImageUtilities.readMBF(new File("chessImage\\p2.png")));
			pieces.put("r", ImageUtilities.readMBF(new File("chessImage\\r.png")));
			pieces.put("n", ImageUtilities.readMBF(new File("chessImage\\n.png")));
			pieces.put("b", ImageUtilities.readMBF(new File("chessImage\\b.png")));
			pieces.put("a", ImageUtilities.readMBF(new File("chessImage\\a.png")));
			pieces.put("k", ImageUtilities.readMBF(new File("chessImage\\k.png")));
			pieces.put("c", ImageUtilities.readMBF(new File("chessImage\\c.png")));
			pieces.put("p", ImageUtilities.readMBF(new File("chessImage\\p.png")));
			for (int i = 0; i < 90; i++) {
				pieces.put("s" + i, ImageUtilities.readMBF(new File("chessImage\\" + i + ".png")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String getFenByImages(List<MBFImage> chessImages) {
		StringBuilder sb = new StringBuilder();
		HistogramModel model = new HistogramModel(4, 4, 4);
		double tempMinScore = Double.MAX_VALUE;
		int count = 0;
		int countSpace = 0;
		String tempString = "";
		for (MBFImage mbf : chessImages) {
			if (count != 0 && count % pieceLineNum == 0) {
				if (countSpace != 0) {
					sb.append(countSpace + "/");
					countSpace = 0;
				} else {
					sb.append("/");
				}
			}
			for (Entry<String, MBFImage> entry : pieces.entrySet()) {
				MBFImage piece = entry.getValue();
				model.estimateModel(piece);
				MultidimensionalHistogram pieceH = model.histogram.clone();
				model.estimateModel(mbf);
				MultidimensionalHistogram mbfH = model.histogram.clone();
				double distanceScore = pieceH.compare(mbfH, DoubleFVComparison.EUCLIDEAN);
				if (distanceScore < tempMinScore) {
					tempString = entry.getKey();
					tempMinScore = distanceScore;
				}
			}
			// 当前格子没有棋子
			if (tempString.contains("s")) {
				countSpace++;
				count++;
				tempMinScore = Double.MAX_VALUE;
				tempString = "";
				continue;
			}
			if (countSpace != 0) {
				sb.append(countSpace + tempString);
			} else {
				sb.append(tempString);
			}
			count++;
			countSpace = 0;
			tempMinScore = Double.MAX_VALUE;
			tempString = "";
		}
		if (countSpace != 0)
			sb.append(countSpace);
		// return sb.toString();
		return "RNBAKABNR/9/4C2C1/P1P1P1P1P/9/9/p1p1p1p1p/1c5c1/9/rnbakabnr";
	}

	public static void main(String[] args) throws InterruptedException, AWTException {
		List<MBFImage> grapChessPiecesByScreen = ScreenUtilities.grapChessPiecesByScreen();
		String fenByImages = getFenByImages(grapChessPiecesByScreen);
		System.out.println(fenByImages);
	}

}
