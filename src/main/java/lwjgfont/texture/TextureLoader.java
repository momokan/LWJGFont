package lwjgfont.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA16;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

public class TextureLoader {
	
	public Texture loadTexture(String imagePath) throws IOException {
		BufferedImage	srcImage;
		int				srcImageType;

		srcImage = ImageIO.read(new File(imagePath));
		srcImageType = srcImage.getType();
		
		
		int				target = GL_TEXTURE_2D;			// target
		int				dstPixelFormat = GL_RGBA;		// dst pixel format
		int				format = GL_UNSIGNED_BYTE;		// data type

		//	テクスチャー ID を生成する
		int				textureID = GL11.glGenTextures();
		Texture			texture = new Texture(target, textureID);

		//	glTexImage2D() の対象となるテクスチャー ID をバインドする
		glBindTexture(target, textureID);

		// All RGB bytes are aligned to each other and each component is 1 byte
//		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		int				width = srcImage.getWidth();
		int				height = srcImage.getHeight();

		texture.setWidth(width);
		texture.setHeight(height);
		texture.setTextureWidth(width);
		texture.setTextureHeight(height);
		texture.setAlphaPremultiplied(false);

		ByteBuffer		byteBuffer;
		Pixel			pixel = null;

//		System.out.println(srcImageType);
		if (srcImageType == BufferedImage.TYPE_INT_ARGB) {
			throw new RuntimeException("Unsupported type: " + srcImage.getType());
		} else if (srcImageType == BufferedImage.TYPE_3BYTE_BGR) {
			pixel = new Pixel3ByteBGR();
		} else if (srcImageType == BufferedImage.TYPE_4BYTE_ABGR) {
			//	Photoshop の 8bit/チャネル として処理する
			//	これは ABGR の各色について、それぞれが 8bit(1Byte) の画像フォーマットとなる。
			pixel = new Pixel4ByteABGR();
		} else if (srcImageType == BufferedImage.TYPE_CUSTOM) {
			//	Photoshop の 16bit/チャネル として処理する
			//	これは ABGR の各色について、それぞれが 16bit(2Byte) の画像フォーマットとなる。
//			pixel = new Pixel8ByteABGR();
			//	この辺の設定は、ひとまず
			//	MikMikuStudio/engine/src/core/com/jme3/texture/Image.java
			//	MikMikuStudio/engine/src/jogl2/com/jme3/renderer/jogl/TextureUtil.java
			//	を参考にしてみた
//			pixel = new Pixel4ByteABGR();
			/*
			dstPixelFormat = GL_RGBA2;
			dstPixelFormat = GL_RGBA8;
			*/
			dstPixelFormat = GL_RGBA16;
			/*
			format = GL_UNSIGNED_SHORT;
			format = GL_SHORT;
			format = GL_UNSIGNED_SHORT_4_4_4_4;
			*/

			//	Miku.png (8bit/channel)がこれで動いたので、ひとまず Pixel4ByteABGR で動かす
			pixel = new Pixel4ByteABGR();
		} else {
			throw new RuntimeException("Unsupported type: " + srcImage.getType());
		}

		byteBuffer = pixel.toBuffer(srcImage);
		byteBuffer.order(ByteOrder.nativeOrder());
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int	b[] = srcImage.getRaster().getPixel(x, y, pixel.getBuffer());
				
				byteBuffer.put(pixel.getRed());		//	Red
				byteBuffer.put(pixel.getGreen());	//	Green
				byteBuffer.put(pixel.getBlue());	//	Blue
				byteBuffer.put(pixel.getAlpha());	//	Alpha
			}
		}
		byteBuffer.flip();

		//	画像の拡大・縮小時の補間方法を設定する
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		//	バイト配列と色情報のフォーマットからテクスチャーを生成する
		glTexImage2D(target,
					0,
					dstPixelFormat,	//	テクスチャ内のカラー要素数
					width,
					height,
					0,			//	テクスチャの境界幅。境界が存在しない場合は 0、存在する場合は 1
					GL_RGBA,	//	ピクセル内の色の順序。 参考 http://wisdom.sakura.ne.jp/system/opengl/gl22.html
					format,		//	各チャネル（色）のデータ型。　参考 http://wisdom.sakura.ne.jp/system/opengl/gl22.html
					byteBuffer);

		//	ミップマップの自動生成
