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

import static net.chocolapod.lwjgfont.packager.TestResources.CLASS_MOSAMOSAFONT_18;
import static net.chocolapod.lwjgfont.packager.TestResources.FILE_MOSAMOSAFONT;
import static net.chocolapod.lwjgfont.packager.TestResources.FILE_TEST_PROPERTIES;
import static net.chocolapod.lwjgfont.packager.TestResources.TEST_JAR_PATH;
import static net.chocolapod.lwjgfont.packager.TestResources.IMAGE_MOSAMOSAFONT_18; 
import static net.chocolapod.lwjgfont.packager.TestResources.FILE_MAIN_PACKAGER_BASE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.FontFormatException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.imageio.ImageIO;

import net.chocolapod.lwjgfont.LWJGFont;
import net.chocolapod.lwjgfont.MappedCharacter;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

public class TestLWJGFontFactory {
	
	@BeforeClass
	public static void ceateJar() throws IOException, FontFormatException {
		File	jar = new File(TEST_JAR_PATH);
		
		jar.delete();
		if (jar.exists()) {
			throw new IOException("cannot initialize " + TEST_JAR_PATH);
		}

		//	create jar
		LwjgFontFactory		lwjgFont = new LwjgFontFactory(FILE_TEST_PROPERTIES);

		lwjgFont.create(new FontSetting(FILE_MOSAMOSAFONT, 8));
		lwjgFont.makePackage();
//		lwjgFont.writeProcessLog();
	}
	
	@Test
	public void existsJar() {
		assertEquals(true, new File(TEST_JAR_PATH).isFile());
	}

	@Test
	public void loadLWJGFontClass() throws MalformedURLException, ClassNotFoundException {
		URLClassLoader	classLoader = new URLClassLoader(new URL[] {new File(TEST_JAR_PATH).toURI().toURL()});
		Class			clazz = classLoader.loadClass(CLASS_MOSAMOSAFONT_18);

		assertNotNull(clazz);
	}

	@Test
	public void loadLWJGFont() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		URLClassLoader	classLoader = new URLClassLoader(new URL[] {new File(TEST_JAR_PATH).toURI().toURL()});
		Class			clazz = classLoader.loadClass(CLASS_MOSAMOSAFONT_18);
		Object			object = clazz.newInstance();

		assertEquals(true, object instanceof LWJGFont);
	}

	@Test
	public void loadImage() throws IOException {
		URLClassLoader	classLoader = new URLClassLoader(new URL[] {new File(TEST_JAR_PATH).toURI().toURL()});
		Image			image = ImageIO.read(classLoader.getResourceAsStream(IMAGE_MOSAMOSAFONT_18));

		assertNotNull(image);
	}

	@Test
	public void getMappedCharacters() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		URLClassLoader		classLoader = new URLClassLoader(new URL[] {new File(TEST_JAR_PATH).toURI().toURL()});
		Class				clazz = classLoader.loadClass(CLASS_MOSAMOSAFONT_18);
		LWJGFont			font = (LWJGFont)clazz.newInstance();
		List<CharacterFile>	files = CharacterFile.listStreams(FILE_MAIN_PACKAGER_BASE);
		
		for (CharacterFile file: files) {
			Character			c;
			MappedCharacter		mappedCharacter;
			
			try {
				file.open();
				
				while ((c = file.next()) != null) {
					mappedCharacter = font.getMappedCharacter(c);

					assertNotNull(mappedCharacter);
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
	}
}
