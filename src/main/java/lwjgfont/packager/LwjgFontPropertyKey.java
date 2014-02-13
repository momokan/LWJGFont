package lwjgfont.packager;

import static lwjgfont.packager.FontMapPainter.DEFAULT_CHARACTERS_DIR;
import static lwjgfont.packager.FontMapPainter.DEFAULT_PADDING;
import static lwjgfont.packager.LwjgFont.DEFAULT_TEMP_DIR;
import static lwjgfont.packager.FontMapPainter.DEFAULT_WRITE_IMAGE;
import static lwjgfont.packager.FontMapPainter.DEFAULT_WRITE_IMAGE_FRAME;

public enum LwjgFontPropertyKey {
	IMAGE_DRAW(DEFAULT_WRITE_IMAGE),
	IMAGE_DRAW_FRAME(DEFAULT_WRITE_IMAGE_FRAME),
	IMAGE_CHARACTER_PADDING(DEFAULT_PADDING),
	CHARACTER_FILE_DIR(DEFAULT_CHARACTERS_DIR),
	TEMP_DIR(DEFAULT_TEMP_DIR),
	ARTIFACT_NAME("myfont"),	//	TODO あれこれ置換する
	ARTIFACT_VERSION("1.0-SNAPSHOT");

	private final String	defaultValue;

	private LwjgFontPropertyKey() {
		this("");
	}
	private LwjgFontPropertyKey(boolean defaultValue) {
		this(String.valueOf(defaultValue));
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
