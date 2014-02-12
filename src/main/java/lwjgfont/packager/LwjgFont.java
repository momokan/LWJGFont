package lwjgfont.packager;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lwjgfont.FontMap;
import lwjgfont.FontStore;
import lwjgfont.MappedFont;

import static javax.tools.JavaFileObject.Kind.SOURCE;
import static javax.tools.StandardLocation.SOURCE_PATH;
import static lwjgfont.packager.LwjgFontPropertyKey.CHARACTER_FILE_DIR;
import static lwjgfont.packager.LwjgFontPropertyKey.IMAGE_CHARACTER_PADDING;
import static lwjgfont.packager.LwjgFontPropertyKey.RESOURCE_BASE_DIR;
import static lwjgfont.packager.LwjgFontPropertyKey.ARTIFACT_NAME;
import static lwjgfont.packager.LwjgFontPropertyKey.ARTIFACT_VERSION;

public class LwjgFont {
	public static final String			DEFAULT_TEMP_DIR = "temp/";
	private static final String			SOURCE_DIR = "src";
	public static final String			RESOURCE_DIR = "resources";
	private static final String			COMPILES_DIR = "target";
	
	private LwjgFontProperties	properties;
	
	private int					maxCharacterRegistration = 500;
	
	public LwjgFont(String propertiesPath) throws IOException {
		properties = LwjgFontProperties.load(propertiesPath);
	}
	
	public void process(String fontPath, int fontSize) throws IOException, FontFormatException {
		String			baseDir = properties.getAsString(RESOURCE_BASE_DIR);
		String			srcDir = LwjgFontUtil.prepareDirectory(baseDir, SOURCE_DIR).getPath();
		String			resourceDir = LwjgFontUtil.prepareDirectory(baseDir, RESOURCE_DIR).getPath();
		SourceBuffer	sourceBuffer = processClass(fontPath, fontSize, baseDir);
		
		writeJavaSource(sourceBuffer, srcDir);
		
		SourceCompiler	sourceCompiler = new SourceCompiler();
		
		sourceCompiler.setSourceDir(srcDir);
		sourceCompiler.setResourceDir(resourceDir);
		sourceCompiler.setTargetDir(LwjgFontUtil.prepareDirectory(baseDir, COMPILES_DIR).getPath());
		sourceCompiler.compile(sourceBuffer.getCannonicalClassName());
		
		PackagedResources	packagedResource = new PackagedResources();
		
		packagedResource.addReplacePatterns(properties, ARTIFACT_NAME, ARTIFACT_VERSION);
		packagedResource.setResourcesDir(resourceDir);
		packagedResource.copy();
	}

	private SourceBuffer processClass(String fontPath, int fontSize, String baseDir) throws IOException, FontFormatException {
		FontMapPainter	fontMapPainter = new FontMapPainter();
		File			fontFile = new File(fontPath);
		String			packageName = FontStore.class.getPackage().getName();
		String			packageDirs = packageName.replace('.', File.separatorChar);

		fontMapPainter.setWriteImage(false);
		fontMapPainter.setPadding(properties.getAsInt(IMAGE_CHARACTER_PADDING));
		fontMapPainter.setCharactersDir(properties.getAsString(CHARACTER_FILE_DIR));
		fontMapPainter.setResourceBaseDir(baseDir);
		fontMapPainter.setPackageDirs(packageDirs);
		
		FontMap			fontMap = fontMapPainter.paint(fontPath, fontSize);
		SourceBuffer	source = new SourceBuffer(packageName);

		source.openClass(toFontClassName(fontFile.getName()), null, FontStore.class);

		printStaticFieldFontMap(source);
		printPrepareFontMap(source, fontMap);
		printMethodGetFontMap(source);
		
		source.closeClass();
		
		return source;
	}

	private void printStaticFieldFontMap(SourceBuffer source) {
		source.importClass(FontMap.class);
		source.println(
				"private static final %s map = new %s();",
				FontMap.class.getSimpleName(),
				FontMap.class.getSimpleName()
		);
		source.println();
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

	private String toFontClassName(String fontName) {
		fontName = LwjgFontUtil.trimExtention(fontName);
		
		String		className = "";
		String[]	tokens = fontName.split("[^0-9a-zA-Z]+");
		for (String token: tokens) {
			className += LwjgFontUtil.capitalize(token);
		}
		className += "Font";
		
		return className;
	}

	public static void main(String[] args) throws IOException, FontFormatException {
		String		propertiesPath = null;
		
		if ((args != null) && (1 <= args.length)) {
			propertiesPath = args[0];
		}
		
		LwjgFont	lwjgFont = new LwjgFont(propertiesPath);
		
		lwjgFont.process("sample/migu-1p-regular.ttf", 30);
	}
}
