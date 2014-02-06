package ttfmap.processor;

import static javax.tools.JavaFileObject.Kind.SOURCE;
import static javax.tools.StandardLocation.SOURCE_PATH;
import static ttfmap.processor.TestResource.assertBinary;
import static ttfmap.processor.TtfmapProcessor.CLASS_APPENDIX;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import ttfmap.processor.TtfmapProcessor;
import ttfmap.processor.exception.AptException;

//import m2o.binarysource.annotation.processor.exception.AptException;
//import m2o.testutil.TestResource;
//import m2o.util.ListUtils;

//import org.seasar.doma.internal.util.IOUtil;

public abstract class AptTest {
	protected static final JavaCompiler		COMPILER = ToolProvider.getSystemJavaCompiler();
	protected static final CompileOption		MAIN_COMPILE_OPTION = new CompileOption("src/main/java/", TestResource.MAIN_BASE_DIR, "target/classes/");
	protected static final CompileOption		TEST_COMPILE_OPTION = new CompileOption("src/test/java/", TestResource.TEST_BASE_DIR, "target/test-classes/");
	
	protected static final Charset			COMPILE_CHARSET_UTF8 = Charset.forName("utf-8");
	protected static final String				TEST_RESOURCE_BYTES_EXTENTION = ".bytes";
//	protected static final String				COMPILE_SOURCE_MAIN_PATH = "src/main/java/";
//	protected static final String				COMPILE_SOURCE_TEST_PATH = ;
//	protected static final String				COMPILE_RESOURCE_TEST_PATH = ;
//	protected static final String				COMPILE_GENERATED_MAIN_PATH = "target/classes/";
//	protected static final String				COMPILE_GENERATED_TEST_PATH = ;
//	protected static final List<String>		COMPILE_OPTIONS = ListUtils.asList("-classpath", System.getProperty("java.class.path"), "-d", COMPILE_GENERATED_TEST_PATH);

//	protected static final JavaFileManager	COMPILE_MAIN_FILE_MANAGER = prepareFileManager(COMPILE_SOURCE_MAIN_PATH);
//	protected static final JavaFileManager	COMPILE_TEST_FILE_MANAGER = prepareFileManager(COMPILE_SOURCE_TEST_PATH);
//	protected static final ClassLoader			COMPILE_MAIN_CLASSLOADER = prepareClassLoader(COMPILE_GENERATED_MAIN_PATH);
//	protected static final ClassLoader			COMPILE_TEST_CLASSLOADER = prepareClassLoader(COMPILE_GENERATED_TEST_PATH);
	
	private boolean	isDump = false;
	
	public AptTest() {
		this(false);
	}
	public AptTest(boolean isDump) {
		this.isDump = isDump;
		System.setProperty("processor.dump", String.valueOf(isDump));
	}

	protected void assertTestCompiled(Processor processor, Class clazz) throws IOException {
		assertCompiled(processor, clazz, true, TEST_COMPILE_OPTION);
	}
	protected void assertTestCompiled(Processor processor, Class clazz, boolean expected) throws IOException {
		assertCompiled(processor, clazz, expected, TEST_COMPILE_OPTION);
	}
	protected void assertMainCompiled(Processor processor, Class clazz) throws IOException {
		assertCompiled(processor, clazz, true, MAIN_COMPILE_OPTION);
	}
	protected void assertMainCompiled(Processor processor, Class clazz, boolean expected) throws IOException {
		assertCompiled(processor, clazz, expected, MAIN_COMPILE_OPTION);
	}
	private void assertCompiled(Processor processor, Class clazz, boolean expected, CompileOption compileOption) throws IOException {
		System.setProperty("processor.dump", String.valueOf(isDump));
		
		boolean	result = compile(processor, clazz, compileOption);
		
		assertEquals(expected, result);
	}
	
	protected void assertTestSource(Processor processor, Class<?> clazz) throws IOException {
		assertSource(processor, clazz, TEST_COMPILE_OPTION);
	}
	protected void assertMainSource(Processor processor, Class<?> clazz) throws IOException {
		assertSource(processor, clazz, MAIN_COMPILE_OPTION);
	}
	private void assertSource(Processor processor, Class<?> clazz, CompileOption compileOption) throws IOException {
		compile(processor, clazz, compileOption);

		File		actual = getGeneratedSource(clazz, compileOption);
		File		expected = toExpectedSource(clazz, compileOption);
		
		//	行毎にソースコードを比較する
		BufferedReader		brExpected = null;
		BufferedReader		brActual = null;
		String					lineExpected;
		String					lineActual;
		int						lineNo = 1;
		
		try {
			brExpected = new BufferedReader(new InputStreamReader(new FileInputStream(expected), COMPILE_CHARSET_UTF8));
			brActual = new BufferedReader(new InputStreamReader(new FileInputStream(actual), COMPILE_CHARSET_UTF8));
			
			while (true) {
				lineExpected = brExpected.readLine();
				lineActual = brActual.readLine();
				
				if ((lineExpected == null) && (lineActual == null)) {
					break;
				}
				try {
					assertEquals(lineExpected, lineActual);
				} catch(AssertionError e) {
					System.out.println(expected.getCanonicalPath());
					System.out.println("Expected: " + lineNo + ": " + lineExpected);
					System.out.println("Actual:   " + lineNo + ": " + lineActual);
					
					throw e;
				}
				lineNo++;
			}
		} catch(IOException e) {
			throw e;
		} finally {
			if (brExpected != null) {
				brExpected.close();
			}
			if (brActual != null) {
				brActual.close();
			}
		}
	}

