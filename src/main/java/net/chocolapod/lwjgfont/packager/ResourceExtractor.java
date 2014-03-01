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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import net.chocolapod.lwjgfont.exception.LwjgFontException;

import static net.chocolapod.lwjgfont.packager.LwjgFontUtil.CHARSET_UTF8;

public class ResourceExtractor {

	private final Map<String, String>			resourcePaths;
	private LinkedHashMap<String, String>	resourceReplaces;
	private String								resourcesDir;

	public ResourceExtractor() {
		resourcePaths = new LinkedHashMap<>();
		resourceReplaces = new LinkedHashMap<>();
		resourcesDir = "";
	}
	
	public void copy() throws IOException {
		for (String resource: resourcePaths.keySet()) {
			File	file = new File(resourcePaths.get(resource));
			File	dstDir = LwjgFontUtil.prepareDirectory(resourcesDir, file.getParent());

			copyResource(resource, LwjgFontUtil.toDirectoryPath(dstDir.getPath()) + file.getName());
		}
	}

	private void copyResource(String resource, String dstPath) throws IOException {
		BufferedReader		br = null;
		InputStream			in = null;
		PrintWriter			pw = null;
		String				line;

		try {
			in = ResourceExtractor.class.getResourceAsStream(resource);
			
			if (in == null) {
				throw new LwjgFontException("Resource not found: " + resource);
			}
			
			br = new BufferedReader(new InputStreamReader(in, CHARSET_UTF8));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(dstPath), CHARSET_UTF8));
			
			while ((line = br.readLine()) != null) {
				for (String pattern: resourceReplaces.keySet()) {
					line = line.replaceAll(pattern, resourceReplaces.get(pattern));
				}
				pw.println(line);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
	}

	public void addResourcePath(String srcResourcePath, String dstResourcePath) {
		this.resourcePaths.put(srcResourcePath, dstResourcePath);
	}
	
	public void addReplacePattern(LwjgFontProperties properties, LwjgFontPropertyKey key) {
		String		pattern = LwjgFontPropertyKey.toResourceReplacePattern(key);
		String		replacement = properties.getAsString(key);
		
		resourceReplaces.put(pattern, replacement);
	}
	public void addReplacePatterns(LwjgFontProperties properties, LwjgFontPropertyKey ...keys) {
		for (LwjgFontPropertyKey key : keys) {
			addReplacePattern(properties, key);
		}
	}
	
	public void setResourcesDir(String resourcesDir) {
		this.resourcesDir = resourcesDir;
	}

}
