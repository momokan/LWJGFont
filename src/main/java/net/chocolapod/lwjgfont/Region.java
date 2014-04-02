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

public class Region {
	private int		width;
	private int		height;
	
	public Region() {
		this(0, 0);
	}
	public Region(int width, int height) {
		this.width = width;
		this.height = height;
	}

	void updateRegion(Region region, int lineMargin) {
		updateWidth(region.getWidth());
		extendHeight(region.getHeight(), lineMargin);
	}

	void updateWidth(int width) {
		if (this.width < width) {
			this.width = width;
		}
	}
	
	void extendHeight(int extension, int lineMargin) {
		if (0 < this.height) {
			this.height += lineMargin;
		}
		this.height += extension;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}


}
