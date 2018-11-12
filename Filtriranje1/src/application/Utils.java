package application;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Utils {
	/*
	 * @param matrix original matrix for manipulation
	 * 
	 * @return Image object to show in ImageView
	 */
	public static Image matToImage(Mat matrix)
	{
		BufferedImage image = null;
		int width = matrix.width() < 1 ? 1 : matrix.width();
		int height = matrix.height() < 1 ? 1 : matrix.height();
		int channels = matrix.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		matrix.get(0, 0, sourcePixels);
		
		// if image is in color
		if (matrix.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return SwingFXUtils.toFXImage(image, null);
	}
	
	public static Mat ImgToMat(Image img) {
		BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		try {
			ImageIO.write(bImage, "png", s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] res  = s.toByteArray();
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		Mat matImg = new Mat((int)img.getHeight(), (int)img.getWidth(), CvType.CV_8UC3);
		matImg.put(0, 0, res);
		
		return new Mat();
	}
}
