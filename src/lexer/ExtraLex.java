package lexer;
import java.util.ArrayList;
import java.util.Arrays;

public class ExtraLex{
	private ArrayList<Token> list;
	private Token newToken; //used to set tokens with new values
	private ArrayList<String> classList; //keeps track of the classes in the file, for connecting class vars
	private ArrayList<String> constType = new ArrayList<String>(
		Arrays.asList("byte", "Byte", "short", "Short", "int", "Integer", 
					"long", "Long", "float", "Float", "double", "Double",
					"boolean", "Boolean", "char", "Character", "String", 
					"Object", "byte[]", "short[]", "int[]", "long[]", "float[]", 
					"double[]", "boolean[]", "char[]", "String[]", "Object[]"));
	
	public ExtraLex(ArrayList<Token> list){
		this.list = list;
		classList = new ArrayList<>();
	}

	 /**
	  * The first pass labels classes, methods, loops, and conditionals
	  * as well as giving them a context.
	  * Try, catch and finally statements get a context.
	  * Right braces get labeled the context outside whatever they're ending
	  * It also gives annotations a context
	  */
	public void firstPass(){
		ArrayList<String> methodList = new ArrayList<String>();
		ArrayList<String> context = new ArrayList<>();	//Each item in context is another context level
		ArrayList<String> contextList = new ArrayList<>(); //Keeps track of the different contexts to make sure they're different
		ArrayList<Integer> braceCount = new ArrayList<>();
		String modifier = ""; //Keeps track of a class or method's modifiers (eg. 'public static void')
		boolean boolClass = false, boolMethod = false; //tells me if the next word is a class or method name
		boolean boolDo = false; //do while loops require a special case
		String id, word;
		int line, bcindex = -1;
		int parenCount = 0; //Right now, just to let for statements have single line bodies
		for(int i = 0; i < list.size(); i++){
			id = list.get(i).getId();
			word = list.get(i).getWord();
			line = list.get(i).getLine();
			//modifier
			if(id.equals("modifier")){
				modifier += word + "-";
			}
			//'class' or 'interface'
			else if(id.equals("declare")){
				modifier += word;
				boolClass = true;
			}
			//method coming up
			//needs to change
			else if((id.equals("type") || id.equals("void")) && list.get(i + 2).getId().equals("Lparen")){
				modifier += word + "-method";
				boolMethod = true;
			}
			//class, interface, or method name
			else if(id.equals("other") && (boolClass || boolMethod)){
				context.add(word);
				makeUnique(contextList, context);
				braceCount.add(0);
				bcindex = braceCount.size() - 1;
				newToken = new Token(modifier, word, line, context(context));
				list.set(i, newToken);
				methodList.add(word);
				modifier = "";
				//for class vars
				if(boolClass){
					classList.add(word);
				}
				boolClass = false;
				boolMethod = false;
			}
			//method call
			else if(methodList.contains(word) && list.get(i + 1).getId().equals("Lparen")){
				newToken = new Token("method", word, line, context(context));
				list.set(i, newToken);
			}
			//loop or conditional
			else if(id.equals("loop") || id.equals("conditional")){
				//If boolDo is true, then we're at the while part of do-while
				if(!boolDo){
					context.add(word);
					makeUnique(contextList, context);
					braceCount.add(0);
					bcindex = braceCount.size() - 1;
				}
				//turns 'else if' into one token
				if(word.equals("else") && list.get(i + 1).getWord().equals("if")){
					context.set(context.size() - 1, context.get(context.size() - 1 ) + "if");
					newToken = new Token(id, word + " if", line, context(context));
					list.remove(i + 1);
				}
				else{
					newToken = new Token(id, word, line, context(context));
				}
				list.set(i, newToken);
			}
			//try, catch, finally
			else if(id.equals("try") || id.equals("catch") || id.equals("finally")){
				context.add(word);
				makeUnique(contextList, context);
				braceCount.add(0);
				bcindex = braceCount.size() - 1;
				newToken = new Token(id, word, line, context(context));
				list.set(i, newToken);
			}
			else if(id.equals("Lbrace")){
				braceCount.set(bcindex, braceCount.get(bcindex) + 1);
				modifier = "";
				if(braceCount.get(bcindex) == 1){
					//label '{' as the new context
					if(i < list.size() - 1){
						newToken = new Token(id, word, line, context(context));
						list.set(i, newToken);
					}
				}
			}
			else if(id.equals("Rbrace")){
				braceCount.set(bcindex, braceCount.get(bcindex) - 1);
				//do-while loops end in a different way
				if(braceCount.get(bcindex) == 0){
					if(!context.get(context.size() - 1).equals("do")){
						braceCount.remove(bcindex);
						context.remove(bcindex);
						bcindex--;
						//label '}' as the new context
						if(i < list.size() - 1){
							if(context(context).isEmpty()) newToken = new Token(id, word, line, " ");
							else newToken = new Token(id, word, line, context(context));
							list.set(i, newToken);
						}
					}
					else{
						context.remove(bcindex);
						//label '}' as the new context
						if(i < list.size() - 1){
							newToken = new Token(id, word, line, context(context));
							list.set(i, newToken);
						}
						context.add("do");
						boolDo = true; //Signals end of do-while is near
					}
				}
			}
			//Annotation
			else if(id.equals("annotation")){
				newToken = new Token(id, word, line, context(context));
				list.set(i, newToken);
			}
			else if(id.equals("semi_colon")){
				modifier = "";
				//End of do-while
				if(boolDo){
					braceCount.remove(bcindex);
					context.remove(bcindex);
					bcindex--;
					//label ';' as the new context
					if(i < list.size() - 1){
						newToken = new Token(id, word, line, context(context));
						list.set(i, newToken);
					}
					boolDo = false;
				}
				else if(bcindex >= 0 && braceCount.get(bcindex) == 0 && parenCount == 0){
					braceCount.remove(bcindex);
					context.remove(bcindex);
					bcindex--;
					//label ';' as the new context for bodies without braces
					if(i < list.size() - 1){
						newToken = new Token(id, word, line, context(context));
						list.set(i, newToken);
					}
				}
			}
			
			//For statement needs this to have a single line body.
			//See conditional for semi_colons
			parenCount += (id.equals("Lparen")) ? 1 : 0;
			parenCount -= (id.equals("Rparen")) ? 1 : 0;
		}
		//ready for the second pass
		secondPass();
	}
	
