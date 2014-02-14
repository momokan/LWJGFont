package lwjgfont.processor;

import java.io.File;
import java.io.IOException;


import net.chocolapod.lwjgfont.packager.LwjgFontUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static lwjgfont.processor.FileTestManager.assertFileExists;
import static lwjgfont.processor.FileTestManager.assertDirectoryExists;
import static lwjgfont.processor.FileTestManager.assertFilePath;
import static lwjgfont.processor.FileTestManager.assertNotExists;
import static lwjgfont.processor.FileTestManager.createFile;
import static lwjgfont.processor.FileTestManager.TEST_BASE_DIR;
import static lwjgfont.processor.FileTestManager.TEST_DIR;

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
