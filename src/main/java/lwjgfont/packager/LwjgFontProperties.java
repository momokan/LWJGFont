package lwjgfont.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
}