	/**
	 * The second pass labels variables and parameters as well as giving them a context
	 * It also adds a method's parameters to its token id
	 * Determines if ++/-- are used as postfix or prefix operators and whether +/- are unary or arithmetic operators
	 * It does not connect all the variables to each other
	 */
	public void secondPass(){
		ArrayList<String> varList = new ArrayList<String>();
		String modifier = "";
		boolean boolComma = false; //for multiple vars declared on one line
		boolean boolVar = false; //tells me if the next word is a variable being declared
		boolean boolParam = false; //tells me if the word could be a parameter
		int parenCount = 0; //tells me if the word could still be a parameter
		boolean varDecl = false; //indicates a variable decalartion statement
		String type = ""; //for keeping track of the type being  of var declared
		int braceCount = 0, parenCount2 = 0; //for avoiding array elements and method calls when looking for variable declarations
		int methodIndex = 0; //keeps track of which method the parameters belong to
		ArrayList<String> paramList = new ArrayList<String>();
		String id, word, context = ""; //Keeps track of current context
		int line;
		for(int i = 0; i < list.size(); i++){
			id = list.get(i).getId();
			word = list.get(i).getWord();
			line = list.get(i).getLine();
			if(list.get(i).getContext().length() > 0){
				context = list.get(i).getContext();
			}
			//modifier
			if(id.equals("modifier")){
				modifier += word + "-";
			}
			//type
			else if(id.equals("type")){
				if(boolParam){
					paramList.add(word);
				}
				//Variable Declaration
				else if(!list.get(i + 1).getId().contains("method")){
					varDecl = true;
					type = word + "-";
				}
				modifier += word + "-";
				boolVar = true;
			}
			//this
			else if(id.equals("this")){
				modifier += "this-";
			}
			//variable or parameter
			else if(id.equals("other") && (varList.contains(word.split("\\[")[0]) || boolVar || boolParam)){
				//variable is a parameter
				if(boolParam){
					newToken = new Token(modifier + "parameter", word, line, context);
					modifier = "";
				}
				//variable is something else
				else if(boolVar){
					//checking if it's a class var
					boolean classVar = true;
					String[] con = context.split("-");
					for(int j = 0; j < con.length; j++){
						if(!classList.contains(con[j])){
							classVar = false;
							break;
						}
					}
					if(classVar){
						modifier += "c"; //distinguishes class vars from other vars
					}
					newToken = new Token(modifier + "var", word, line, context);
				}
				else if(varList.contains(word.split("\\[")[0])){
					if(modifier.equals("this-")){
						newToken = new Token("this-var", word, line, context);
						modifier = "";
					}
					else{
						newToken = new Token("var", word, line, context);
					}
				}
				list.set(i, newToken);
				if(!varList.contains(word.split("\\[")[0])){
					varList.add(word.split("\\[")[0]);
				}
				boolVar = false;
			}
			//Multiple variables declared in one statement
			else if(id.equals("other") && boolComma){
				newToken = new Token(type + "var", word, line, context);
				list.set(i, newToken);
				if(!varList.contains(word.split("\\[")[0])){
					varList.add(word.split("\\[")[0]);
				}
				boolComma = false;
			}
			//Method parameters
			else if((id.contains("method") && !id.equals("method"))){
				methodIndex = i;
				boolParam = true;
			}
			else if(varDecl && parenCount2 == 0 && braceCount == 0 && id.equals("comma")){
				boolComma = true;
			}
			else if(id.equals("Lbrace")){
				modifier = "";
				if(varDecl){
					braceCount++;
				}
			}
			else if(id.equals("Rbrace")){
				if(varDecl){
					braceCount--;
				}
			}
			else if(id.equals("Lparen")){
				if(boolParam){
					parenCount++;
				}
				if(varDecl){
					parenCount2++;
				}
				modifier = "";
			}
			else if(id.equals("Rparen")){
				if(boolParam){
					parenCount--;
				}
				if(varDecl){
					parenCount2--;
				}
				if(parenCount == 0){
					//add parameters to the method's tokenId
					if(paramList.size() > 0){
						String newId = list.get(methodIndex).getId() + "-" + context(paramList);
						newToken = list.get(methodIndex);
						newToken.setId(newId);
						list.set(methodIndex, newToken);
						methodIndex = 0;
						paramList = new ArrayList<String>();
					}
					boolParam = false;
				}
			}
			//Determines if ++/-- are postfix or prefix operators
			else if(word.equals("++") || word.equals("--")){
				if(list.get(i - 1).getId().contains("var")){
					newToken = new Token("post-" + id, word, line, context);
					list.set(i, newToken);
				}
				else if(list.get(i + 1).getId().contains("var") || list.get(i + 1).getId().equals("other")){
					newToken = new Token("pre-" + id, word, line, context);
					list.set(i, newToken);					
				}
			}
			//Determines if + or - are unary operators
			else if(word.equals("+") || word.equals("-")){
				String preId = list.get(i - 1).getId();
				if(!(preId.equals("Rparen") || preId.contains("var") || preId.contains("post-unary_op") || constType.contains(preId))){
					newToken = new Token("unary_op", word, line, context);
					list.set(i, newToken);
				}
			}
			//Labels for break and continue statements
			else if(id.equals("other") && list.get(Math.min(list.size() - 1, i + 1)).getId().equals("colon")){
				newToken = new Token("label", word, line, context);
				list.set(i, newToken);
			}
			else if(id.equals("semi_colon")){
				modifier = "";
				varDecl = false;
			}
			
			//set context for every element that doesn't already have it
			if(list.get(i).getContext().isEmpty()){
				if(i < list.size() - 1){
					newToken = new Token(id, word, line, context);
					list.set(i, newToken);
				}
			}
		}
		//ready for the third pass
		thirdPass();
	}
	
