package lwjgfont.packager;

import static javax.tools.JavaFileObject.Kind.SOURCE;
import static javax.tools.StandardLocation.SOURCE_PATH;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import lwjgfont.processor.exception.AptException;

public class SourceCompiler {
	
	protected static final Charset			COMPILE_CHARSET_UTF8 = Charset.forName("utf-8");
	protected static final JavaCompiler		COMPILER = ToolProvider.getSystemJavaCompiler();
//	protected static final CompileOption	MAIN_COMPILE_OPTION = new CompileOption("src/main/java/", TestResource.MAIN_BASE_DIR, "target/classes/");
//	protected static final CompileOption	TEST_COMPILE_OPTION = new CompileOption("src/test/java/", TestResource.TEST_BASE_DIR, "target/test-classes/");
	
	private String	srcDir = "";
	private String	resourceDir = "";
	private String	targetDir = "";

	public void compile(String classCanonicalName) throws IOException {
		LwjgFontUtil.prepareDirectory(targetDir);
		
		CompileOption			compileOption = new CompileOption(srcDir, resourceDir, targetDir);
		List<JavaFileObject>	javaFiles = new ArrayList<>();

		javaFiles.add(compileOption.fileManager.getJavaFileForInput(SOURCE_PATH, classCanonicalName, SOURCE));

		compile(compileOption, javaFiles);
	}

	private boolean compile(CompileOption compileOption, List<JavaFileObject> javaFiles) throws IOException {
		/*
		JavaCompiler			compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager	fileManager = compiler.getStandardFileManager(null, null, null);
		
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(compileOption.comiledClassPath)));
		*/

		CompilationTask		task = COMPILER.getTask(null, null, new DiagnosticsReporter(), compileOption.compileOptions, null, javaFiles);

		try {
			return task.call();
		} catch(RuntimeException e){
			if (e.getCause() instanceof AptException) {
				throw (AptException)e.getCause();
			} else {
				throw e;
			}
		}
	}
	
	public void setSourceDir(String srcDir) {
		this.srcDir = srcDir;
	}
	public void setResourceDir(String resourceDir) {
		this.resourceDir = resourceDir;
	}
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

	class DiagnosticsReporter implements DiagnosticListener<JavaFileObject> {

		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
//			if (LwjgFontProcessor.isDebug()) {
				System.out.println(diagnostic);
//			}
			/*
			System.out.println(diagnostic.getCode());
			System.out.println(diagnostic.getKind());
			System.out.println(diagnostic.getPosition());
			System.out.println(diagnostic.getStartPosition());
			System.out.println(diagnostic.getEndPosition());
			System.out.println(diagnostic.getSource());
			System.out.println(diagnostic.getMessage(null));
			*/
		}
		
	}
	
	private static JavaFileManager prepareFileManager(String sourcePath) {
		StandardJavaFileManager			standardJavaFileManager = COMPILER.getStandardFileManager(null, Locale.JAPAN, COMPILE_CHARSET_UTF8);
		
		try {
			standardJavaFileManager.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(new File(sourcePath)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return standardJavaFileManager;
	}
	
	private static ClassLoader prepareClassLoader(String compiledClassPath) {
		try {
			return URLClassLoader.newInstance(new URL[] {new File(compiledClassPath).toURI().toURL()});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class CompileOption {
		public final String			sourcePath;
		public final String			resourcePath;
		public final String			comiledClassPath;
		public final List<String>	compileOptions;
		
		private final JavaFileManager	fileManager;
		private final ClassLoader		classLoader;

		public CompileOption(String sourcePath, String resourcePath, String comiledClassPath) {
			this.sourcePath = sourcePath;
			this.resourcePath = resourcePath;
			this.comiledClassPath = comiledClassPath;
			
			this.compileOptions = Arrays.asList("-classpath", System.getProperty("java.class.path"), "-d", comiledClassPath);
			this.fileManager = prepareFileManager(sourcePath);
			this.classLoader = prepareClassLoader(comiledClassPath);
		}
	}
}
