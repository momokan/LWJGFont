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
