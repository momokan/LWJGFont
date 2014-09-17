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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.chocolapod.lwjgfont.LWJGFont;

import static net.chocolapod.lwjgfont.cli.CliMessage.GUIDE_GENERATED_BY;
import static net.chocolapod.lwjgfont.cli.CliMessage.GENERATED_JAR_FORMAT;
import static net.chocolapod.lwjgfont.cli.CliMessage.GENERATED_POM_FORMAT;
import static net.chocolapod.lwjgfont.cli.CliMessage.HEADER_LIST_CLASSES;
import static net.chocolapod.lwjgfont.cli.CliMessage.HEADER_INSTALL_JAR;
import static net.chocolapod.lwjgfont.cli.CliMessage.HEADER_HOW_TO_USE_IN_MAVEN;
import static net.chocolapod.lwjgfont.packager.LwjgFontUtil.CHARSET_UTF8;

public class ProcessLog {
	private static final String		LOG_FILE_NAME = "lwjgfont.log"; 

	private final String			lwjgFontVersion;
	private final String			jarName;
	private final String			pomName;
	private final String			groupId;
	private final String			artifactId;
	private final String			version;

	private Map<String, String>		classMap;
	private int						classLength;

	public ProcessLog(String lwjgFontVersion, String jarName, String pomName, String groupId, String artifactId, String version) {
		this.lwjgFontVersion = lwjgFontVersion;
		this.jarName = jarName;
		this.pomName = pomName;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.classMap = new HashMap<String, String>();
		this.classLength = 0;
	}
	
	public void add(String fontName, int size, String fontAlias, String className) {
		String		key = fontName + " (Size: " + size + ")";
		
		if (!LwjgFontUtil.isEmpty(fontAlias)) {
			key = fontAlias + "@" + key;
		}

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
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(LOG_FILE_NAME), CHARSET_UTF8));
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
		String		buff = "";
		
		buff += GUIDE_GENERATED_BY.format(lwjgFontVersion) + "\n";
		buff += "\n";

		buff += GENERATED_JAR_FORMAT.format(jarName) + "\n";
		buff += GENERATED_POM_FORMAT.format(pomName) + "\n";

		buff += "\n";
		buff += HEADER_LIST_CLASSES + "\n";

		for (String key: classMap.keySet()) {
			buff += String.format("    %" + classLength + "s <- %s\n", classMap.get(key), key);
		}

		buff += "\n";
		buff += HEADER_INSTALL_JAR + "\n";
		buff += String.format(
					"    > mvn install:install-file -Dfile=%s -DpomFile=%s -DgroupId=%s" +
					" -DartifactId=%s -Dversion=%s -Dpackaging=jar\n",
					jarName, pomName, groupId, artifactId, version
				);

		buff += "\n";
		buff += HEADER_HOW_TO_USE_IN_MAVEN + "\n";
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
