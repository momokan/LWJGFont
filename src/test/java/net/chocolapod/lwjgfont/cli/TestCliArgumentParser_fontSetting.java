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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.chocolapod.lwjgfont.exception.cli.UnknownArgumentException;
import net.chocolapod.lwjgfont.packager.FontSetting;

import org.junit.Test;

public class TestCliArgumentParser_fontSetting {

	@Test
	public void normal() {
		assertHasSingleFontSetting("font.ttf", 20, "font.ttf:20");
	}

	@Test
	public void relativeFontPath() {
		assertHasSingleFontSetting("../fonts/font.ttf", 20, "../fonts/font.ttf:20");
	}
	
	@Test(expected = UnknownArgumentException.class)
	public void noPath() {
		assertHasSingleFontSetting(null, 20, ":20");
	}

	@Test(expected = UnknownArgumentException.class)
	public void noSize() {
		assertHasSingleFontSetting(null, 20, "font.ttf:");
	}

	private void assertHasSingleFontSetting(String expectedFontPath, int expectedFontSize, String optionString) {
		CliArgumentParser	parser = new CliArgumentParser(optionString.split(" +"));
		CliArgument[]			arguments = parser.listArguments();

		assertEquals(1, arguments.length);
		assertTrue(arguments[0] instanceof FontSetting);
		TestCliArgumentParser.assertFontSetting(new FontSetting(expectedFontPath, expectedFontSize), (FontSetting)arguments[0]);
	}

}
