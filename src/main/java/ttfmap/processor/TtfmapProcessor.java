package ttfmap.processor;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.type.TypeKind.NONE;
import static javax.lang.model.type.TypeKind.NULL;
import static javax.lang.model.type.TypeKind.VOID;
import static javax.tools.Diagnostic.Kind.ERROR;
import static ttfmap.processor.TtfPainter.DEFAULT_RESOURCE_BASE_DIR;

import java.awt.FontFormatException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import sun.reflect.generics.tree.ClassTypeSignature;
import ttfmap.FontMap;
import ttfmap.FontStore;
import ttfmap.MappedFont;
import ttfmap.annotation.Ttfmap;
import ttfmap.processor.exception.AptException;
import ttfmap.processor.exception.CannotMakeImageOutputDirException;
import ttfmap.processor.exception.ImageOutputDirIsInvalidException;
import ttfmap.processor.exception.IsNotInterfaceTargetException;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
//@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("ttfmap.annotation.Ttfmap")
public class TtfmapProcessor extends AbstractProcessor {
	private static final String			RESOURCE_OUTPUT_DIR_PREFIX = "src/main/resources/";
	
	public static final String				CLASS_APPENDIX = "Store";
	private static final Charset			UTF8 = Charset.forName("utf-8");
	/*
	private static final Set<String>		PRIMITIVE_TYPES = SetUtils.asSet("int", "long", "double", "float", "boolean", "byte");
	private static final Set<String>		PRIMITIVE_WRAPPER_TYPES = SetUtils.asSet(
														Integer.class.getCanonicalName(),
														Long.class.getCanonicalName(),
														Double.class.getCanonicalName(),
														Float.class.getCanonicalName(),
														Boolean.class.getCanonicalName());
	private static final Set<String>		CHUNK_UTILS_TYPES = SetUtils.mergeSet(String.class.getCanonicalName(), PRIMITIVE_TYPES, PRIMITIVE_WRAPPER_TYPES);
	*/
	
	private Map<String, TypeMirror>		unboxedPrimitiveTypeMirrors;
	
