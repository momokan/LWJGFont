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
import java.io.IOException;


import net.chocolapod.lwjgfont.packager.LwjgFontUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.chocolapod.lwjgfont.packager.FileTestManager.TEST_BASE_DIR;
import static net.chocolapod.lwjgfont.packager.FileTestManager.assertDirectoryExists;
import static net.chocolapod.lwjgfont.packager.FileTestManager.assertFilePath;
import static net.chocolapod.lwjgfont.packager.FileTestManager.assertNotExists;

public class TestLwjgFontUtil_prepareDirectory {

	@Before
	@After
	public void cleanTestBaseDir() {
		LwjgFontUtil.deleteFile(TEST_BASE_DIR);
		assertNotExists(TEST_BASE_DIR);
	}

	@Test
	public void prepare1Directory() throws IOException {
		LwjgFontUtil.prepareDirectory(TEST_BASE_DIR);
		
		assertDirectoryExists(TEST_BASE_DIR);
	}

	@Test
	public void prepare2Directory() throws IOException {
		LwjgFontUtil.prepareDirectory(TEST_BASE_DIR, "dir01");
		
		assertDirectoryExists(TEST_BASE_DIR);
		assertDirectoryExists(TEST_BASE_DIR + File.separator + "dir01");
	}
	
	@Test
	public void prepare3Directory() throws IOException {
		LwjgFontUtil.prepareDirectory(TEST_BASE_DIR, "dir01", "dir02");
		
		assertDirectoryExists(TEST_BASE_DIR);
		assertDirectoryExists(TEST_BASE_DIR + File.separator + "dir01");
		assertDirectoryExists(TEST_BASE_DIR + File.separator + "dir01" + File.separator + "dir02");
	}
	
	@Test
	public void returnPathFromPreapareDirectory() throws IOException {
		File	file = LwjgFontUtil.prepareDirectory(TEST_BASE_DIR, "dir01", "dir02");
		
		assertFilePath(TEST_BASE_DIR + File.separator + "dir01" + File.separator + "dir02", file);
	}
}
