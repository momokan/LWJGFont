package net.chocolapod.lwjgfont.packager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ClassMapLog {
	private static final String		LOG_FILE_NAME = "lwjgfont.log"; 
	
	private Map<String, String>	classMap;

	public ClassMapLog() {
		this.classMap = new HashMap<String, String>();
	}
	
	public void add(String fontName, int size, String className) {
		classMap.put(fontName + " (Size: " + size + ")", className);
	}

	public void write() throws IOException {
		PrintWriter		pw = null;
		
		try {
			pw = new PrintWriter(new FileOutputStream(LOG_FILE_NAME));
			
			for (String key: classMap.keySet()) {
				pw.println(key + " --> " + classMap.get(key));
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

		
	}

}
