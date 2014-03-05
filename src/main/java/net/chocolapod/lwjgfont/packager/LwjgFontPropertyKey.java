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

import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_CHARACTERS_DIR;
import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_PADDING;
import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_WRITE_IMAGE;
import static net.chocolapod.lwjgfont.packager.FontMapPainter.DEFAULT_WRITE_IMAGE_FRAME;
import static net.chocolapod.lwjgfont.packager.LwjgFontFactory.DEFAULT_TEMP_DIR;
import static net.chocolapod.lwjgfont.packager.LwjgFontFactory.DEFAULT_DIST_DIR;

/**
 * A LwjgFontPropertyKey represents property keys of LWJGFont configuration properties file (it is lwjgfont.properties at default).<br>
 * As property key name, each enum'name are converted to be lower case and be separated by periods instead of underscore. 
 */
public enum LwjgFontPropertyKey {
	/**
	 *	Indicates the artifact name which LWJGFont generates.
	 * This value is set in a pom.xml to manage this jar as a Maven artifact.<br>
	 *	Default value is "myfont".<br>
	 * "artifact.name" is a key of this property on properties file.
	 */
	ARTIFACT_NAME("myfont"),

	/**
	 *	Indicates the artifact version which LWJGFont generates.
	 * This value is set in a pom.xml to manage this jar as a Maven artifact.<br>
	 *	Default value is "1.0-SNAPSHOT".<br>
	 * "artifact.version" is a key of this property on properties file.
	 */
	ARTIFACT_VERSION("1.0-SNAPSHOT"),

	/**
	 * Indicates the directory path which contains character files to be able to render.
	 * LWJGFont draw all characters of these character files on images.<br>
	 * Default value is "characters"<br> 
	 * "character.file.dir" is a key of this property on properties file.
	 */
	CHARACTER_FILE_DIR(DEFAULT_CHARACTERS_DIR),

	/**
	 * Indicates the directory path which LWJGFont generate a jar file on.
	 * Default value is "". This means current directory.<br> 
	 * "dist.dir" is a key of this property on properties file.
	 */
	DIST_DIR(DEFAULT_DIST_DIR),

	/**
	 * Indicates the directory path which contains temporary files.
	 * To generate jar, LWJGFont create some images, java source files, java class files, and other resource files into this directory.<br>
	 * Notice that when LWJGFont start packaging process, This directory is deleted.<br> 
	 * Default value is "temp"<br> 
	 * "temp.dir" is a key of this property on properties file.
	 */
	TEMP_DIR(DEFAULT_TEMP_DIR),

	/**
	 * Indicates padding size around each characters on images.
	 * If some characters overlap with neighbor characters, specify padding size to more larger.<br>
	 * Default value is 5.<br> 
	 * "image.character.padding" is a key of this property on properties file.
	 */
	IMAGE_CHARACTER_PADDING(DEFAULT_PADDING),

	/**
	 * Indicates whether LWJGFont generates some images which characters are drawn on.
	 * This property is for debug.<br>
	 * Default value is true and LWJGFont generates images.<br> 
	 * "image.draw" is a key of this property on properties file.
	 */
	IMAGE_DRAW(DEFAULT_WRITE_IMAGE),

	/**
	 * Indicates whether LWJGFont draws frame around each characters on images.
	 * This property is for debug.<br>
	 * Default value is false and LWJGFont dose not draw frames.<br> 
	 * "image.draw.frame" is a key of this property on properties file.
	 */
	IMAGE_DRAW_FRAME(DEFAULT_WRITE_IMAGE_FRAME),
	
	/**
	 *	Indicates the LWJGFont version.
	 * This value must be specified by users, so it is automatically overrode by LWJGFont.<br>
	 *	Default value is "<unknown>", and automatically correct to LWJGFont version.<br>
	 * "lwjgfont.version" is a key of this property on properties file.
	 */
	LWJGFONT_VERSION("<unknown>");

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

	/**
	 * Returns the default value of this property.
	 * @return the default value of this property.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Returns a replacement symbol of this property for built-in resources.
	 * When LWJGFont packages a jar, LWJGFont replaces symbols in built-in resources to its property's value
	 * and add built-in resources to the jar.<br>
	 * Built-in resources are MANIFEST.MF, Maven pom.xml, pom.properties and the others.<br>
	 * <br>
	 * For example, with ARTIFACT_VERSION property,
	 * a line "version=%artifactVersion%" in pom.xml is converted to "version=1.0-SNAPSHOT" at default.
	 * @return a replacement symbol of this property for built-in resources.
	 */
	public String toResourceReplacePattern() {
		String[]	tokens = name().toLowerCase().split("_");
		String		pattern = "";
		
		for (String token: tokens) {
			pattern += LwjgFontUtil.capitalize(token);
		}

		pattern = pattern.substring(0, 1).toLowerCase() + pattern.substring(1);
		pattern = '%' + pattern + '%';
		
		return pattern;
	}

	/**
	 * Returns a key name of this property.
	 * @return a key name of this property.
	 */
	@Override
	public String toString() {
		String	key = name();
		
		key = key.replaceAll("_", ".");
		key = key.toLowerCase();
		
		return key;
	}
	
}