	private int								maxCharacterRegistration = 500;
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//		prepareUnboxedPrimitiveTypeMirrors();
		for (TypeElement annotation: annotations) {
			for (TypeElement entityElement: ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(annotation))) {
				try {
					JavaFileObject	javaFile;
					PrintWriter		pw = null;
					SourceBuffer		source = processClass(entityElement);
					
					try {
						javaFile = processingEnv.getFiler().createSourceFile(source.getClassName(), entityElement);
						pw = new PrintWriter(new OutputStreamWriter(javaFile.openOutputStream(), UTF8));
						
						pw.print(source.toString());
					} finally {
						if (pw != null) {
							pw.flush();
							pw.close();
						}
					}
				} catch(AptException e) {
					String		message = (e.getMessage() == null)? e.getClass().getCanonicalName(): e.getMessage();

					processingEnv.getMessager().printMessage(ERROR, message, entityElement);

					throw e;
				} catch(Exception e) {
					String		message = (e.getMessage() == null)? e.getClass().getCanonicalName(): e.getMessage();

					e.printStackTrace();
					processingEnv.getMessager().printMessage(ERROR, message, entityElement);
					throw AptException.as(e);
				}
			}
		}

		return true;
	}

	/*
	private void prepareUnboxedPrimitiveTypeMirrors() {
		unboxedPrimitiveTypeMirrors = new HashMap<String, TypeMirror>();
		unboxedPrimitiveTypeMirrors.put(Integer.class.getCanonicalName(), toTypeMirror(int.class));
		unboxedPrimitiveTypeMirrors.put(Long.class.getCanonicalName(), toTypeMirror(long.class));
		unboxedPrimitiveTypeMirrors.put(Float.class.getCanonicalName(), toTypeMirror(float.class));
		unboxedPrimitiveTypeMirrors.put(Double.class.getCanonicalName(), toTypeMirror(double.class));
		unboxedPrimitiveTypeMirrors.put(Short.class.getCanonicalName(), toTypeMirror(short.class));
		unboxedPrimitiveTypeMirrors.put(Boolean.class.getCanonicalName(), toTypeMirror(boolean.class));
	}
	*/
	
	private SourceBuffer processClass(TypeElement entityElement) throws IOException, FontFormatException {
		if (!entityElement.getKind().isInterface()) {
			//	対象のクラスはインターフェースでなければならない
			throw new IsNotInterfaceTargetException(entityElement.getQualifiedName() + " must be interface.");
		}

		Ttfmap			annotation = entityElement.getAnnotation(Ttfmap.class);
		TtfPainter	ttfPainter = new TtfPainter();

		ttfPainter.setPadding(annotation.padding());
		ttfPainter.setCharactersDir(annotation.charactersDir());
		ttfPainter.setResourceBaseDir(annotation.resourceBaseDir());
		
		String		packageDirs = processingEnv.getElementUtils().getPackageOf(entityElement).toString();
		
		packageDirs = packageDirs.replace('.', File.separatorChar);
		ttfPainter.setPackageDirs(packageDirs);
		
		FontMap		fontMap = ttfPainter.paint(annotation.fontPath(), annotation.fontSize());

		/*
		boolean		isInterface = true;
		
		if (entityElement.getKind().isInterface()) {
			//	インターフェースとして扱う
		} else if ((entityElement.getKind().isClass()) && (entityElement.getModifiers().contains(Modifier.ABSTRACT))) {
			//	クラスとして扱う
			//	TODO テストケースを書く
			isInterface = false;
		} else {
			//	インターフェース、クラス以外はエラーとする
			throw new IllegalClassException(entityElement.getQualifiedName());
		}
		validateClass(entityElement);

		Pair<List<BinaryField>, List<ExecutableElement>> result = parseClass(entityElement, isInterface); 
		
		List<BinaryField>		fields = result.getValue1();
		*/ 



		
		SourceBuffer		source = new SourceBuffer(entityElement, CLASS_APPENDIX);

		source.openClass(entityElement.getTypeParameters(), toTypeElement(FontStore.class.getCanonicalName()));
		
		printStaticFieldFontMap(source);
		printPrepareFontMap(source, fontMap);
		printMethodGetFontMap(source);
		
		/*
		printFields(source, fields);
		printFieldAccessorMethods(source, fields);

		//	クラスの場合は継承するので、書き出さない
		if (isInterface) {
			printOtherMethods(source, result.getValue2());
		}

		printReadMethod(source, entityElement, fields);
		printWriteMethod(source, fields);
		*/
		source.closeClass();
		
		return source;
	}

	private void printStaticFieldFontMap(SourceBuffer source) {
		source.importClass(toTypeElement(FontMap.class.getCanonicalName()));
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
		SourceBuffer			configMethodSource = null;
		Map<String, Class>	configMethodArguments = new HashMap<String, Class>();
		int						configMethodIndex = 0;
		int						count = 0;

		configMethodArguments.put("map", FontMap.class);
		
		source.importClass(toTypeElement(MappedFont.class.getCanonicalName()));
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
		source.importClass(toTypeElement(FontMap.class.getCanonicalName()));
		source.openMethod("getFontMap", FontMap.class.getSimpleName(), new HashMap<String, Class>(), "protected", false);
		source.println("return map;");
		source.closeMethod();
	}

	/**
	 *	指定の TypeElement の持つアクセサメソッドから、フィールドを取り出す。
	 * @param isInterface TODO
	 */
	/*
	private Pair<List<BinaryField>, List<ExecutableElement>> parseClass(TypeElement entityElement, boolean isInterface) {
		List<BinaryField>			fields = new ArrayList<BinaryField>();
		Set<ExecutableElement>		otherMethods = new LinkedHashSet<ExecutableElement>();
		Map<String, MethodInfo>		getters = new HashMap<String, MethodInfo>();
		Map<String, MethodInfo>		setters = new HashMap<String, MethodInfo>();
		boolean						hasRead = false;
		boolean						hasWrite = false;

		//	すべてのメソッドを取り出す
		List<? extends Element>		allMethods = mergeAllEnqlosedElements(entityElement);

		//	アクセサメソッドを取り出す
		for (Element element: allMethods) {
			if ((element.getKind() != METHOD) || (!(element instanceof ExecutableElement))) {
				continue;
			}
			//	クラスが対象の場合は abstract メソッドのみ対象とする
			if (!isInterface) {
				if (!entityElement.getModifiers().contains(Modifier.ABSTRACT)) {
					continue;
				}
			}
			
			ExecutableElement		methodElement = (ExecutableElement)element;
			
			//	read, write メソッドかを確認する
			if ((!hasRead) && (parseRead(entityElement, methodElement))) {
				hasRead = true;
				continue;
			} else if ((!hasWrite) && (parseWrite(methodElement))) {
				hasWrite = true;
				continue;
			}
			
			//	アクセサメソッドかを確認する
			MethodInfo				getter = parseGetter(methodElement);
			MethodInfo				setter = parseSetter(methodElement);
			BinaryField				field = null;
//			MethodInfo				setter = parseAccessorField("set", toTypeMirror(void.class), toTypeMirror(String.class), methodElement);

//			System.out.println(methodElement);
			
			if (getter != null) {
				if ((field = matchAccrssor(getter, setters)) == null) {
					//	getter の一覧に加える
					getters.put(getter.getName(), getter);
				} else {
					//	すでに setter が見つかっていれば、バイナリ変換対象とする
					fields.add(field);
				}
			} else if (setter != null) {
				if ((field = matchAccrssor(setter, getters)) == null) {
					//	setter の一覧に加える
					setters.put(setter.getName(), setter);
				} else {
					//	すでに getter が見つかっていれば、バイナリ変換対象とする
					fields.add(field);
				}
			} else {
				otherMethods.add(methodElement);
			}
		}
		
		if (!hasRead) {
			throw new NoReadMethodException(
					entityElement.getQualifiedName().toString(),
//					entityElement.getQualifiedName().toString(),
					ByteBuffer.class.getCanonicalName(),
					IOException.class.getCanonicalName());
		} else if (!hasWrite) {
			throw new NoWriteMethodException(
					entityElement.getQualifiedName().toString(),
					ByteArrayOutputStream.class.getCanonicalName(),
					IOException.class.getCanonicalName());
		}
		
		//	マッチしなかったメソッドは変換対象として、実装のみ作成する
		for (MethodInfo getter: getters.values()) {
			otherMethods.add(getter.getMethod());
		}
		for (MethodInfo setter: setters.values()) {
			otherMethods.add(setter.getMethod());
		}

		List<ExecutableElement>		otherMethodsList = new ArrayList<ExecutableElement>(otherMethods);

		Collections.sort(otherMethodsList, new MethodComparator());

		return new Pair<List<BinaryField>, List<ExecutableElement>>(fields, otherMethodsList);
	}
	*/

	/**
	 *	Getter, Setter が揃っている場合、変換対象のフィールドとする
	 */
	/*
	private BinaryField matchAccrssor(MethodInfo method, Map<String, MethodInfo> theOthers) {
		MethodInfo		theOther = theOthers.get(method.getName());
		
		if (theOther == null) {
			return null;
		}
		
		if (!isSameType(method.getType(), theOther.getType())) {
			return null;
		}

		theOthers.remove(theOther.getName());
		
		//	アノテーションをマージして保持する
		Set<Annotation>	annotations = new HashSet<Annotation>(method.getAnnotations());
		
		annotations.addAll(theOther.getAnnotations());
		
		//	BinaryField を生成する
		if (method.isGetter()) {
			return new BinaryField(this, method.getName(), method.getMethod(), theOther.getMethod(), method.getType(), annotations);
		} else {
			return new BinaryField(this, method.getName(), theOther.getMethod(), method.getMethod(), method.getType(), annotations);
		}
	}

	private void printFields(SourceBuffer source, List<BinaryField> fields) {
		source.println("// バイナリデータ用のフィールド");
		for (BinaryField field: fields) {
			source.println("private %s %s;",  field.getType().toString(), field.getName());
		}
		source.println();
	}
	*/

