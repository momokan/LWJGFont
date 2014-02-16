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

import net.chocolapod.lwjgfont.AbstractFont;
import net.chocolapod.lwjgfont.FontMap;
import net.chocolapod.lwjgfont.MappedFont;


import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.ARTIFACT_NAME;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.ARTIFACT_VERSION;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.CHARACTER_FILE_DIR;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.IMAGE_CHARACTER_PADDING;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.IMAGE_DRAW;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.IMAGE_DRAW_FRAME;
import static net.chocolapod.lwjgfont.packager.LwjgFontPropertyKey.TEMP_DIR;

public class LwjgFont {
	public static final String			DEFAULT_TEMP_DIR = "temp/";
	private static final String			SOURCE_DIR = "src";
	public static final String			RESOURCE_DIR = "resources";
	private static final String			COMPILES_DIR = "target";
	
	private LwjgFontProperties	properties;
	private ClassMapLog			classMapLog;
	
	private int					maxCharacterRegistration = 500;
	
	public LwjgFont(String propertiesPath) throws IOException {
		properties = LwjgFontProperties.load(propertiesPath);
		classMapLog = new ClassMapLog();
	}
	
	public void process(String fontPath, int fontSize) throws IOException, FontFormatException {
		String			tempDir = properties.getAsString(TEMP_DIR);

		LwjgFontUtil.deleteFile(tempDir);

		String			srcDir = LwjgFontUtil.prepareDirectory(tempDir, SOURCE_DIR).getPath();
		String			resourceDir = LwjgFontUtil.prepareDirectory(tempDir, RESOURCE_DIR).getPath();
		String			targetDir = LwjgFontUtil.prepareDirectory(tempDir, COMPILES_DIR).getPath();
		SourceBuffer	sourceBuffer = processClass(fontPath, fontSize, resourceDir);
		
		writeJavaSource(sourceBuffer, srcDir);
		
		SourceCompiler	sourceCompiler = new SourceCompiler();
		
		sourceCompiler.setSourceDir(srcDir);
		sourceCompiler.setResourceDir(resourceDir);
		sourceCompiler.setTargetDir(targetDir);
		sourceCompiler.compile(sourceBuffer.getCannonicalClassName());
		
		extractStaticResources(resourceDir);
		
		Packager		packager = new Packager();
		String			packageName = properties.getAsString(ARTIFACT_NAME) + "-" + properties.getAsString(ARTIFACT_VERSION) + ".jar";

		packager.setResourceDir(resourceDir);
		packager.setTargetDir(targetDir);
		packager.process(packageName);
		
		classMapLog.add(fontPath, fontSize, sourceBuffer.getCannonicalClassName());
	}

