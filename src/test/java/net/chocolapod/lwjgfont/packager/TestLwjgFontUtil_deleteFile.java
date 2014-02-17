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

import org.junit.Test;

import static net.chocolapod.lwjgfont.packager.FileTestManager.TEST_BASE_DIR;
import static net.chocolapod.lwjgfont.packager.FileTestManager.TEST_DIR;
import static net.chocolapod.lwjgfont.packager.FileTestManager.assertDirectoryExists;
import static net.chocolapod.lwjgfont.packager.FileTestManager.assertFileExists;
import static net.chocolapod.lwjgfont.packager.FileTestManager.assertFilePath;
import static net.chocolapod.lwjgfont.packager.FileTestManager.assertNotExists;
import static net.chocolapod.lwjgfont.packager.FileTestManager.createFile;
import static org.junit.Assert.assertEquals;

public class TestLwjgFontUtil_deleteFile {

	@Test
	public void delete1Directory() throws IOException {
		LwjgFontUtil.prepareDirectory(TEST_BASE_DIR);
		
		assertDirectoryExists(TEST_BASE_DIR);
		
		LwjgFontUtil.deleteFile(TEST_BASE_DIR);
		
		assertNotExists(TEST_BASE_DIR);
	}
	
	@Test
	public void delete1DirectoryAsFile() throws IOException {
		LwjgFontUtil.prepareDirectory(TEST_BASE_DIR);
		
		assertDirectoryExists(TEST_BASE_DIR);
		
		LwjgFontUtil.deleteFile(new File(TEST_BASE_DIR));
		
		assertNotExists(TEST_BASE_DIR);
	}

	@Test
	public void delete2Directory() throws IOException {
		LwjgFontUtil.prepareDirectory(TEST_BASE_DIR, "dir01");
		
		assertDirectoryExists(TEST_BASE_DIR);
		assertDirectoryExists(TEST_BASE_DIR + File.separator + "dir01");

		LwjgFontUtil.deleteFile(TEST_BASE_DIR);
		
		assertNotExists(TEST_BASE_DIR + File.separator + "dir01");
		assertNotExists(TEST_BASE_DIR);
	}

	@Test
	public void deleteChildDirectory() throws IOException {
		File	dir01 = LwjgFontUtil.prepareDirectory(TEST_BASE_DIR, "dir01");
		
		assertDirectoryExists(TEST_BASE_DIR);
		assertDirectoryExists(TEST_BASE_DIR + File.separator + "dir01");

		LwjgFontUtil.deleteFile(dir01);
		
		assertNotExists(TEST_BASE_DIR + File.separator + "dir01");
		assertDirectoryExists(TEST_BASE_DIR);
	}
	
	@Test
	public void deleteDirectoryChildren() throws IOException {
		LwjgFontUtil.prepareDirectory(TEST_BASE_DIR, "dir01", "dir02");

		assertDirectoryExists(TEST_BASE_DIR);
		String		assertDir01 = assertDirectoryExists(TEST_BASE_DIR + File.separator + "dir01");
		String		assertDir02 = assertDirectoryExists(TEST_BASE_DIR + File.separator + "dir01" + File.separator + "dir02");

		String		assertFile01 = createFile(assertDir02 + File.separator + "file01");
		String		assertFile02 = createFile(assertDir02 + File.separator + "file02");
		String		assertFile03 = createFile(assertDir02 + File.separator + "file03");
		
		assertFileExists(assertFile01);
		assertFileExists(assertFile02);
		assertFileExists(assertFile03);

		LwjgFontUtil.deleteFile(TEST_BASE_DIR);

		assertNotExists(assertFile01);
		assertNotExists(assertFile02);
		assertNotExists(assertFile03);
		assertNotExists(assertDir02);
		assertNotExists(assertDir01);
		assertNotExists(TEST_BASE_DIR);
	}

	@Test
	public void cannotDeleteOverCurrentDirectory() throws IOException {
		File	temporaryDir = new File("../" + TEST_DIR);
		
		LwjgFontUtil.prepareDirectory(temporaryDir.getPath());

		assertDirectoryExists(temporaryDir.getPath());
		
		LwjgFontUtil.deleteFile(temporaryDir.getPath());

		assertDirectoryExists(temporaryDir.getPath());
		
		//	作ったの temporaryDir は削除する
		temporaryDir.deleteOnExit();
	}
	
	@Test
	public void cannotDeleteCurrentDirectory() throws IOException {
		File	temporaryDir = new File(".");

		assertDirectoryExists(temporaryDir.getPath());
		
		LwjgFontUtil.deleteFile(".");

		assertDirectoryExists(temporaryDir.getPath());
		
		LwjgFontUtil.deleteFile("");

		assertDirectoryExists(temporaryDir.getPath());

		//	temporaryDir はカレントディレクトリなので削除しないこと
	}
	
	@Test
	public void returnPathFromDeleteDirectory() throws IOException {
		File	file = LwjgFontUtil.prepareDirectory(TEST_BASE_DIR, "dir01", "dir02");
		
		assertDirectoryExists(file.getPath());
		
		File	actual = LwjgFontUtil.deleteFile(file);

		assertFilePath(TEST_BASE_DIR + File.separator + "dir01" + File.separator + "dir02", actual);
	}
	
	@Test
	public void returnPathFromDeleteNotExistsDirectory() throws IOException {
		File	file = new File(TEST_BASE_DIR + File.separator + "dir01" + File.separator + "dir02");
		
		assertNotExists(file.getPath());
		
		File	actual = LwjgFontUtil.deleteFile(file);
		
		assertEquals(actual, null);
	}

}