	protected void assertTestBinary(Class clazz, ByteArrayOutputStream actual) throws IOException {
		assertBinary(toFile(clazz, TEST_COMPILE_OPTION.resourcePath, ".bytes").getAbsolutePath(), actual.toByteArray());
//		assertBinary(clazz, actual, TEST_COMPILE_OPTION);
	}
	protected void assertMainBinary(Class clazz, ByteArrayOutputStream actual) throws IOException {
		assertBinary(toFile(clazz, MAIN_COMPILE_OPTION.resourcePath, ".bytes").getAbsolutePath(), actual.toByteArray());
//		assertBinary(clazz, actual, MAIN_COMPILE_OPTION);
	}
	/*
	protected void assertBinary(Class clazz, ByteArrayOutputStream actual, CompileOption compileOption) throws IOException {
		File			expected = toFile(clazz, compileOption.resourcePath, ".bytes");
		ByteBuffer		buffExpected = ByteBuffer.allocate((int)expected.length());
		byte[]			buffActual = actual.toByteArray();

		//	バイト毎にバイナリーデータを比較する
		FileChannel			in = null;
		int						i = 0;

		try {
			in = new FileInputStream(expected).getChannel();
			in.read(buffExpected);
			buffExpected.flip();
			
			assertEquals(buffExpected.capacity(), buffActual.length);
			while (i < buffActual.length) {
				byte	byteExpected = buffExpected.get();
				byte	byteActual = buffActual[i];
				
				assertEquals(byteExpected, byteActual);
				i++;
			}
		} catch(IOException e) {
			throw e;
		} finally {
			IOUtil.close(in);
		}
	}
	*/

	protected <T> T assertTestInstantinate(Processor processor, Class<T> clazz) throws IOException, ReflectiveOperationException {
		return assertInstantinate(processor, clazz, TEST_COMPILE_OPTION);
	}
	protected <T> T assertMainInstantinate(Processor processor, Class<T> clazz) throws IOException, ReflectiveOperationException {
		return assertInstantinate(processor, clazz, MAIN_COMPILE_OPTION);
	}
	private <T> T assertInstantinate(Processor processor, Class<T> clazz, CompileOption compileOption) throws IOException, ReflectiveOperationException {
		compile(processor, clazz, compileOption);

		T	target = getGeneratedObject(clazz, compileOption);
		
		assertEquals(true, target != null);
		
		return target;
	}
 
	private boolean compile(Processor processor, Class clazz, CompileOption compileOption) throws IOException {
		JavaFileObject		javaFile = compileOption.fileManager.getJavaFileForInput(SOURCE_PATH, clazz.getCanonicalName(), SOURCE);
		CompilationTask		task = COMPILER.getTask(null, null, new DiagnosticsReporter(), compileOption.compileOptions, null, Arrays.asList(javaFile));

		task.setProcessors(Arrays.asList(processor));

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

	private File getGeneratedSource(Class clazz, CompileOption compileOption) {
		return new File(compileOption.comiledClassPath + clazz.getCanonicalName().replaceAll("\\.", "/") + CLASS_APPENDIX + ".java");
	}
	
	protected File toExpectedSource(Class clazz, CompileOption compileOption) {
		return toFile(clazz, compileOption.resourcePath, ".java");
	}
	protected File toFile(Class clazz, String baseDir, String fileExtention) {
		String		filepath = clazz.getCanonicalName().replaceAll("\\.", "/");
		
		filepath += CLASS_APPENDIX;
		filepath += fileExtention;
		filepath = baseDir + filepath;

		return new File(filepath);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getGeneratedTestObject(Class clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return (T)getGeneratedClass(clazz, TEST_COMPILE_OPTION).newInstance();
	}
	@SuppressWarnings("unchecked")
	protected <T> T getGeneratedMainObject(Class clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return (T)getGeneratedClass(clazz, MAIN_COMPILE_OPTION).newInstance();
	}
	@SuppressWarnings("unchecked")
	protected <T> T getGeneratedObject(Class clazz, CompileOption compileOption) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return (T)getGeneratedClass(clazz, compileOption).newInstance();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> Class<T> getGeneratedTestClass(Class clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return getGeneratedClass(clazz, TEST_COMPILE_OPTION);
	}
	@SuppressWarnings("unchecked")
	protected <T> Class<T> getGeneratedMainClass(Class clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return getGeneratedClass(clazz, MAIN_COMPILE_OPTION);
	}
	@SuppressWarnings("unchecked")
	private <T> Class<T> getGeneratedClass(Class clazz, CompileOption compileOption) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?>	compiled = compileOption.classLoader.loadClass(clazz.getCanonicalName() + CLASS_APPENDIX);
		
		return (Class<T>)compiled;
	}

	/*
	protected ByteBuffer readFromFile(File file) throws IOException {
		ByteBuffer				buff = ByteBuffer.allocate((int)file.length());
		FileChannel			in = null;

		try {
			in = new FileInputStream(file).getChannel();
			in.read(buff);
			buff.flip();

			return buff;
		} catch (IOException e) {
			throw e;
		} finally  {
			IOUtil.close(in);
		}
	}
	protected void writeToFile(File file, ByteArrayOutputStream source) throws IOException {
		FileOutputStream		out = null;
		File					dir = file.getParentFile();
		
		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			out = new FileOutputStream(file);
			out.write(source.toByteArray());
		} catch (IOException e) {
			throw e;
		} finally  {
			IOUtil.close(out);
		}
	}
	*/
	
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
	
	class DiagnosticsReporter implements DiagnosticListener<JavaFileObject> {

		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			if (TtfmapProcessor.isDebug()) {
				System.out.println(diagnostic);
			}
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
