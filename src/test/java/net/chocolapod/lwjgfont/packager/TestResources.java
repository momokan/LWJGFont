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

import static net.chocolapod.lwjgfont.packager.TestResources.PROPERTY_DIST_DIR;
import static net.chocolapod.lwjgfont.packager.TestResources.TEST_JAR_NAME;

import java.io.File;

/**
 *	Define resources font unit test code.
 *	
 *	Any test cases use Mosamosa-v1.1.ttf (もさもさフォント).
 *	The font is not included in LWJGFont jar and the genereted jar by LWJGFont.
 *
 *	Mosamosa-v1.1.ttf (もさもさフォント) 's copyright is @Longsword (https://twitter.com/Longsword).
 *	Distributed at http://lovalotta.pya.jp/mosamosa/
 */
public class TestResources {

	public static final String	FILE_BASE = "src/test/resources/";
	public static final String	FILE_PACKAGER_BASE = FILE_BASE + TestResources.class.getPackage().getName().replaceAll("\\.", "/") + "/";
	public static final String	FILE_TEST_PROPERTIES = FILE_PACKAGER_BASE + "lwjgfont-test.properties";
	public static final String	FILE_MOSAMOSAFONT = FILE_PACKAGER_BASE + "Mosamosa-v1.1.ttf";

	public static final String	PROPERTY_DIST_DIR = "target/";
	public static final String	TEST_JAR_NAME = "test_lwjgfont-0.99TR.jar";
	public static final String	TEST_JAR_PATH = PROPERTY_DIST_DIR + TEST_JAR_NAME;

	public static final String	CLASS_MOSAMOSAFONT_18 = "net.chocolapod.lwjgfont.test_lwjgfont.MosamosaV11H8Font";
	public static final String	IMAGE_MOSAMOSAFONT_18 = "net/chocolapod/lwjgfont/test_lwjgfont/MosamosaV11H8Font_H8_0.png";

	public static final String	SYSTEM_PROPERTY_FILE_SEPARATOR = "file.separator";

}
