package lwjgfont;

public class MappedFont {
	private final char		character;
	private final int		imageIndex;
	private final int		srcX;
	private final int		srcY;
	private final int		ascent;
	private final int		descent;
	private final int		advance;
	private final int		padding;

	public MappedFont(char character, int imageIndex, int srcX, int srcY, int ascent, int descent, int advance, int padding) {
		this.character = character;
		this.imageIndex = imageIndex;
		this.srcX = srcX;
		this.srcY = srcY;
		this.ascent = ascent;
		this.descent = descent;
		this.advance = advance;
		this.padding = padding;
	}

	public char getCharacter() {
		return character;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public int getSrcX() {
		return srcX;
	}

	public int getSrcY() {
		return srcY;
	}

	public int getAscent() {
		return ascent;
	}

	public int getDescent() {
		return descent;
	}

	public int getAdvance() {
		return advance;
	}

	public int getPadding() {
		return padding;
	}

}
