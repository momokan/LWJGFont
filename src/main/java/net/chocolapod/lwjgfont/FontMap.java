package net.chocolapod.lwjgfont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FontMap {
	private final Map<Integer, String>			imageFiles;
	private final Map<Character, MappedFont>	mappedFonts;

	public FontMap() {
		this.imageFiles = new HashMap<Integer, String>();
		this.mappedFonts = new LinkedHashMap<Character, MappedFont>();
	}
	
	public void addImageFile(int index, String imageFileName) {
		imageFiles.put(index, imageFileName);
	}
	
	public void addMappedFont(MappedFont mappedFont) {
		mappedFonts.put(mappedFont.getCharacter(), mappedFont);
	}
	
	public List<Integer> listImageIndexes() {
		return new ArrayList<>(imageFiles.keySet());
	}
	public String getImageFile(int imageIndex) {
		return imageFiles.get(imageIndex);
	}

	public List<Character> listCharacters() {
		return new ArrayList<>(mappedFonts.keySet());
	}
	public MappedFont getMappedFont(char c) {
		return mappedFonts.get(c);
	}
}
