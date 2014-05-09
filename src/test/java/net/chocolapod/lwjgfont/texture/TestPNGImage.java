package net.chocolapod.lwjgfont.texture;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestPNGImage {

	/**
	 *	画像の decode() 結果が ImageIO での読み込み結果と等しいか確認する
	 */
	@Test
	public void decode8bitRGB_nonInterlaced() throws IOException {
		testImage("8bitRGB_nonInterlaced.png");
	}
	
	@Test
	public void decode8bitRGBA_nonInterlaced() throws IOException {
		testImage("8bitRGBA_nonInterlaced.png");
	}

	private void testImage(String filePath) throws IOException {
		BufferedImage	imageSDK = ImageIO.read(TestPNGImage.class.getResourceAsStream(filePath));
		PNGImage		imagePng = PNGImage.decode(TestPNGImage.class.getResourceAsStream(filePath));
		ByteBuffer		imagePnglBuffer = imagePng.getImageData();
		int				x = 0;
		int				y = 0;
		int				size = imagePnglBuffer.limit();
		int				count = 0;
		
		while (count < size) {
			int		pixel = imageSDK.getRGB(x, y);
			int		alpha = (pixel >> 24) & 0xff;
			int		red = (pixel >> 16) & 0xff;
			int		green = (pixel >> 8) & 0xff;
			int		blue = (pixel ) & 0xff;
			
			int[]	actual = new int[] {
					imagePnglBuffer.get() & 0xff,
					imagePnglBuffer.get() & 0xff,
					imagePnglBuffer.get() & 0xff,
					imagePnglBuffer.get() & 0xff
			};
			assertArrayEquals(new int[] {red, green, blue, alpha}, actual);

			x++;
			if (imagePng.getWidth() <= x) {
				x = 0;
				y++;
			}
			count += 4;
		}
		
		assertEquals(imageSDK.getData().getWidth(), imagePng.getWidth());
		assertEquals(imageSDK.getData().getHeight(), imagePng.getHeight());
	}
	
}
