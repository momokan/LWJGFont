package net.chocolapod.lwjgfont.processor.exception;


public class LwjgFontException extends RuntimeException {

	public LwjgFontException() {
		this(null);
	}

	public LwjgFontException(String message, Object... args) {
		super(String.format(message, args));
	}

	public LwjgFontException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public static LwjgFontException as(Throwable cause) {
		if (cause instanceof LwjgFontException) {
			return (LwjgFontException)cause;
		} else {
			return new LwjgFontException(cause.getMessage(), cause);
		}
	}

}
