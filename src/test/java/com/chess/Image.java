package com.chess;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.junit.Test;
import org.openimaj.image.DisplayUtilities;

public class Image {
	@Test
	public void grapScreen() throws InterruptedException, AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.setSize(954, 681);
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		DisplayUtilities.display(image);
		robot.delay(5000);//
	}

}
