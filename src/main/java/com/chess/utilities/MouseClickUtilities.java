package com.chess.utilities;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.chess.model.Point;

public class MouseClickUtilities {
	public static void clickByXY(int x, int y) throws AWTException {
		Robot robot = new Robot();
		robot.mouseMove(x, y);
		robot.mousePress(KeyEvent.BUTTON1_MASK);
		robot.mouseRelease(KeyEvent.BUTTON1_MASK);
	}

	public static void clickInOrder(Point[] clickPoints) throws AWTException {
		for (Point p : clickPoints)
			clickByXY(p.x, p.y);
	}
}
