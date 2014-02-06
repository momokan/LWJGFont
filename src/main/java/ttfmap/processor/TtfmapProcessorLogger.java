package ttfmap.processor;

import java.util.logging.Logger;

public class TtfmapProcessorLogger extends Logger {
	private static TtfmapProcessorLogger		instance = null;

	private TtfmapProcessorLogger() {
		super(TtfmapProcessorLogger.class.getName(), TtfmapProcessorLogger.class.getName() + ".log");
	}

	public static TtfmapProcessorLogger getInstance() {
		if (instance == null) {
			instance = new TtfmapProcessorLogger();
		}
		
		return instance;
	}
}