	/**
	 * contains multiple passes
	 * connects variables to each other using context
	 * because of how context works, the priority of variables is
	 * in the order: parameters, main method vars, other method vars, class vars
	 */
	public void thirdPass(){
		String id, word, context = ""; //Keeps track of current context
		int line;
		String methodContext = "";
		Token newToken;
		ArrayList<Token> varList = new ArrayList<>();
		//first pass for parameters
		for(int i = 0; i < list.size(); i++){
			id = list.get(i).getId();
			word = list.get(i).getWord();
			line = list.get(i).getLine();
			if(list.get(i).getContext().length() > 0){
				context = list.get(i).getContext();
			}
			//start of new method
			if(id.contains("method")){
				methodContext = context;
				i++;
				id = list.get(i).getId();
				word = list.get(i).getWord();
				line = list.get(i).getLine();
				context = list.get(i).getContext();
				while(i < list.size() - 1 && (context.contains(methodContext) || context.equals(""))){
					//parameters
					if(id.contains("parameter")){
						varList.add(list.get(i));
					}
					//iterating through list of parameters
					else if(id.equals("var") && !id.contains("this")){
						for(int j = 0; j < varList.size(); j++){
							if(varList.get(j).getWord().equals(word.split("\\[")[0])){
								newToken = new Token(varList.get(j).getId(), word, line, context);
								list.set(i, newToken);
								break;
							}
						}
					}
					i++;
					id = list.get(i).getId();
					word = list.get(i).getWord();
					line = list.get(i).getLine();
					context = list.get(i).getContext();
				}
				varList = new ArrayList<Token>(); // reset varList
				i--; //while breaks when we're in a new context, so we need to go back one
			}
		}
		
		//second pass for the main method
		boolean inMain = false;
		for(int i = 0; i < list.size(); i++){
			id = list.get(i).getId();
			word = list.get(i).getWord();
			line = list.get(i).getLine();
			if(list.get(i).getContext().length() > 0){
				context = list.get(i).getContext();
			}
			String mainContext = "";
			if(id.contains("public") && id.contains("static") && id.contains("void") &&
			word.contains("main") && id.contains("String[]")){
				inMain = true;
				mainContext = context;
			}
			else if(inMain){
				if(id.contains("-var")){
					String id1, word1, context1 = context;
					int line1;
					for(int j = i + 1; j < list.size() && context1.contains(context); j++){
						id1 = list.get(j).getId();
						word1 = list.get(j).getWord();
						line1 = list.get(j).getLine();
						if(list.get(j).getContext().length() > 0){
							context1 = list.get(j).getContext();
						}
						if(id1.equals("var") && word.split("\\[")[0].equals(word1.split("\\[")[0])){
							newToken = new Token(id, word1, line1, context1);
							list.set(j, newToken);
						}
					}
				}
			}
			//out of main method
			if(!context.contains(mainContext) && !context.equals("")){
				break;
			}
			
		}
		
		//third pass for all the other method vars
		for(int i = 0; i < list.size(); i++){
			id = list.get(i).getId();
			word = list.get(i).getWord();
			line = list.get(i).getLine();
			if(list.get(i).getContext().length() > 0){
				context = list.get(i).getContext();
			}
			//found declared variable
			if(id.contains("-var")){
				String id1, word1, context1 = context;
				int line1;
				for(int j = i + 1; j < list.size() && context1.contains(context); j++){
					id1 = list.get(j).getId();
					word1 = list.get(j).getWord();
					line1 = list.get(j).getLine();
					if(list.get(j).getContext().length() > 0){
						context1 = list.get(j).getContext();
					}
					if(id1.equals("var") && word.split("\\[")[0].equals(word1.split("\\[")[0])){
						newToken = new Token(id, word1, line1, context1);
						list.set(j, newToken);
					}
				}
			}
		}
		
		//fourth pass for class vars, start from the end
		for(int i = list.size() - 1; i >= 0; i--){
			id = list.get(i).getId();
			word = list.get(i).getWord();
			line = list.get(i).getLine();
			if(list.get(i).getContext().length() > 0){
				context = list.get(i).getContext();
			}
			//found declared class variable
			if(id.contains("-cvar")){
				String id1, word1, context1 = context;
				int line1;
				for(int j = i + 1; j < list.size() && context1.contains(context); j++){
					id1 = list.get(j).getId();
					word1 = list.get(j).getWord();
					line1 = list.get(j).getLine();
					if(list.get(j).getContext().length() > 0){
						context1 = list.get(j).getContext();
					}
					if(id1.equals("var") && word.split("\\[")[0].equals(word1.split("\\[")[0])){
						newToken = new Token(id, word1, line1, context1);
						list.set(j, newToken);
					}
					else if(id1.equals("this-var") && word.split("\\[")[0].equals(word1.split("\\[")[0])){
						newToken = new Token("this-" + id, word1, line1, context1);
						list.set(j, newToken);
					}
				}
			}
		}
		fourthPass();
	}
	
