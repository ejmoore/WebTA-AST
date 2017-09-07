package lexer;

import java.util.ArrayList;
import java.util.Arrays;

public class AltLex implements Alt{
	private ArrayList<Token> list;
	private AltLexHelper helper; //gets initialized multiple times because list keeps changing and I don't think helper.list changes along with it
	public AltLex(ArrayList<Token> list){
		this.list = list;
	}
	
	public void pass() throws ClassNotFoundException{
		pass1();
		pass2();
		pass3();
		pass4();
		pass5();
		pass6();
		pass7();
		pass8();
		pass9();
		pass10();
		pass11();
		pass12();
		pass13();
		pass14();
		pass15();
		pass16();
		pass17();
		pass18();
		pass19();
		pass20();
		pass21();
		pass22();
		pass23();
		pass24();
		pass25();
	}
	
	/**
	 * Finds each class, interface, and enum in the file and adds them to their list,
	 * and gives them the proper id.
	 */
	public void pass1(){
		String cm = "", im = "", em = "";
		boolean isClass = false, isInterface = false, isEnum = false;
		for(Token token : list){
			String word = token.getWord();
			if(isClass){
				classList.add(token);
				cm = cm.substring(0, Math.max(0, cm.length() - 1));
				token.setId("class");
				token.setModifier(cm);
			}
			else if(isInterface){
				interfaceList.add(token);
				im = im.substring(0, Math.max(0, im.length() - 1));
				token.setId("interface");
				token.setModifier(im);
			}
			else if(isEnum){
				enumList.add(token);
				token.setId("enum");
				em = em.substring(0, Math.max(0, em.length() - 1));
				token.setModifier(em);
			}
			isClass = (token.getWord().equals("class")) ? true : false;
			isInterface = (token.getWord().equals("interface")) ? true : false;
			isEnum = (token.getWord().equals("enum")) ? true : false;
			if(enumModifiers.contains(word)) em += word + "-";
			if(interfaceModifiers.contains(word)) im += word + "-";
			if(classModifiers.contains(word)) cm += word + "-";
			else if(!isClass && !isInterface && !isEnum){
				cm = "";
				im = "";
				em = "";
			}
		}
	}
	
	/**
	 * Gives generics the correct id and adds them to the param field of their class or interface
	 */
	public void pass2(){
		classInterEnumList.addAll(classList); //Enums aren't needed here yet, so they are not added
		classInterEnumList.addAll(interfaceList);
		for(Token t : classInterEnumList){
			int index = t.getIndex() + 1; //check for '<'
			if(list.get(index).getWord().equals("<")){
				list.get(index).setId("Lcorner");
				String genericList = "";
				for(index += 1; index < list.size() && !list.get(index).getWord().equals(">"); index++){
					Token tok = list.get(index);
					if(!tok.getId().equals("comma")){
						tok.setId("generic");
						genericList += tok.getWord() + "-";
						returnType.add(tok.getWord()); //gets added to varType when returnType is added to varType
					}
				}
				if(list.get(index).getWord().equals(">")){
					list.get(index).setId("Rcorner");
					genericList = genericList.substring(0, Math.max(0, genericList.length() - 1)); //gets rid of hanging '-'
					t.setParam(genericList);
				}
			}
		}
	}
	
	/**
	 * Finds all of the class and interface types used/imported 
	 * in the file and adds them to returnType
	 */
	private void pass3(){
		ArrayList<String> addLater = new ArrayList<>(); //class types to be added to importList after reading through the file
		for(int i = 0; i < list.size(); i++){
			Token token = list.get(i);
			if(token.getId().equals("package")){
				importList.add(list.get(i + 1).getWord());
			}
			else if(token.getId().equals("import")){
				String imp = "", current = "";
				i += 1; //to skip 'import'
				while(!list.get(i + 1).getWord().equals("*") && !list.get(i + 2).getWord().equals(";")){
					current = list.get(i).getWord();
					imp += current;
					i++;
				}
				//This is the type itself being imported, so it can just be added to returnType
				if(!list.get(i + 1).getWord().equals("*")){ //change this to check the last word in imp
					returnType.add(list.get(i + 1).getWord());
					addLater.add(imp);
				}
				else if(!importList.contains(imp)) importList.add(imp);
			}
			//'other' id may mean it's an imported type
			else if(token.getId().equals("other")){
				//tests the 'other' token to see if it's a type from any of the imports
				for(String aPackage : importList) {
					try {
						String fqn = aPackage + "." + token.getWord();
						Class.forName(fqn);
						returnType.add(token.getWord());
					}
					catch (Exception | NoClassDefFoundError n) {
						// Ignore
					}
				}
			}
		}
		importList.addAll(addLater); //now types are added so types in the file weren't mistakenly added to returnType
	}
	
