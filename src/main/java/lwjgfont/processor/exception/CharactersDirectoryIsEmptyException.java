package lwjgfont.processor.exception;

import java.io.File;

public class CharactersDirectoryIsEmptyException extends AptException {

	public CharactersDirectoryIsEmptyException(File charactersDir) {
		super("character files directory: " + charactersDir.getAbsolutePath() + " is empty.");
	}

}