	/**
	 * connect method calls to their original declarations
	 */
	public void fourthPass(){
		//make a list of methods
		ArrayList<Token> methodList = new ArrayList<>();
		for(Token m : list){
			if(m.getId().contains("-method")){
				methodList.add(m);
			}
		}
		String id;
		System.out.println(methodList);
		for(int i = 0; i < list.size(); i++){
			id = list.get(i).getId();
			
			if(id.equals("method")){
				findMethod(i, methodList);
			}
		}
	}
	
	//connects the method found at the given index of the original list
	//to the method instance it is calling
	//returns the return type of that method
	public String findMethod(int i, ArrayList<Token> methodList){
		String word = list.get(i).getWord();
		int line = list.get(i).getLine();
		//look through list of methods to find a match
		for(int j = 0; j < methodList.size(); j++){
			ArrayList<String> paramArr = getParam(i, methodList);
			String[] methParam = methodList.get(j).getId().split("-");
			//compare parameters
			boolean boolParam = false;
			boolean sameParam = true;
			int startIndex = 0;
			if(methodList.get(j).getWord().equals(word)){
				for(int k = 0; k < methParam.length; k++){
					if(methParam[k].equals("method")){
						boolParam = true;
						startIndex = k + 1;
						//no parameters
						if(startIndex == methParam.length && paramArr.get(0).equals("")){//why the second condition?
							break;
						}
						if(methParam.length - startIndex != paramArr.size()){
							sameParam = false;
						}
					}
					else if(boolParam){
						if(!methParam[k].equals(paramArr.get(k - startIndex))){
							sameParam = false;
							break;
						}
					}
				}
				if(sameParam){
					newToken = new Token(methodList.get(j).getId(), word, line, list.get(i).getContext());
					list.set(i, newToken);
					return methParam[startIndex - 2]; //the return type
				}
			
			}
		}
		return null;//no method found
	}
	
