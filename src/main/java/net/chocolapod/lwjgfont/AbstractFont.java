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
package net.chocolapod.lwjgfont;

import java.io.IOException;

import net.chocolapod.lwjgfont.texture.Texture;
import net.chocolapod.lwjgfont.texture.TextureLoader;


import static net.chocolapod.lwjgfont.packager.BuiltinCharacter.NotMatchedSign;

public abstract class AbstractFont {
	protected static final FontMap	map = new FontMap();

	private float r = 1f;
	private float g = 1f;
	private float b = 1f;
	
	public void drawString(String text, float dstX, float dstY, float dstZ) throws IOException {
		DrawPoint		drawPoint = new DrawPoint(dstX, dstY, dstZ);
		
		for (int i = 0; i < text.length(); i++) {
			drawCharacter(drawPoint, text.charAt(i));
		}
	}
	
	private void drawCharacter(DrawPoint drawPoint, char ch) throws IOException {
		MappedFont	font = getMappedCharacter(ch);
		
		if (font == null) {
			//	指定の文字が描画対象でなければ、豆腐を表示する
			font = getMappedCharacter(NotMatchedSign.getCharacter());
		}

		float	dstX1 = drawPoint.dstX - font.getPadding();
		float	dstY1 = drawPoint.dstY + font.getAscent() + font.getPadding();
		float	dstX2 = drawPoint.dstX + font.getAdvance() + font.getPadding();
		float	dstY2 = drawPoint.dstY - font.getDescent() - font.getPadding();
		float	srcX1 = font.getSrcX() - font.getPadding();
		float	srcY1 = font.getSrcY() - font.getAscent() - font.getPadding();
		float	srcX2 = font.getSrcX() + font.getAdvance() + font.getPadding();
		float	srcY2 = font.getSrcY() + font.getDescent() + font.getPadding();
		
		String		imagePath = getImagePath(font.getImageIndex());
		Texture		texture = TextureLoader.loadTexture(this.getClass(), imagePath);
		
		texture.setColor(r, g, b);
		texture.draw(dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2);
		
		drawPoint.dstX += font.getAdvance();
	}
	
	public MappedFont getMappedCharacter(char ch) {
		return getFontMap().getMappedFont(ch);
	}
	
	private String getImagePath(int index) {
		return map.getImageFile(index);
	}
	
	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	protected abstract FontMap getFontMap();

	class DrawPoint {
		private float		dstX;
		private float		dstY;
		private float		dstZ;
		
		private DrawPoint(float dstX, float dstY, float dstZ) {
			this.dstX = dstX;
			this.dstY = dstY;
			this.dstZ = dstZ;
		}
	}
}
