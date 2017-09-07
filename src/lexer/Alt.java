package lexer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 Gameplan for AltLex:
	1.  Identify class, interface, and enum tokens, and give each token the proper id.
	2.  Gives generics the correct id and adds them to the param field of their class or interface
	3.  Finds all of the class and interface types used in the file and adds them to returnType
	4.  Identifies method tokens and gives them the proper id
	5.  Identifies constructor tokens, and gives them the proper id.
	6.  Adds parameter types to the param field of each method and constructor and gives parameters the correct id.
	7.  Turn 'else if' into one token
	8.  Sets the context for every token based on what class/interface/enum or method/constructor or loop/conditional it's in
	9.  Identifies variable tokens inside methods and constructors and gives them the proper id.
	10. Identifies enum constants and gives them the proper id.
	11. Identifies class and interface variables and gives them the proper id.
	12. Gives the correct id to all variables that were declared as parameters.	(Could be combined with 14 using 14's structure)
	13. Gives the correct id to all variables that were declared inside a method.
	14. Gives the correct id to all variables that were declared as instance class/interface variables.
	15. Gives the correct id to all variables that were declared as static class/interface variables.
	16. Determines if ++/-- are used as postfix or prefix operators and whether +/- are unary or arithmetic operators
	17. Gives the correct id to all constructors (except those of imported classes).
	18. Gives the correct id to all constructors of imported types.
	19. Gives the correct id to all static methods.
	20. Gives the correct id to all instance methods.
	21. Gives the correct tokenId to all methods called from a variable (e.g. list.size())
	22. Identifies object types and gives them the proper id.
	23. Gives the correct tokenId to all methods called from a class (e.g. Class.forName(string))
	24. Gives the correct tokenId to all methods called from another method (e.g. token.getId().getCharAt(0))
	25. Gives the correct id to labels for break and continue statements
 */

public interface Alt{
	final ArrayList<String> 
		constType = new ArrayList<>(
			Arrays.asList("byte", "Byte", "short", "Short", "int", "Integer", "long", "Long", "float", 
				"Float", "double", "Double", "boolean", "Boolean", "char", "Character", "String", "Object")),
		classModifiers = new ArrayList<>(
			Arrays.asList("public", "private", "protected", "abstract", "static", "final", "strictfp")),
		interfaceModifiers = new ArrayList<>(
			Arrays.asList("public", "private", "protected", "abstract", "static", "strictfp")),
		enumModifiers = new ArrayList<>(
			Arrays.asList("public", "private", "protected", "static", "strictfp")),
		constructorModifiers = new ArrayList<>(
			Arrays.asList("public", "private", "protected")),
		methodModifiers = new ArrayList<>(
			Arrays.asList("public", "private", "protected", "abstract", "static", "final", "strictfp")),
		classVarModifiers = new ArrayList<>(
			Arrays.asList("public", "private", "protected", "static", "final", "transient", "volatile")),
		interfaceVarModifiers = new ArrayList<>(
			Arrays.asList("public", "static", "final"));
	
	ArrayList<Token> 	classList = new ArrayList<>(),
						interfaceList = new ArrayList<>(),
						enumList = new ArrayList<>(),
						instMethodList = new ArrayList<>(),
						staticMethodList = new ArrayList<>(),
						methodList = new ArrayList<>(),
						instVarList = new ArrayList<>(),
						staticVarList = new ArrayList<>(),
						conMethodVarList = new ArrayList<>(),
						varList = new ArrayList<>(),
						constructorList = new ArrayList<>(),
						classInterEnumList = new ArrayList<>(),
						conMethodList = new ArrayList<>();
	
	ArrayList<String> 	returnType = new ArrayList<>(),
						varType = new ArrayList<>(),
						classNameList = new ArrayList<>(),
						interfaceNameList = new ArrayList<>(),
						instMethodNameList = new ArrayList<>(),
						staticMethodNameList = new ArrayList<>(),
						methodNameList = new ArrayList<>(),
						importList = new ArrayList<>(Arrays.asList("java.lang"));
	
}
