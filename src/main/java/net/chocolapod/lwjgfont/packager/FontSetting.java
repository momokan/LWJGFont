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

import java.io.File;

import net.chocolapod.lwjgfont.cli.CliArgument;


public class FontSetting implements CliArgument {
	private static final String	SYSTEM_FONT_ALIAS = "System Font";
	
	private static int		systemFontNumber = 0;
	
	private final String	fontPath;
	private final int		fontSize;
	private final String	fontAlias;
	private final boolean	isSystemFont;
	
	private String			fontClassName;
	
	public FontSetting(String fontPath, int fontSize) {
		this.fontPath = fontPath;
		this.fontSize = fontSize;
		this.fontAlias = null;
		this.isSystemFont = false;
	}
	private FontSetting(String fontPath, int fontSize, String fontAlias) {
		this.fontPath = fontPath;
		this.fontSize = fontSize;
		this.fontAlias = fontAlias;
		this.isSystemFont = true;
	}
	
	public String getFontPath() {
		return fontPath;
	}
	public int getFontSize() {
		return fontSize;
	}
	public boolean isSystemFont() {
		return isSystemFont;
	}

	//	TODO リファクタリング
	public void configure(String loadedFontName) {
		String	fontName;
		boolean	isArrangeCase = true;

		if (isSystemFont) {
			if (LwjgFontUtil.isEmpty(fontAlias)) {
				if (loadedFontName.matches("\\p{ASCII}*")) {
					//	ASCII 文字のみであれば、そのまま使う
					fontName = loadedFontName;
				} else {
					//	ASCII 文字以外が含まれていれば、連番を割り当てる
					fontName = SYSTEM_FONT_ALIAS + systemFontNumber;
					systemFontNumber++;
				}
			} else {
				fontName = fontAlias;
				isArrangeCase = false;
			}
		} else {
			fontName = LwjgFontUtil.trimExtention(new File(fontPath).getName());
		}
		
		//	fontClassName を設定する
		fontClassName = toFontClassName(fontName, isArrangeCase);
	}
	
	private String toFontClassName(String fontName, boolean isArrangeCase) {
		String	fontClassName = "";
		
		if (isArrangeCase) {
			String[]	tokens = fontName.split("[^0-9a-zA-Z]+");
	
			for (String token: tokens) {
				fontClassName += LwjgFontUtil.capitalize(token);
			}
		} else {
			fontClassName = fontName;
		}
		
		fontClassName += "H" + fontSize;
		fontClassName += "Font";
		
		return fontClassName;
	}

	public String getImageFileName(int imageIndex) {
		return fontClassName + "_" + imageIndex + ".png";
	}

	public String getFontClassName() {
		return fontClassName;
	}

	public String getFontAlias() {
		return fontAlias;
	}
	
	public static FontSetting asSystemFont(String fontPath, int fontSize) {
		int		p = fontPath.indexOf("@");
		
		if (p <= -1) {
			return null;
		}
		
		String	fontName = fontPath.substring(p + 1);
		String	fontAlias = fontPath.substring(0, p);
		
		if (LwjgFontUtil.isEmpty(fontAlias)) {
			fontAlias = null;
		}
		
		return new FontSetting(fontName, fontSize, fontAlias);
	}

}
