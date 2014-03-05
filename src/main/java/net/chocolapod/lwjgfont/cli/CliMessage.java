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
package net.chocolapod.lwjgfont.cli;

import net.chocolapod.lwjgfont.packager.MessagePropertiesFile;

public enum CliMessage {
	LWJGFONT_VERSION_FORMAT,
	GENERATED_JAR_FORMAT,
	GENERATED_POM_FORMAT,
	HEADER_LIST_CLASSES,
	HEADER_INSTALL_JAR,
	HEADER_HOW_TO_USE_IN_MAVEN,
	NOTE_JAR_DEPENDS_LWJGFONT,
	WARNING_INVALID_CLI_ARGUMENT;

	private static final MessagePropertiesFile		properties = MessagePropertiesFile.loadProperties(CliMessage.class, "cli");

	@Override
	public String toString() {
		return properties.getMessage(this.name());
	}

	public String format(Object ...args) {
		return properties.format(this.name(), args);
	}
}
