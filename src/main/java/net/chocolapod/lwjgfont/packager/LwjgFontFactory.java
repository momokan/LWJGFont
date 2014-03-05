/**
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2014 momokan (http://lwjgfont.chocolapod.net)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.chocolapod.lwjgfont.packager;

import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.chocolapod.lwjgfont.LWJGFont;
import net.chocolapod.lwjgfont.FontMap;
import net.chocolapod.lwjgfont.MappedCharacter;
import net.chocolapod.lwjgfont.cli.Main;


import static net.chocolapod.lwjgfont.cli.CliMessage.LWJGFONT_VERSION_FORMAT;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.ARTIFACT_NAME;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.ARTIFACT_VERSION;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.CHARACTER_FILE_DIR;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.IMAGE_CHARACTER_PADDING;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.IMAGE_DRAW;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.IMAGE_DRAW_FRAME;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.TEMP_DIR;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.DIST_DIR;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.LWJGFONT_VERSION;
import static net.chocolapod.lwjgfont.packager.LwjgFontUtil.CHARSET_UTF8;

public class LwjgFontFactory {
	public static final String			DEFAULT_DIST_DIR = "";
	public static final String			DEFAULT_TEMP_DIR = "temp/";
	private static final String			SOURCE_DIR = "src";
	public static final String			RESOURCE_DIR = "resources";
	private static final String			COMPILES_DIR = "target";
	private static final String			VERSION_PROPERTIES = "version.properties";
	
	private LwjgFontProperties	properties;
	private ProcessLog			classMapLog;
	
	private String				tempDir;
	private String				srcDir;
	private String				resourceDir;
	private String				targetDir;
	private String				packageName;
	private String				pomName;

	private int					maxCharacterRegistration = 500;
	
	public LwjgFontFactory(String propertiesPath) throws IOException {
		properties = LwjgFontProperties.load(propertiesPath);
		properties.appendProerties(LWJGFont.class, VERSION_PROPERTIES);

		tempDir = properties.getAsString(TEMP_DIR);
		LwjgFontUtil.deleteFile(tempDir);

		srcDir = LwjgFontUtil.prepareDirectory(tempDir, SOURCE_DIR).getPath();
		resourceDir = LwjgFontUtil.prepareDirectory(tempDir, RESOURCE_DIR).getPath();
		targetDir = LwjgFontUtil.prepareDirectory(tempDir, COMPILES_DIR).getPath();
		
		String		groupId = LWJGFont.class.getPackage().getName();
		String		artifactId = properties.getAsString(ARTIFACT_NAME); 
		String		version = properties.getAsString(ARTIFACT_VERSION);
		String		dstDir = LwjgFontUtil.prepareDirectory(properties.getAsString(DIST_DIR)).getPath();
		
		packageName = LwjgFontUtil.toDirectoryPath(dstDir) + artifactId + "-" + version + ".jar";
		pomName = LwjgFontUtil.toDirectoryPath(dstDir) + artifactId + "-" + version + ".pom.xml";
		classMapLog = new ProcessLog(packageName, pomName, groupId, artifactId, version);
	}

	public void create(FontSetting fontSetting) throws IOException, FontFormatException {
		SourceBuffer	sourceBuffer = processClass(fontSetting, resourceDir);

		writeJavaSource(sourceBuffer, srcDir);

		classMapLog.add(fontSetting.getFontPath(), fontSetting.getFontSize(), fontSetting.getFontAlias(), sourceBuffer.getCannonicalClassName());
	}
	
	public void makePackage() throws IOException {
		//	生成した Java ソースコードをコンパイルする
		SourceCompiler	sourceCompiler = new SourceCompiler();
		
		sourceCompiler.setSourceDir(srcDir);
		sourceCompiler.setResourceDir(resourceDir);
		sourceCompiler.setTargetDir(targetDir);
		sourceCompiler.compile(classMapLog.listClasses());
		
		extractStaticResources(resourceDir);

		//	Jar ファイルにアーカイブする
		Packager		packager = new Packager();

		packager.setResourceDir(resourceDir);
		packager.setTargetDir(targetDir);
		packager.process(packageName);
		
		//	pom.xml を展開する
		ResourceExtractor	resourceExtractor = prepareResourceExtractor();

		resourceExtractor.addResourcePath(
				"packagedResources/META-INF/maven/net/chocolapod/lwjgfont/lwjgfont/pom.xml",
				pomName);
		resourceExtractor.copy();
	}

	private SourceBuffer processClass(FontSetting fontSetting, String resourceDir) throws IOException, FontFormatException {
		FontMapPainter	fontMapPainter = new FontMapPainter();
		String			artifactName = properties.getAsString(ARTIFACT_NAME);
		String			packageName = LWJGFont.class.getPackage().getName() + "." + artifactName;
		String			packageDirs = packageName.replace('.', File.separatorChar);

		fontMapPainter.setWriteImage(properties.getAsBoolean(IMAGE_DRAW));
		fontMapPainter.setWriteImageFrame(properties.getAsBoolean(IMAGE_DRAW_FRAME));
		fontMapPainter.setPadding(properties.getAsInt(IMAGE_CHARACTER_PADDING));
		fontMapPainter.setCharactersDir(properties.getAsString(CHARACTER_FILE_DIR));
		fontMapPainter.setResourceDir(resourceDir);
		fontMapPainter.setPackageDirs(packageDirs);
		
		FontMap			fontMap = fontMapPainter.paint(fontSetting);
		SourceBuffer	source = new SourceBuffer(packageName);

		source.printJavadocComment(
				"The subclass of net.chocolapod.lwjgfont.LWJGFont<br>",
				"to render string with font: \"" + fontSetting.getFontPath() + "\", with size: " + fontSetting.getFontSize() + ".<br>",
				"<br>",
				"This class has utility methods to reder any strings with the above font.<br>",
				"@see net.chocolapod.lwjgfont.LWJGFont"
		);
		source.openClass(fontSetting.getFontClassName(), null, true, LWJGFont.class);

		printStaticFieldFontMap(source);
		printPrepareFontMap(source, fontMap);
		printMethodGetFontMap(source);
		printMethodGetDefaultLineHieght(source, fontMap.getLineHeight());
		
		source.closeClass();
		
		return source;
	}

	private void printStaticFieldFontMap(SourceBuffer source) {
		source.println("private static final FontMap\tmap = new FontMap();");
	}

	private void printPrepareFontMap(SourceBuffer source, FontMap fontMap) {
		source.println("static ");
		source.openBrace();

		//	ImageIndex と画像ファイルの対応を書き出す
		for (int imageIndex: fontMap.listImageIndexes()) {
			String		imageFile = fontMap.getImageFile(imageIndex);
			
			source.println("map.addImageFile(%d, \"%s\");", imageIndex, imageFile);
		}
		source.println();
		
		//	文字とフォント情報を書き出す
		List<SourceBuffer>	configMethodSources = new ArrayList<>();
		SourceBuffer		configMethodSource = null;
		Map<String, Class>	configMethodArguments = new HashMap<String, Class>();
		int					configMethodIndex = 0;
		int					count = 0;

		configMethodArguments.put("map", FontMap.class);
		
		source.importClass(MappedCharacter.class);
		for (char character: fontMap.listCharacters()) {
			if ((configMethodSource == null) || (maxCharacterRegistration <= count)) {
				if (configMethodSource != null) {
					configMethodSource.closeMethod();
					configMethodSources.add(configMethodSource);
				}
				configMethodSource = new SourceBuffer();
				configMethodSource.openMethod("configMap" + configMethodIndex, null, configMethodArguments, "private", true);
				source.println("configMap%d(map);", configMethodIndex);
				configMethodIndex++;
				count = 0;
			}
			
			MappedCharacter	mappedCharacter = fontMap.getMappedCharacter(character);
			String			escapedCharacter = String.valueOf(mappedCharacter.getCharacter());
			
			if (escapedCharacter.equals("'")) {
				escapedCharacter = "\\'";
			} else if (escapedCharacter.equals("\\")) {
				escapedCharacter = "\\\\";
			}
			
			configMethodSource.println(
					"map.addCharacter(new %s('%s', %d, %d, %d, %d, %d, %d, %d));",
					MappedCharacter.class.getSimpleName(),
					escapedCharacter,
					mappedCharacter.getImageIndex(),
					mappedCharacter.getSrcX(),
					mappedCharacter.getSrcY(),
					mappedCharacter.getAscent(),
					mappedCharacter.getDescent(),
					mappedCharacter.getAdvance(),
					mappedCharacter.getPadding()
			);
			
			count++;
		}
		
		if (configMethodSource != null) {
			configMethodSource.closeMethod();
			configMethodSources.add(configMethodSource);
		}

		source.closeBrace();
		source.println();

		for (SourceBuffer toMerge: configMethodSources) {
			source.merge(toMerge, 1);
			source.println();
		}
		
	}
	
	private void printMethodGetFontMap(SourceBuffer source) {
		source.printJavadocComment("@see net.chocolapod.lwjgfont.LWJGFont#getFontMap()");

		source.importClass(FontMap.class);
		source.openMethod("getFontMap", FontMap.class.getSimpleName(), new HashMap<String, Class>(), "protected", false);
		source.println("return map;");
		source.closeMethod();
	}
	
	private void printMethodGetDefaultLineHieght(SourceBuffer source, int lineHeight) {
		source.println("@Override");
		source.openMethod("getDefaultLineHeight", "int", new HashMap<String, Class>(), false);
		source.println("return " + lineHeight + ";");
		source.closeMethod();
	}
	
	private void writeJavaSource(SourceBuffer sourceBuffer, String baseDir) throws IOException {
		PrintWriter		pw = null;
		File			sourceFile = sourceBuffer.getFile(baseDir);
		
		LwjgFontUtil.prepareDirectory(sourceFile.getParent());
		
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(sourceFile), CHARSET_UTF8));
			pw.print(sourceBuffer.toString());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private void extractStaticResources(String resourceDir) throws IOException {
		ResourceExtractor	resourceExtractor = prepareResourceExtractor();
		String				artifactName = properties.getAsString(ARTIFACT_NAME);

		resourceExtractor.addResourcePath(
				"packagedResources/META-INF/maven/net/chocolapod/lwjgfont/lwjgfont/pom.properties",
				"META-INF/maven/net/chocolapod/lwjgfont/" + artifactName + "/pom.properties");
		resourceExtractor.addResourcePath(
				"packagedResources/META-INF/maven/net/chocolapod/lwjgfont/lwjgfont/pom.xml",
				"META-INF/maven/net/chocolapod/lwjgfont/" + artifactName + "/pom.xml");
		resourceExtractor.addResourcePath(
				"packagedResources/META-INF/MANIFEST.MF",
				"META-INF/MANIFEST.MF");

//		resourceExtractor.addReplacePatterns(properties, ARTIFACT_NAME, ARTIFACT_VERSION);
		resourceExtractor.setResourcesDir(resourceDir);
		resourceExtractor.copy();
	}
	
	private ResourceExtractor prepareResourceExtractor() {
		ResourceExtractor	resourceExtractor = new ResourceExtractor();

		resourceExtractor.addReplacePatterns(properties, ARTIFACT_NAME, ARTIFACT_VERSION, LWJGFONT_VERSION);
		
		return resourceExtractor;
	}
	
	public void writeProcessLog() throws IOException {
		classMapLog.write();
		System.out.println(classMapLog);
	}

	//	TODO リファクタ
	public void extractCharacterFiles() throws IOException, URISyntaxException {
		URL			urlCharacters = this.getClass().getClassLoader().getResource(this.getClass().getPackage().getName().replaceAll("\\.", "/") + "/characters/");

		if (urlCharacters.toURI().getScheme().equals("file")) {
			extractCharacterFilesFromDir(urlCharacters);
		} else {
			extractCharacterFilesFromJar(urlCharacters);
		}
	}

	//	TODO リファクタ
	private void extractCharacterFilesFromDir(URL urlCharacters) throws URISyntaxException, IOException {
		File					dir = new File(urlCharacters.toURI());
		String					pathMask = urlCharacters.getPath();
		ResourceExtractor	resourceExtractor = new ResourceExtractor();

		//	Windows 環境では絶対パスの先頭に / がつかないので、取り除く 
		if ((File.separator.equals("\\")) && (pathMask.startsWith("/"))) {
			pathMask = pathMask.substring(1);
		}

		for (File nextFile: dir.listFiles()) {
			String				filePath = nextFile.getPath();
			
			filePath = filePath.substring(pathMask.length());
			filePath = "characters/" + filePath;
			
			resourceExtractor.addResourcePath(filePath, nextFile.getName());
		}

		resourceExtractor.setResourcesDir(properties.getAsString(CHARACTER_FILE_DIR));
		resourceExtractor.copy();
	}

	//	TODO リファクタ
	private void extractCharacterFilesFromJar(URL urlCharacters) throws IOException {
		JarURLConnection		connection = (JarURLConnection)urlCharacters.openConnection();
		String					charactersDir = properties.getAsString(CHARACTER_FILE_DIR);
		ZipInputStream		in = null;
		ZipEntry				zipEntry = null;
		String					basePath = connection.getJarEntry().getName();
		byte[]					buff = new byte[1024 * 1024];
		int						size;
		
		charactersDir = LwjgFontUtil.prepareDirectory(charactersDir).getPath();
		charactersDir = LwjgFontUtil.toDirectoryPath(charactersDir);
		
		try {
			in = new ZipInputStream(new FileInputStream(connection.getJarFile().getName()));
			
			while ((zipEntry = in.getNextEntry()) != null) {
				if ((!zipEntry.getName().startsWith(basePath)) || (zipEntry.getName().length() <= basePath.length())) {
					continue;
				}
				
				FileOutputStream		out = null;
				String					fileName = zipEntry.getName().substring(basePath.length());
				
				try {
					out = new FileOutputStream(charactersDir + fileName);
					
					while (0 < (size = in.read(buff))) {
						out.write(buff, 0, size);
					}
				} catch (Exception e) {
					continue;
				} finally {
					try {
						in.closeEntry();
					} catch (IOException e2) {}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e2) {}
					}
				}
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
	}

	public void printVersion() {
		System.out.println(LWJGFONT_VERSION_FORMAT.format(properties.getAsString(LWJGFONT_VERSION)));
	}
	
}
