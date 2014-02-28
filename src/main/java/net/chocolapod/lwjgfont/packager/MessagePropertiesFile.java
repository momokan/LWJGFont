package net.chocolapod.lwjgfont.packager;

import static net.chocolapod.lwjgfont.packager.LwjgFontUtil.CHARSET_UTF8;

import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

import net.chocolapod.lwjgfont.exception.LwjgFontErrorMessage;

public class MessagePropertiesFile {
	private final Properties		properties;

	public MessagePropertiesFile(Properties properties) {
		this.properties = properties;
	}

	public String getMessage(String key) {
		String		message = properties.getProperty(key);
		
		if (LwjgFontUtil.isEmpty(message)) {
			message = key;
		}

		return message;
	}

	public String format(String key, Object[] args) {
		return String.format(getMessage(key), args);
	}

	public static MessagePropertiesFile loadProperties(Class clazz, String resourceKey) {
		Properties	properties = new Properties();
		
		if ((properties = loadProperties(clazz, resourceKey, Locale.getDefault())) == null) {
			properties = loadProperties(clazz, resourceKey, Locale.ENGLISH);
		}
		return new MessagePropertiesFile(properties);
	}
	private static Properties loadProperties(Class clazz, String resourceKey, Locale locale) {
		Properties	properties = new Properties();
		String		resourceName = String.format("%s.%s.properties", resourceKey, locale.getLanguage());

		try {
			properties.clear();
			properties.load(new InputStreamReader(clazz.getResourceAsStream(resourceName), CHARSET_UTF8));
		} catch (Exception e) {
			System.err.println(resourceName + " is not found.");
			return null;
		}
		return properties;
	}

}
