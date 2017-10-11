package com.chess;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;
import org.openimaj.util.pair.Pair;

/**
 * Hello world!
 *
 */
public class App {
	@Test
	public void screenCapture() throws AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.setSize(855, 624);
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		robot.delay(5000);
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		image = image.getSubimage(340, 50, 148, 25);
		MBFImage createMBFImage = ImageUtilities.createMBFImage(image, true);
		DisplayUtilities.display(createMBFImage);
		robot.delay(50000);
		// return createMBFImage;
	}

	// 比较两个图片相识度
	@Test
	public void distanceScore() throws IOException {
		URL[] imageURLs = new URL[] { new URL("http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist1.jpg"),
				new URL("http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist2.jpg"),
				new URL("http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist3.jpg") };

		List<MultidimensionalHistogram> histograms = new ArrayList<MultidimensionalHistogram>();
		HistogramModel model = new HistogramModel(4, 4, 4);

		for (URL u : imageURLs) {
			model.estimateModel(ImageUtilities.readMBF(u));
			histograms.add(model.histogram.clone());
		}
		double distanceScore = histograms.get(0).compare(histograms.get(1), DoubleFVComparison.EUCLIDEAN);
		double distanceScore2 = histograms.get(1).compare(histograms.get(2), DoubleFVComparison.EUCLIDEAN);
		System.out.println(distanceScore);
		System.out.println(distanceScore2);
	}

	@Test
	public void distanceScore2() throws IOException, AWTException {
		List<MultidimensionalHistogram> histograms = new ArrayList<MultidimensionalHistogram>();
		HistogramModel model = new HistogramModel(4, 4, 4);
		model.estimateModel(ImageUtilities.readMBF(new File("D:\\Users\\86\\cut.jpg")));
		histograms.add(model.histogram.clone());
		model.estimateModel(ScreenUtilities.grapChessPiecesByScreen().get(0));
		histograms.add(model.histogram.clone());
		double distanceScore = histograms.get(0).compare(histograms.get(1), DoubleFVComparison.EUCLIDEAN);
		System.out.println(distanceScore);
	}

	// 从图片中查找特定元素
	@Test
	public void queryImage() throws MalformedURLException, IOException, InterruptedException {
		MBFImage query = ImageUtilities.readMBF(new File("D:\\Users\\86\\horse.png"));
		MBFImage target = ImageUtilities.readMBF(new File("D:\\Users\\86\\chess2.png"));
		DoGSIFTEngine engine = new DoGSIFTEngine();
		LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
		LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());
		RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 15000,
				new RANSAC.PercentageInliersStoppingCondition(0.8));
		LocalFeatureMatcher<Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
				new FastBasicKeypointMatcher<Keypoint>(6), modelFitter);
		matcher.setModelFeatures(queryKeypoints);
		matcher.findMatches(targetKeypoints);
		// 每个pair中firstObject属于target，secondObject属于query
		List<Pair<Keypoint>> matches = matcher.getMatches();
		List<Keypoint> targetMatchPoints = new ArrayList<>();
		for (Pair<Keypoint> p : matches) {
			targetMatchPoints.add(p.firstObject());
		}
		target.drawPoints(targetMatchPoints, RGBColour.RED, 8);
		DisplayUtilities.display(target);
		// MBFImage consistentMatches = MatchingUtilities.drawMatches(query,
		// target, matches, RGBColour.RED);
		// DisplayUtilities.display(consistentMatches);
		Thread.sleep(50000);
	}
}
