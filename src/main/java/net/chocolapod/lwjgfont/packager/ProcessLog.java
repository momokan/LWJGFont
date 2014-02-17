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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessLog {
	private static final String		LOG_FILE_NAME = "lwjgfont.log"; 

	private final String			jarName;
	private final String			groupId;
	private final String			artifactId;
	private final String			version;

	private Map<String, String>	classMap;
	private int						classLength;

	public ProcessLog(String jarName, String groupId, String artifactId, String version) {
		this.jarName = jarName;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.classMap = new HashMap<String, String>();
		this.classLength = 0;
	}
	
	public void add(String fontName, int size, String className) {
		String		key = fontName + " (Size: " + size + ")";
		
		classMap.put(key, className);

		if (classLength < className.length()) {
			classLength = className.length();
		}
	}
	public List<String> listClasses() {
		return new ArrayList<String>(classMap.values());
	}

	public void write() throws IOException {
		PrintWriter		pw = null;
		
		try {
			pw = new PrintWriter(new FileOutputStream(LOG_FILE_NAME));
			pw.println(toString());
		} catch (IOException e) {
			throw e;
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@Override
	public String toString() {
		String		buff = "Generate: " + jarName + "\n";
		
		buff += "\n";
		buff += "* This jar file contains these classes.\n";

		for (String key: classMap.keySet()) {
			buff += String.format("    %" + classLength + "s <- %s\n", classMap.get(key), key);
		}

		buff += "\n";
		buff += "* How to install this jar into Maven local repository.\n";
		buff += String.format(
					"    > mvn install:install-file -Dfile=%s -DgroupId=%s" + 
					" -DartifactId=%s -Dversion=%s -Dpackaging=jar -DgeneratePom=true\n",
					jarName, groupId, artifactId, version
				);

		buff += "\n";
		buff += "* How to use this jar with Maven ( pom.xml settings )\n";
		buff += String.format(
					"    --------------------------------------------------------------------\n" +
					"    <dependency>\n" +
					"        <groupId>%s</groupId>\n" + 
					"        <artifactId>%s</artifactId>\n" +
					"        <version>%s</version>\n" +
					"    </dependency>\n" +
					"    --------------------------------------------------------------------\n",
					groupId, artifactId, version
				);

		buff += "\n";
		buff += "Thanks!\n";

		return buff;
	}


}