//	private void printFieldAccessorMethods(SourceBuffer source, List<BinaryField> fields) {
//		source.println("/** バイナリデータ用のアクセサメソッド **/");
//		for (BinaryField field: fields) {
//			source.openMethod(field.getGetterMethod(), true);
//			source.println("return this.%s;", field.getName());
//			source.closeMethod();
//			source.println();
//			
//			source.openMethod(field.getSetterMethod(), true);
//			source.println("this.%s = %s;", field.getName(), field.getSetterMethod().getParameters().get(0).getSimpleName());
//			source.closeMethod();
//			source.println();
//		}
//	}

//	private void printOtherMethods(SourceBuffer source, List<ExecutableElement> methods) {
//		source.println("/** 利用しないメソッド **/");
//		for (ExecutableElement method: methods) {
//			source.openMethod(method, true);
//			source.printUnsupportedException();
//			source.closeMethod();
//			source.println();
//		}
//		source.println();
//	}
	
//	private void printReadMethod(SourceBuffer source, TypeElement typeElement, List<BinaryField> fields) {
//		source.println("/**");
//		source.println(" * このインスタンスをバイナリデータから読み込む");
//		source.println(" */");
//		source.println("@SuppressWarnings(\"unchecked\")");
//		source.openMethod("public int read(%s buff) throws %s {", ByteBuffer.class.getCanonicalName(), IOException.class.getCanonicalName());
//
//		source.println("%s length = %s.readLength(buff);", ByteLength.class.getCanonicalName(), ByteLength.class.getCanonicalName());
//		source.startIf("length.getRemains() < 0");
//		source.println("//\tサイズが -1 なら -1 を返す");
//		source.println("return -1;");
//		source.startElseIf("length.isEmpty()");
//		source.println("return 0;");
//		source.endIf();
//		int[][]	a = new int[][] {};
//
//		source.println();
//		
//		for (BinaryField field: fields) {
////			TypeMirror	memberType = processingEnv.getTypeUtils().getDeclaredType(containing, typeElem, typeArgs);
//			source.println("// %s の読み込み", field.getName());
//			printReadObject(source, "this." + field.getName(), field.getType(), new Stack<String>(), /*field.getSetterValidator(),*/ field.getAnnotations());
//		}
//		source.println();
//		
//		source.println("length.assertEmpty();");
//		source.println("return length.getLength();");
//		source.closeMethod();
//		source.println();
//	}
	
	/*
	void printReadObject(SourceBuffer source, String name, TypeMirror type, Stack<String> arraySizeStack, Map<Class<? extends Annotation>, Annotation> annotations) {
		String					typeString = toRawTypeString(type);
		CollectionProcessor		collectionProcessor = null;
		Annotation				binary = null;
		
		if (type instanceof ArrayType) {
			//	配列は要素数を読んでから、その要素分各要素を読み込む
			TypeMirror		componentType = ((ArrayType)type).getComponentType();
			String			count = source.newVariable();
			String			loopCursor;

			source.println();
			source.println("int %s = length.readInt();", count);
//			source.println("%s = new %s[%s];", name, componentType.toString(), count);
			loopCursor = source.startFor(count);
			arraySizeStack.add(count);
			if (!(componentType instanceof ArrayType)) {
				String		orgName = toArrayComponentString(name);
				String		arrayStructure = "";

				for (String arraySize: arraySizeStack) {
					arrayStructure += String.format("[%s]", arraySize);
				}
				
				source.println("if (%s == null) {", orgName);
				source.println("\t%s = new %s%s;", orgName, toArrayComponentString(componentType), arrayStructure);
				source.println("}");
			}
			printReadObject(source, String.format("%s[%s]", name, loopCursor), componentType, arraySizeStack, null);
			source.endFor();
			source.println();
		} else if ((collectionProcessor = CollectionProcessor.of(typeString)) != null) {
			//	Collection は要素数を書いてから、各要素を書き出す
			List<? extends TypeMirror>	typeArguments = ((DeclaredType)type).getTypeArguments();

			collectionProcessor.processRead(this, source, name, type, typeArguments);
		} else if ((parseAnnotation(type, Binary.class)) != null) {
			String			size = source.newVariable();

			source.println("%s = new %sImpl();", name, typeString);
			source.println("int %s = %s.read(buff);", size, name);

			source.startIf("%s < 0", size);
			source.println("//\tサイズが -1 なら、null として読み込む");
			source.println("%s = null;", name);
			source.println("length.decrease(%s.INT_SIZE);", ChunkUtils.class.getCanonicalName());
			source.startElse();
			source.println("length.decrease(%s);", size);
			source.endIf();
		} else {
			printReadVariable(source, name, type, annotations);
		}
	}
	
	private void printReadVariable(SourceBuffer source, String name, TypeMirror type, Map<Class<? extends Annotation>, Annotation> annotations) {
		String		typeString = type.toString(); 
		String		readMethod;
		String		methodSourceDecoration = "%s";

		if (PRIMITIVE_TYPES.contains(typeString)) {
//			readMethod = "read" + typeString.substring(0, 1).toUpperCase() + typeString.substring(1);
			readMethod = "read" + StringUtils.capitalize(typeString);
//			typeClass = TypeUtils.getPrimitiveClass(typeString);
		} else if (PRIMITIVE_WRAPPER_TYPES.contains(typeString)) {
			Class	typeClass = TypeUtils.toPrimitiveClass(typeString);
			readMethod = "read" + StringUtils.capitalize(typeClass.toString());
		} else if (String.class.getCanonicalName().equals(typeString)) {
			readMethod = "readString";
//			typeClass = String.class;
		} else {
//			typeClass = getTargetAsEnumVariable(typeString);
			BinarizeEnum	binarizeEnum;
			
			if (checkAnnotation(annotations, OrdinalEnum.class) != null) {
				//	@OrdinalEnum がある場合は、Enum 名ではなく序数でバイナリ化を行う
				readMethod = "readInt";
				methodSourceDecoration = String.format("%s.values()[%%s]", typeString);
			} else if (((binarizeEnum = checkAnnotation(annotations, BinarizeEnum.class)) != null) ||
					((binarizeEnum = parseAnnotation(type, BinarizeEnum.class)) != null)) {
				//	@BinarizeIdEnum がある場合は、Enum 名ではなく指定のメソッドでバイナリ化を行う

				//	バイナリ化する際のプリミティブ型をチェックする
				TypeKind	typeKind = getBinirizedTypeKind(binarizeEnum);
				String		typeName = typeKind.name().toLowerCase();

				readMethod = "read" + StringUtils.capitalize(typeName);
				
				//	バイナリ化する際のメソッドをチェックする
				if (!hasMethod(type, binarizeEnum.unbinrizedMethod(), toTypeMirror(typeName), type, true)) {
					throw new InvalidBinarizeEnumException("@BinarizeEnum.unbinrizedMethod() で指定されたメソッド %s %s(%s) がありません。", typeString, binarizeEnum.unbinrizedMethod(), typeName);
				}
//				Method		unbinrizedMethod = null;
//				try {
//					Class		paramClass = TypeUtils.getPrimitiveClass(typeName);
//
//					if ((unbinrizedMethod = typeClass.getMethod(binarizeEnum.unbinrizedMethod(), paramClass)) == null) {
//						throw new InvalidBinarizeEnumException("@BinarizeEnum.unbinrizedMethod() で指定されたメソッド %s(%s) がありません。", binarizeEnum.unbinrizedMethod(), typeName);
//					}
//				} catch(Exception e) {
//					throw new InvalidBinarizeEnumException("@BinarizeEnum.unbinrizedMethod() で指定されたメソッド %s(%s) がありません。", binarizeEnum.unbinrizedMethod(), typeName);
//				}
//				if (unbinrizedMethod.getReturnType() != typeClass) {
//					throw new InvalidBinarizeEnumException("@BinarizeEnum.unbinrizedMethod() で指定されたメソッド %s(%s) が %s を返しません。", binarizeEnum.unbinrizedMethod(), typeName, typeString);
//				}
				methodSourceDecoration = String.format("%s.%s(%%s)", typeString, binarizeEnum.unbinrizedMethod());
			} else {
				readMethod = "readString";
				methodSourceDecoration = String.format("Enum.valueOf(%s.class, %%s)", typeString);
			}
//			throw new UnsupportedOperationException("Unknown type: " + typeString);
		}
		
		String		methodSource = String.format("length.%s()", readMethod);
		
		methodSource = String.format(methodSourceDecoration, methodSource);

		//	@Validation が指定されていれば、バリデーション処理を追加する
		if ((annotations != null) && (annotations.containsKey(Validation.class))) {
			Validator		validator = ((Validation)annotations.get(Validation.class)).value();
			TypeMirror	validatedType = toTypeMirror(validator.getValidatedClass());
			TypeMirror	unboxedType = null;
			
			if (isSameType(type, validatedType)) {
			} else if (((unboxedType = unboxedPrimitiveTypeMirrors.get(type.toString())) != null) && (isSameType(unboxedType, validatedType))) {
			} else {
				throw new ValidationTypeMissMatchException(type, validator);
			}
			methodSource = String.format("%s.%s(%s)", ChunkValidator.class.getCanonicalName(), validator.getValidationMethod(), methodSource);
		}
		
		source.println("%s = %s;", name, methodSource);
	}
	*/

	/*
	private Class getTargetAsEnumVariable(String typeString) {
		Class		typeClass;

		try {
			typeClass = Class.forName(typeString);
//			System.out.println("\t\t find class " + typeString);
		} catch (ClassNotFoundException e) {
			throw new UnsupportedClassException(typeString, String.format("クラス %s が見つかりません。", typeString));
		}
		if (!typeClass.isEnum()) {
			throw new UnsupportedClassException(typeString, String.format("%s は Enum ではありません。", typeString));
		}
		
		return typeClass;
	}
	*/
	
	/*
	private TypeKind getBinirizedTypeKind(BinarizeEnum binarizeEnum) {
		TypeKind	typeKind = null;

		try {
			//	APT で Annotation の Class にアクセスすると MirroredTypeException が発生する
			binarizeEnum.type();
		} catch(MirroredTypeException e) {
			typeKind = e.getTypeMirror().getKind();
		}

		if (typeKind == null) {
			throw new InvalidBinarizeEnumException("@BinarizeEnum.type() にはプリミティブ型のみ指定できます。");
		}
		if (!typeKind.isPrimitive()) {
			throw new InvalidBinarizeEnumException("@BinarizeEnum.type() にはプリミティブ型のみ指定できます。: %s", typeKind.name());
		}
		
		return typeKind;
	}
	
	private boolean hasMethod(TypeMirror targetType, String methodName, TypeMirror parameterType, TypeMirror methodReturnType, boolean isStatic) {
		TypeElement			targetElement = toTypeElement(targetType.toString());
		ExecutableElement	methodElement;

		for (Element element: mergeAllEnqlosedElements(targetElement)) {
			if ((element.getKind() != METHOD) || (!(element instanceof ExecutableElement))) {
				continue;
			}
			
			//	メソッドの名前を確認する
			if (!element.getSimpleName().toString().equals(methodName)) {
				continue;
			}
			
			//	メソッドの修飾子を確認する
			methodElement = (ExecutableElement)element;
			if (isStatic) {
				if (!methodElement.getModifiers().contains(Modifier.STATIC)) {
					continue;
				}
			}
			
			//	メソッドの返り値を確認する
			if (!isSameType(methodElement.getReturnType(), methodReturnType)) {
				continue;
			}
			
			//	メソッドの引数の型と数を確認する
			if (parameterType != null) {
				List<? extends VariableElement>	parameters = methodElement.getParameters();
				
				if ((parameters.size() != 1) || (!isSameType(parameters.get(0).asType(), parameterType))) {
					continue;
				}
			}
			
			return true;
		}
		
		return false;
	}
	*/

