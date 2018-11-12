package application;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
			Mat matrix = Imgcodecs.imread(selectedFile.getAbsolutePath());
			
			// Convert to grayscale
			Mat grayscaleMat = new Mat();
			Imgproc.cvtColor(matrix, grayscaleMat, Imgproc.COLOR_BGR2GRAY);
			
			// convert Matrix to image
			Image img = matToImage(matrix);
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
			Mat frame = new Mat();
			
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
							videoCapture.read(frame);
							Image img = matToImage(frame);				
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
		Mat frame = new Mat();

		if(!cameraActivated) {
			videoCapture.open(cameraNum);
			btn_activateCamera.setText("Stop Live Stream");
			
			if(videoCapture.isOpened()) {
				cameraActivated = true;
				
				// Grab frame every 44ms (25 fps) on another thread, and render on
				// JavaFX Application GUI Thread
				Runnable frameGetter = new Runnable() {
					@Override
					public void run() {
						videoCapture.read(frame);
						Image img = matToImage(frame);				
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
		Mat matrix = new Mat();
		
		if(videoCapture.isOpened()) {
			videoCapture.read(matrix);
		} else {
			imageCapture.open(cameraNum);
			imageCapture.read(matrix);
			imageCapture.release();
		}
		
		Image img = matToImage(matrix);
		imageView.setImage(img);
	}
	
	/*
	 * @param matrix original matrix for manipulation
	 * 
	 * @return Image object to show in ImageView
	 */
	private static Image matToImage(Mat matrix)
	{
		BufferedImage image = null;
		int width = matrix.width() < 1 ? 1 : matrix.width();
		int height = matrix.height() < 1 ? 1 : matrix.height();
		int channels = matrix.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		matrix.get(0, 0, sourcePixels);
		
		// if image is in color
		if (matrix.channels() > 1)
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		else
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return SwingFXUtils.toFXImage(image, null);
	}
}