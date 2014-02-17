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

public class MappedCharacter {
	private final char		character;
	private final int		imageIndex;
	private final int		srcX;
	private final int		srcY;
	private final int		ascent;
	private final int		descent;
	private final int		advance;
	private final int		padding;

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

	public char getCharacter() {
		return character;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public int getSrcX() {
		return srcX;
	}

	public int getSrcY() {
		return srcY;
	}

	public int getAscent() {
		return ascent;
	}

	public int getDescent() {
		return descent;
	}

	public int getAdvance() {
		return advance;
	}

	public int getPadding() {
		return padding;
	}

}
