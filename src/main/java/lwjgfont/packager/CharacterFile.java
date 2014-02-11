package lwjgfont.packager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import lwjgfont.processor.exception.CharactersDirectoryIsEmptyException;

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
