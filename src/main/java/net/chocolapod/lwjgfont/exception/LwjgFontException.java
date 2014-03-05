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

import net.chocolapod.lwjgfont.packager.LwjgFontUtil;
import net.chocolapod.lwjgfont.packager.MessagePropertiesFile;

/**
 * LwjgFontException is the superclass of some specific exceptions that can be thrown during the jar-generation of LWJGFont.
 * Note that LwjgFontException and its subclasses are unchecked exceptions.
 */
public class LwjgFontException extends RuntimeException {
	private static final MessagePropertiesFile		properties = MessagePropertiesFile.loadProperties(LwjgFontException.class, "error");

	/**
	 * Constructs a new LwjgFontException with a formatted detail message using the specified format string, and arguments.
	 * Note that the detail message associated with cause is not automatically incorporated in this runtime exception's detail message.
	 * @param message A format string defined by {@link java.util.Formatter}. This formating is processed with {@link java.lang.String#format}.
	 * @param args Arguments referenced by the format specifiers in the format string.
	 */
	public LwjgFontException(String message, Object... args) {
		super(String.format(message, args));
	}

	/**
	 * Constructs a new LwjgFontException with the specified detail message and cause.
	 * Note that the detail message associated with cause is not automatically incorporated in this runtime exception's detail message.
	 * @param message The detail message (which is saved for later retrieval by the Throwable.getMessage() method).
	 * @param cause The cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public LwjgFontException(String message, Throwable cause) {
		super(message, cause);
	}

	protected LwjgFontException(Class<? extends LwjgFontException> clazz, Object... args) {
		super(String.format(getMessageResource(clazz), args));
	}

	protected LwjgFontException(Class<? extends LwjgFontException> clazz, Throwable cause, Object... args) {
		super(String.format(getMessageResource(clazz), args));
	}

	private static String getMessageResource(Class<? extends LwjgFontException> clazz) {
		String		message = properties.getMessage(clazz.getSimpleName());

		if (LwjgFontUtil.isEmpty(message)) {
			message = properties.getMessage(LwjgFontException.class.getSimpleName());
		}

		return message;
	}

}
