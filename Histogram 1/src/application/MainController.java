package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
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
	private VideoCapture imageCapture;
	// field, so multiple functions can access it and process it
	private Mat imgMatrix;
	
	@FXML
	private Button btn_browseImage;
	@FXML
	private Button btn_takePic;
	@FXML
	private ImageView imageView;
	@FXML
	private ImageView histogram;
	@FXML
	private Button btn_applyContrast;
	@FXML
	private Button btn_applyColor;
	@FXML
	private Button btn_changeColorModel;
	@FXML
	private Button btn_revertColorModel;
	@FXML
	private Slider contrast_slider;
	@FXML
	private TextField contrast_min;
	@FXML
	private TextField contrast_max;
	@FXML
	private Slider slider_red;
	@FXML
	private Slider slider_green;
	@FXML
	private Slider slider_blue;
	@FXML
	private RadioButton cmodel_rgb;
	@FXML
	private RadioButton cmodel_hsv;
	@FXML
	private RadioButton cmodel_ycrcb;

	boolean rgb = false, hsv = false, ycrcb = false;
	
	// Default constructor to initialize VideoCapture object
	public MainController() {
		imageCapture = new VideoCapture();
		imgMatrix = new Mat();
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
	public void takePicture() {
		imageCapture.open(cameraNum);
		imageCapture.read(imgMatrix);
		imageCapture.release();
		
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
			
			Image im = Utils.matToImage(newMat);
			imageView.setImage(im);
		}
	}
	
	@FXML
	protected void applyColor() {
		int red = (int) slider_red.getValue();
		int green = (int) slider_green.getValue();
		int blue = (int) slider_blue.getValue();		
		
		Mat newMat = colorManipulation(imgMatrix, red, green, blue);
		Image im = Utils.matToImage(newMat);
		imageView.setImage(im);
		
		updateHistogram();
	}
	
	private Mat colorManipulation(Mat matrix, int red, int green, int blue) {
		Mat newMat = matrix.clone();
		double[] x = new double[3]; // RGB
		
		for(int i = 0; i < matrix.rows(); i++) {
			for(int j = 0; j < matrix.cols(); j++) {
				for(int k = 0; k < matrix.channels(); k++) {
					x[0] = matrix.get(i, j)[0] + blue;
					x[1] = matrix.get(i, j)[1] + green;
					x[2] = matrix.get(i, j)[2] + red;
				}
				newMat.put(i, j, x);
			}
		}
		
		return newMat;
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
		
		updateHistogram();
		
		return newMat;
	}
	
	private void updateHistogram() {
		// split the frames in multiple images
		List<Mat> images = new ArrayList<Mat>();
		Core.split(imgMatrix, images);
		
		// set the number of bins at 256
		MatOfInt histSize = new MatOfInt(256);
		// only one channel
		MatOfInt channels = new MatOfInt(0);
		// set the ranges
		MatOfFloat histRange = new MatOfFloat(0, 256);
		
		// compute the histograms for the B, G and R components
		Mat hist_b = new Mat();
		Mat hist_g = new Mat();
		Mat hist_r = new Mat();
		
		Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);
		Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
		Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);

		int hist_w = 150;
		int hist_h = 150;
		int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);
		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
		
		Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		
		for (int i = 1; i < histSize.get(0, 0)[0]; i++)
		{
			// B component or gray image
			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
			// G and R components (if the image is not in gray scale)

			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
						0);
			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
						0);
		}
		
		Image histogramImg = Utils.matToImage(histImage);
		histogram.setImage(histogramImg);
	}
	
	@FXML
	protected void revertColorModel() {
		if(ycrcb) {
			Imgproc.cvtColor(imgMatrix, imgMatrix, Imgproc.COLOR_YCrCb2BGR);
			ycrcb = false;
		} else if(rgb) {
			Imgproc.cvtColor(imgMatrix, imgMatrix, Imgproc.COLOR_RGB2BGR);
			rgb = false;
		} else if(hsv) {
			Imgproc.cvtColor(imgMatrix, imgMatrix, Imgproc.COLOR_HSV2BGR);
			hsv = false;
		}
		
		updateImage();
		updateHistogram();
	}
	
	@FXML
	protected void changeColorModel() {
		if(imageView.getImage() == null) {
			return;
		}
		// reset to BGR color model
		revertColorModel();

		if(cmodel_rgb.isSelected() && !rgb) {
			Imgproc.cvtColor(imgMatrix, imgMatrix, Imgproc.COLOR_BGR2RGB);
			rgb = true;
			hsv = ycrcb = false;
		}
		if(cmodel_hsv.isSelected() && !hsv) {
			Imgproc.cvtColor(imgMatrix, imgMatrix, Imgproc.COLOR_BGR2HSV);
			hsv = true;
			rgb = ycrcb = false;
		}
		if(cmodel_ycrcb.isSelected() && !ycrcb) {			
			Imgproc.cvtColor(imgMatrix, imgMatrix, Imgproc.COLOR_BGR2YCrCb);
			ycrcb = true;
			rgb = hsv = false;
		}
		
		updateImage();
		updateHistogram();
	}
	
	private void updateImage() {
		Image img = Utils.matToImage(imgMatrix);
		imageView.setImage(img);
	}
}