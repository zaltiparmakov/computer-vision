package application;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
	private TextField sigmaSmoothing;
	@FXML
	private TextField x_order_txt;
	@FXML
	private TextField y_order_txt;
	@FXML
	private TextField findEdges;
	@FXML
	private ImageView imageView;
	
	private VideoCapture imageCapture;
	private final static int cameraNum = 0;
	private Mat imageMatrix;
	private Mat grayMatrix;
	private boolean sobeled = false;
	
	public MainController() {
		imageCapture = new VideoCapture();
		imageMatrix = new Mat();
		grayMatrix = new Mat();
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
			
			// Convert to grayscale
			Mat grayscaleMat = new Mat();
			Imgproc.cvtColor(imageMatrix, grayscaleMat, Imgproc.COLOR_BGR2GRAY);
			
			// convert Matrix to image
			Image img = Utils.matToImage(imageMatrix);
			// show images
			imageView.setImage(img);
		}
	}
	
	@FXML
	public void takePicture() {
		imageCapture.open(cameraNum);
		imageCapture.read(imageMatrix);
		imageCapture.release();
		
		Image img = Utils.matToImage(imageMatrix);
		imageView.setImage(img);
	}
	
	@FXML
	public void applySmoothing() {
		float sigma;
		
		try {
			sigma = Float.parseFloat(sigmaSmoothing.getText());
		} catch(NumberFormatException e) {
			sigma = 1.0f;
		}
		
		Imgproc.cvtColor(imageMatrix, grayMatrix, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(grayMatrix, grayMatrix, new Size(-1, -1), sigma);
		
		Image img = Utils.matToImage(grayMatrix);
		imageView.setImage(img);
		
		sobeled = false;
	}
	
	@FXML
	public void applySobel() {
		if(sobeled) {
			return;
		}
		
		int x_order = 1, y_order = 1;
		try {
			x_order = Integer.parseInt(x_order_txt.getText());
			y_order = Integer.parseInt(y_order_txt.getText());
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		int kernel = Math.max(x_order, y_order) + 2;
		
		if(kernel % 2 == 0) {
			kernel += 1;
		}
		
		Mat grad_x = new Mat();
		Mat grad_y = new Mat();
		Mat abs_grad_x = new Mat();
		Mat abs_grad_y = new Mat();
		
		Imgproc.Sobel(grayMatrix, grad_x, CvType.CV_16S, x_order, 0, kernel, 1, 0);
		Imgproc.Sobel(grayMatrix, grad_y, CvType.CV_16S, 0, y_order, kernel, 1, 0);
		
		// Convert back to 8u
		Core.convertScaleAbs(grad_x, abs_grad_x);
		Core.convertScaleAbs(grad_y, abs_grad_y);
		
		Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grayMatrix);
		
		Image img = Utils.matToImage(grayMatrix);
		imageView.setImage(img);
		
		sobeled = true;
	}
}
