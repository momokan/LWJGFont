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
import net.chocolapod.lwjgfont.packager.FontSetting;

import org.junit.Test;

import static net.chocolapod.lwjgfont.cli.CliOption._x;
import static net.chocolapod.lwjgfont.cli.CliOption._p;
import static net.chocolapod.lwjgfont.cli.CliOption._v;

public class TestCliArgumentParser {

	@Test
	public void x() {
		assertArguments(
				new String[] {"-x"},
				_x);
	}

	@Test
	public void options() {
		assertArguments(
				new String[] {"-x", "-p", "lwjgfont.properties", "-v"},
				_x,
				_p,
				_v);
	}

	@Test
	public void pAndFont() {
		assertArguments(
				new String[] {"-p", "lwjgfont.properties", "font.ttf:20"},
				_p,
				new FontSetting("font.ttf", 20));
	}

	@Test
	public void pAndFonts() {
		assertArguments(
				new String[] {"-p", "lwjgfont.properties", "font.ttf:20", "../share/font.ttf:16"},
				_p,
				new FontSetting("font.ttf", 20),
				new FontSetting("../share/font.ttf", 16));
	}

	@Test
	public void pAndFontAndSystemFont() {
		assertArguments(
				new String[] {"-p", "lwjgfont.properties", "font.ttf:20", "@font01:16"},
				_p,
				new FontSetting("font.ttf", 20),
				new SystemFontSetting4Test("font01", 16, null));
	}

	@Test
	public void pAndFontAndSystemFonts() {
		assertArguments(
				new String[] {"-p", "lwjgfont.properties", "font.ttf:20", "@font01:16", "f02@font02:14"},
				_p,
				new FontSetting("font.ttf", 20),
				new SystemFontSetting4Test("font01", 16, null),
				new SystemFontSetting4Test("font02", 14, "f02"));
	}

	private void assertArguments(String[] args, CliArgument ...expecteds) {
		CliArgumentParser	parser = new CliArgumentParser(args);
		CliArgument[]			actuals = parser.listArguments();

		assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			CliArgument			actual = actuals[i];
			CliArgument			expected = expecteds[i];

			if (expected instanceof FontSetting) {
				assertFontSetting((FontSetting)expected, (FontSetting)actual);
			} else if (expected instanceof CliOption) {
				assertEquals(expected, actual);
				if (((CliOption)expected).hasValue()) {
					assertTrue(parser.get((CliOption)actual) != null);
				} else {
					assertTrue(parser.get((CliOption)actual) == null);
				}
			} else {
				assertTrue(false);
			}
		}
	}

	static void assertFontSetting(FontSetting expected, FontSetting actual) {
		assertEquals(expected.isSystemFont(), actual.isSystemFont());
		assertEquals(expected.getFontPath(), actual.getFontPath());
		assertEquals(expected.getFontAlias(), actual.getFontAlias());
		assertEquals(expected.getFontSize(), actual.getFontSize());
	}

	static void assertSystemFontSetting(String expectedFontPath, int expectedFontSize, String expectedFontAlias, FontSetting actual) {
		assertFontSetting(new SystemFontSetting4Test(expectedFontPath, expectedFontSize, expectedFontAlias), actual);
	}

	static class SystemFontSetting4Test extends FontSetting {

		public SystemFontSetting4Test(String fontPath, int fontSize, String fontAlias) {
			super(fontPath, fontSize, fontAlias);
		}
		
	}
}
