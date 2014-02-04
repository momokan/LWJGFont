package ttfmap.processor;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestResource {
	public static final String		MAIN_BASE_DIR = "src/main/resources/";
	public static final String		TEST_BASE_DIR = "src/test/resources/";
	public static final String		TEST_COMPILED_DIR = "target/test-classes/";

	public static String getPathWithClass(Class clazz, String fileExtention) {
		return MAIN_BASE_DIR + clazz.getCanonicalName().replaceAll("\\.", "/") + "." + fileExtention;
	}

	public static String getPathWithPackage(Class clazz, String fileName) {
		return MAIN_BASE_DIR + clazz.getPackage().getName().replaceAll("\\.", "/") + "/" + fileName;
	}
	
	public static String getTestPathWithClass(Class clazz, String fileExtention) {
		return TEST_BASE_DIR + clazz.getCanonicalName().replaceAll("\\.", "/") + "." + fileExtention;
	}

	public static String getTestPathWithPackage(Class clazz, String fileName) {
		return TEST_BASE_DIR + clazz.getPackage().getName().replaceAll("\\.", "/") + "/" + fileName;
	}
	
	public static String getTestCompiledPathWithClass(Class clazz, String fileExtention) {
		return TEST_COMPILED_DIR + clazz.getCanonicalName().replaceAll("\\.", "/") + "." + fileExtention;
	}

	public static String getTestCompiledPathWithPackage(Class clazz, String fileName) {
		return TEST_COMPILED_DIR + clazz.getPackage().getName().replaceAll("\\.", "/") + "/" + fileName;
	}

	/**
	 *	指定のバイト配列と、指定のファイルデータの内容が等しいか試験する
	 */
	public static void assertBinary(String expectedFilePath, byte[] buffActual) throws IOException {
		File			expected = new File(expectedFilePath);
		ByteBuffer	buffExpected = ByteBuffer.allocate((int)expected.length());

		//	バイト毎にバイナリーデータを比較する
		FileChannel	in = null;
		int				i = 0;

		try {
			in = new FileInputStream(expected).getChannel();
			in.read(buffExpected);
			buffExpected.flip();
			
			assertEquals(buffExpected.capacity(), buffActual.length);
			while (i < buffActual.length) {
				byte	byteExpected = buffExpected.get();
				byte	byteActual = buffActual[i];
				
				assertEquals(byteExpected, byteActual);
				i++;
			}
		} catch(IOException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch(IOException e) {}
			}
		}
	}
}
