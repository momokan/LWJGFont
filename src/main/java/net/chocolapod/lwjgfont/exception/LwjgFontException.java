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

import net.chocolapod.lwjgfont.packager.MessagePropertiesFile;

public class LwjgFontException extends RuntimeException {
	private static final MessagePropertiesFile		properties = MessagePropertiesFile.loadProperties(LwjgFontException.class, "error");

	public LwjgFontException(String message, Object... args) {
		super(String.format(message, args));
	}

	public LwjgFontException(String message, Throwable cause) {
		super(message, cause);
	}
	
	protected LwjgFontException(Class<? extends LwjgFontException> clazz, Object... args) {
		super(String.format(getMessageResource(clazz), args));
	}

	protected LwjgFontException(Class<? extends LwjgFontException> clazz, Throwable cause, Object... args) {
		super(String.format(getMessageResource(clazz), args));
	}
	
	public static LwjgFontException as(Throwable cause) {
		if (cause instanceof LwjgFontException) {
			return (LwjgFontException)cause;
		} else {
			return new LwjgFontException(cause.getMessage(), cause);
		}
	}

	private static String getMessageResource(Class<? extends LwjgFontException> clazz) {
		return properties.getMessage(clazz.getSimpleName());
	}

}
