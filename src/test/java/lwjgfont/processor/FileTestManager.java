package lwjgfont.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lwjgfont.packager.LwjgFontUtil;

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
