package application;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class MainController {
	// camera number
	private int cameraNum = 0;
	private boolean cameraActivated = false;
	private ScheduledExecutorService timer;
	private VideoCapture videoCapture;
	private VideoCapture imageCapture;
	// field, so multiple functions can access it and process it
	private Mat imgMatrix = new Mat();
	private Mat videoMatrix = new Mat();
	
	@FXML
	private Button btn_browseImage;
	@FXML
	private Button btn_browseVideo;
	@FXML
	private Button btn_takePic;
	@FXML
	private Button btn_activateCamera;
	@FXML
	private ImageView imageView;
	@FXML
	private ImageView videoView;
	@FXML
	private Button btn_applyContrast;
	@FXML
	private Slider contrast_slider;
	@FXML
	private TextField contrast_min;
	@FXML
	private TextField contrast_max;
	
	// Default constructor to initialize VideoCapture object
	public MainController() {
		 videoCapture = new VideoCapture();
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
			imgMatrix = Imgcodecs.imread(selectedFile.getAbsolutePath());
			
			// Convert to grayscale
			Mat grayscaleMat = new Mat();
			Imgproc.cvtColor(imgMatrix, grayscaleMat, Imgproc.COLOR_BGR2GRAY);
			
			// convert Matrix to image
			Image img = Utils.matToImage(imgMatrix);
			// show images
			imageView.setImage(img);
		}
	}
	
	@FXML
	public void browseVideo(ActionEvent ae) {
		Node source = (Node) ae.getSource();
		Window primaryStage = source.getScene().getWindow();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a video");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Video Files", "*.avi"));
		
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if(selectedFile != null) {
			videoMatrix = new Mat();
			
			if(!cameraActivated) {
				videoCapture.open(selectedFile.getAbsolutePath());
				btn_activateCamera.setText("Stop Live Stream");
				
				if(videoCapture.isOpened()) {
					cameraActivated = true;
					
					// Grab frame every 44ms (25 fps) on another thread, and render on
					// JavaFX Application GUI Thread
					Runnable frameGetter = new Runnable() {
						@Override
						public void run() {
							videoCapture.read(videoMatrix);
							Image img = Utils.matToImage(videoMatrix);				
							updateVideoView(videoView, img);
						}
					};
					timer = Executors.newSingleThreadScheduledExecutor();
					timer.scheduleAtFixedRate(frameGetter, 0, 40, TimeUnit.MILLISECONDS);
				} else {
					System.err.println("Cant open camera");
				}
			} else {
				cameraActivated = false;
				btn_activateCamera.setText("Start camera");
				videoView.setImage(null);
				
				releaseCamera();
			}			
		}
	}
	
	@FXML
	public void activateCamera() {
		if(!cameraActivated) {
			videoCapture.open(cameraNum);
			btn_activateCamera.setText("Stop Live Stream");
			
			if(videoCapture.isOpened()) {
				cameraActivated = true;
				
				// Grab frame every 44ms (25 fps) on another thread, and render on
				// JavaFX Application GUI Thread
				Runnable frameGetter = new Runnable() {
					Mat newMat;
					@Override
					public void run() {
						int sliderValue = (int) contrast_slider.getValue();
						
						int value_min = 0;
						int value_max = 0;
						if(!contrast_min.getText().isEmpty() || !contrast_max.getText().isEmpty()) {
							value_min = Integer.parseInt(contrast_min.getText());
							value_max = Integer.parseInt(contrast_max.getText());
							sliderValue = 0;
						}
						System.out.println(sliderValue);

						newMat = brightnessManipulation(videoMatrix, sliderValue, value_min, value_max);
						System.out.println(newMat);

						videoCapture.read(newMat);
						Image img = Utils.matToImage(newMat);
						updateVideoView(videoView, img);
					}
				};
				timer = Executors.newSingleThreadScheduledExecutor();
				timer.scheduleAtFixedRate(frameGetter, 0, 40, TimeUnit.MILLISECONDS);
			} else {
				System.err.println("Cant open camera");
			}
		} else {
			cameraActivated = false;
			btn_activateCamera.setText("Start camera");
			videoView.setImage(null);
			
			releaseCamera();
		}
	}
	
	private void releaseCamera() {
		// shutdown the timer in order to stop executing the thread for grabbing images
		if(timer != null && !timer.isShutdown()) {
				timer.shutdown();
		}
		
		if(videoCapture.isOpened()) {
			// shutdown the camera
			videoCapture.release();
		}
	}
	
	public void closeWindow() {
		releaseCamera();
	}
	
	// Update images on GUI Thread
	private void updateVideoView(ImageView frame, Image img) {
		Platform.runLater(() -> {
			frame.setImage(img);
		});
	}
	
	@FXML
	public void takePicture() {		
		if(videoCapture.isOpened()) {
			videoCapture.read(imgMatrix);
		} else {
			imageCapture.open(cameraNum);
			imageCapture.read(imgMatrix);
			imageCapture.release();
		}
		
		Image img = Utils.matToImage(imgMatrix);
		imageView.setImage(img);
	}
	
	@FXML
	public void applyContrast() {
		int sliderValue = (int) contrast_slider.getValue();
		
		int value_min = 0;
		int value_max = 0;
		if(!contrast_min.getText().isEmpty() || !contrast_max.getText().isEmpty()) {
			value_min = Integer.parseInt(contrast_min.getText());
			value_max = Integer.parseInt(contrast_max.getText());
			sliderValue = 0;
		}

		if(imgMatrix != null) {
			Mat newMat = brightnessManipulation(imgMatrix, sliderValue, value_min, value_max);
			
			Image img = (Image) imageView.getImage();
			Image im = Utils.matToImage(newMat);
			imageView.setImage(im);
		}
	}
	
	private Mat brightnessManipulation(Mat matrix, int sliderValue, int value_min, int value_max) {
		Mat newMat = matrix.clone();
		double[] x = new double[3]; // RGB
		
		for(int i = 0; i < matrix.rows(); i++) {
			for(int j = 0; j < matrix.cols(); j++) {
				for(int k = 0; k < matrix.channels(); k++) {
					if(sliderValue != 0) {
						x[k] = matrix.get(i, j)[k] + sliderValue * 3;
					} else {
						x[k] = (matrix.get(i, j)[k] - value_min) / (value_max - value_min);
					}
					x[k] = Math.max(0, Math.min(x[k], 255));
				}
				newMat.put(i, j, x);
			}
		}
		
		return newMat;
	}
}