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

import java.io.IOException;


import net.chocolapod.lwjgfont.packager.CharacterFile;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCharacterFile {

	@Test
	public void readAllCharacters() throws IOException {
		char[]				expecteds = new char[] {'a', 'b', 'C', 'D', 'に', '本', '語'};
		
		CharacterFile		file = getCaracterFile("TestCharacterFile_readAllCharacters.txt");
		int					i = 0;
		Character			actual;
		
		try {
			file.open();
			while ((actual = file.next()) != null) {
				assertTrue(i < expecteds.length);
				assertEquals(expecteds[i], actual.charValue());
				i++;
			}
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void readNoCharacters() throws IOException {
		CharacterFile		file = getCaracterFile("TestCharacterFile_readNoCharacters.txt");

		try {
			file.open();
			assertEquals(file.next(), null);
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private CharacterFile getCaracterFile(String fileName) {
		return new CharacterFile(
				"src/test/resources/" +
				TestCharacterFile.class.getPackage().getName().replaceAll("\\.", "/") +
				"/" +
				fileName);
	}

}
