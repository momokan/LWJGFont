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

import static net.chocolapod.lwjgfont.packager.TestResources.CLASS_MOSAMOSAFONT_8;
import static net.chocolapod.lwjgfont.packager.TestResources.ENTRY_MOSAMOSAFONT_8;
import static net.chocolapod.lwjgfont.packager.TestResources.IMAGE_MOSAMOSAFONT_8;
import static net.chocolapod.lwjgfont.packager.TestResources.CLASS_MOSAMOSAFONT_9;
import static net.chocolapod.lwjgfont.packager.TestResources.ENTRY_MOSAMOSAFONT_9;
import static net.chocolapod.lwjgfont.packager.TestResources.IMAGE_MOSAMOSAFONT_9;
import static net.chocolapod.lwjgfont.packager.TestResources.CLASS_MOSAMOSAFONT_10;
import static net.chocolapod.lwjgfont.packager.TestResources.ENTRY_MOSAMOSAFONT_10;
import static net.chocolapod.lwjgfont.packager.TestResources.IMAGE_MOSAMOSAFONT_10;
import static net.chocolapod.lwjgfont.packager.TestResources.CLASS_MOSAMOSAFONT_11;
import static net.chocolapod.lwjgfont.packager.TestResources.ENTRY_MOSAMOSAFONT_11;
import static net.chocolapod.lwjgfont.packager.TestResources.IMAGE_MOSAMOSAFONT_11;
import static net.chocolapod.lwjgfont.packager.TestResources.FILE_MOSAMOSAFONT;
import static net.chocolapod.lwjgfont.packager.TestResources.FILE_MOSAMOSAFONT_ALIAS01;
import static net.chocolapod.lwjgfont.packager.TestResources.FILE_MOSAMOSAFONT_ALIAS02;
import static net.chocolapod.lwjgfont.packager.TestResources.TEST_JAR_PATH;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestLWJGFontFactory_packageMultiFonts {
	
	@BeforeClass
	public static void prepareJar() throws IOException, FontFormatException, URISyntaxException {
		TestLWJGFontFactory.ceateJar(
			new FontSetting(FILE_MOSAMOSAFONT, 8),
			new FontSetting(FILE_MOSAMOSAFONT, 9),
			new FontSetting(FILE_MOSAMOSAFONT_ALIAS01, 8),
			new FontSetting(FILE_MOSAMOSAFONT_ALIAS01, 10),
			new FontSetting(FILE_MOSAMOSAFONT_ALIAS02, 8),
			new FontSetting(FILE_MOSAMOSAFONT_ALIAS02, 11)
		);
	}
	
	@Test
	public void loadLWJGFontClass() throws ClassNotFoundException, IOException, FontFormatException {
		URLClassLoader	classLoader = null;
		
		try {
			classLoader = new URLClassLoader(new URL[] {new File(TEST_JAR_PATH).toURI().toURL()});
	
			assertNotNull(classLoader.loadClass(CLASS_MOSAMOSAFONT_8));
			assertNotNull(classLoader.loadClass(CLASS_MOSAMOSAFONT_9));
			assertNotNull(classLoader.loadClass(CLASS_MOSAMOSAFONT_10));
			assertNotNull(classLoader.loadClass(CLASS_MOSAMOSAFONT_11));
		} finally {
			if (classLoader != null) {
				try {
					classLoader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Test
	public void checkEntries() throws IOException {
		TestLWJGFontFactory_packageDuplicateFonts.assertEntiries(
			ENTRY_MOSAMOSAFONT_8, IMAGE_MOSAMOSAFONT_8,
			ENTRY_MOSAMOSAFONT_9, IMAGE_MOSAMOSAFONT_9,
			ENTRY_MOSAMOSAFONT_10, IMAGE_MOSAMOSAFONT_10,
			ENTRY_MOSAMOSAFONT_11, IMAGE_MOSAMOSAFONT_11
		);
	}

}
