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
import java.util.Iterator;
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
		JavaCompiler					compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager		fileManager = compiler.getStandardFileManager(null, Locale.getDefault(), COMPILE_CHARSET_UTF8);
		List<File>					classPaths = getClassPathAsFileList(fileManager);
		
		fileManager.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(new File(srcDir)));
		fileManager.setLocation(StandardLocation.CLASS_PATH, classPaths);
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(targetDir)));

		try {
			JavaFileObject			file = fileManager.getJavaFileForInput(SOURCE_PATH, classCanonicalName, SOURCE);

			compiler.getTask(null, fileManager, new DiagnosticsReporter(), null, null, Arrays.asList(file)).call();
		} finally {
			fileManager.close();
		}
	}

	private List<File> getClassPathAsFileList(StandardJavaFileManager fileManager) {
		List<File>					classPaths = new ArrayList<>();
		Iterator<? extends File>	iterator = fileManager.getLocation(StandardLocation.CLASS_PATH).iterator();

		classPaths.add(new File(resourceDir));
		while (iterator.hasNext()) {
			classPaths.add(iterator.next());
		}

		return classPaths;
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
}
