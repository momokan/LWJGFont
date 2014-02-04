package ttfmap.processor;

import static javax.lang.model.element.Modifier.ABSTRACT;

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
	private TypeElement			interfaceElement;
//	private String				classAppendix;
	private String				className;
	private List<TypeElement>	importedElements;
	private int					indent;
	private int					forCount;
	private int					variableCount;

	public SourceBuffer(TypeElement interfaceElement, String classAppendix) {
		this.buff = new StringBuilder();
		this.interfaceElement = interfaceElement;
		this.className = interfaceElement.getSimpleName() + classAppendix;
		this.importedElements = new ArrayList<TypeElement>();
		this.indent = 0;
		this.forCount = 0;
	}
	
	public void openClass(List<? extends TypeParameterElement> typeParameters, TypeElement superClass /*, TypeElement... interfaceElements*/) {
		Line		line = new Line();
		String		typeParametersString = "";
		
		if (0 < typeParameters.size()) {
			for (TypeParameterElement typeParameter: typeParameters) {
				if (0 < typeParametersString.length()) {
					typeParametersString += ", ";
				}
				typeParametersString += typeParameter.toString();
			}
			typeParametersString = String.format("<%s>", typeParametersString);
		}

		line.append("public class %s%s ", className, typeParametersString);
		
		line.append("extends %s ", superClass.getSimpleName());
		importedElements.add(superClass);
		
		line.append("implements %s ", interfaceElement.getSimpleName());
		importedElements.add(interfaceElement);

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
		println();
		
		Line line = new Line();
		
		line.append("public ");
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
	
	private StringBuilder getHeader() {
		StringBuilder		buff = new StringBuilder();
		
		buff.append(String.format("package %s;\n", getPackage(interfaceElement)));
		buff.append("\n");

		for (TypeElement impoertedElement: importedElements) {
			buff.append(String.format("import %s;\n", impoertedElement.getQualifiedName().toString()));
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

	public String getSimpleClassName() {
		return className;
	}

	public String getClassName() {
		return getPackage(interfaceElement) + "." + className;
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
