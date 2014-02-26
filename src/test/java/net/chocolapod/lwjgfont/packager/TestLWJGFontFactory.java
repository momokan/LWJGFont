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

import static org.junit.Assert.assertEquals;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import static net.chocolapod.lwjgfont.packager.TestResources.PROPERTY_DIST_DIR;
import static net.chocolapod.lwjgfont.packager.TestResources.TEST_JAR_PATH;

public class TestLWJGFontFactory {
	
	@BeforeClass
	public static void ceateJar() throws IOException, FontFormatException {
		File	jar = new File(TEST_JAR_PATH);
		
		jar.delete();
		if (jar.exists()) {
			throw new IOException("cannot initialize " + TEST_JAR_PATH);
		}
		
		//	create jar
		LwjgFontFactory		lwjgFont = new LwjgFontFactory(TestResources.FILE_TEST_PROPERTIES);

		lwjgFont.create(new FontSetting(TestResources.FILE_MOSAMOSAFONT, 8));
		lwjgFont.makePackage();
//		lwjgFont.writeProcessLog();
	}
	
	@Test
	public void existsJar() {
		assertEquals(true, new File(TEST_JAR_PATH).isFile());
	}

	//	TODO
}