	/**
	 * Finds each method, adds it to methodList,
	 * and gives them the proper id.
	 */
	private void pass4(){
		returnType.addAll(constType);
		for(Token t : classList) returnType.add(t.getWord());
		for(Token t : interfaceList) returnType.add(t.getWord());
		for(Token t : enumList) returnType.add(t.getWord());
		returnType.add("void");
		//Add all the classes from the imports and packages in the program
		//returnType.addAll(helper.getAllImportsAndPackages());
		String modifier = "", retType = "";
		boolean isMethod = false;
		for(int i = 0; i < list.size(); i++){
			String id = list.get(i).getId();
			String word = list.get(i).getWord();
			if(isMethod){
				methodList.add(list.get(i));
				if(modifier.contains("static")) staticMethodList.add(list.get(i));
				else instMethodList.add(list.get(i));
				modifier = modifier.substring(0, Math.max(0, modifier.length() - 1));
				list.get(i).setId("method");
				list.get(i).setModifier(modifier);
				list.get(i).setType(retType);
				isMethod = false;
			}
			//Finds methods that don't have generics in their return type
			isMethod = (returnType.contains(word) && list.get(i + 1).getId().equals("other") && list.get(i + 2).getId().equals("Lparen")) ? true : false;
			//Finds methods that do have generics in their return type
			if(returnType.contains(word) && list.get(i + 1).getWord().equals("<")){
				helper = new AltLexHelper(list);
				i = helper.getToEndingMarker("<", ">", i + 1);
				isMethod = (list.get(i + 1).getId().equals("other") && list.get(i + 2).getId().equals("Lparen")) ? true : false;
			}
			if(methodModifiers.contains(word)) modifier += word + "-";
			else if(returnType.contains(word)) retType += word;
			else if(word.equals(".")) retType += word;
			else if(id.contains("bracket")) retType += word;
			else if(!isMethod){
				modifier = "";
				retType = "";
			}
		}
	}
	
	/**
	 * Identifies constructor tokens, and gives them the proper id.
	 */
	public void pass5(){
		for(Token t : classList) classNameList.add(t.getWord());
		String modifier = "";
		boolean isConstructor = false;
		for(int i = 0; i < list.size(); i++){
			String word = list.get(i).getWord();
			//It's a constructor if it has the name of a class and the 
			//first token after its ending parenthesis is a left brace
			if(classNameList.contains(word) && !returnType.contains(list.get(i - 1).getWord())){
				int parenCount = 0;
				parenCount += (list.get(i + 1).getId().equals("Lparen")) ? 1 : 0;
				parenCount -= (list.get(i + 1).getId().equals("Rparen")) ? 1 : 0;
				for(int j = i + 2; j < list.size(); j++){
					if(parenCount == 0 && list.get(j).getId().equals("Lbrace")){
						isConstructor = true;
						break;
					}
					else if(parenCount == 0) break;
					parenCount += (list.get(j).getId().equals("Lparen")) ? 1 : 0;
					parenCount -= (list.get(j).getId().equals("Rparen")) ? 1 : 0;
				}
			}
			if(isConstructor){
				constructorList.add(list.get(i));
				modifier = modifier.substring(0, Math.max(0, modifier.length() - 1));
				list.get(i).setId("constructor");
				list.get(i).setModifier(modifier);
				isConstructor = false;
			}
			if(constructorModifiers.contains(word)) modifier += word + "-";
			else if(!isConstructor) modifier = "";
		}
	}
	
