package lwjgfont.packager;

import static javax.lang.model.element.Modifier.ABSTRACT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

public class SourceBuffer {
	private StringBuilder		buff;
	private String				packageName;
	private String				className;
//	private String				classAppendix;
	private List<String>		importedClasses;
	private int					indent;
	private int					forCount;
	private int					variableCount;

	public SourceBuffer() {
		this(null);
	}
	public SourceBuffer(String packageName) {
		this.packageName = packageName;
		this.className = null;
		this.buff = new StringBuilder();
		this.importedClasses = new ArrayList<String>();
		this.indent = 0;
		this.forCount = 0;
	}
	
	public void openClass(String className, List<? extends TypeParameterElement> typeParameters, Object superClass, Object ...interfaceClasses) {
		Line		line = new Line();
		String		typeParametersString = "";
		
		if ((typeParameters != null) && (0 < typeParameters.size())) {
			for (TypeParameterElement typeParameter: typeParameters) {
				if (0 < typeParametersString.length()) {
					typeParametersString += ", ";
				}
				typeParametersString += typeParameter.toString();
			}
			typeParametersString = String.format("<%s>", typeParametersString);
		}

		line.append("public class %s%s ", className, typeParametersString);
		if (this.className == null) {
			this.className = className;
		}
		
		if (superClass != null) {
			String		superClassName = toClassName(superClass);
			
			line.append("extends %s ", superClassName);
			importClass(superClassName);
		}
		
		boolean isImplement = false;
		
		for (Object interfaceClass: interfaceClasses) {
			if (interfaceClass != null) {
				if (isImplement) {
					line.append(", ");
				} else {
					line.append("implements ");
					isImplement = true;
				}
				
				String		interfaceName = toClassName(interfaceClass);
				
				line.append(interfaceName);
				importClass(interfaceName);
			}
		}
		
		/*
		for (TypeElement interfaceElement: interfaceElements) {
			line.append(", %s", interfaceElement.getSimpleName());
			importedElements.add(interfaceElement);
		}
		*/
		line.append(" {");
		println(line);
		indent++;
	}
	
	private String toClassName(Object object) {
		if (object instanceof Class) {
			return ((Class)object).getCanonicalName();
		} else {
			return object.toString();
		}
	}
	
	public void closeClass() {
		println();
		closeMethod();

		buff.insert(0, getHeader());
	}

	public void openMethod(String format, Object... args) {
		Line line = new Line();
		
		line.append(format, args);

		println(line);
		indent++;
	}

	public void openMethod(String methodName, String retuenClass, Map<String, Class> args, boolean isStatic) {
		openMethod(methodName, retuenClass, args, "public", isStatic);
	}
	public void openMethod(String methodName, String retuenClass, Map<String, Class> args, String scope, boolean isStatic) {
		println();
		
		Line line = new Line();
		
		line.append(scope + " ");
		if (isStatic) {
			line.append("static ");
		}
		if (retuenClass == null) {
			line.append("void ");
		} else {
			line.append(retuenClass + " ");
		}
		line.append("%s(", methodName);
		
		//	引数を書き出す
		boolean	isFirst = true;
		for (String name: args.keySet()) {
			Class	type = args.get(name);

			if (!isFirst) {
				line.append(", ");
			}
			line.append("%s %s", type.getCanonicalName(), name);
			isFirst = false;
		}
		
		line.append(") {");
		println(line);
		indent++;
	}

	public void openMethod(ExecutableElement method, boolean implementAbstract) {
		Line		line = new Line();
		boolean	isAbstract = false;

		//	修飾子を書き出す
		for (Modifier modifier: method.getModifiers()) {
			if (modifier == ABSTRACT) {
				if (implementAbstract) {
					//	abstract を展開するのであれば、無視する
					continue;
				} else {
					isAbstract = true;
				}
			}
			
			line.append("%s ", modifier);
		}

		//	返り値とメソッド名を書き出す
		line.append("%s %s(", method.getReturnType(), method.getSimpleName());

		//	引数を書き出す
		boolean	isFirst = true;
		for (VariableElement value: method.getParameters()) {
			if (!isFirst) {
				line.append(", ");
			}
			line.append("%s %s", value.asType(), value.getSimpleName());
			isFirst = false;
		}
		
		line.append(")");

		if (isAbstract) {
			//	abstract を展開しない
			line.append(";");
		} else {
			line.append(" {");
		}

		println(line);
		indent++;
	}

