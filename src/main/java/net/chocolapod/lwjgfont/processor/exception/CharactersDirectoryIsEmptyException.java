package net.chocolapod.lwjgfont.processor.exception;

import java.io.File;

public class CharactersDirectoryIsEmptyException extends LwjgFontException {

	public CharactersDirectoryIsEmptyException(File charactersDir) {
		super("character files directory: " + charactersDir.getAbsolutePath() + " is empty.");
	}

}