	/**
	 * Adds parameter types to the param field of each method and constructor and gives parameters the correct id
	 */
	public void pass6(){
		conMethodList.addAll(methodList);
		conMethodList.addAll(constructorList);
		varType.addAll(returnType);
		varType.remove("void");
		for(Token t : conMethodList){
			int index = t.getIndex();
			String param = "";
			int parenCount = 
				(list.get(index + 1).getId().equals("Lparen")) ? 1 : 0; //opening parenthesis
			String fin = ""; //notifies whether parameter is final or not
			String type = "";
			for(int i = index + 2; i < list.size() && parenCount != 0; i++){
				String id = list.get(i).getId(),
					word = list.get(i).getWord();
				if(varType.contains(word) && !varType.contains(list.get(i - 1).getWord()))
					type += word;
				else if(word.equals(".")) type += word;
				else if(id.contains("bracket")) type += word;
				else if(word.equals("final")) fin = "final";
				else if(varType.contains(list.get(i - 1).getWord()) || list.get(i - 1).getId().contains("bracket")){
					list.get(i).setId("pvar");
					list.get(i).setModifier(fin);
					list.get(i).setType(type);
					param += type + "-";
					fin = "";
					type = "";
				}
					
				parenCount += (list.get(i).getId().equals("Lparen")) ? 1 : 0;
				parenCount -= (list.get(i).getId().equals("Rparen")) ? 1 : 0;
			}
			param = param.substring(0, Math.max(0, param.length() - 1));
			t.setParam(param);
		}
	}
	
	/**
	 * Turn 'else if' into one token and a finds floats too
	 */
	public void pass7(){
		for(int i = 0; i < list.size() - 1; i++){
			String word = list.get(i).getWord();
			if(word.equals("else") && list.get(i + 1).getWord().equals("if")){
				list.get(i).setWord(word + " if");
				list.remove(i + 1);
			}
			list.get(i).setIndex(i);
		}
		list.get(list.size() - 1).setIndex(list.size() - 1); //For the last element in the list
	}
	
	/**
	 * Sets the context for every token based on what class/interface/enum
	 * or method/constructor or loop/conditional it's in
	 */
	public void pass8(){
		classInterEnumList.addAll(enumList); //Already has classes and interfaces from pass2()
		//Set the context for each token
		ArrayList<String> contextList = new ArrayList<>();
		ArrayList<String> idList = new ArrayList<>(
				Arrays.asList("conditional", "loop", "try", "catch", "finally"));
		
		ArrayList<Integer> braceCount = new ArrayList<Integer>(), parenCount =  new ArrayList<Integer>();
		int size = 0; //size of braceCount and parenCount
		//int braceCount = 0, parenCount = 0;
		
		helper = new AltLexHelper(list);
		for(int i = 0; i < list.size(); i++){
			String id = list.get(i).getId(), word = list.get(i).getWord();
			if(idList.contains(id) || classInterEnumList.contains(list.get(i)) || conMethodList.contains(list.get(i))){
				contextList.add(word);
				braceCount.add(0);
				parenCount.add(0);
				size++;
			}
			else if(!contextList.isEmpty()){
				if(id.equals("Lbrace")) braceCount.set(size - 1, braceCount.get(size - 1) + 1);
				else if(id.equals("Rbrace")){
					braceCount.set(size - 1, braceCount.get(size - 1) - 1);
					if(braceCount.get(size - 1) == 0){
						contextList.remove(size - 1);
						braceCount.remove(size - 1);
						parenCount.remove(size - 1);
						size--;
					}
				}
				else if(id.equals("Lparen")) parenCount.set(size - 1, parenCount.get(size - 1) + 1);
				else if(id.equals("Rparen")) parenCount.set(size - 1, parenCount.get(size - 1) - 1);
				else if(id.equals("semi_colon") && parenCount.get(size - 1) == 0 && braceCount.get(size - 1) == 0){
					if(!contextList.isEmpty()){
						contextList.remove(size - 1);
						braceCount.remove(size - 1);
						parenCount.remove(size - 1);
						size--;
					}
				}
			}
			list.get(i).setContext(helper.setContext(contextList));
		}
	}
	
