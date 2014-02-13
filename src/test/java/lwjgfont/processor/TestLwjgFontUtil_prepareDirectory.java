package lwjgfont.processor;

import java.io.File;
import java.io.IOException;

import lwjgfont.packager.LwjgFontUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static lwjgfont.processor.FileTestManager.assertFilePath;
import static lwjgfont.processor.FileTestManager.assertNotExists;
import static lwjgfont.processor.FileTestManager.assertDirectoryExists;
import static lwjgfont.processor.FileTestManager.TEST_BASE_DIR;

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
