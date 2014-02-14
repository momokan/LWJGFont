package net.chocolapod.lwjgfont.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CliArgumentParser {
	private Map<CliArgument, String>	parsedArguments;
	private Map<String, Integer>		fontPaths;

	public CliArgumentParser(String[] args) {
		List<String>		arguments = new ArrayList<>(Arrays.asList(args));
		
		parsedArguments = new HashMap<>();
		fontPaths = new LinkedHashMap<>();
		while (0 < arguments.size()) {
			String			arg = arguments.remove(0);
			
			if (isArgument(arg, arguments)) {
				continue;
			}
			
			if (isFontArgument(arg)) {
				continue;
			}
			
			//	無効なオプションとして処理する
			System.err.println("Invalid argument: " + arg);
		}
	}
	
	private boolean isFontArgument(String arg) {
		//	フォントの設定として解釈する
		String[]	tokens = arg.split(":");

		try {
			if (tokens.length == 2) {
				fontPaths.put(tokens[0], Integer.parseInt(tokens[1]));
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
	
	public String[] listFontPaths() {
		return fontPaths.keySet().toArray(new String[] {});
	}

	public Integer getFontSize(String fontName) {
		return fontPaths.get(fontName);
	}
}