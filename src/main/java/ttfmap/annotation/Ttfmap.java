package ttfmap.annotation;

import static ttfmap.processor.TtfPainter.DEFAULT_PADDING;
import static ttfmap.processor.TtfPainter.DEFAULT_CHARACTERS_DIR;
import static ttfmap.processor.TtfPainter.DEFAULT_IMAGE_OUTPUT_DIR;

public @interface Ttfmap {
	String	fontPath();
	int		fontSize();
	
	int		padding() default DEFAULT_PADDING;
	String	charactersDir() default DEFAULT_CHARACTERS_DIR;
	String	imageOutputDir() default DEFAULT_IMAGE_OUTPUT_DIR;
}
