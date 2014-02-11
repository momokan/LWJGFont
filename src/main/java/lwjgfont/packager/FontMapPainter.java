package lwjgfont.packager;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static lwjgfont.packager.LwjgFont.DEFAULT_TEMP_DIR;

import javax.imageio.ImageIO;

import lwjgfont.FontMap;
import lwjgfont.MappedFont;

public class FontMapPainter {
	private static final int		IMAGE_HEIGHT_DEFAULT_EXPONENT = 6;
	private static final int		IMAGE_MAX_LENGTH = 4096;
	public static final int			DEFAULT_PADDING = 5;
	public static final String		DEFAULT_CHARACTERS_DIR = "characters";

	private int						padding = DEFAULT_PADDING;
	private String					charactersDir = DEFAULT_CHARACTERS_DIR;
	private String					resourceBaseDir = DEFAULT_TEMP_DIR;
	private String					packageDirs = null;
	private int						defaultImageHeightExponent = IMAGE_HEIGHT_DEFAULT_EXPONENT;
	private boolean					isWriteImage = true;
	
	//	paint 処理用。paint() の開始から終了までの間の一時データ
	private CharacterFile			file;
	private List<CharacterFile>		files;
	private Font					font;
	private int						imageHeightExponent;
	private int						height;
	private int						width;
	private BufferedImage			bufferedImage;
	private Graphics2D				g;
	
	public FontMap paint(String fontPath, int fontSize) throws IOException, FontFormatException {
		FontMap			fontMap = new FontMap();
		int				x = 0;
		int				y = 0;
		int				srcX;
		int				srcY;
		Character		c = null;
		int				stringWidthOnMap;
		int				stringHeightOnMap;
		int				maxAscent;
		int				maxDescent;
		int				advance;
		int				imageIndex = 0;

		/**
		 *	font.getSize() … ベースラインから上辺までの高さ。この内部は重なることはない。
		 *	g.getFontMetrics().stringWidth() … 左辺から右辺までの高さ。この内部は重なることはない。
		 *	実際には、字によってこの範囲からはみでることがある。
		 *	だが、次の字を描く座標については、この範囲を元に計算してよい。
		 *	参考: http://docs.oracle.com/javase/tutorial/2d/text/measuringtext.html
		 */
		files = CharacterFile.listStreams(charactersDir);
		files.add(new BuiltinCharacterFile());
		imageHeightExponent = defaultImageHeightExponent;
		bufferedImage = null;
		g = null;
		try {
			font = loadFont(fontPath, fontSize);
			/*
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			g = bufferedImage.createGraphics();

			g.setColor(new Color(0f, 0f, 0f, 0f));
			g.fillRect(0, 0, width, height);

			g.setFont(font);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			*/
			prepareImageBuffer();
			
			maxAscent = g.getFontMetrics(font).getMaxAscent();
			maxDescent = g.getFontMetrics(font).getMaxDescent();
			stringHeightOnMap = maxAscent + maxDescent;
			y = maxAscent;
			while ((c = nextCharacter()) != null) {
//				stringWidth = g.getFontMetrics().stringWidth(c.toString());
				advance = g.getFontMetrics(font).charWidth(c);
				stringWidthOnMap = advance + (padding * 2);
				
				if (width < x + stringWidthOnMap) {
					x = 0;
					
					if (height < y + stringHeightOnMap) {
						//	extent image height
						if (!extentImageBuffer()) {
							break;
						}
					}
					y += stringHeightOnMap;
				}
				g.setColor(new Color(0.8f, 0.8f, 1f));
				g.drawRect(x, y - maxAscent, stringWidthOnMap, stringHeightOnMap);

				srcX = x + padding;
				srcY = y;
				g.setColor(Color.WHITE);
				g.drawString(c.toString(), srcX, srcY);
				System.out.println("(" + x + ", " + y + ") :" + c.toString());
				fontMap.addMappedFont(new MappedFont(c, imageIndex, srcX, srcY, maxAscent, maxDescent, advance, imageIndex));
				x += stringWidthOnMap;
			}

			fontMap.addImageFile(imageIndex, writeFontMapImage(bufferedImage, font.getName(), imageIndex));
			imageIndex++;
		} finally {
			disposeImageBuffer(g, bufferedImage);
			if (file != null) {
				file.close();
			}
		}
		
		return fontMap;
	}
	
	private String writeFontMapImage(BufferedImage bufferedImage, String fontName, int imageIndex) throws IOException {
		String		fileName = fontName.replace(' ', '_') + "_" + imageIndex + ".png";
		File		dir = LwjgFontUtil.prepareDirectory(resourceBaseDir, packageDirs);
		
		if (isWriteImage) {		
			ImageIO.write(bufferedImage, "png", new File(dir.getPath() + File.separator + fileName));
		}
		
		return fileName;
	}

	private Character nextCharacter() throws IOException {
		Character		c = null;
		
		if (file == null) {
			if (files.size() <= 0) {
				//	次のファイルがなければ null を返す
				return null;
			}
			//	次のファイルを開く
			file = files.remove(0);
			file.open();
		}
		
		c = file.next();
		
		if (c == null) {
			//	現在のファイルからはもう読めない
			file.close();
			file = null;
			return nextCharacter();
		}
		
		return c;
	}
	
	/**
	 *	フォントを読み込む
	 */
	private Font loadFont(String fontPath, int fontSize) throws IOException, FontFormatException {
		InputStream		in = null;

		try {
			in = new FileInputStream(fontPath);

			return Font.createFont(Font.TRUETYPE_FONT, in).deriveFont((float)fontSize);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
	}
	
	private void prepareImageBuffer() {
		height = (int)Math.pow(2, imageHeightExponent);
		width = IMAGE_MAX_LENGTH;
		
		bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		g = bufferedImage.createGraphics();

		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, width, height);

		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//		System.out.println("create @" + bufferedImage.hashCode() + " " + width + "x" + height);
	}
	
	private void disposeImageBuffer(Graphics2D g, BufferedImage bufferedImage) {
		if (g != null) {
			g.dispose();
		}
		if (bufferedImage != null) {
			bufferedImage.flush();
//			System.out.println("dispose @" + bufferedImage.hashCode() );
		}
	}
	
	private boolean extentImageBuffer() {
		imageHeightExponent++;
		
		if (IMAGE_MAX_LENGTH <= (int)Math.pow(2, imageHeightExponent)) {
			return false;
		}

		Graphics2D			oldG = g;
		BufferedImage		oldBufferedImage = bufferedImage; 
		
		prepareImageBuffer();

		g.drawImage(oldBufferedImage, 0, 0, null);
		
		disposeImageBuffer(oldG, oldBufferedImage);
		
		return true;
	}
	
	public void setPadding(int padding) {
		this.padding = padding;
	}
	public void setImageHeightExponent(int imageHeightExponent) {
		this.imageHeightExponent = imageHeightExponent;
	}
	public void setResourceBaseDir(String resourceBaseDir) {
		this.resourceBaseDir = resourceBaseDir;
	}
	void setPackageDirs(String packageDirs) {
		this.packageDirs = packageDirs;
	}
	public void setCharactersDir(String charactersDir) {
		this.charactersDir = charactersDir;
	}
	public void setWriteImage(boolean isWriteImage) {
		this.isWriteImage = isWriteImage;
	}

	public static void main(String[] args) throws IOException, FontFormatException {
		new FontMapPainter().paint("sample/migu-1p-regular.ttf", 40);
	}

}
