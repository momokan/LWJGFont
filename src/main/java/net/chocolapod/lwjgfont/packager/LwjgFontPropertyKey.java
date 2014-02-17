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

import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_CHARACTERS_DIR;
import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_PADDING;
import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_WRITE_IMAGE;
import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_WRITE_IMAGE_FRAME;
import static net.chocolapod.lwjgfont.packager.LwjgFont.DEFAULT_TEMP_DIR;

public enum LwjgFontPropertyKey {
	IMAGE_DRAW(DEFAULT_WRITE_IMAGE),
	IMAGE_DRAW_FRAME(DEFAULT_WRITE_IMAGE_FRAME),
	IMAGE_CHARACTER_PADDING(DEFAULT_PADDING),
	CHARACTER_FILE_DIR(DEFAULT_CHARACTERS_DIR),
	TEMP_DIR(DEFAULT_TEMP_DIR),
	ARTIFACT_NAME("myfont"),
	ARTIFACT_VERSION("1.0-SNAPSHOT");

	private final String	defaultValue;

	private LwjgFontPropertyKey() {
		this("");
	}
	private LwjgFontPropertyKey(boolean defaultValue) {
		this(String.valueOf(defaultValue));
	}
	private LwjgFontPropertyKey(int defaultValue) {
		this(String.valueOf(defaultValue));
	}
	private LwjgFontPropertyKey(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	
	public static String toResourceReplacePattern(LwjgFontPropertyKey key) {
		String[]	tokens = key.name().toLowerCase().split("_");
		String		pattern = "";
		
		for (String token: tokens) {
			pattern += LwjgFontUtil.capitalize(token);
		}

		pattern = pattern.substring(0, 1).toLowerCase() + pattern.substring(1);
		pattern = '%' + pattern + '%';
		
		return pattern;
	}

	@Override
	public String toString() {
		String	key = name();
		
		key = key.replaceAll("_", ".");
		key = key.toLowerCase();
		
		return key;
	}
	
}
