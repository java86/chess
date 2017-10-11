package com.chess;

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
			pieces.put("s", ImageUtilities.readMBF(new File("D:\\Users\\86\\cut.jpg")));
			pieces.put("N", ImageUtilities.readMBF(new File("D:\\Users\\86\\blank.jpg")));
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
			if ("s".equals(tempString)) {
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
		sb.append(" r");
		return sb.toString();
	}

	public static void main(String[] args) throws InterruptedException {
	}

}
