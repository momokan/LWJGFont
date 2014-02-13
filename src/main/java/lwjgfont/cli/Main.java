package lwjgfont.cli;

import java.awt.FontFormatException;
import java.io.IOException;

import lwjgfont.packager.LwjgFont;

public class Main {

	public static void main(String[] args) throws IOException, FontFormatException {
		CliArgumentParser	parser = new CliArgumentParser(args);	
		LwjgFont				lwjgFont = new LwjgFont(parser.get(CliArgument._p));
		
		for (String fontPath: parser.listFontPaths()) {
			Integer			fontSize = parser.getFontSize(fontPath);
			
			lwjgFont.process(fontPath, fontSize);
//			lwjgFont.process("sample/migu-1p-regular.ttf", 30);
		}
	}
}
