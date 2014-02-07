package ttfmap.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ttfmap.BuiltinCharacter;

public class BuiltinCharacterFile extends CharacterFile {
	private int	position;

	public BuiltinCharacterFile() {
		super(null);
	}

	@Override
	public void open() throws UnsupportedEncodingException, FileNotFoundException {
		// 何もしない
		position = 0;
	}

	@Override
	public Character next() throws IOException {
		if (position < BuiltinCharacter.values().length) {
			char	c = BuiltinCharacter.values()[position].getCharacter();
			
			position++;
			return c;
		} else {
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		// 何もしない
	}

}