	//gets the parameters of the method found at the given index of the original list
	public ArrayList<String> getParam(int i, ArrayList<Token> methodList){
		//List of types
		ArrayList<String> type = new ArrayList<String>(
		Arrays.asList("byte", "short", "int", "long", "float", "double",
					"boolean", "char", "String", "Object",
					"byte[]", "short[]", "int[]", "long[]", "float[]", "double[]",
					"boolean[]", "char[]", "String[]", "Object[]"));
		int parenCount = 0;
		String id, word;
		ArrayList<String> param = new ArrayList<>();
		for(i = i + 1; i < list.size(); i++){
			id = list.get(i).getId();
			word = list.get(i).getWord();
			if(id.equals("Lparen")){
				parenCount++;
			}
			else if(type.contains(id)){
				param.add(id);
			}
			else if(id.contains("var")){
				String[] idArr = id.split("-");
				for(int j = 0; j < idArr.length; j++){
					if(type.contains(idArr[j].split("\\[")[0])){
						//When finding the type of an argument, arrays are tricky.
						//I'm counting the appearances of ['x'] to figure out its type.
						//E.g. int[][] arr can be used as int[][] (arr), int[] (arr[x]), or int (arr[x][y])
						int squareCount = 0;
						for(int k = 0; k < word.length(); k++){
							if(word.charAt(k) == '['){
								if(squareCount == 0){
									//remove a set of [] from id
									idArr[j] = idArr[j].substring(0, Math.max(0, idArr[j].length() - 2));
								}
								squareCount++;
							}
							else if(word.charAt(k) == ']'){
								squareCount--;
							}
						}
						param.add(idArr[j]);
					}
				}
			}
			else if(id.equals("method")){
				param.add(findMethod(i, methodList));
				int parenCount2 = 0;
				i++;
				do{
					if(list.get(i).getId().equals("Lparen")){
						parenCount2++;
					}
					else if(list.get(i).getId().equals("Rparen")){
						parenCount2--;
					}
					i++;
				}while(parenCount2 != 0);
			}
			else if(id.equals("Rparen")){
				parenCount--;
				if(parenCount == 0){
					break;
				}
			}
		}
		return param;
	}
	
	//sort vars by context and whether or not they are a parameter
	public void sortByContext(ArrayList<Token> list){
		int[] size = new int[list.size()];
		//get number of dashes
		for(int i = 0; i < list.size(); i++){
			//get size from splitting by '-'
			size[i] = list.get(i).getContext().split("-").length;
		}
		//sort by size
		for(int i = 0; i < size.length - 1; i++){
			for(int j = i + 1; j < size.length; j++){
				if(size[j] < size[i]){
					//swap size values
					int k = size[i];
					size[i] = size[j];
					size[j] = k;
					//swap list values
					Token m = list.get(i);
					list.set(i, list.get(j));
					list.set(j, m);
				}
			}
		}
		//sort by parameter vs non parameter
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getId().contains("parameter")){
				list.add(0, list.get(i));
				list.remove(i);
				i--;
			}
		}
	}
	
	//Turns context arraylist to a string
	public static String context(ArrayList<String> context){
		if(context.size() > 0){
			String contextString = "";
			for(String i : context){
				contextString += i + "-";
			}
			return contextString.substring(0, contextString.length() - 1);
		}
		return "";
	}
	
	//Makes sure each context is unique
	public void makeUnique(ArrayList<String> contextList, ArrayList<String> context){
		int i = 1;
		int last = context.size() - 1;
		String orig = context.get(last);
		while(contextList.contains(context(context))){
			String word = i + orig;
			context.set(last, word);
			i++;
		}
		contextList.add(context(context));
	}
	
}