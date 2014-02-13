package lwjgfont.cli;

public enum CliArgument {
	_p(true),
	_x(false);
	
	private final boolean hasValue;
	
	private CliArgument(boolean hasValue) {
		this.hasValue = hasValue;
	}
	
	public String toArgument() {
		return name().replace('_', '-');
	}
	public boolean hasValue() {
		return hasValue;
	}
}