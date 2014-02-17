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

/**
 * The abstract class of all font classes which are generated by LWJGFont.<br>
 * Each subclasses of this class represent a font and its size to render strings on LWJGL.<br>
 *	This class provide some utility methods for text rendering.<br>
 *	<br>
 *	Example with ClassicTrueTypeH20Font (Let This class be a subclass of AbstractFont).<br>
 * <br>
 * <pre>
 * {@code
 * // Create font a class.
 * ClassicTrueTypeH20Font font = new ClassicTrueTypeH20Font();
 * // Set foreground color.
 * font.setColor(1f, 0f, 0f);
 * // Draw a string on the location.
 * font.drawString("Hello, LWJGFont.", 100f, 100f, 0f);
 * }
 * </pre>
 */
public abstract class AbstractFont {
	private float r = 1f;
	private float g = 1f;
	private float b = 1f;
	
	/**
	 * Draws the text given by the specified string, using this font instance's current color.<br>
	 * Note that the specified destination coordinates is a left point of the rendered string's baseline.
	 * @param text the string to be drawn.
	 * @param dstX the x coordinate to render the string.
	 * @param dstY the y coordinate to render the string.
	 * @param dstZ the z coordinate to render the string.
	 * @throws IOException Indicates a failure to read font images as textures.
	 */
	public final void drawString(String text, float dstX, float dstY, float dstZ) throws IOException {
		DrawPoint		drawPoint = new DrawPoint(dstX, dstY, dstZ);
		
		for (int i = 0; i < text.length(); i++) {
			drawCharacter(drawPoint, text.charAt(i));
		}
	}
	
	private void drawCharacter(DrawPoint drawPoint, char ch) throws IOException {
		MappedCharacter	character = getMappedCharacter(ch);
		
		if (character == null) {
			//	指定の文字が描画対象でなければ、豆腐を表示する
			character = getMappedCharacter(NotMatchedSign.getCharacter());
		}

		float	dstX1 = drawPoint.dstX - character.getPadding();
		float	dstY1 = drawPoint.dstY + character.getAscent() + character.getPadding();
		float	dstX2 = drawPoint.dstX + character.getAdvance() + character.getPadding();
		float	dstY2 = drawPoint.dstY - character.getDescent() - character.getPadding();
		float	srcX1 = character.getSrcX() - character.getPadding();
		float	srcY1 = character.getSrcY() - character.getAscent() - character.getPadding();
		float	srcX2 = character.getSrcX() + character.getAdvance() + character.getPadding();
		float	srcY2 = character.getSrcY() + character.getDescent() + character.getPadding();
		
		String		imagePath = getImagePath(character.getImageIndex());
		Texture		texture = TextureLoader.loadTexture(this.getClass(), imagePath);
		
		texture.setColor(r, g, b);
		texture.draw(dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2);
		
		drawPoint.dstX += character.getAdvance();
	}
	
	/**
	 * Return the specified character's font informations to render the character with font which this instance represents.<br>
	 * The returned MappedCharacter instance has ascent size, descent size, advance size and more of the specified character.
	 * @param character the target character.
	 * @return a MappedCharacter represents the specified character's font informations to render.
	 */
	public final MappedCharacter getMappedCharacter(char character) {
		return getFontMap().getMappedCharacter(character);
	}
	
	private String getImagePath(int index) {
		return getFontMap().getImageFile(index);
	}
	
	/**
	 * Set the color value as RGB to render any string with the font represented by this class.<br>
	 * @param r the red value of the color. It must be between 0f to 1f.
	 * @param g the green value of the color. It must be between 0f to 1f.
	 * @param b the blue value of the color. It must be between 0f to 1f.
	 */
	public final void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 *	Returns a FontMap instance which has informations that how to render any character with the font represented by this class.<br>
	 *	This method is only called by subclass which is generated by LwjgFont.
	 * @return a FontMap which has informations that how to render any character with the font represented by this class.
	 */
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