	private SourceBuffer processClass(String fontPath, int fontSize, String resourceDir) throws IOException, FontFormatException {
		FontMapPainter	fontMapPainter = new FontMapPainter();
		File			fontFile = new File(fontPath);
		String			artifactName = properties.getAsString(ARTIFACT_NAME);
		String			packageName = AbstractFont.class.getPackage().getName() + "." + artifactName;
		String			packageDirs = packageName.replace('.', File.separatorChar);

		fontMapPainter.setWriteImage(properties.getAsBoolean(IMAGE_DRAW));
		fontMapPainter.setWriteImageFrame(properties.getAsBoolean(IMAGE_DRAW_FRAME));
		fontMapPainter.setPadding(properties.getAsInt(IMAGE_CHARACTER_PADDING));
		fontMapPainter.setCharactersDir(properties.getAsString(CHARACTER_FILE_DIR));
		fontMapPainter.setResourceDir(resourceDir);
		fontMapPainter.setPackageDirs(packageDirs);
		
		FontMap			fontMap = fontMapPainter.paint(fontPath, fontSize);
		SourceBuffer	source = new SourceBuffer(packageName);

		source.openClass(toFontClassName(fontFile.getName(), fontSize), null, AbstractFont.class);

		printPrepareFontMap(source, fontMap);
		printMethodGetFontMap(source);
		
		source.closeClass();
		
		return source;
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
		
		source.importClass(MappedFont.class);
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
			
			MappedFont	mappedFont = fontMap.getMappedFont(character);
			String			escapedCharacter = String.valueOf(mappedFont.getCharacter());
			
			if (escapedCharacter.equals("'")) {
				escapedCharacter = "\\'";
			} else if (escapedCharacter.equals("\\")) {
				escapedCharacter = "\\\\";
			}
			
			configMethodSource.println(
					"map.addMappedFont(new %s('%s', %d, %d, %d, %d, %d, %d, %d));",
					MappedFont.class.getSimpleName(),
					escapedCharacter,
					mappedFont.getImageIndex(),
					mappedFont.getSrcX(),
					mappedFont.getSrcY(),
					mappedFont.getAscent(),
					mappedFont.getDescent(),
					mappedFont.getAdvance(),
					mappedFont.getPadding()
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
		source.importClass(FontMap.class);
		source.openMethod("getFontMap", FontMap.class.getSimpleName(), new HashMap<String, Class>(), "protected", false);
		source.println("return map;");
		source.closeMethod();
	}
	
	private void writeJavaSource(SourceBuffer sourceBuffer, String baseDir) throws IOException {
		PrintWriter		pw = null;
		File			sourceFile = sourceBuffer.getFile(baseDir);
		
		LwjgFontUtil.prepareDirectory(sourceFile.getParent());
		
		try {
			pw = new PrintWriter(sourceFile);
			pw.print(sourceBuffer.toString());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private String toFontClassName(String fontName, int fontSize) {
		fontName = LwjgFontUtil.trimExtention(fontName);
		
		String		className = "";
		String[]	tokens = fontName.split("[^0-9a-zA-Z]+");
		for (String token: tokens) {
			className += LwjgFontUtil.capitalize(token);
		}
		className += "H" + fontSize;
		className += "Font";
		
		return className;
	}

	private void extractStaticResources(String resourceDir) throws IOException {
		ResourceExtractor	resourceExtractor = new ResourceExtractor();
		String					artifactName = properties.getAsString(ARTIFACT_NAME);

		resourceExtractor.addResourcePath(
				"packagedResources/META-INF/maven/net/chocolapod/lwjgfont/lwjgfont/pom.properties",
				"META-INF/maven/net/chocolapod/lwjgfont/" + artifactName + "/pom.properties");
		resourceExtractor.addResourcePath(
				"packagedResources/META-INF/maven/net/chocolapod/lwjgfont/lwjgfont/pom.xml",
				"META-INF/maven/net/chocolapod/lwjgfont/" + artifactName + "/pom.xml");
		resourceExtractor.addResourcePath(
				"packagedResources/META-INF/MANIFEST.MF",
				"META-INF/MANIFEST.MF");

		resourceExtractor.addReplacePatterns(properties, ARTIFACT_NAME, ARTIFACT_VERSION);
		resourceExtractor.setResourcesDir(resourceDir);
		resourceExtractor.copy();
	}
	
	public void writeClassMapLog() throws IOException {
		classMapLog.write();
		classMapLog = new ClassMapLog();
	}

	//	TODO リファクタ
	public void extractCharacterFiles() throws IOException, URISyntaxException {
		String		charactersDir = LwjgFontUtil.prepareDirectory("characters").getPath();
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

		for (File nextFile: dir.listFiles()) {
			String				filePath = nextFile.getPath();
			
			filePath = filePath.substring(pathMask.length());
			filePath = "characters/" + filePath;
			
			resourceExtractor.addResourcePath(filePath, nextFile.getName());
		}

		resourceExtractor.setResourcesDir("characters");
		resourceExtractor.copy();
	}

	//	TODO リファクタ
	private void extractCharacterFilesFromJar(URL urlCharacters) throws IOException {
		JarURLConnection		connection = (JarURLConnection)urlCharacters.openConnection();
		ZipInputStream		in = null;
		ZipEntry				zipEntry = null;
		String					basePath = connection.getJarEntry().getName();
		byte[]					buff = new byte[1024 * 1024];
		int						size;
		
		try {
			in = new ZipInputStream(new FileInputStream(connection.getJarFile().getName()));
			
			while ((zipEntry = in.getNextEntry()) != null) {
				if ((!zipEntry.getName().startsWith(basePath)) || (zipEntry.getName().length() <= basePath.length())) {
					continue;
				}
				
				FileOutputStream		out = null;
				String					fileName = zipEntry.getName().substring(basePath.length());
				
				try {
					out = new FileOutputStream("characters/" + fileName);
					
					while (0 < (size = in.read(buff))) {
						out.write(buff, 0, size);
					}
				} catch (Exception e) {
					continue;
				} finally {
					try {
						in.closeEntry();
					} catch (IOException e2) {}
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException e2) {}
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

}
