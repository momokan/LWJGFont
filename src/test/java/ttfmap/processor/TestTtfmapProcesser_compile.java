package ttfmap.processor;

import java.io.IOException;

import org.junit.Test;

import ttfmap.processor.TtfmapProcessor;

public class TestTtfmapProcesser_compile extends AptTest {
	
	public TestTtfmapProcesser_compile() {
		super(true);
	}

	@Test
	public void binaryTarget() throws IOException {
		assertTestCompiled(new TtfmapProcessor(), Mig1MFont.class);
	}

}
