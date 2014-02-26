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
package net.chocolapod.lwjgfont.exception;

import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

import net.chocolapod.lwjgfont.packager.LwjgFontUtil;

import static net.chocolapod.lwjgfont.packager.LwjgFontUtil.CHARSET_UTF8;

public enum LwjgFontErrorMessage {
	DEFAULT_ERROR,
	SYSTEM_COMPILER_NOT_FOUND,
	CHARACTERS_DIR_NOT_FOUND;

	private static final Properties		properties = new Properties();
	static {
		if (!loadProperties(Locale.getDefault())) {
			loadProperties(Locale.ENGLISH);
		}
	}
	
	private static boolean loadProperties(Locale locale) {
		String		resourceName = String.format("error.%s.properties", locale.getLanguage());

		try {
			properties.clear();
			properties.load(new InputStreamReader(LwjgFontErrorMessage.class.getResourceAsStream(resourceName), CHARSET_UTF8));
		} catch (Exception e) {
			System.err.println(resourceName + " is not found.");
			return false;
		}
		return true;
	}

	public String getMessage() {
		String		message = properties.getProperty(this.name());
		
		if (LwjgFontUtil.isEmpty(message)) {
			message = this.name();
		}

		return message;
	}
	
}
