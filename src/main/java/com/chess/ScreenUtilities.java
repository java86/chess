package com.chess;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

/**
 * 获取屏幕棋盘中的棋子，棋盘左上角需和屏幕坐标(0,0)对齐
 * 
 * @author 86
 * @date 2017年10月11日 下午3:36:37
 */
public class ScreenUtilities {
	private static final int CChessDefaultWidth = 855;// 新中国象棋棋盘默认宽度
	private static final int CChessDefaultHeight = 624;// 新中国象棋默认高度
	private static final int ChessPieceDefaultWidth = 148;// 新中国象棋棋盘格子默认宽度
	private static final int ChessPieceDefaultHeight = 25;// 新中国象棋默认格子高度
	private static final Point[] points = new Point[] { new Point(335, 25), new Point(340, 50), new Point(335, 25),
			new Point(335, 25), new Point(335, 25), new Point(340, 50), new Point(340, 50), new Point(340, 50),
			new Point(335, 25), new Point(340, 50), new Point(340, 50), new Point(340, 50), new Point(340, 50),
			new Point(340, 50), new Point(335, 25), new Point(335, 25), new Point(335, 25), new Point(340, 50) };// 可以放棋子的格子坐标
	private static final String[] tag = new String[] { "R", "N", "B", "A", "K", "C", "P", "r", "n", "b", "a", "k", "c", "p" };

	public static List<MBFImage> grapChessPiecesByScreen() throws AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.setSize(CChessDefaultWidth, CChessDefaultHeight);
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		List<MBFImage> chessPiecesMBFImages = new ArrayList<>();
		for (Point p : points) {
			BufferedImage pointImage = image.getSubimage(p.x, p.y, ChessPieceDefaultWidth, ChessPieceDefaultHeight);
			MBFImage createMBFImage = ImageUtilities.createMBFImage(pointImage, true);
			chessPiecesMBFImages.add(createMBFImage);
		}
		return chessPiecesMBFImages;
	}

	public static List<MBFImage> grapChessPiecesByScreen2() throws AWTException, IOException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.setSize(CChessDefaultWidth, CChessDefaultHeight);
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		List<MBFImage> chessPiecesMBFImages = new ArrayList<>();
		for (int index = 0; index < points.length; index++) {
			BufferedImage pointImage = image.getSubimage(points[index].x, points[index].y, ChessPieceDefaultWidth,
					ChessPieceDefaultHeight);
			File f = new File("D:\\chessImage\\");
			if (!f.exists())
				f.mkdirs();
			f = new File(f, index + ".png");
			ImageIO.write(pointImage, "png", f);
		}
		return chessPiecesMBFImages;
	}

	public static void main(String[] args) throws AWTException, InterruptedException, IOException {
		String fenByImages = FenUtilities.getFenByImages(grapChessPiecesByScreen());
		grapChessPiecesByScreen2();
		System.out.println(fenByImages);
	}
}
