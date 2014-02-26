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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LwjgFontProperties {
	private static final String	DEFAULT_PROPERTIES_FILE = "lwjgfont.properties";
	
	private final Properties	properties;

	private LwjgFontProperties(String filePath) throws IOException {
		this.properties = new Properties();
		
		//	プロパティファイルを読み込む
		if (LwjgFontUtil.isEmpty(filePath)) {
			filePath = DEFAULT_PROPERTIES_FILE;
		}		
		
		File	file = new File(filePath);
		if (file.exists()) {
			properties.load(new FileInputStream(file));
		}
	}
	
	public String getAsString(LwjgFontPropertyKey key) {
		return get(key, new StringConverter());
	}
	public int getAsInt(LwjgFontPropertyKey key) {
		return get(key, new IntConverter());
	}
	public boolean getAsBoolean(LwjgFontPropertyKey key) {
		return get(key, new BooleanConverter());
	}

	private <T> T get(LwjgFontPropertyKey key, ValueConverter<T> converter) {
		String	value = properties.getProperty(key.toString());

		try {
			return (T)converter.convert(value);
		} catch(Exception e) {}
		
		return (T)converter.convert(key.getDefaultValue());
	}

	public static LwjgFontProperties load(String filePath) throws IOException {
		return new LwjgFontProperties(filePath);
	}

	interface ValueConverter<T> {
		
		public T convert(String value);
		
	}
	
	class StringConverter implements ValueConverter<String> {

		@Override
		public String convert(String value) {
			if (LwjgFontUtil.isEmpty(value)) {
				throw new IllegalArgumentException("empty string.");
			}

			return value;
		}
		
	}
	
	class IntConverter implements ValueConverter<Integer> {

		@Override
		public Integer convert(String value) {
			try {
				return Integer.parseInt(value);
			} catch(Exception e) {
				throw new IllegalArgumentException("not integer.", e);
			}
		}
		
	}
	
	class BooleanConverter implements ValueConverter<Boolean> {

		@Override
		public Boolean convert(String value) {
			if (value == null) {
				throw new IllegalArgumentException("not boolean.");
			}
			
			try {
				return Boolean.parseBoolean(value);
			} catch(Exception e) {
				throw new IllegalArgumentException("not boolean.", e);
			}
		}
		
	}
}
