package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {
	@FXML
	private ImageView originalImage;
	@FXML
	private ImageView newImage;
	@FXML
	private ImageView maskImage;
	@FXML
	private ComboBox<String> options_operation;
	@FXML
	private ComboBox<String> options_shape;
	@FXML
	private TextField txt_iterations;
	@FXML
	private TextField txt_size;
	
	private VideoCapture imageCapture;
	private short cameraNum = 0;
	private Mat imageMatrix;
	private Mat mask;
	private Mat thresholded;
	private Mat finalMat;
	private Mat imgMat2;
	
	public MainController() {
		imageMatrix = new Mat();
		imageCapture = new VideoCapture();
	}
	
	@FXML
	public void browseImage(ActionEvent ae) {
		Node source = (Node) ae.getSource();
		Window primaryStage = source.getScene().getWindow();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Browse an image");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Image Files", "*.png", "*.jpg"));
		
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if(selectedFile != null) {
			imageMatrix = Imgcodecs.imread(selectedFile.getAbsolutePath());
			Image img = Utils.matToImage(imageMatrix);
			originalImage.setImage(img);
		}
		
		segmentation();
		resetMat();
	}
	
	@FXML
	public void takeImage() {
		imageCapture.open(cameraNum);
		imageCapture.read(imageMatrix);
		imageCapture.release();
		
		Image img = Utils.matToImage(imageMatrix);
		originalImage.setImage(img);
		
		segmentation();
		resetMat();
	}
	
	@FXML
	public void segmentation() {
		thresholded = imageMatrix.clone();
		finalMat = thresholded.clone();
		Imgproc.cvtColor(imageMatrix, thresholded, Imgproc.COLOR_BGR2GRAY);
		
		Imgproc.adaptiveThreshold(thresholded, thresholded, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 5, 15);

		mask = thresholded.clone();
		Image thresholdedImg = Utils.matToImage(thresholded);
		newImage.setImage(thresholdedImg);
		
		Image maskImg = Utils.matToImage(finalMat);
		maskImage.setImage(maskImg);
	}
	
	@FXML
	public void resetMat() {
		mask = thresholded.clone();
		imgMat2 = imageMatrix.clone();
	}
	
	public void applyMorph() {
		Mat kernel;
		String selectedModel = options_operation.getSelectionModel().getSelectedItem();
		String selectedShape = options_shape.getSelectionModel().getSelectedItem();
		int operation = Imgproc.MORPH_ERODE;
		int shape = Imgproc.CV_SHAPE_RECT;
		int ksize;
		
		try {
			ksize = Integer.parseInt(txt_size.getText());
		} catch(NumberFormatException e) {
			ksize = 0;
		}
		
		switch(selectedModel) {
			case "Erosion":
				operation = Imgproc.MORPH_ERODE;
				break;
			case "Dilation":
				operation = Imgproc.MORPH_DILATE;
				break;
			case "Opening":
				operation = Imgproc.MORPH_OPEN;
				break;
			case "Closing":
				operation = Imgproc.MORPH_CLOSE;
				break;
			case "Gradient":
				operation = Imgproc.MORPH_GRADIENT;
				break;
			case "TopHat":
				operation = Imgproc.MORPH_TOPHAT;
				break;
			case "Blackhat":
				operation = Imgproc.MORPH_BLACKHAT;
				break;
		}
		
		switch(selectedShape) {
			case "Rectangle":
				shape = Imgproc.CV_SHAPE_RECT;
				break;
			case "Eclipse":
				shape = Imgproc.CV_SHAPE_ELLIPSE;
				break;
			case "Cross":
				shape = Imgproc.CV_SHAPE_CROSS;
				break;
		}
		
		if(ksize > 0) {
			kernel = Imgproc.getStructuringElement(shape, new Size(ksize, ksize));
		} else {
	        kernel = new Mat(new Size(3, 3), CvType.CV_8UC1, new Scalar(255));
		}

		Imgproc.morphologyEx(mask, mask, operation, kernel);
		
		findContours();
	}
	
	private void findContours() {
		finalMat = mask.clone();
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(finalMat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(finalMat, contours, -1, new Scalar(255,0,0), 5);

		MatOfPoint2f approxCurve = new MatOfPoint2f();
		for(int i = 0; i < contours.size(); i++) {
	        MatOfPoint2f curve = new MatOfPoint2f(contours.get(i).toArray());
	        Imgproc.approxPolyDP(curve, approxCurve, 0.02 * Imgproc.arcLength(curve, true), true);
	        Rect rect = Imgproc.boundingRect(contours.get(i));
	        
	        if(approxCurve.total() == 4 && !(Math.abs(finalMat.width() - rect.width) <= 15)) {
				// rectangle
				if(rect.width > rect.height) {
					Imgproc.rectangle(imgMat2, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 0), 15);
				}
				
				// square
				if(rect.width == rect.height) {
					Imgproc.rectangle(imgMat2, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 0), 15);
				}
	        }
	        
	        // triangle
	        if(approxCurve.total() == 3) {
	        	Imgproc.drawContours(imgMat2, contours, i, new Scalar(0, 0, 0), 15);
	        }
		}


        // ellipse
		Mat circles = new Mat();
		Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height() / 4, 100, 50, 10, 400);
		for(int j = 0; j < circles.cols(); j++) {
			double[] vecCircle = circles.get(0, j);
			int x = (int) vecCircle[0];
			int y = (int) vecCircle[1];
			int r = (int) vecCircle[2];
			Imgproc.drawContours(imgMat2, contours, j, new Scalar(0,255,0), 15);
		}
		
		Image thresholdedImg = Utils.matToImage(finalMat);
		newImage.setImage(thresholdedImg);
		
		Image finalImg = Utils.matToImage(imgMat2);
		maskImage.setImage(finalImg);
	}
}