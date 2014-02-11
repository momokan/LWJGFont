package lwjgfont.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import lwjgfont.packager.CharacterFile;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCharacterFile {

	@Test
	public void readAllCharacters() throws IOException {
		char[]				expecteds = new char[] {'a', 'b', 'C', 'D', 'に', '本', '語'};
		
		CharacterFile		file = new CharacterFile("src/test/resources/lwjgfont/processor/TestCharacterFile_readAllCharacters.txt");
		int					i = 0;
		Character			actual;
		
		try {
			file.open();
			while ((actual = file.next()) != null) {
				assertTrue(i < expecteds.length);
				assertEquals(expecteds[i], actual.charValue());
				i++;
			}
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
