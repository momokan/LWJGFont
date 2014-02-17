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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FontMap {
	private final Map<Integer, String>				imageFiles;
	private final Map<Character, MappedCharacter>	mappedCharacters;

	public FontMap() {
		this.imageFiles = new HashMap<Integer, String>();
		this.mappedCharacters = new LinkedHashMap<Character, MappedCharacter>();
	}
	
	public void addImageFile(int index, String imageFileName) {
		imageFiles.put(index, imageFileName);
	}
	
	public void addCharacter(MappedCharacter mappedCharacter) {
		mappedCharacters.put(mappedCharacter.getCharacter(), mappedCharacter);
	}
	
	public List<Integer> listImageIndexes() {
		return new ArrayList<>(imageFiles.keySet());
	}
	public String getImageFile(int imageIndex) {
		return imageFiles.get(imageIndex);
	}

	public List<Character> listCharacters() {
		return new ArrayList<>(mappedCharacters.keySet());
	}
	public MappedCharacter getMappedCharacter(char character) {
		return mappedCharacters.get(character);
	}
}
