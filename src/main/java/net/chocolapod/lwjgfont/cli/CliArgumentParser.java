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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.chocolapod.lwjgfont.packager.FontSetting;

import static net.chocolapod.lwjgfont.cli.CliMessage.WARNING_INVALID_CLI_ARGUMENT;

public class CliArgumentParser {
	private Map<CliArgument, String>		parsedArguments;
	private List<FontSetting>		fontSettings;

	public CliArgumentParser(String[] args) {
		List<String>		arguments = new ArrayList<>(Arrays.asList(args));
		
		parsedArguments = new HashMap<>();
		fontSettings = new ArrayList<>();
		while (0 < arguments.size()) {
			String			arg = arguments.remove(0);
			
			if (isArgument(arg, arguments)) {
				continue;
			}
			
			if (isFontArgument(arg)) {
				continue;
			}
			
			//	無効なオプションとして処理する
			System.err.println(WARNING_INVALID_CLI_ARGUMENT.format(arg));
		}
	}
	
	private boolean isFontArgument(String arg) {
		//	フォントの設定として解釈する
		String[]	tokens = arg.split(":");

		try {
			if (tokens.length == 2) {
				FontSetting	fontSetting = FontSetting.asSystemFont(tokens[0], Integer.parseInt(tokens[1]));

				if (fontSetting != null) {
					fontSettings.add(fontSetting);
				} else {
					fontSettings.add(new FontSetting(tokens[0], Integer.parseInt(tokens[1])));
				}
				return true;
			}
		} catch (Exception e) {}

		return false;
	}

	private boolean isArgument(String arg, List<String> arguments) {
		for (CliArgument cliArgument: CliArgument.values()) {
			if (cliArgument.toArgument().equals(arg)) {
				if (cliArgument.hasValue()) {
					parsedArguments.put(cliArgument, arguments.remove(0));
				} else {
					parsedArguments.put(cliArgument, null);
				}
				return true;
			}
		}

		return false;
	}
	
	public boolean hasArgument(CliArgument argument) {
		return (parsedArguments.containsKey(argument));
	}

	public String get(CliArgument argument) {
		return parsedArguments.get(argument);
	}

	public FontSetting[] listFontSettings() {
		return fontSettings.toArray(new FontSetting[] {});
	}

	public boolean hasFontSettings() {
		return ((fontSettings != null) && (0 < fontSettings.size()));
	}
}