	/**
	 * Identifies variable tokens inside methods and constructors
	 * and gives them the proper id.
	 */
	public void pass9(){
		for(Token t : conMethodList){
			String context = t.getContext(), ogContext = t.getContext();
			String fin = "", type = "";
			boolean boolVar = false;
			boolean boolType = false;
			int braceCount = 0, parenCount = 0, cornerCount = 0;
			for(int i = t.getIndex() + 1; i < list.size() && context.contains(ogContext); i++){
				Token token = list.get(i), pastToken = list.get(i - 1);
				context = token.getContext();
				
				braceCount += (list.get(i).getId().equals("Lbrace")) ? 1 : 0;
				braceCount -= (list.get(i).getId().equals("Rbrace")) ? 1 : 0;
				parenCount += (list.get(i).getId().equals("Lparen")) ? 1 : 0;
				parenCount -= (list.get(i).getId().equals("Rparen")) ? 1 : 0;
				cornerCount += (list.get(i).getWord().equals("<")) ? 1 : 0;
				cornerCount -= (list.get(i).getWord().equals(">")) ? 1 : 0;
				String variableType = token.getWord();
				if(token.getId().equals("other") && boolType && (varType.contains(pastToken.getWord()) || 
						pastToken.getId().equals("Rbracket") || pastToken.getWord().equals(">"))){
					boolVar = true;
					braceCount = 0;
					parenCount = 0;
					cornerCount = 0;
					String bracket = "";
					for(int j = i + 1; list.get(j).getId().contains("bracket"); j++){
						bracket += list.get(j).getWord();
					}
					token.setId("var");
					token.setModifier(fin);
					token.setType(type + bracket);
					conMethodVarList.add(token);
					varList.add(token);
				}
				else if(boolVar && token.getId().equals("other") 
						&& pastToken.getId().equals("comma") && braceCount == 0 && parenCount == 0){
					String bracket = "";
					for(int j = i + 1; list.get(j).getId().contains("bracket"); j++){
						bracket += list.get(j).getWord();
					}
					token.setId("var");
					token.setModifier(fin);
					token.setType(type + bracket);
					conMethodVarList.add(token);
					varList.add(token);
				}
				else if(token.getWord().equals("final"))
					fin = "final";
				else if(varType.contains(variableType) && !boolVar){ //!boolVar means the first variable name hasn't been reached yet
					type += variableType;
					boolType = true;
				}
				else if(token.getId().contains("bracket") && !boolVar)
					type += token.getWord();
				else if((token.getWord().equals("<") ||token.getWord().equals(">")) && !boolVar)
					type += token.getWord();
				else if(token.getId().equals("comma") && cornerCount != 0)
					type += token.getWord();
				else if(token.getId().equals("dot_op") && varType.contains(pastToken.getWord()))
					type += ".";
				else if(token.getId().equals("semi_colon") && boolVar){
					fin = "";
					type = "";
					boolVar = false;
				}
				else if(!boolVar){
					fin = "";
					type = "";
				}
			}
		}
	}
	
	/**
	 * Not yet implemented.
	 * Identifies enum constants and gives them the proper id.
	 */
	public void pass10(){
		
	}
	
