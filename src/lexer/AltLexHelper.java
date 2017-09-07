package lexer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class AltLexHelper implements Alt{
	ArrayList<Token> list;		//list of all tokens
	Collection<String> packages;
	public AltLexHelper(ArrayList<Token> t){
		list = t;
	}
	
	public ArrayList<String> getAllImportsAndPackages(){
		ArrayList<String> ip = new ArrayList<>();
		
		return ip;
	}
	
	private Collection<String> getPackages() {
	    Set<String> packages = new HashSet<String>();
	    for (Package aPackage : Package.getPackages()) {
	        packages.add(aPackage.getName());
	    }
	    return packages;
	}
	
	public ArrayList<String> getFQNs(String simpleName) {
	    if (this.packages == null) {
	        this.packages = getPackages();
	    }

	    ArrayList<String> fqns = new ArrayList<String>();
	    for (String aPackage : packages) {
	        try {
	            String fqn = aPackage + "." + simpleName;
	            Class.forName(fqn);
	            fqns.add(fqn);
	        } catch (Exception e) {
	            // Ignore
	        }
	    }
	    return fqns;
	}	
	
	/**
	 * Returns the token of the recently matched method/constructor
	 * @param index The method identifier
	 * @param matchList The list of methods or constructors to search through
	 * @return The token of the recently matched method/constructor
	 */
	public Token getId(int index, ArrayList<Token> matchList){
		findMethod(index, matchList);
		return list.get(index);
	}
	
	/**
	 * Finds what method this method call is referring to and
	 * gives it the proper id
	 * @param index	The method identifier
	 * @param matchList The list of methods or constructors to search through
	 * @return The return type of the method
	 */
	public String findMethod(int index, ArrayList<Token> matchList){
		String word = list.get(index).getWord();
		int i = index + 1;
		//if it's a constructor at index, it may have generics needed to be skipped
		if(list.get(i).getWord().equals("<")){
			int cornerCount = 1;
			for(i += 1; i < list.size() && cornerCount != 0; i++){
				String corner = list.get(i).getWord();
				cornerCount += (corner.equals("<")) ? 1 : 0;
				cornerCount -= (corner.equals(">")) ? 1 : 0;
			}
		}
		//look through list of methods to find a match
		String param = getParam(index + 1);
		//System.out.println(list.get(index).getWord() + ": " + param);
		
		//classes have default constructors that we need to look out for
		if(param.equals("") && list.get(Math.max(0, index - 1)).getId().equals("new") && classNameList.contains(word) &&
				(list.get(index + 1).getId().equals("Lparen") || list.get(index + 1).getWord().equals("<"))){
			list.get(index).setId("constructor");
			list.get(index).setType(word);
			return word; //the name of the constructor
		}
		
		for(int j = 0; j < matchList.size(); j++){
			//compare parameters
			//check for matching methods or constructors in matchList
			if(matchList.get(j).getWord().equals(word) && matchList.get(j).getParam().equals(param)){
				list.get(index).setId(matchList.get(j).getId());
				list.get(index).setModifier(matchList.get(j).getModifier());
				list.get(index).setType(matchList.get(j).getType());
				list.get(index).setParam(matchList.get(j).getParam());
				if(list.get(index).getId().contains("method"))
					return list.get(index).getType(); //returns the return type of the method
				else if(list.get(index).getId().contains("constructor"))
					return word; //the name of the constructor
			}
		}
		return null; //no method found
	}
	
	/**
	 * Compiles a list of parameters for the method at the given index
	 * @param index Starts at the first left parenthesis of the method call
	 * or at the left corner bracket if it has generics
	 * @return The string of parameters in type-type format
	 */
	public String getParam(int index){
		if(list.get(index).getWord().equals("<")){
			index = getToEndingMarker("<", ">", index);
			index++; //move past '>'
		}
		int parenCount = 1;
		String param = "";
		ArrayList<String> typeList = new ArrayList<>(); //used to determine what 'type' a mathematical expression evaluates to
		//Finding the parameters of the method call
		for(int i = index + 1; i < list.size() && parenCount != 0; i++){
			parenCount += (list.get(i).getId().equals("Lparen")) ? 1 : 0;
			parenCount -= (list.get(i).getId().equals("Rparen")) ? 1 : 0;
			String type = "";
			//constructor
			if(list.get(Math.max(0, i - 1)).getId().equals("new") && classNameList.contains(list.get(i).getWord()) && 
					(list.get(i + 1).getId().equals("Lparen") || list.get(i + 1).getWord().equals("<"))){
				type = findMethod(i, constructorList);
				i = getToEndingMarker("<", ">", i + 1);
			}
			//method
			else if(methodNameList.contains(list.get(i).getWord()) && list.get(i + 1).getId().equals("Lparen") && !list.get(i).getId().contains("constructor")){
				type = findMethod(i, methodList);
				i = getToEndingMarker("(", ")", i + 1); //So parenCount stays balanced
			}
			//array variable
			else if(list.get(i).getType().contains("[")){
				type = getArrayVarType(i);
				//get past array brackets
				int bracketCount = 0;
				i++;
				while(list.get(i).getWord().equals("[") || list.get(i).getWord().equals("]") || bracketCount != 0){
					bracketCount += (list.get(i).getWord().equals("[")) ? 1 : 0;
					bracketCount -= (list.get(i).getWord().equals("]")) ? 1 : 0;
					i++;
				}
				i--;
			}
			//new array declared
			else if(varType.contains(list.get(i).getWord()) && list.get(i + 1).getId().equals("Lbracket")){
				type = getArrayDeclType(i);
				//get past array brackets
				int bracketCount = 0;
				i++;
				while(list.get(i).getWord().equals("[") || list.get(i).getWord().equals("]") || bracketCount != 0){
					//type += (list.get(i).getWord().equals("[")) ? "[" : "";
					//type += (list.get(i).getWord().equals("]")) ? "]" : "";
					bracketCount += (list.get(i).getWord().equals("[")) ? 1 : 0;
					bracketCount -= (list.get(i).getWord().equals("]")) ? 1 : 0;
					i++;
				}
				//get past the braces
				if(list.get(i).getId().equals("Lbrace")){
					int braceCount = 1;
					for(i += 1; i < list.size() && braceCount != 0; i++){
						braceCount += (list.get(i).getWord().equals("{")) ? 1 : 0;
						braceCount -= (list.get(i).getWord().equals("}")) ? 1 : 0;
					}
				}
				i--;
			}
			//variable or constant
			else if(list.get(i).getType() != "" && !list.get(i + 1).getId().equals("dot_op")){
				type = list.get(i).getType();
			}
			//method called from variable or class
			else if((list.get(i).getType() != "" || list.get(i).getId().equals("other")) && list.get(i + 1).getId().equals("dot_op")){
				type = findMethod(i + 2, getMethodsFromType(list.get(i).getType()));
				i = getToEndingMarker("(", ")", i + 3); //So parenCount stays balanced
			}
			//evaluate expression into a single type
			else if(list.get(i).getId().equals("comma")){
				param += typeEval(typeList) + "-";
				typeList = new ArrayList<>();
			}
			if(type != null && type.length() > 0) typeList.add(type);
		}
		
		//evaluate expression into a single type
		String lastType = typeEval(typeList);
		param += (lastType != null) ? lastType : "";
		return param;
	}
	
	/**
	 * Gets the type and dimension of the array variable
	 * @param index The index of the variable
	 * @return The type and dimension of the array variable
	 */
	public String getArrayVarType(int index){
		String type = list.get(index).getType();
		int bracketCount = 0;
		index++;
		while(list.get(index).getWord().equals("[") || list.get(index).getWord().equals("]") || bracketCount != 0){
			type = (list.get(index).getWord().equals("[") || list.get(index).getWord().equals("]")) ? 
				type.substring(0, Math.max(0, type.length() - 1)) : type;
			bracketCount += (list.get(index).getWord().equals("[")) ? 1 : 0;
			bracketCount -= (list.get(index).getWord().equals("]")) ? 1 : 0;
			index++;
		}
		return type;
	}
	
	/**
	 * Gets the type and dimension of the array declaration
	 * @param index The index of the type
	 * @return The type and dimension of the array declaration
	 */
	public String getArrayDeclType(int index){
		String type = list.get(index).getWord();
		int bracketCount = 0;
		index++;
		while(list.get(index).getWord().equals("[") || list.get(index).getWord().equals("]") || bracketCount != 0){
			type += (list.get(index).getWord().equals("[")) ? "[" : "";
			type += (list.get(index).getWord().equals("]")) ? "]" : "";
			bracketCount += (list.get(index).getWord().equals("[")) ? 1 : 0;
			bracketCount -= (list.get(index).getWord().equals("]")) ? 1 : 0;
			index++;
		}
		return type;
	}
	
	/**
	 * Tests to see if the Strings have the same list of parameters
	 * @param methodId The tokenId of a known method, must search for parameters
	 * @param tokenParamId The list of parameters of a method call
	 * @return True if they have the same parameters, false otherwise
	
	private boolean compareParameters(String methodId, String tokenParamId){
		String param = "";
		boolean atParam = false;
		for(String id : methodId.split("-")){
			if(atParam) param += id + "-";
			if(id.equals("method") || id.equals("constructor")) atParam = true;
		}
		return tokenParamId.equals(param.substring(0, Math.max(0, param.length() - 1)));
	}
	*/
	
	/**
	 * Starts at a left marker and counts every instance of Lmarker as +1
	 * and Rmarker as -1. Keeps iterating until markCount == 0.
	 * right-parenthesis
	 * @param index The index of the first instance of Lmarker
	 * @return The index of the last matching Rmarker or the original
	 * index if no ending marker is found
	 */
	public int getToEndingMarker(String Lmarker, String Rmarker, int index){
		int originalIndex = index;
		int markCount = (list.get(index).getWord().equals(Lmarker)) ? 1 : 0;
		for(index += 1; index < list.size() && markCount != 0; index++){
			markCount += (list.get(index).getWord().equals(Lmarker)) ? 1 : 0;
			markCount -= (list.get(index).getWord().equals(Rmarker)) ? 1 : 0;
		}
		if(index == list.size()) return originalIndex; //The ending marker was not found
		return index - 1; //index - 1 because it increments one too far in the for loop
	}
	
	/**
	 * Creates a string in word-word-word format from the give list
	 * @param contextList The list that the String will be made from
	 * @return String in word-word-word format
	 */
	public String setContext(ArrayList<String> contextList){
		String context = "";
		for(String s : contextList)
			context += s + "-";
		return context.substring(0, Math.max(0, context.length() - 1)); //removes hanging dash
	}
	
	/**
	 * Determines the resulting type of an expression
	 * @param typeList One or more types part of an expression
	 * @return The resulting type of evaluating typeList
	 */
	public String typeEval(ArrayList<String> typeList){
		String type = null;
		int precedence = 0;
		for(int i = 0; i < typeList.size(); i++){
			String s = typeList.get(i);
			if(s.equals("boolean")){
				String s2 = typeList.get(Math.max(typeList.size() - 1, i + 1));
				type = (s2.equals("boolean")) ? "boolean" : (s2.equals("String")) ? "String" : null;
			}
			else if(s.equals("byte") && precedence == 0){
				if(typeList.size() == 1){
					type = "byte";
					precedence = 0;
				}
				else{
					type = "int";
					precedence = 1;
				}
			}
			else if(s.equals("short") && precedence == 0){
				if(typeList.size() == 1){
					type = "short";
					precedence = 0;
				}
				else{
					type = "int";
					precedence = 1;
				}
			}
			else if(s.equals("char") && precedence == 0){
				if(typeList.size() == 1){
					type = "char";
					precedence = 0;
				}
				else{
					type = "int";
					precedence = 1;
				}
			}
			else if(s.equals("int") && precedence == 0){
				type = "int";
				precedence = 1;
			}
			else if(s.equals("long") && precedence < 2){
				type = "long";
				precedence = 2;
			}
			else if(s.equals("float") && precedence < 3){
				type = "float";
				precedence = 3;
			}
			else if(s.equals("double") && precedence < 4){
				type = "double";
				precedence = 4;
			}
			else if(s.equals("String")) type = "String";
			else return s; //it's either an array or an object
			if(type.equals("String")) break;
		}
		return type;
	}
	
	/**
	 * Takes an array of Methods and converts it to an ArrayList
	 * of Tokens so it can be used in another method
	 * @param method The array of Methods
	 * @param generic The type parameter of the method if it's generic
	 * @return The ArrayList of Tokens converted from method
	 */
	public ArrayList<Token> methodToToken(Method[] method, String generic){
		ArrayList<Token> tokenList = new ArrayList<>();
		for(Method m : method){
			String word = m.getName(), type = m.getReturnType().getSimpleName(), param = "";
			for(Class<?> p : m.getParameterTypes()){
				param += (p.getSimpleName().equals("Object")) ? generic + "-" : p.getSimpleName() + "-";
			}
			param = param.substring(0, Math.max(0, param.length() - 1));
			Token newToken = new Token("method", word, 0, "");
			newToken.setType((type.equals("Object")) ? generic : type);
			newToken.setParam(param);
			tokenList.add(newToken);
		}
		return tokenList;
	}
	
	/**
	 * Takes an array of Methods and converts it to an ArrayList
	 * of Tokens so it can be used in another method
	 * @param method The array of Methods
	 * @return The ArrayList of Tokens converted from method
	 */
	public ArrayList<Token> constructorToToken(Constructor<?>[] constructor){
		ArrayList<Token> tokenList = new ArrayList<>();
		for(Constructor<?> c : constructor){
			String[] nameArray = c.getName().split("\\."); //split(".") doesn't work because regex or somthing
			String wordAndType = nameArray[Math.max(0, nameArray.length - 1)], param = "";
			for(Class<?> p : c.getParameterTypes()){
				param += p.getSimpleName() + "-";
			}
			param = param.substring(0, Math.max(0, param.length() - 1));
			Token newToken = new Token("constructor", wordAndType, 0, "");
			newToken.setType(wordAndType);
			newToken.setParam(param);
			tokenList.add(newToken);
		}
		return tokenList;
	}
	
	/**
	 * Finds and returns a list of methods that can be used from the class type represented by a
	 * String and all of its superclasses
	 * @param type The name of a class type in String format whose list of methods it's finding
	 * @return The list of methods that can be called from the type field of the given token var.
	 * Returns ___BLANK___ if the type is invalid (null or methodList from Alt?)
	 */
	public ArrayList<Token> getMethodsFromType(String type){
		ArrayList<Token> varMethodList = new ArrayList<Token>();
		String[] typeArr = type.split("<");
		type = typeArr[0];
		String generic = (typeArr.length > 1) ? typeArr[1] : "Object"; //In case the type has a generic, ignoring multiple generics for now
		generic = generic.substring(0, Math.max(0, generic.length() - 1)); //gets rid of trailing '>'
		Class<?> cls = null;
		for(String aPackage : importList) {
			try {
				String fqn = aPackage + "." + type;
				cls = Class.forName(fqn);
				break;
			}
			catch (Exception | NoClassDefFoundError e) {
				// Ignore
			}
		}
		while(cls != null){
			varMethodList.addAll(methodToToken(cls.getDeclaredMethods(), generic));
			cls = cls.getSuperclass();
		}
	
		return varMethodList;
	}
	
	public void getAllMethods(String type) throws ClassNotFoundException{
		Class<?> cls = Class.forName("java.lang.String");
		while(cls != null){
			System.out.println(cls.getSimpleName());
			cls = cls.getEnclosingClass();
		}
	}
}
