package net.chocolapod.lwjgfont.texture;

public class Profiler {
	private static final boolean	isActive = true;
	
	private String	prefix;
	private	long	startTime;
	private	long	beforeTime;
	
	public Profiler(String prefix) {
		this.prefix = prefix;
		this.startTime = System.currentTimeMillis();
		this.beforeTime = startTime;
	}
	
	public void mark(String message) {
		if (!isActive) return;
		
		long	currentTime = System.currentTimeMillis();

		System.out.println(prefix + " " + message + ": " + (currentTime - beforeTime) + " / " + (currentTime - startTime));
		
		this.beforeTime = currentTime;
	}

	public void end() {
		mark("end");
	}

}
