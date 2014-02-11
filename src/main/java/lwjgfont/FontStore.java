package lwjgfont;

import java.util.HashMap;
import java.util.Map;

public abstract class FontStore {
	protected final Map<Character, MappedFont>	map = new HashMap<Character, MappedFont>();

	public void drawString(String text, float dstX, float dstY, float dstZ) {
		//	TODO
	}
	
	public MappedFont getMappedCharacter(char ch) {
		return getFontMap().getMappedFont(ch);
	}

	protected abstract FontMap getFontMap();

}