//	private void printWriteMethod(SourceBuffer source, List<BinaryField> fields) {
//		source.println("/**");
//		source.println(" * このインスタンスをバイナリデータとして書き出す");
//		source.println(" */");
//		source.println("@SuppressWarnings(\"unchecked\")");
//		source.openMethod("public void write(%s buff) throws %s {", ByteArrayOutputStream.class.getCanonicalName(), IOException.class.getCanonicalName());
//
//		source.println("final %s\tlocalBuff = new %s();", ByteArrayOutputStream.class.getCanonicalName(), ByteArrayOutputStream.class.getCanonicalName());
//		source.println();
//		
//		for (BinaryField field: fields) {
////			TypeMirror	memberType = processingEnv.getTypeUtils().getDeclaredType(containing, typeElem, typeArgs);
//			source.println("// %s の書き出し", field.getName());
//			printWriteObject(source, "this." + field.getName(), field.getType(), field.getAnnotations());
//		}
//		source.println();
//
//		source.println("buff.write(%s.toBytes(localBuff.size()));", ChunkUtils.class.getCanonicalName());
//		source.println("buff.write(localBuff.toByteArray());");
//		
//		source.closeMethod();
//		source.println();
//	}
	
	/*
	void printWriteObject(SourceBuffer source, String name, TypeMirror type, Map<Class<? extends Annotation>, Annotation> annotations) {
		String					typeString = toRawTypeString(type);
		CollectionProcessor		collectionWriter = null;
		Annotation			binary = null;
		
		if (type instanceof ArrayType) {
			//	配列は要素数を書いてから、各要素を書き出す
			TypeMirror	componentType = ((ArrayType)type).getComponentType();
			String			loopCursor;

			source.println();
			source.println("localBuff.write(%s.toBytes(%s.length));", ChunkUtils.class.getCanonicalName(), name);
			loopCursor = source.startFor("%s.length", name);
			printWriteObject(source, String.format("%s[%s]", name, loopCursor), componentType, null);
			source.endFor();
			source.println();
		} else if ((collectionWriter = CollectionProcessor.of(typeString)) != null) {
			//	Collection は要素数を書いてから、各要素を書き出す
			List<? extends TypeMirror>	typeArguments = ((DeclaredType)type).getTypeArguments();
			String							localVariable = source.newVariable();

			source.println("%s %s = %s;", type.toString(), localVariable, name);

			collectionWriter.processWrite(this, source, localVariable, type, typeArguments);
		} else if ((parseAnnotation(type, Binary.class)) != null) {
			source.startIf("%s == null", name);
			source.println("//\tフィールドが null ならば、-1 を書き込む");
			source.println("localBuff.write(%s.toBytes(-1));", ChunkUtils.class.getCanonicalName());
			source.startElse();
			source.println("%s.write(localBuff);", name);
			source.endIf();
		} else {
			printWriteVariable(source, name, type, annotations);
		}
	}
	
	private void printWriteVariable(SourceBuffer source, String name, TypeMirror type, Map<Class<? extends Annotation>, Annotation> annotations) {
		String		typeString = type.toString(); 

		if (CHUNK_UTILS_TYPES.contains(typeString)) {
			source.println("localBuff.write(%s.toBytes(%s));", ChunkUtils.class.getCanonicalName(), name);
		} else {
			//	対象として適切な Enum かを確認する
//			Class	typeClass = getTargetAsEnumVariable(typeString);
			BinarizeEnum	binarizeEnum;
			
			if (checkAnnotation(annotations, OrdinalEnum.class) != null) {
				//	@OrdinalEnum がある場合は、Enum 名ではなく序数でバイナリ化を行う
				source.println("localBuff.write(%s.toBytes(%s.ordinal()));", ChunkUtils.class.getCanonicalName(), name);
			} else if (((binarizeEnum = checkAnnotation(annotations, BinarizeEnum.class)) != null) ||
					((binarizeEnum = parseAnnotation(type, BinarizeEnum.class)) != null)) {
				//	@BinarizeIdEnum がある場合は、Enum 名ではなく指定のメソッドでバイナリ化を行う
				
				//	バイナリ化する際のプリミティブ型をチェックする
				TypeKind	typeKind = getBinirizedTypeKind(binarizeEnum);
				String		typeName = typeKind.name().toLowerCase();
				
				//	バイナリ化する際のメソッドをチェックする
				if (!hasMethod(type, binarizeEnum.binrizedMethod(), null, toTypeMirror(typeName), false)) {
					throw new InvalidBinarizeEnumException("@BinarizeEnum.binrizedMethod() で指定されたメソッド %s %s() がありません。", typeName, binarizeEnum.binrizedMethod());
				}
//				Method		binrizedMethod = null;
//				Class		returnClass = null;
//
//				try {
//					if ((binrizedMethod = typeClass.getMethod(binarizeEnum.binrizedMethod())) == null) {
//						throw new InvalidBinarizeEnumException("@BinarizeEnum.binrizedMethod() で指定されたメソッド %s() がありません。", binarizeEnum.binrizedMethod());
//					}
//					returnClass = TypeUtils.getPrimitiveClass(typeName);
//				} catch(Exception e) {
//					throw new InvalidBinarizeEnumException("@BinarizeEnum.binrizedMethod() で指定されたメソッド %s() がありません。", binarizeEnum.binrizedMethod());
//				}
//				if (binrizedMethod.getReturnType() != returnClass) {
//					throw new InvalidBinarizeEnumException("@BinarizeEnum.binrizedMethod() で指定されたメソッド %s() が %s を返しません。", binarizeEnum.binrizedMethod(), typeName);
//				}
				source.println("localBuff.write(%s.toBytes(%s.%s()));", ChunkUtils.class.getCanonicalName(), name, binarizeEnum.binrizedMethod());
			} else {
				source.println("localBuff.write(%s.toBytes(%s.name()));", ChunkUtils.class.getCanonicalName(), name);
			}
//			throw new UnsupportedOperationException("Unknown type: " + typeString);
		}
	}
	
	private <T extends Annotation> T checkAnnotation(Map<Class<? extends Annotation>, Annotation> annotations, Class<T> clazz) {
		if (annotations == null) {
			return null;
		}
		
		return (T)annotations.get(clazz);
	}

	private <T extends Annotation> T parseAnnotation(TypeMirror type, Class<? extends Annotation> clazz) {
		if (type.getKind().isPrimitive()) {
			return null;
		} else {
			Element		element = processingEnv.getTypeUtils().asElement(type);
			Annotation		annotation = element.getAnnotation(clazz);

			return (T)annotation;
		}
	}
	
	protected void validateClass(TypeElement interfaceElement) {
//		if (!interfaceElement.getTypeParameters().isEmpty()) {
//			throw new HasTypeParametersException(interfaceElement.getQualifiedName());
//		}
		
		if (interfaceElement.getNestingKind().isNested()) {
			throw new NestedInterfaceException(interfaceElement.getQualifiedName());
		}
	}

	private MethodInfo parseGetter(ExecutableElement element) {
		TypeMirror							methodReturnType = element.getReturnType();
		List<? extends VariableElement>	methodArgTypes = element.getParameters();

		if (isSameType(methodReturnType, getVoidType())) {
			return null;
		}

		if (methodArgTypes.size() != 0) {
			return null;
		}
		
		//	ゲッターの名前は boolean 型のみ isXxx() とする
		String		methodPrefix = "get";

		if (isSameType(methodReturnType, toTypeMirror(boolean.class))) {
			methodPrefix = "is";
		}

		//	名前がゲッターか確認する
		String		name = getAccessorFieldName(methodPrefix, element);

		if (StringUtils.isEmpty(name)) {
			return null;
		}

		return new MethodInfo(name, element, methodReturnType, true, getAccessorAnnotations(element));
	}

	private MethodInfo parseSetter(ExecutableElement element) {
		String		name = getAccessorFieldName("set", element);

		if (StringUtils.isEmpty(name)) {
			return null;
		}

		TypeMirror							methodReturnType = element.getReturnType();
		List<? extends VariableElement>	methodArgTypes = element.getParameters();

		if (!isSameType(methodReturnType, getVoidType())) {
			return null;
		}

		if (methodArgTypes.size() != 1) {
			return null;
		}

//		//	セッターでは @Validation があるか確認する
//		Validation							validation = element.getAnnotation(Validation.class);
//		Validator								validator = (validation == null)? null: validation.value();
		
		return new MethodInfo(name, element, methodArgTypes.get(0).asType(), false, getAccessorAnnotations(element));
	}
	
	private Set<Annotation> getAccessorAnnotations(ExecutableElement element) {
		Set<Annotation>							annotations = new HashSet<Annotation>();
		List<Class<? extends Annotation>>		classes = new ArrayList<Class<? extends Annotation>>();
		
		classes.add(Validation.class);
		classes.add(OrdinalEnum.class);
		classes.add(BinarizeEnum.class);

		for (Class<? extends Annotation> clazz: classes) {
			Annotation	annotation = element.getAnnotation(clazz);
			
			if (annotation != null) {
				annotations.add(annotation);
			}
		}
		
		return annotations;
	}

	private boolean parseRead(TypeElement entityElement, ExecutableElement element) {
		String									methodName = element.getSimpleName().toString();
		TypeMirror								methodReturnType = element.getReturnType();
		List<? extends VariableElement>	methodArgTypes = element.getParameters();
		List<? extends TypeMirror>			mthodThrows = element.getThrownTypes();

		return ((methodName.equals("read")) &&
				(isSameType(methodReturnType, toTypeMirror(int.class))) &&
				(methodArgTypes.size() == 1) &&
				(isSameType(methodArgTypes.get(0).asType(), toTypeMirror(ByteBuffer.class))) &&
				(mthodThrows.size() == 1) &&
				(isSameType(mthodThrows.get(0), toTypeMirror(IOException.class))));
	}
	
	private boolean parseWrite(ExecutableElement element) {
		String									methodName = element.getSimpleName().toString();
		TypeMirror								methodReturnType = element.getReturnType();
		List<? extends VariableElement>	methodArgTypes = element.getParameters();
		List<? extends TypeMirror>			mthodThrows = element.getThrownTypes();

		return ((methodName.equals("write")) &&
				(isSameType(methodReturnType, getVoidType())) &&
				(methodArgTypes.size() == 1) &&
				(isSameType(methodArgTypes.get(0).asType(), toTypeMirror(ByteArrayOutputStream.class))) &&
				(mthodThrows.size() == 1) &&
				(isSameType(mthodThrows.get(0), toTypeMirror(IOException.class))));
	}
	
	private List<? extends Element> mergeAllEnqlosedElements(TypeElement entityElement) {
		List<Element>		all = new ArrayList<Element>();
		
		all.addAll(entityElement.getEnclosedElements());
		
		for (TypeMirror typeMirror: entityElement.getInterfaces()) {
			Element		element = processingEnv.getTypeUtils().asElement(typeMirror);
			
			all.addAll(element.getEnclosedElements());
		}
		
		TypeElement		typeElement = entityElement;

		while (true) {
			TypeMirror	superClass = typeElement.getSuperclass();
			Element		element = processingEnv.getTypeUtils().asElement(superClass);
			
			if ((element == null) || (!(element instanceof TypeElement))) {
				break;
			}
			typeElement = (TypeElement)element;

			all.addAll(typeElement.getEnclosedElements());
		}

		return all;
	}

	private String getAccessorFieldName(String accessorPrefix, ExecutableElement element) {
		String			name = element.getSimpleName().toString();
		int				p = accessorPrefix.length();
		
		if (!name.startsWith(accessorPrefix)) {
			return null;
		}

		name = name.substring(p, p + 1).toLowerCase() + name.substring(p + 1);
		
		if (PRIMITIVE_TYPES.contains(name)) {
			name += "Value";
		}
		
		return name;
	}
	*/
	
	/*
	private TableData getData(Element classElement, ProcessingEnvironment processingEnv) {
		TableData	data = new TableData();
		String		packageName = AptUtils.getPackageName(classElement, processingEnv);
		
		data.setSrcPackageName(packageName);
		data.setSrcSimpleName(classElement.getSimpleName().toString());
		data.setDestPackageName(packageName);
		data.setDestSimpleName(data.getSrcSimpleName() + "Dao");

		for (Element element : classElement.getEnclosedElements()) {
			// フィールドのみ対象
			if (element.getKind() != ElementKind.FIELD) {
				continue;
			}
			// Excludeアノテーションの付いているフィールドは無視
			if (element.getAnnotation(Exclude.class) != null) {
				continue;
			}
			String type = element.asType().toString();
			String name = element.getSimpleName().toString();
			data.addField(type, name);
		}

		return data;
	}
	*/
	
	/*
	public NoType getNoneType() {
		return processingEnv.getTypeUtils().getNoType(TypeKind.NONE);
	}

	public NoType getVoidType() {
		return processingEnv.getTypeUtils().getNoType(TypeKind.VOID);
	}
	*/

	public TypeElement toTypeElement(String clazz) {
		assertNotNull(clazz, processingEnv);
		
		return processingEnv.getElementUtils().getTypeElement(clazz);
	}
	
	public TypeMirror toTypeMirror(Class<?> clazz) {
		return toTypeMirror(clazz.getCanonicalName());
	}

	public TypeMirror toTypeMirror(String clazz) {
		TypeKind	typeKind = null;
		
		for (TypeKind type: TypeKind.values()) {
			if (type.name().equalsIgnoreCase(clazz)) {
				typeKind = type;
				break;
			}
		}
		
		if (typeKind != null) {
			if (typeKind.isPrimitive()) {
				return processingEnv.getTypeUtils().getPrimitiveType(typeKind);
			} else {
				System.out.println("not implemented");
			}
		}

		TypeElement	typeElement = toTypeElement(clazz);

		if (typeElement == null) {
			return null;
		} else {
			return typeElement.asType();
		}
	}

	/*
	public boolean isSameType(TypeMirror type1, TypeMirror type2) {
		assertNotNull(type1, type2, processingEnv);

		if ((type1.getKind() == NONE) || (type2.getKind() == NONE)) {
			return false;
		}

		if ((type1.getKind() == NULL) || (type1.getKind() == VOID) ||
				(type2.getKind() == NULL) || (type2.getKind() == VOID)) {
			return (type1.getKind() == type2.getKind());
		}

		TypeMirror	eraasuredType1 = processingEnv.getTypeUtils().erasure(type1);
		TypeMirror	eraasuredType2 = processingEnv.getTypeUtils().erasure(type2);

		return eraasuredType1.equals(eraasuredType2);
	}
	
	public String toRawTypeString(TypeMirror type) {
		String	string = type.toString();
		
		string = string.replaceAll("<[\\w\\.,<>]+>", "");
		
		return string;
	}
	
	public String toArrayComponentString(TypeMirror type) {
		return toArrayComponentString(type.toString());
	}
	public String toArrayComponentString(String string) {
		string = string.replaceAll("\\[[\\w]*\\]", "");
		
		return string;
	}
	 */

	public void assertNotNull(Object... targets) {
		int		i = 0;
		for (Object target: targets) {
			if (target == null) {
				throw new AssertionError(String.format("Argument %d is null", i));
			}
			i++;
		}
	}
	
	public static boolean isDebug() {
		String		value = System.getProperty("processor.dump");
		Boolean	isDebug = Boolean.parseBoolean(value);
		
		return isDebug.booleanValue();
	}

	/*
	class MethodInfo {
		private final String				name;
		private final ExecutableElement	method;
		private final TypeMirror			type;
		private final boolean				isGetter;
//		private final Validator				validator;
		private final Set<Annotation>		annotations;

		public MethodInfo(String name, ExecutableElement method, TypeMirror type, boolean isGetter) {
			this(name, method, type, isGetter, null);
		}
		public MethodInfo(String name, ExecutableElement method, TypeMirror type, boolean isGetter, Set<Annotation> annotations) {
			if (StringUtils.isEmpty(name)) {
				throw new IllegalArgumentException("name is empty");
			}
			
			this.name = name;
			this.method = method;
			this.type = type;
			this.isGetter = isGetter;
//			this.validator = validator;
			this.annotations = annotations;
		}

		public String getName() {
			return name;
		}

		public ExecutableElement getMethod() {
			return method;
		}

		public TypeMirror getType() {
			return type;
		}

		public boolean isGetter() {
			return isGetter;
		}
		
		public Set<Annotation> getAnnotations() {
			return annotations;
		}
	}
	*/

	/*
	class BinaryField {
		private final String					name;
		private final ExecutableElement		getterMethod;
		private final ExecutableElement		setterMethod;
//		private final Validator					setterValidator;
		private final TypeMirror				type;
		private final Map<Class<? extends Annotation>, Annotation>	annotations;

		public BinaryField(BinaryProcessor processor, String name, ExecutableElement getterMethod, ExecutableElement setterMethod, TypeMirror type, Set<Annotation> annotations) {
			this.name = name;
			this.getterMethod = getterMethod;
			this.setterMethod = setterMethod;
//			this.setterValidator = setterValidator;
			this.type = type;
			this.annotations = new HashMap<Class<? extends Annotation>, Annotation>();
			
			for (Annotation annottation: annotations) {
				this.annotations.put(annottation.annotationType(), annottation);
			}
		}

		public String getName() {
			return name;
		}

		public ExecutableElement getGetterMethod() {
			return getterMethod;
		}

		public ExecutableElement getSetterMethod() {
			return setterMethod;
		}

		public TypeMirror getType() {
			return type;
		}

		public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
			return annotations;
		}
	}
	*/

}
