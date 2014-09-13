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
package net.chocolapod.lwjgfont;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import net.chocolapod.lwjgfont.packager.LwjgFontUtil;

class MissingCharacterLogger {
	static final String				LOG_FILE_NAME_FORMAT = "missing-characters-%s.txt";
	private static final int		DEFAULT_CHAT_COUNT_PER_LINE = 4;

	private final String	logFile;

	private int				charCountPerLine;

	private Set<Character>	set;

	MissingCharacterLogger(Class<? extends LWJGFont> fontClass) {
		this(fontClass, null);
	}
	MissingCharacterLogger(Class<? extends LWJGFont> fontClass, String targetDir) {
		//	出力先ディレクトリが指定されていなければ、カレントディレクトリに出力する
		logFile = LwjgFontUtil.toDirectoryPath(targetDir) + String.format(LOG_FILE_NAME_FORMAT, fontClass.getCanonicalName());
		charCountPerLine = DEFAULT_CHAT_COUNT_PER_LINE;
		set = new HashSet<>();
	}

	boolean log(char ch) throws IOException {
		if (!set.contains(ch)) {
			set.add(ch);
			writeMissing(ch);
			
			return true;
		} else {
			return false;
		}
	}

	private void writeMissing(char ch) throws IOException {
		PrintWriter	pw = null;
		
		try {
			pw = new PrintWriter(new FileOutputStream(logFile, true));
			pw.print(ch);
			
			//	ログファイルの 1 行に書き出す最大文字数毎に改行する
			if (set.size() % charCountPerLine == 0) {
				pw.print("\n");
			}
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
	
	Set<Character> load() throws IOException {
		BufferedReader	br = null;
		String			line;

		set.clear();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
			
			while ((line = br.readLine()) != null) {
				for (char ch: line.toCharArray()) {
					if (!set.contains(ch)) {
						set.add(ch);
					}
				}
			}
		} catch (FileNotFoundException e) {
			//	ignore.
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return set;
	}

	String getLogFile() {
		return logFile;
	}

}