	/**
	 * Identifies class and interface variables and gives them the proper id.
	 */
	public void pass11(){
		for(Token t : interfaceList) interfaceNameList.add(t.getWord());
		boolean boolClass = false, boolInter = false;
		String modifier = "", type = "";
		boolean boolVar = false;
		boolean boolType = false; //Indicates that it passed a variable type
		int braceCount = 0, parenCount = 0, cornerCount = 0;
		for(int i = 1; i < list.size(); i++){ //starts at 1 for pastToken on the line below
			Token token = list.get(i), pastToken = list.get(i - 1);
			String[] context = token.getContext().split("-");
			if(context.length > 0){
				boolClass = classNameList.contains(context[context.length - 1]);
				boolInter = interfaceNameList.contains(context[context.length - 1]);
			}
			braceCount += (list.get(i).getId().equals("Lbrace")) ? 1 : 0;
			braceCount -= (list.get(i).getId().equals("Rbrace")) ? 1 : 0;
			parenCount += (list.get(i).getId().equals("Lparen")) ? 1 : 0;
			parenCount -= (list.get(i).getId().equals("Rparen")) ? 1 : 0;
			cornerCount += (list.get(i).getWord().equals("<")) ? 1 : 0;
			cornerCount -= (list.get(i).getWord().equals(">")) ? 1 : 0;
			
			String variableType = token.getWord();
			if((boolClass || boolInter) && (token.getId().equals("other") || token.getId().equals("var")) &&
					boolType && (varType.contains(pastToken.getWord()) || 
					pastToken.getId().equals("Rbracket") || pastToken.getWord().equals(">"))) {
				boolVar = true;
				braceCount = 0;
				parenCount = 0;
				cornerCount = 0;
				String brackets = "";
				for(int j = i + 1; list.get(j).getId().contains("bracket"); j++){
					brackets += list.get(j).getWord();
				}
				token.setId("cvar");
				modifier = modifier.substring(0, Math.max(0, modifier.length() - 1));
				token.setModifier(modifier);
				token.setType(type + brackets);
				if(modifier.contains("static")) staticVarList.add(token);
				else instVarList.add(token);
				varList.add(token);
			}
			else if(boolVar && token.getId().equals("other") && 
					pastToken.getId().equals("comma") && braceCount == 0 && parenCount == 0){
				String brackets = "";
				for(int j = i + 1; list.get(j).getId().contains("bracket"); j++){
					brackets += list.get(j).getWord();
				}
				token.setId("cvar");
				token.setModifier(modifier);
				token.setType(type + brackets);
				if(modifier.contains("static")) staticVarList.add(token);
				else instVarList.add(token);
				varList.add(token);
			}
			else if(boolClass && classVarModifiers.contains(token.getWord())) 
				modifier += token.getWord() + "-";
			else if(boolInter && interfaceVarModifiers.contains(token.getWord())) 
				modifier += token.getWord() + "-";
			else if(varType.contains(variableType) && !boolVar){ //!boolVar means the first variable name hasn't been reached yet
				type += token.getWord();
				boolType = true;
			}
			else if(boolType && token.getId().equals("dot_op"))
				type += ".";
			else if(token.getId().contains("bracket") && !boolVar)
				type += token.getWord();
			else if((token.getWord().equals("<") ||token.getWord().equals(">")) && !boolVar)
				type += token.getWord();
			else if(token.getId().equals("comma") && cornerCount != 0)
				type += token.getWord();
			else if(token.getId().equals("semi_colon")){
				modifier = "";
				type = "";
				boolVar = false;
				boolType = false;
			}
			else if(!boolVar){
				modifier = "";
				type = "";
			}
		}
	}
	
	/**
	 * Gives the correct id to all variables that were declared as parameters
	 */
	public void pass12(){
		for(Token t : conMethodList){
			String context = t.getContext(), ogContext = t.getContext();
			//Compiling a list of parameters for this method/constructor
			ArrayList<Token> paramList = new ArrayList<>();
			int i = t.getIndex();
			int parenCount = 0; //opening parenthesis
			parenCount += (list.get(i + 1).getId().equals("Lparen")) ? 1 : 0;
			parenCount -= (list.get(i + 1).getId().equals("Rparen")) ? 1 : 0;
			for(i = i + 2; i < list.size() && parenCount != 0; i++){
				if(list.get(i).getId().contains("pvar")) paramList.add(list.get(i));
				parenCount += (list.get(i).getId().equals("Lparen")) ? 1 : 0;
				parenCount -= (list.get(i).getId().equals("Rparen")) ? 1 : 0;
			}
			//Searching through the method/constructor for matching variable names
			for(; i < list.size() && context.contains(ogContext); i++, context = list.get(i).getContext()){
				if(list.get(i).getId().equals("other")){
					for(Token p : paramList){
						if(list.get(i).getWord().equals(p.getWord())) 
							list.get(i).setId(p.getId());
							list.get(i).setModifier(p.getModifier());
							list.get(i).setType(p.getType());
					}
				}
			}
		}
	}
	
