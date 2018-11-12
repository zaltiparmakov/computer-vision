package application;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class MainController {
	@FXML
	private ImageView imageView;
	
	private VideoCapture imageCapture;
	private final static int cameraNum = 0;
	private Mat imageMatrix;
	
	public MainController() {
		imageCapture = new VideoCapture();
		imageMatrix = new Mat();
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
	public void convert() {
		// CONVERT TO GRAYSCALE
		Mat grayMat = new Mat();
		Imgproc.cvtColor(imageMatrix, grayMat, Imgproc.COLOR_BGR2GRAY);

		Mat newMat = new Mat(grayMat.rows(),grayMat.cols(),grayMat.type());
		
        Mat kernel = new Mat(9, 9, CvType.CV_32F){
            {
               put(0,0,-1);
               put(0,1,0);
               put(0,2,1);

               put(1,0-2);
               put(1,1,0);
               put(1,2,2);

               put(2,0,-1);
               put(2,1,0);
               put(2,2,1);
            }
         };

         Mat photo_dx = new Mat();
         Mat photo_dy = new Mat();
		
        Imgproc.filter2D(imageMatrix, newMat, -1, kernel);
        
        Mat photo_grad = new Mat();
        Mat photo_angle = new Mat();
        
        Imgproc.Sobel(grayMat, photo_dx, 3, 1, 0);
        Imgproc.Sobel(grayMat, photo_dy, 3, 0, 1);
        Core.cartToPolar(photo_dx, photo_dy, photo_grad, photo_angle, true);
        
        Image img = Utils.matToImage(photo_angle);
		
		imageView.setImage(img);
	}
}
