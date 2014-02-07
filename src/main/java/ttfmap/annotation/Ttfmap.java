package ttfmap.annotation;

import static ttfmap.processor.TtfPainter.DEFAULT_PADDING;
import static ttfmap.processor.TtfPainter.DEFAULT_CHARACTERS_DIR;
import static ttfmap.processor.TtfPainter.DEFAULT_RESOURCE_BASE_DIR;

public @interface Ttfmap {
	String	fontPath();
	int		fontSize();
	
	int		padding() default DEFAULT_PADDING;
	String	charactersDir() default DEFAULT_CHARACTERS_DIR;
	String	resourceBaseDir() default DEFAULT_RESOURCE_BASE_DIR;
}