//		GL30.glGenerateMipmap(GL_TEXTURE_2D);

		byteBuffer.clear();

		return texture;
	}
	
	interface Pixel {
		public ByteBuffer toBuffer(BufferedImage srcImage);

		public int[] getBuffer();
		public byte[] getRed();
		public byte[] getGreen();
		public byte[] getBlue();
		public byte[] getAlpha();

	}

	//	1 ピクセルあたりのビット深度 64 の画像フォーマットの変換用クラス
	class Pixel8ByteABGR implements Pixel {
		private int[]	buffer = new int[8];

		@Override
		public int[] getBuffer() {
			return buffer;
		}

		@Override
		public byte[] getRed() {
			return new byte[] {(byte)buffer[0]};
//			return new byte[] {(byte)buffer[0], (byte)buffer[1]};
		}

		@Override
		public byte[] getGreen() {
			return new byte[] {(byte)buffer[1]};
//			return new byte[] {(byte)buffer[2], (byte)buffer[3]};
		}

		@Override
		public byte[] getBlue() {
			return new byte[] {(byte)buffer[2]};
//			return new byte[] {(byte)buffer[4], (byte)buffer[5]};
		}

		@Override
		public byte[] getAlpha() {
			return new byte[] {(byte)buffer[3]};
//			return new byte[] {(byte)buffer[6], (byte)buffer[7]};
		}
		
		@Override
		public ByteBuffer toBuffer(BufferedImage srcImage) {
			DataBufferUShort	imageBuffer = (DataBufferUShort)srcImage.getRaster().getDataBuffer();	
			ByteBuffer			byteBuffer = ByteBuffer.allocateDirect(imageBuffer.getSize() * 2);
			
			return byteBuffer;
		}
	}
	
	//	1 ピクセルあたりのビット深度 32 の画像フォーマットの変換用クラス
	class Pixel4ByteABGR implements Pixel {
		private int[]	buffer = new int[4];

		@Override
		public int[] getBuffer() {
			return buffer;
		}

		@Override
		public byte[] getRed() {
			return new byte[] {(byte)buffer[0]};
		}

		@Override
		public byte[] getGreen() {
			return new byte[] {(byte)buffer[1]};
		}

		@Override
		public byte[] getBlue() {
			return new byte[] {(byte)buffer[2]};
		}

		@Override
		public byte[] getAlpha() {
			return new byte[] {(byte)buffer[3]};
		}

		@Override
		public ByteBuffer toBuffer(BufferedImage srcImage) {
			DataBufferByte	imageBuffer = (DataBufferByte)srcImage.getRaster().getDataBuffer();	
			ByteBuffer		byteBuffer = ByteBuffer.allocateDirect(imageBuffer.getSize());
			
			return byteBuffer;
		}
	}

	class Pixel3ByteBGR implements Pixel {
		private int[]	buffer = new int[3];

		@Override
		public int[] getBuffer() {
			return buffer;
		}

		@Override
		public byte[] getRed() {
			return new byte[] {(byte)buffer[0]};
		}

		@Override
		public byte[] getGreen() {
			return new byte[] {(byte)buffer[1]};
		}

		@Override
		public byte[] getBlue() {
			return new byte[] {(byte)buffer[2]};
		}

		@Override
		public byte[] getAlpha() {
			return new byte[] {(byte)0xff};
		}

		@Override
		public ByteBuffer toBuffer(BufferedImage srcImage) {
			DataBufferByte	imageBuffer = (DataBufferByte)srcImage.getRaster().getDataBuffer();	
			ByteBuffer		byteBuffer = ByteBuffer.allocateDirect(imageBuffer.getSize());
			
			return byteBuffer;
		}
	}

}
