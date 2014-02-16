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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.chocolapod.lwjgfont.processor.exception.CharactersDirectoryIsEmptyException;

public class CharacterFile {
	private final String	filePath;
	
	private BufferedReader	br;
	private String			line;

	public CharacterFile(String filePath) {
		this.filePath = filePath;
	}
	
	public void open() throws UnsupportedEncodingException, FileNotFoundException {
		if (br == null) {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
		}
	}
	
	public Character next() throws IOException {
		if ((line == null) || (line.length() <= 0)) {
			line = br.readLine();
			if (line == null) {
				return null;
			}
		}
		
		while (0 < line.length()) {
			char	c = line.charAt(0);
			
			line = line.substring(1);
			
			if (!Character.isWhitespace(c)) {
				return Character.valueOf(c);
			}
		}
		
		return next();
	}
	
	public void close() throws IOException {
		if (br != null) {
			br.close();
		}
	}

	public static List<CharacterFile> listStreams(String directory) {
		List<CharacterFile>	streams = new ArrayList<>();
		File				fileDir = new File(directory);
		File[]				files = fileDir.listFiles();
		
		if ((files == null) || (files.length <= 0)) {
			throw new CharactersDirectoryIsEmptyException(fileDir);
		}
		
		for (File file: files) {
			if (file.getName().endsWith(".txt")) {
				streams.add(new CharacterFile(file.getAbsolutePath()));
			}
		}
		
		return streams;
	}



}
