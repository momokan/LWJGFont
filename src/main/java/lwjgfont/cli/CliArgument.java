package lwjgfont.cli;

public enum CliArgument {
	_p;
	
	public String toArgument() {
		return name().replace('_', '-');
	}
}