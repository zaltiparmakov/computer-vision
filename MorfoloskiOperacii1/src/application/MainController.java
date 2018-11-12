package application;

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
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
	private Slider slider_color_value;
	@FXML
	private Slider slider_step;
	@FXML
	private ComboBox<String> options_operation;
	@FXML
	private TextField txt_iterations;
	
	private VideoCapture imageCapture;
	private short cameraNum = 0;
	private Mat imageMatrix;
	private Mat hsvMat;
	private Mat mask;
	
	public MainController() {
		imageMatrix = new Mat();
		hsvMat = new Mat();
		mask = new Mat();
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
			// imread function returns Mat object from input file
			imageMatrix = Imgcodecs.imread(selectedFile.getAbsolutePath());
			
			// convert Matrix to image
			Image img = Utils.matToImage(imageMatrix);
			// show image
			originalImage.setImage(img);
		}
	}
	
	@FXML
	public void takeImage() {
		imageCapture.open(cameraNum);
		imageCapture.read(imageMatrix);
		imageCapture.release();
		
		Image img = Utils.matToImage(imageMatrix);
		originalImage.setImage(img);
	}
	
	@FXML
	public void segmentation() {
		// CONVERT TO HSV model to manipulate
		hsvMat = imageMatrix.clone();
		Imgproc.cvtColor(imageMatrix, hsvMat, Imgproc.COLOR_BGR2HSV);
		// gaussian blur to 
		Imgproc.GaussianBlur(hsvMat, hsvMat, new Size(0,0), 0.8);
		Core.flip(hsvMat, hsvMat, 1);
		mask = hsvMat.clone();
		Core.flip(mask, mask, 1);
		Mat mask2 = hsvMat.clone();
		
		int values_range = 360;
		int hue = 0, saturation = 1, value = 2;
		double[] data = new double[3];
		
		int color_value = (int) slider_color_value.getValue();
		int step = (int) slider_step.getValue();
		System.out.println(color_value);
		System.out.println(step);

		// SEGMENTATION, OpenCV Hue channel only support 180 color variations instead of 360
		int min_value = ((color_value - step) % values_range) / 2;
		int max_value = ((color_value + step) % values_range) / 2;
		// forth argument is alpha channel
		Scalar lower_color = new Scalar(min_value, 50, 50, 0);
		Scalar upper_color = new Scalar(max_value, 255, 255, 0);
		Core.inRange(hsvMat, lower_color, upper_color, mask);
		
		// TEST
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(11, 11));
		Imgproc.morphologyEx(mask, mask2, Imgproc.MORPH_ERODE, kernel, new Point(-1, -1), 2);
		
		// DISPLAY ONLY ONE COLOR
		for(int row = 0; row < hsvMat.rows(); row++) {
			for(int col = 0; col < hsvMat.cols(); col++) {
				data = hsvMat.get(row, col);
				// set the mask on saturation channel
				if(mask.get(row, col)[0] == 0) {
					data[hue] = 0;
					data[saturation] = 0;
					data[value] = 0;
				}
				hsvMat.put(row, col, data);
			}
		}
		
		// Convert back to RGB to display
		Imgproc.cvtColor(hsvMat, hsvMat, Imgproc.COLOR_HSV2BGR);
		
		Image img = Utils.matToImage(hsvMat);
		newImage.setImage(img);

		Image maskImg = Utils.matToImage(mask);
		maskImage.setImage(maskImg);
	}
	
	public void applyMorph() {
		Mat newMask = mask.clone();
		String selectedModel = options_operation.getSelectionModel().getSelectedItem();
		int iterations;
		int operation = Imgproc.MORPH_ERODE;
		
		try {
			 iterations = Integer.parseInt(txt_iterations.getText());
		} catch(NumberFormatException e) {
			iterations = 1;
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
		}

        Mat kernel = new Mat(new Size(3, 3), CvType.CV_8UC1, new Scalar(255));
		Imgproc.morphologyEx(mask, newMask, operation, kernel, new Point(-1,-1), iterations);
		
		Image maskImg = Utils.matToImage(newMask);
		maskImage.setImage(maskImg);
	}
}