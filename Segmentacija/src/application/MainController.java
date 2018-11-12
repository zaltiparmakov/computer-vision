package application;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
	private TextField txt_color_value;
	@FXML
	private TextField txt_step;
	
	private VideoCapture imageCapture;
	private short cameraNum = 0;
	private Mat imageMatrix;
	private Mat hsvMat;
	
	public MainController() {
		imageMatrix = new Mat();
		hsvMat = new Mat();
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
		Mat mask = hsvMat.clone();
		
		int values_range = 360;
		int hue = 0, saturation = 1, value = 2;
		double[] data = new double[3];
		
		int color_value = 170;
		int step = 1;
		try {
			color_value = Integer.parseInt(txt_color_value.getText());
			step = Integer.parseInt(txt_step.getText());
		} catch(NumberFormatException e) {
			color_value = 180;
			step = 1;
		}

		// SEGMENTATION, OpenCV Hue channel only support 180 color variations instead of 360
		int min_value = ((color_value - step) % values_range) / 2;
		int max_value = ((color_value + step) % values_range) / 2;
		// forth argument is alpha channel
		Scalar lower_color = new Scalar(min_value, 50, 50, 0);
		Scalar upper_color = new Scalar(max_value, 255, 255, 0);
		Core.inRange(hsvMat, lower_color, upper_color, mask);
		
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
	}
}
