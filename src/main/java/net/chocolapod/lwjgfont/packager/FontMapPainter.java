/**
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2014 momokan (http://lwjgfont.chocolapod.net)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.chocolapod.lwjgfont.packager;

import static net.chocolapod.lwjgfont.packager.LwjgFontFactory.DEFAULT_TEMP_DIR;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import net.chocolapod.lwjgfont.FontMap;
import net.chocolapod.lwjgfont.MappedCharacter;


public class FontMapPainter {
	private static final int		IMAGE_HEIGHT_DEFAULT_EXPONENT = 6;
	private static final int		IMAGE_MAX_LENGTH = 4096;
	public static final int			DEFAULT_PADDING = 5;
	public static final String		DEFAULT_CHARACTERS_DIR = "characters";
	public static final boolean		DEFAULT_WRITE_IMAGE = true;
	public static final boolean		DEFAULT_WRITE_IMAGE_FRAME = false;

	private int						padding = DEFAULT_PADDING;
	private String					charactersDir = DEFAULT_CHARACTERS_DIR;
	private String					resourceDir = DEFAULT_TEMP_DIR;
	private String					packageDirs = null;
	private int						defaultImageHeightExponent = IMAGE_HEIGHT_DEFAULT_EXPONENT;
	private boolean					isWriteImage = DEFAULT_WRITE_IMAGE;
	private boolean					isWriteImageFrame = DEFAULT_WRITE_IMAGE_FRAME;

	//	paint 処理用。paint() の開始から終了までの間の一時データ
	private CharacterFile			file;
	private List<CharacterFile>		files;
	private Font					font;
	private int						imageHeightExponent;
	private int						height;
	private int						width;
	private BufferedImage			bufferedImage;
	private Graphics2D				g;
	
	public FontMap paint(FontSetting fontSetting) throws IOException, FontFormatException {
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
		Color			frameColor = new Color(0.8f, 0.8f, 1f);

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
			if (fontSetting.isSystemFont()) {
				//	@ で始まるフォントはシステムフォントとして扱う
				font = new Font(fontSetting.getFontPath(), Font.PLAIN, fontSetting.getFontSize());
			} else {
				//	指定のパスのファイルをフォントとして読み込む
				font = loadFont(fontSetting.getFontPath(), fontSetting.getFontSize());
			}
			fontSetting.configure(font.getName());
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
			fontMap.setLineHeight(stringHeightOnMap);
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
				if (isWriteImageFrame) {
					g.setColor(frameColor);
					g.drawRect(x, y - maxAscent, stringWidthOnMap, stringHeightOnMap);
				}

				srcX = x + padding;
				srcY = y;
				g.setColor(Color.WHITE);
				g.drawString(c.toString(), srcX, srcY);
//				System.out.println("(" + x + ", " + y + ") :" + c.toString());
				fontMap.addCharacter(new MappedCharacter(c, imageIndex, srcX, srcY, maxAscent, maxDescent, advance, imageIndex));
				x += stringWidthOnMap;
			}

			fontMap.addImageFile(imageIndex, writeFontMapImage(bufferedImage, fontSetting, imageIndex));
			imageIndex++;
		} finally {
			disposeImageBuffer(g, bufferedImage);
			if (file != null) {
				file.close();
			}
		}
		
		return fontMap;
	}

	private String writeFontMapImage(BufferedImage bufferedImage, FontSetting fontSettings, int imageIndex) throws IOException {
		String		fileName = fontSettings.getImageFileName(imageIndex);
		File		dir = LwjgFontUtil.prepareDirectory(resourceDir, packageDirs);
		
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
	public void setResourceDir(String resourceDir) {
		this.resourceDir = resourceDir;
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
	public void setWriteImageFrame(boolean isWriteImageFrame) {
		this.isWriteImageFrame = isWriteImageFrame;
	}

}
