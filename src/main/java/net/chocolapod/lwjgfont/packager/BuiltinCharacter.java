package net.chocolapod.lwjgfont.packager;

public enum BuiltinCharacter {
	Space(' '),
	MultibytesSpace('　'),
	NotMatchedSign('■');

	private final char c; 
	
	private BuiltinCharacter(char c) {
		this.c = c;
	}
	
	public char getCharacter() {
		return c;
	}
}
