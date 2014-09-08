package net.chocolapod.lwjgfont;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class MissingCharacterLogger {
	private static final String		DEFAULT_LOG_FILE_NAME = "missing-characters.txt";
	private static final int		DEFAULT_CHAT_COUNT_PER_LINE = 4;

	private String	logFile;
	private int		charCountPerLine;

	private Set<Character>	set;

	public MissingCharacterLogger() {
		logFile = DEFAULT_LOG_FILE_NAME;
		charCountPerLine = DEFAULT_CHAT_COUNT_PER_LINE;
		set = new HashSet<>();
	}

	public boolean log(char ch) throws IOException {
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
	
	public Set<Character> load() throws IOException {
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

	public String getLogFile() {
		return logFile;
	}

}
