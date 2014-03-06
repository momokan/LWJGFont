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

import org.lwjgl.LWJGLUtil;

import net.chocolapod.lwjgfont.exception.cli.MissingOptionValueException;
import net.chocolapod.lwjgfont.exception.cli.UnknownArgumentException;
import net.chocolapod.lwjgfont.packager.FontSetting;
import net.chocolapod.lwjgfont.packager.LwjgFontUtil;

public class CliArgumentParser {
	private List<CliArgument>				cliArguments;
	private Map<CliOption, String>		optionsValueMap;
	private List<FontSetting>				fontSettings;

	public CliArgumentParser(String[] args) {
		List<String>		arguments = new ArrayList<>(Arrays.asList(args));
		
		cliArguments = new ArrayList<CliArgument>();
		optionsValueMap = new HashMap<>();
		fontSettings = new ArrayList<>();
		while (0 < arguments.size()) {
			String			arg = arguments.remove(0);
			CliArgument	cliArgument;

			if (
					((cliArgument = parseOption(arg, arguments)) != null) ||
					((cliArgument = parseFontArgument(arg)) != null)
			) {
				cliArguments.add(cliArgument);
				continue;
			}
			
			//	無効なオプションとして処理する
			throw new UnknownArgumentException(arg);
		}
	}

	private CliArgument parseFontArgument(String arg) {
		//	フォントの設定として解釈する
		String[]	tokens = arg.split(":");

		try {
			if (tokens.length == 2) {
				FontSetting	fontSetting = FontSetting.asSystemFont(tokens[0], Integer.parseInt(tokens[1]));

				if (fontSetting == null) {
					if (LwjgFontUtil.isEmpty(tokens[0])) {
						throw new NullPointerException();
					}
					fontSetting = new FontSetting(tokens[0], Integer.parseInt(tokens[1]));
				}
				fontSettings.add(fontSetting);
				return fontSetting;
			}
		} catch (Exception e) {}

		return null;
	}

	private CliArgument parseOption(String arg, List<String> arguments) {
		for (CliOption cliArgument: CliOption.values()) {
			if (cliArgument.toArgument().equals(arg)) {
				if (cliArgument.hasValue()) {
					if (arguments.size() <= 0) {
						throw new MissingOptionValueException(cliArgument);
					}
					optionsValueMap.put(cliArgument, arguments.remove(0));
				} else {
					optionsValueMap.put(cliArgument, null);
				}
				return cliArgument;
			}
		}

		return null;
	}
	
	public boolean hasOption(CliOption option) {
		return (optionsValueMap.containsKey(option));
	}

	public String get(CliOption option) {
		return optionsValueMap.get(option);
	}

	public CliArgument[] listArguments() {
		return cliArguments.toArray(new CliArgument[] {});
	}

	public CliArgument getBeforeArgument(CliArgument cliArgument) {
		int		index = cliArguments.indexOf(cliArgument);

		if (0 < index) {
			return cliArguments.get(index);
		}

		return null;
	}

	public FontSetting[] listFontSettings() {
		return fontSettings.toArray(new FontSetting[] {});
	}

	public boolean hasFontSettings() {
		return ((fontSettings != null) && (0 < fontSettings.size()));
	}
}
