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
package net.chocolapod.lwjgfont.cli;

import java.awt.FontFormatException;
import java.io.IOException;
import java.net.URISyntaxException;

import net.chocolapod.lwjgfont.packager.LwjgFont;


import static net.chocolapod.lwjgfont.cli.CliArgument._p;
import static net.chocolapod.lwjgfont.cli.CliArgument._x;

/**
 * Main logic of LWJGFont in command line mode.
 */
public class Main {

	/**
	 * Main method of LWJGFont in command line mode.
	 */
	public static void main(String[] args) throws IOException, FontFormatException, URISyntaxException {
		CliArgumentParser	parser = new CliArgumentParser(args);	
		LwjgFont				lwjgFont = new LwjgFont(parser.get(_p));
		
		if (parser.hasArgument(_x)) {
			//	キャラクターファイルを展開する
			lwjgFont.extractCharacterFiles();
		} else {
			//	フォントマップを作成する
			for (String fontPath: parser.listFontPaths()) {
				Integer			fontSize = parser.getFontSize(fontPath);
				
				lwjgFont.process(fontPath, fontSize);
			}
			lwjgFont.makePackage();
			lwjgFont.writeProcessLog();
		}
	}

}
