package application;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
	@FXML
	private Button btn_browseImage;

	@FXML
	private ImageView colorImage;
	
	@FXML
	private ImageView grayImage;
	
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
			Image colored = matToImage(matrix);
			Image grayscale = matToImage(grayscaleMat);
			// show images
			colorImage.setImage(colored);
			grayImage.setImage(grayscale);
		}
	}
	
	/*
	 * @param matrix original matrix for manipulation
	 * 
	 * @return Image object to show in ImageView
	 */
	private static Image matToImage(Mat matrix)
	{
		BufferedImage image = null;
		int width = matrix.width(), height = matrix.height(), channels = matrix.channels();
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