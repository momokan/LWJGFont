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
package net.chocolapod.lwjgfont;

import static net.chocolapod.lwjgfont.MissingCharacterLogger.LOG_FILE_NAME_FORMAT;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import net.chocolapod.lwjgfont.packager.FileTestManager;

import org.junit.Before;
import org.junit.Test;

public class TestMissingCharacterLogger {
	private static final String	LOG_TARGET_DIR = "target";
	private static final String	LOG_FILE = LOG_TARGET_DIR + File.separator + String.format(LOG_FILE_NAME_FORMAT, TestFont.class.getCanonicalName());

	@Before
	public void before() {
		new File(LOG_FILE).delete();
	}
	
	@Test
	public void log() throws IOException {
		MissingCharacterLogger	logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);
		
		logger.log('重');
		
		assertLogFile("重");
	}
	
	@Test
	public void log2() throws IOException {
		MissingCharacterLogger	logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);

		logger.log('重');
		logger.log('巡');

		assertLogFile("重巡");
	}

	@Test
	public void logTwice() throws IOException {
		MissingCharacterLogger	logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);

		logger.log('重');
		logger.log('巡');
		logger.log('重');
		
		assertLogFile("重巡");
	}
	
	@Test
	public void logTwice2() throws IOException {
		MissingCharacterLogger	logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);

		logger.log('重');
		logger.log('巡');
		logger.log('重');
		logger.log('巡');
		
		assertLogFile("重巡");
	}
	
	@Test
	public void load() throws IOException {
		MissingCharacterLogger	logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);
		
		logger.log('重');

		logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);
		logger.load();

		logger.log('重');
		
		assertLogFile("重");
	}

	@Test
	public void load2() throws IOException {
		MissingCharacterLogger	logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);
		
		logger.log('重');
		logger.log('巡');

		logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);
		logger.load();

		logger.log('巡');
		logger.log('重');
		
		assertLogFile("重巡");
	}
	
	@Test
	public void load3() throws IOException {
		MissingCharacterLogger	logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);

		logger.log('重');
		logger.log('巡');

		logger = new MissingCharacterLogger(TestFont.class, LOG_TARGET_DIR);
		logger.load();

		logger.log('愛');
		logger.log('宕');
		logger.log('巡');
		logger.log('重');

		assertLogFile("重巡愛宕");
	}

	private void assertLogFile(String logContent) throws IOException {
		FileTestManager.assertFileExists(LOG_FILE);
		
		String	actual = FileTestManager.getFileContent(LOG_FILE);
		
		assertEquals(logContent, actual);
	}


	class TestFont extends LWJGFont {

		@Override
		public int getMaxAscent() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		protected FontMap getFontMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected int getDefaultLineHeight() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
