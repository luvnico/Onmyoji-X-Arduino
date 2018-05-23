package OnmyojiHelper;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.xfeatures2d.BriefDescriptorExtractor;
import org.opencv.imgcodecs.Imgcodecs;
import javax.imageio.ImageIO;

public class ScreenCapture {

    /**
     * Compare that two images is similar using feature mapping
     * @author minikim, updated by Luvnico
     * @param filename1 - the first image
     * @param filename2 - the second image
     * @return integer - count that has the similarity within images
     */
    public static int compareFeature(String filename1, String filename2) {
        int retVal = 0;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load images to compare
        Mat img1 = Imgcodecs.imread(filename1, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat img2 = Imgcodecs.imread(filename2, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        // Declare key point of images
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        // Definition of ORB key point detector and descriptor extractors
        FastFeatureDetector detector = FastFeatureDetector.create();
        BriefDescriptorExtractor extractor = BriefDescriptorExtractor.create();

        // Detect key points
        detector.detect(img1, keypoints1);
        detector.detect(img2, keypoints2);

        // Extract descriptors
        extractor.compute(img1, keypoints1, descriptors1);
        extractor.compute(img2, keypoints2, descriptors2);

        // Definition of descriptor matcher
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        // Match points of two images
        MatOfDMatch matches = new MatOfDMatch();
        // Avoid to assertion failed
        if (descriptors2.cols() == descriptors1.cols()) {
            matcher.match(descriptors1, descriptors2 ,matches);

            // Check matches of key points
            DMatch[] match = matches.toArray();
            double max_dist = 0; double min_dist = 100;

            for (int i = 0; i < descriptors1.rows(); i++) {
                double dist = match[i].distance;
                if( dist < min_dist ) min_dist = dist;
                if( dist > max_dist ) max_dist = dist;
            }

            // Extract good images (distances are under 10)
            for (int i = 0; i < descriptors1.rows(); i++) {
                if (match[i].distance <= 10) {
                    retVal++;
                }
            }
        }

        return retVal;
    }

    /**
     * This program demonstrates how to capture screenshot of a portion of screen.
     * @author www.codejava.net, revised by Luvnico
     *
     */
    public static String capture(){
        String format = "jpg";
        String fileName = "LatestScreenshot." + format;

        try{
            Robot robot = new Robot();
            Rectangle captureRect = new Rectangle(0, 20, 426, 240);
            BufferedImage screenFullImage = robot.createScreenCapture(captureRect);
            ImageIO.write(screenFullImage, format, new File(fileName));
        }
        catch(AWTException | IOException ex){
            System.err.println(ex);
        }
        return fileName;

    }

}
