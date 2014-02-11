package lwjgfont.packager;

public enum BuiltinCharacter {
	Space(' '),
	MultibytesSpace('　');

	private final char c; 
	
	private BuiltinCharacter(char c) {
		this.c = c;
	}
	
	public char getCharacter() {
		return c;
	}
}
