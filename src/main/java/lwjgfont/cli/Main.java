package lwjgfont.cli;

import java.awt.FontFormatException;
import java.io.IOException;
import java.net.URISyntaxException;

import lwjgfont.packager.LwjgFont;

import static lwjgfont.cli.CliArgument._p;
import static lwjgfont.cli.CliArgument._x;

public class Main {

	public static void main(String[] args) throws IOException, FontFormatException, URISyntaxException {
		CliArgumentParser	parser = new CliArgumentParser(args);	
		LwjgFont				lwjgFont = new LwjgFont(parser.get(_p));
		
		if (parser.hasArgument(_x)) {
			//	キャラクターファイルを展開する
			lwjgFont.extractCharacterFiles();
		} else {
			//	フォントマップを作成する
			for (String fontPath: parser.listFontPaths()) {
				Integer			fontSize = parser.getFontSize(fontPath);
				
				lwjgFont.process(fontPath, fontSize);
			}
		}
	}
}