	public void closeMethod() {
		indent--;
		println("}");
	}
	
	public void startIf(String condition, Object... args) {
		println("if (%s) {", String.format(condition, args));
		indent++;
	}

	public void startElseIf(String condition, Object... args) {
		indent--;
		println("} else if (%s) {", String.format(condition, args));
		indent++;
	}

	public void startElse() {
		indent--;
		println("} else {");
		indent++;
	}

	public void endIf() {
		indent--;
		println("}");
	}

	public String startFor(String countStringFormat, Object... args) {
		String	loopCursor = newLoopCursor();
		
		println("for (int %s = 0; %s < %s; %s++) {", loopCursor, loopCursor, String.format(countStringFormat, args), loopCursor);
		indent++;
		
		return loopCursor;
	}
	public String startForeach(String elementType, String collenctionName) {
		String	loopCursor = newLoopCursor();
		
		println("for (%s %s: %s) {", elementType, loopCursor, collenctionName);
		indent++;
		
		return loopCursor;
	}
	public void endFor() {
		forCount--;
		closeMethod();
	}
	private String newLoopCursor() {
		String		loopCursor = "i" + forCount;

		forCount++;
		
		return loopCursor;
	}
	
	public String newVariable() {
		String		variable = "v" + variableCount;

		variableCount++;
		
		return variable;
	}
	
	public void openBrace() {
		println("{");
		indent++;
	}
	public void closeBrace() {
		indent--;
		println("}");
	}

	public void importClass(Class importedClass) {
		importClass(importedClass.getCanonicalName());
	}
	public void importClass(String importedClass) {
		if (!importedClasses.contains(importedClass)) {
			importedClasses.add(importedClass);
		}
	}
	
	private StringBuilder getHeader() {
		StringBuilder		buff = new StringBuilder();
		
		buff.append(String.format("package %s;\n", packageName));
		buff.append("\n");

		for (String importedClass: importedClasses) {
			buff.append(String.format("import %s;\n", importedClass));
		}

		buff.append("\n");
		
		return buff;
	}

	public void printUnsupportedException() {
		println("throw new %s();", UnsupportedOperationException.class.getCanonicalName());
	}
	
	public void println() {
		buff.append("\n");
	}

	public void println(Line line) {
		println(line.toString());
	}
	public void println(String line) {
		for (int i = 0; i < indent; i++) {
			buff.append("\t");
		}
		buff.append(line + "\n");
	}
	public void println(String lineFormat, Object... args) {
		println(String.format(lineFormat, args));
	}

	@Override
	public String toString() {
		return buff.toString();
	}

	public String getPackage(TypeElement classElement) {
		String		className = classElement.getQualifiedName().toString();
		int			pos = className.lastIndexOf('.');
		
		if (pos < 0) {
			return "";
		}
		return className.substring(0, pos);
	}
	
	public void merge(SourceBuffer sourceBuffer, int baseIndent) {
		String[]	lines = sourceBuffer.toString().split("\n");

		for (String line: lines) {
			println(line);
		}
	}
	
	public File getFile(String baseDir) {
		if ((this.className != null) && (this.packageName != null)) {
			return new File(baseDir + File.separator + packageName.replace(".", File.separator) + File.separator + className + ".java");
		}
		throw new UnsupportedOperationException("not contain class.");
	}

	public String getCannonicalClassName() {
		if ((this.className != null) && (this.packageName != null)) {
			return packageName + "." + className;
		}
		throw new UnsupportedOperationException("not contain class.");
	}

	class Line {
		private StringBuilder	buff;
		
		public Line() {
			buff = new StringBuilder();
		}
		
		public void append(String text) {
			buff.append(text);
		}
		
		public void append(String format, Object... args) {
			buff.append(String.format(format, args));
		}

		public String toString() {
			return buff.toString();
		}
	}



}
