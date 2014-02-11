package lwjgfont.processor.exception;


public class AptException extends RuntimeException {

	public AptException() {
		this(null);
	}

	public AptException(String message, Object... args) {
		super(String.format(message, args));
	}

	public AptException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public static AptException as(Throwable cause) {
		if (cause instanceof AptException) {
			return (AptException)cause;
		} else {
			return new AptException(cause.getMessage(), cause);
		}
	}

}
