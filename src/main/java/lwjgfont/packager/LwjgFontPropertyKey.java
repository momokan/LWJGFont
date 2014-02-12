package lwjgfont.packager;

import static lwjgfont.packager.FontMapPainter.DEFAULT_CHARACTERS_DIR;
import static lwjgfont.packager.FontMapPainter.DEFAULT_PADDING;
import static lwjgfont.packager.LwjgFont.DEFAULT_TEMP_DIR;

public enum LwjgFontPropertyKey {
	IMAGE_CHARACTER_PADDING(DEFAULT_PADDING),
	CHARACTER_FILE_DIR("characters"),
	RESOURCE_BASE_DIR(DEFAULT_TEMP_DIR),
	ARTIFACT_NAME("myfont"),	//	TODO あれこれ置換する
	ARTIFACT_VERSION("1.0-SNAPSHOT");

	private final String	defaultValue;
	
	private LwjgFontPropertyKey() {
		this("");
	}
	private LwjgFontPropertyKey(int defaultValue) {
		this(String.valueOf(defaultValue));
	}
	private LwjgFontPropertyKey(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	
	public static String toResourceReplacePattern(LwjgFontPropertyKey key) {
		String[]	tokens = key.name().toLowerCase().split("_");
		String		pattern = "";
		
		for (String token: tokens) {
			pattern += LwjgFontUtil.capitalize(token);
		}

		pattern = pattern.substring(0, 1).toLowerCase() + pattern.substring(1);
		pattern = '%' + pattern + '%';
		
		return pattern;
	}

	@Override
	public String toString() {
		String	key = name();
		
		key = key.replaceAll("_", ".");
		key = key.toLowerCase();
		
		return key;
	}
	
}