	/**
	 * Gives the correct id to all variables that were declared inside a method
	 */
	public void pass13(){
		for(Token t : conMethodList){
			ArrayList<Token> declaredVars = new ArrayList<>();
			String context = t.getContext(), ogContext = t.getContext();
			for(int i = t.getIndex(); i < list.size() && context.contains(ogContext); i++, context = list.get(i).getContext()){
				Token token = list.get(i);
				if(token.getId().contains("var")){
					declaredVars.add(token);
				}
				else if(token.getId().equals("other")){
					for(Token v : declaredVars){
						if(token.getContext().contains(v.getContext()) && token.getWord().equals(v.getWord())){
							token.setId(v.getId());
							token.setModifier(v.getModifier());
							token.setType(v.getType());
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gives the correct id to all variables that were declared as 
	 * instance class/interface variables.
	 */
	public void pass14(){
		int i = classInterEnumList.size() - 1;
		for(Token t = classInterEnumList.get(i); i >= 0; i--){
			t = classInterEnumList.get(i);
			if(!t.getId().contains("enum")){
				boolean staticMethod = false;
				for(int j = t.getIndex(); j < list.size() && list.get(j).getContext().contains(t.getContext()); j++){
					Token token = list.get(j);
					boolean con1 = token.getId().contains("var"),
							con2 = list.get(j - 1).getId().equals("dot_op"),
							con3 = list.get(j - 2).getId().equals("this"),
							con4 = false;
					for(String type : list.get(j - 2).getType().split("-")){
						if(classNameList.contains(type) || interfaceNameList.contains(type)){
							con4 = true;
							break;
						}
					}
					if(token.getId().contains("method") || token.getId().contains("constructor"))
						staticMethod = token.getModifier().contains("static");
					else if(token.getId().equals("other") || (con1 && con2 && (con3 || con4))){
						for(Token v : instVarList){
							if(token.getContext().contains(v.getContext()) && token.getWord().equals(v.getWord())){
								if(staticMethod && token.getId().equals("other"))
									token.setId("badInstanceReference");
								else {
									token.setId(v.getId());
									token.setModifier(v.getModifier());
									token.setType(v.getType());
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gives the correct id to all variables that were declared as 
	 * static class/interface variables.
	 */
	public void pass15(){
		int i = classInterEnumList.size() - 1;
		for(Token t = classInterEnumList.get(i); i >= 0; i--){
			t = classInterEnumList.get(i);
			if(!t.getId().contains("enum")){
				for(int j = t.getIndex(); j < list.size() && list.get(j).getContext().contains(t.getContext()); j++){
					Token token = list.get(j);
					boolean con1 = token.getId().contains("var"),
							con2 = list.get(j - 1).getId().equals("dot_op"),
							con3 = list.get(j - 2).getId().equals("this"),
							con4 = false;
					for(String type : list.get(j - 2).getType().split("-")){
						if(classNameList.contains(type) || interfaceNameList.contains(type)){
							con4 = true;
							break;
						}
					}
					if(token.getId().equals("other") || (con1 && con2 && (con3 || con4))){
						for(Token v : staticVarList){
							if(token.getContext().contains(v.getContext()) && token.getWord().equals(v.getWord())){
								token.setId(v.getId());
								token.setModifier(v.getModifier());
								token.setType(v.getType());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Determines if ++/-- are used as postfix or prefix operators and whether +/- are unary or arithmetic operators
	 */
	public void pass16(){
		for(int i = 0; i < list.size(); i++){
			Token token = list.get(i);
			String 	id = token.getId(),
					word = token.getWord();
			//Determines if ++/-- are postfix or prefix operators
			if(word.equals("++") || word.equals("--")){
				if(list.get(i - 1).getId().contains("var"))
					token.setId("post-" + id);
				else if(list.get(i + 1).getId().contains("var"))
					token.setId("pre-" + id);
			}
			//Determines if + or - are unary operators
			else if(word.equals("+") || word.equals("-")){
				String preId = list.get(i - 1).getId();
				if(!(preId.equals("Rparen") || preId.contains("var") || preId.contains("post-unary_op") || preId.equals("const"))){
					token.setId("unary_op");
				}
			}
		}
	}
	
	/**
	 * Gives the correct id to all constructors (except those of imported classes)
	 */
	public void pass17(){
		for(Token t : staticMethodList) staticMethodNameList.add(t.getWord());
		for(Token t : instMethodList) instMethodNameList.add(t.getWord());
		for(Token t : methodList) methodNameList.add(t.getWord());
		helper = new AltLexHelper(list);
		for(Token t : conMethodList){
			String context = t.getContext(), ogContext = t.getContext();
			//'+ 1' so it doesn't read the actual constructor declaration
			for(int i = t.getIndex() + 1; i < list.size() - 1 && context.contains(ogContext); i++, context = list.get(i).getContext()){
				Token token = list.get(i);
				if(list.get(Math.max(0, i - 1)).getId().equals("new") && classNameList.contains(token.getWord()) &&
						(list.get(i + 1).getId().equals("Lparen") || list.get(i + 1).getWord().equals("<"))){
					Token matchedToken = helper.getId(i, constructorList);
					list.get(i).setId(matchedToken.getId());
					list.get(i).setModifier(matchedToken.getModifier());
					list.get(i).setType(matchedToken.getType());
					list.get(i).setParam(matchedToken.getParam());
				}
			}
		}
	}
	
	/**
	 * Gives the correct id to all constructors of imported types.
	 */
	public void pass18(){
		for(Token token : list){
			Token nextToken = list.get(Math.min(token.getIndex() + 1, list.size() - 1));
			boolean leftParen = true;
			if(nextToken.getWord().equals("<"))
				leftParen = list.get(helper.getToEndingMarker("<", ">", nextToken.getIndex()) + 1).getWord().equals("(");
			if((token.getId().equals("other") || token.getId().equals("type")) && varType.contains(token.getWord()) && 
					(nextToken.getId().equals("Lparen") || (nextToken.getWord().equals("<") && leftParen))){
				Class<?> cls = null;
				String type = token.getWord();
				for(String aPackage : importList){
					try{
						String fqn = aPackage + "." + type;
						cls = Class.forName(fqn);
						break;
					}
					catch(Exception | NoClassDefFoundError e){
						// Ignore
					}
				}
				if(cls != null){
					ArrayList<Token> constructorList = helper.constructorToToken(cls.getDeclaredConstructors());
					Token matchedToken = helper.getId(token.getIndex(), constructorList);
					token.setId(matchedToken.getId());
					token.setModifier(matchedToken.getModifier());
					token.setType(matchedToken.getType());
					token.setParam(matchedToken.getParam());
				}
			}
		}
	}
	
	/**
	 * Gives the correct tokenId to all static methods
	 */
	public void pass19(){
		for(Token t : conMethodList){
			String context = t.getContext(), ogContext = t.getContext();
			//'+ 1' so it doesn't read the actual method declaration
			for(int i = t.getIndex() + 1; i < list.size() - 1 && context.contains(ogContext); i++, context = list.get(i).getContext()){
				Token token = list.get(i);
				if(staticMethodNameList.contains(token.getWord()) && list.get(i + 1).getId().equals("Lparen") &&
						!token.getId().contains("constructor")){
					Token matchedToken = helper.getId(i, staticMethodList);
					list.get(i).setId(matchedToken.getId());
					list.get(i).setModifier(matchedToken.getModifier());
					list.get(i).setType(matchedToken.getType());
					list.get(i).setParam(matchedToken.getParam());
				}
			}
		}
	}
	
	/**
	 * Gives the correct tokenId to all instance methods
	 */
	public void pass20(){
		for(Token t : conMethodList){
			String context = t.getContext(), ogContext = t.getContext();
			boolean inStatic = t.getId().contains("static");
			//'+ 1' so it doesn't read the actual method declaration
			for(int i = t.getIndex() + 1; i < list.size() - 1 && context.contains(ogContext); i++, context = list.get(i).getContext()){
				Token token = list.get(i);
				if(instMethodNameList.contains(token.getWord()) && list.get(i + 1).getId().equals("Lparen") &&
						!token.getId().contains("constructor") && !token.getId().contains("method")){
					boolean con1 = list.get(i - 1).getId().equals("dot_op"),
							con2 = false;
					for(String id : list.get(i - 2).getId().split("-")){
						if(classNameList.contains(id) || interfaceNameList.contains(id)){
							con2 = true;
							break;
						}
					}
					if(!inStatic || (inStatic && con1 && con2)){
						Token matchedToken = helper.getId(i, instMethodList);
						list.get(i).setId(matchedToken.getId());
						list.get(i).setModifier(matchedToken.getModifier());
						list.get(i).setType(matchedToken.getType());
						list.get(i).setParam(matchedToken.getParam());
					}
					else{
						list.get(i).setId("badInstanceReference-" + helper.getId(i, instMethodList));
					}
				}
			}
		}
	}
	
	/**
	 * Gives the correct tokenId to all methods called from a variable (e.g. list.size())
	 */
	public void pass21(){
		for(Token token : list){
			if(token.getId().contains("var")){
				Token var = token;
				int i = var.getIndex() + 1;
				String id = list.get(i).getId();
				if(id.equals("dot_op")){
					ArrayList<Token> varMethodList = helper.getMethodsFromType(var.getType());
							//helper.methodToToken(cls.getDeclaredMethods(), generic);
					Token matchedToken = helper.getId(i + 1, varMethodList);
					i++;
					list.get(i).setId(matchedToken.getId());
					list.get(i).setModifier(matchedToken.getModifier());
					list.get(i).setType(matchedToken.getType());
					list.get(i).setParam(matchedToken.getParam());
				}
			}
		}
	}
	
	/**
	 * Identifies object types and gives them the proper id.
	 */
	public void pass22(){
		for(int i = 1; i < list.size() - 1; i++){
			Token token = list.get(i);
			boolean con1 = varType.contains(token.getWord()),
					con2 = token.getId().equals("other"),
					con3 = list.get(i + 1).getId().contains("var"),
					con4 = list.get(i + 1).getId().equals("method"),
					con5 = list.get(i + 1).getId().equals("dot_op"),
					con6 = list.get(i + 1).getWord().equals("<"),
					con7 = list.get(i - 1).getId().equals("instanceof"),
					con8 = list.get(i - 1).getWord().equals("<"),
					con9 = list.get(i + 1).getWord().equals(">") || list.get(i + 1).getWord().equals(",");
			if(con1 && con2 && (con3 || con4 || con5 || con6 || con7 || (con8 && (con6 || con9)))) token.setId("type");
		}
	}
	
	/**
	 * Gives the correct tokenId to all methods called from a class (e.g. Class.forName(string))
	 */
	public void pass23(){
		for(Token token : list){
			if(token.getId().equals("type")){
				int i = token.getIndex() + 1;
				String id = list.get(i).getId();
				String type = token.getWord();
				if(id.equals("dot_op")){
					ArrayList<Token> varMethodList = helper.getMethodsFromType(type);
					Token matchedToken = helper.getId(i + 1, varMethodList);
					i++;
					list.get(i).setId(matchedToken.getId());
					list.get(i).setModifier(matchedToken.getModifier());
					list.get(i).setType(matchedToken.getType());
					list.get(i).setParam(matchedToken.getParam());
				}
			}
		}
	}
	
	/**
	 * Gives the correct tokenId to all methods called from another method (e.g. token.getId().getCharAt(0))
	 */
	public void pass24(){
		for(Token token : list){
			if(token.getId().equals("method")){
				String type = token.getType();
				int i = helper.getToEndingMarker("(", ")", token.getIndex() + 1) + 1;
				String id = list.get(i).getId();
				if(id.equals("dot_op")){
					ArrayList<Token> varMethodList = helper.getMethodsFromType(type);
					Token matchedToken = helper.getId(i + 1, varMethodList);
					i++;
					list.get(i).setId(matchedToken.getId());
					list.get(i).setModifier(matchedToken.getModifier());
					list.get(i).setType(matchedToken.getType());
					list.get(i).setParam(matchedToken.getParam());
				}
			}
		}
	}
	
	/**
	 * Gives the correct tokenId to labels for break and continue statements
	 */
	public void pass25(){
		for(int i = 0; i < list.size(); i++){
			String pre = list.get(Math.max(0, i - 1)).getWord(),
				cur = list.get(i).getWord(),
				post = list.get(Math.min(list.size() - 1, i + 1)).getWord();
			if((!varType.contains(pre) && !pre.equals("case")) && !cur.equals("default") && post.equals(":"))
				list.get(i).setId("label");
			else if((pre.equals("break") || pre.equals("continue")) && !cur.equals(";"))
				list.get(i).setId("label");
		}
	}
}
