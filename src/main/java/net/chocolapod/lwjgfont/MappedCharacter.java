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

/**
 * The MappedCharacter class represents how each characters of the font are mapped on textures by LWJGFont.<br>
 * To drawing the character, this class has these informations.
 * <ul>
 * <li>a index of the image that the character is mapped onto.</li>
 * <li>coordinates that the character is mapped on.</li>
 * <li>a ascent distance from the font's baseline to the top of most alphanumeric characters.</li>
 * <li>a descent distance from the font's baseline to the bottom of most alphanumeric characters.</li>
 * <li>a advance distance from the leftmost point to the rightmost point on the string's baseline.</li>
 * <li>a padding distance around rectangle which the ascent, the descent, and the advance shape</li>
 * </ul>
 */
public class MappedCharacter {
	private final char		character;
	private final int		imageIndex;
	private final int		srcX;
	private final int		srcY;
	private final int		ascent;
	private final int		descent;
	private final int		advance;
	private final int		padding;

	/**
	 * Constructs a new MappedCharacter object that represents how the specified character of the font are mapped on textures by LWJGFont.
	 * @param character the character value mapped by this object.
	 * @param imageIndex the index of source image which contains this character.
	 * @param srcX the x coordinate of the base point on source image.
	 * @param srcY the y coordinate of the base point on source image.
	 * @param ascent this character's ascent of the font
	 * @param descent this character's descent of the font
	 * @param advance this character's advance of the font
	 * @param padding this character's padding distance.
	 */
	public MappedCharacter(char character, int imageIndex, int srcX, int srcY, int ascent, int descent, int advance, int padding) {
		this.character = character;
		this.imageIndex = imageIndex;
		this.srcX = srcX;
		this.srcY = srcY;
		this.ascent = ascent;
		this.descent = descent;
		this.advance = advance;
		this.padding = padding;
	}

	/**
	 * Returns the character value of this MappedCharacter object.
	 * @return the character value mapped by this object.
	 */
	public char getCharacter() {
		return character;
	}

	/**
	 * Returns the index of source image which contains this character.<br>
	 * LWJGFont pre-renders all characters on some images as PNG format
	 * and this method returns its index.
	 * @return the index of source image which contains this character.
	 */
	public int getImageIndex() {
		return imageIndex;
	}

	/**
	 * Returns the x coordinate of the base point on source image.<br>
	 * This base point is mapped to the character's left point on its base line.
	 * @return the x coordinate of the base point on source image.
	 */
	public int getSrcX() {
		return srcX;
	}

	/**
	 * Returns the y coordinate of the base point on source image.<br>
	 * This base point is mapped to the character's left point on its base line.
	 * @return the y coordinate of the base point on source image.
	 */
	public int getSrcY() {
		return srcY;
	}

	/**
	 * Returns this character's ascent of the font which this MappedCharacter instance is mapped on.<br>
	 * The font ascent is the distance from the font's baseline to the top of most alphanumeric characters.
	 * Some characters in the Font might extend above the font ascent line.
	 * @return this character's ascent of the font
	 */
	public int getAscent() {
		return ascent;
	}

	/**
	 * Returns this character's descent of the font which this MappedCharacter instance is mapped on.<br>
	 * The font descent is the distance from the font's baseline to the bottom of most alphanumeric characters.
	 * Some characters in the Font might extend below the font descent line.
	 * @return this character's descent of the font
	 */
	public int getDescent() {
		return descent;
	}

	/**
	 * Returns this character's advance of the font which this MappedCharacter instance is mapped on.<br>
	 * The advance is the distance from the leftmost point to the rightmost point on the string's baseline.
	 * The advance of any String is the sum of the advances of its characters.
	 * @return this character's advance of the font
	 */
	public int getAdvance() {
		return advance;
	}

	/**
	 * Returns this character's padding distance around rectangle which the ascent, the descent, and the advance shape.<br>
	 * Some characters might extend over its ascent, descent and advance. So each characters are mapped with paddings around it.
	 * @return this character's padding distance.
	 */
	public int getPadding() {
		return padding;
	}

}
