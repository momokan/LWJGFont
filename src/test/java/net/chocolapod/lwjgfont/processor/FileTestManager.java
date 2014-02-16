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
package net.chocolapod.lwjgfont.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.chocolapod.lwjgfont.packager.LwjgFontUtil;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

class FileTestManager {
	static final String		TEST_DIR = "test-" + LwjgFontUtil.class.getSimpleName();
	static final String		TEST_BASE_DIR = "target/" + TEST_DIR;
	
	public static String createFile(String path) throws IOException {
		FileOutputStream		out = null;
		
		try {
			out = new FileOutputStream(new File(path));
			out.write(path.getBytes());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return path;
	}
	
	public static String assertFileExists(String path) {
		File	file = new File(path);

		assertTrue(file.exists());
		assertTrue(!file.isDirectory());
		
		return file.getPath();
	}
	
	public static String assertDirectoryExists(String path) {
		File	file = new File(path);

		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		
		return file.getPath();
	}

	public static String assertNotExists(String path) {
		File	file = new File(path);

		assertTrue(!file.exists());
		
		return file.getPath();
	}

	public static void assertFilePath(String expectedFilePath, File actualFile) throws IOException {
		File	expectedFile = new File(expectedFilePath);
		
		String	expectedPath = expectedFile.getCanonicalPath();
		String	actualPath = actualFile.getCanonicalPath();

		assertEquals(expectedPath, actualPath);
	}
}
