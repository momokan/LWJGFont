package lwjgfont.packager;

import java.io.File;
import java.io.IOException;

import org.lwjgl.LWJGLUtil;

public class LwjgFontUtil {

	public static boolean isEmpty(String value) {
		return ((value == null) || (value.length() <= 0));
	}
	
	public static String capitalize(String value) {
		String	capitalized = "";
		
		if (isEmpty(value)) {
			return "";
		}
		
		value = value.toLowerCase();
		
		capitalized = value.substring(0, 1).toUpperCase();
		
		if (1 < value.length()) {
			capitalized += value.substring(1);
		}
		
		return capitalized;
	}

	public static String trimExtention(String fileName) {
		int		p = fileName.lastIndexOf('.');
		
		if (p <= 0) {
			return fileName;
		} else {
			return fileName.substring(0, p);
		}
	}

	public static File prepareDirectory(String ...dirPaths) throws IOException {
		String		fullDirPath = "";
		
		for (String dirPath: dirPaths) {
			if (!LwjgFontUtil.isEmpty(fullDirPath)) {
				fullDirPath += File.separator;
			}
			fullDirPath += dirPath;
		}
		
		File		dir = new File(fullDirPath);

		if (dir.isFile()) {
			throw new IOException("output directory is a file: " + dir.getAbsolutePath());
		} else if ((!dir.exists()) && (!dir.mkdirs())) {
			throw new IOException("cannot make output directory: " + dir.getAbsolutePath());
		}
		
		return dir;
	}
	
}
