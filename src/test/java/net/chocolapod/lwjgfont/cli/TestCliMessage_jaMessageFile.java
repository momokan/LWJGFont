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
package net.chocolapod.lwjgfont.cli;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Properties;

import net.chocolapod.lwjgfont.packager.LwjgFontUtil;
import net.chocolapod.lwjgfont.packager.MessagePropertiesFile;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestCliMessage_jaMessageFile {
	private static Properties		properties;

	@BeforeClass
	public static void init() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		loadProperties(Locale.JAPANESE);
	}
	
	protected static void loadProperties(Locale locale) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method		method = MessagePropertiesFile.class.getDeclaredMethod("loadProperties", Class.class, String.class, Locale.class);
		
		method.setAccessible(true);
		
		properties = (Properties)method.invoke(null, CliMessage.class, "cli", locale);
	}

	@Test
	public void checkMessages() {
		for (CliMessage message: CliMessage.values()) {
			try {
				String	text = properties.getProperty(message.name());

				checkMessage(message, text);
			} catch (AssertionError e) {
				throw new AssertionError("Illegal text: " + message.name(), e);
			}
		}
	}

	protected void checkMessage(CliMessage message, String text) {
		assertTrue(!LwjgFontUtil.isEmpty(text));
	}

}
