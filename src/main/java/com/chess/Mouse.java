package com.chess;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Mouse {
	public static void main(String[] args) throws AWTException {
		Robot robot = new Robot();
		robot.mouseMove(1920, 1080);
		robot.mousePress(KeyEvent.BUTTON1_MASK);
		robot.mouseRelease(KeyEvent.BUTTON1_MASK);
		robot.mouseWheel(10000);
	}
}
