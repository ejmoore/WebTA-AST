package lexer;
import java.util.*;
import java.io.*;

/*
 * Note: Make a list of tokens for function names you find as you read the program
 */

public class Lex implements LexInterface{
	private Scanner scanner;
	private String file;
	public ArrayList<Token> list;
	public ArrayList<String> classNames; //list of class or interface names in the file
	public ArrayList<String> imports;
	public ArrayList<Token> comments;
	
	public Lex(String src)throws FileNotFoundException{
		file = src;
		scanner = new Scanner(new File(file));
		list = new ArrayList<Token>();
		classNames = new ArrayList<>();
		imports = new ArrayList<String>();
		comments = new ArrayList<Token>();
	}
	
	public void lexer(){
		String line, word;
		int lineNumber = 0, lineChange = 0; //lineChange is used for block comments
		while(scanner.hasNextLine()){
			line = scanner.nextLine();
			lineNumber++;
			//get past line number that was added to each line
			int i = 0;
			while(!Character.isWhitespace(line.charAt(i))){
				i++;
			}
			for(; i < line.length(); i++){
				word = "";
				while(i < line.length() && !Character.isWhitespace(line.charAt(i))){
					//string
					if(line.charAt(i) == '"'){
						word += "" + line.charAt(i);
						i++;
						//Breaks if it reaches the end of the string
						while(line.charAt(i) != '"' || (line.charAt(i - 1) == '\\' && line.charAt(i - 2) != '\\')){
							word += "" + line.charAt(i);
							i++;
						}
						word += line.charAt(i);
						break;
					}
					//char - not how char works, but that's not important right now
					else if(line.charAt(i) == '\''){
						word += "" + line.charAt(i);
						i++;
						while(line.charAt(i) != '\'' || (line.charAt(i - 1) == '\\' && line.charAt(i - 2) != '\\')){
							word += "" + line.charAt(i);
							i++;
						}
						word += "" + line.charAt(i);
						break;
					}
					//line comment
					else if(line.charAt(i) == '/' && line.charAt(i - 1) == '/'){
						word = line.substring(i - 1, line.length());
						i = line.length();
						break;
					}
					//block comment
					else if(line.charAt(i) == '*' && line.charAt(i - 1) == '/'){
						while(line.charAt(i) != '/' || line.charAt(i - 1) != '*'){
							word += "" + line.charAt(i);
							i++;
							if(i == line.length()){
								line = scanner.nextLine();
								lineChange++;
								//get past line number that was added to each line
								i = 0;
								while(!Character.isWhitespace(line.charAt(i))){
									i++;
								}
								word += "\n";
							}
						}
						word += line.charAt(i);
						break;
					}
					//annotation
					else if(line.charAt(i) == '@'){
						while(line.charAt(i) != '('){
							word += "" + line.charAt(i);
							i++;
							if(i == line.length()){
								line = scanner.nextLine();
								lineChange++;
								//get past line number that was added to each line
								i = 0;
								while(!Character.isWhitespace(line.charAt(i))){
									i++;
								}
								word += "\n";
							}
						}
						int parenCount = 0;
						do{
							word += line.charAt(i);
							parenCount += (line.charAt(i) != '(') ? 1 : 0;
							parenCount -= (line.charAt(i) != ')') ? 1 : 0;
							i++;
							if(i == line.length()){
								line = scanner.nextLine();
								lineChange++;
								//get past line number that was added to each line
								i = 0;
								while(!Character.isWhitespace(line.charAt(i))){
									i++;
								}
								word += "\n";
							}
						}while(parenCount != 0);
						//word += line.charAt(i);
						break;
					}
					//normal stuff
					else{
						word += "" + line.charAt(i);
						i++;
					}
				}
				//Tokenize
				if(!word.equals("")){
					Token token;
					String id = getToken(word);
					if(constType.contains(id)){
						token = new Token("const", word, lineNumber, "");
						token.setType(id);
					}
					else token = new Token(id, word, lineNumber, "");
					token.setIndex(list.size());
					list.add(token);
					lineNumber += lineChange;
					lineChange = 0;
				}
			}
		}
	}
	
	//Returns a String representing the
	//token of the given String
	public String getToken(String str){
		/*Is str a number?*/
		//Check if it's a number
		boolean isInt = true,
				isDouble = false, //Becomes true if decimal detected
				isFloat = false;
		int index = 0;
		while(index < str.length() && (str.charAt(index) == '+' || str.charAt(index) == '-')){
			index++;
		}
		if(index == str.length()) isInt = false; //This means '+' or '-' were by themselves, without a number
		for(; index < str.length(); index++){
			//str is a double if it has one and only one decimal
			if(str.charAt(index) == '.' && !isDouble && str.length() > 1){
				isDouble = true;
			}
			else if(index == str.length() - 1 && str.charAt(index) == 'f' && str.length() > 1){ //str.length() > 1 so a variable named 'f' doesn't mess it up
				isFloat = true;
			}
			else if(!Character.isDigit(str.charAt(index))){
				isInt = false;
				break;
			}
		}

		//If isInt and isDouble are both true, it's a double
		if(isFloat && (isInt || isDouble)) return "float";
		else if(isInt && isDouble) return "double";
		else if(isInt) return "int";
		
		/*Is str a String or char?*/
		if(str.charAt(0) == '"'){
			return "String";
		}
		if(str.charAt(0) == '\''){
			return "char";
		}
		
		/*Is str null?*/
		if(str.equals("null")){
			return "const";
		}
		
		/*Is str an operator?*/
		if(str.length() <= 4){
			String[] arithOp = {"+", "-", "*", "/", "%"};
			String[] assignOp = {"=", "+=", "-=", "*=", "/=", "%=", "<<=", ">>=", ">>>=", "&=", "|=", "^="};
			String[] bitwiseOp = {"<<", ">>", ">>>"};
			String[] compOp = {"<", ">", "<=", ">="};
			String[] equalityOp = {"==", "!="};
			String[] logicOp = {"&&", "||", "&", "|", "^"};
			String[] ternaryOp = {"?"};
			String[] unaryOp = {"++", "--", "!", "~"};

			for(int i = 0; i < arithOp.length; i++){
				if(str.equals(arithOp[i])) return "arithmetic_op";
			}
			for(int i = 0; i < assignOp.length; i++){
				if(str.equals(assignOp[i])) return "assign_op";
			}
			for(int i = 0; i < bitwiseOp.length; i++){
				if(str.equals(bitwiseOp[i])) return "bitwise_op";
			}
			for(int i = 0; i < compOp.length; i++){
				if(str.equals(compOp[i])) return "compare_op";
			}
			for(int i = 0; i < equalityOp.length; i++){
				if(str.equals(equalityOp[i])) return "equality_op";
			}
			for(int i = 0; i < logicOp.length; i++){
				if(str.equals(logicOp[i])) return "logic_op";
			}
			for(int i = 0; i < ternaryOp.length; i++){
				if(str.equals(ternaryOp[i])) return "ternary_op";
			}
			for(int i = 0; i < unaryOp.length; i++){
				if(str.equals(unaryOp[i])) return "unary_op";
			}
		}

		/*Is str a keyword?*/
		ArrayList<String> type = new ArrayList<String>(
		Arrays.asList("byte", "Byte", "short", "Short", "int", "Integer", 
					"long", "Long", "float", "Float", "double", "Double",
					"boolean", "Boolean", "char", "Character", "String", 
					"byte[]", "short[]", "int[]", "long[]", "float[]", 
					"double[]", "boolean[]", "char[]", "String[]"));
		
		/*
		utilType = new ArrayList<String>(
		Arrays.asList("Collection", "Comparator", "Deque", "Enumeration", 
		"EventListener", "Formattable", "Iterator", "List", "ListIterator", 
		"Map", "Map.Entry", "NavigableMap", "NavigableSet", "Observer",
		"Queue", "RandomAccess", "Set", "SortedMap", "SortedSet",
		"AbstractCollection", "AbstractList", "AbstractMap", "AbstractQueue",
		"AbstractSet", "ArrayDeque", "ArrayList", "Arrays", "BitSet",
		"Calendar", "Collections", "Currency", "Date", "Dictionary",
		"EnumMap", "EnumSet", "EventListenerProxy", "EventObject",
		"FormattableFlags", "Formatter", "GregorianCalendar", "HashMap",
		"HashSet", "Hashtable", "IdentityHashMap", "LinkedHashMap",
		"LinkedHashSet", "LinkedList", "ListResourceBundle", "Locale", "Objects",
		"Observable", "PriorityQueue", "Properties", "PropertyPermission",
		"PropertyResourceBundle", "Random", "ResourceBundle", "Scanner",
		"ServiceLoader", "SimpleTimeZone", "Stack", "StringTokenizer",
		"Timer", "TimerTask", "TimeZone", "TreeMap", "TreeSet", "UUID",
		"Vector", "WeakHashMap"));
		*/				
		ArrayList<String> declare = new ArrayList<>(
		Arrays.asList("class", "interface", "enum"));
		
		ArrayList<String> modifier = new ArrayList<>(
		Arrays.asList("public", "private", "protected", "static", "abstract"));

		ArrayList<String> conditional = new ArrayList<>(
		Arrays.asList("if", "else", "switch"));
		
		ArrayList<String> loop = new ArrayList<>(
		Arrays.asList("for", "do", "while"));
		
		ArrayList<String> bool = new ArrayList<>(
		Arrays.asList("true", "false"));
		
		ArrayList<String> branch = new ArrayList<>(
		Arrays.asList("continue", "break", "return"));
		
		ArrayList<String> other = new ArrayList<>(
		Arrays.asList("assert", "case", "catch", "const", 
		"default", "final", "finally", "goto", 
		"import", "instanceof", "native", "new", "package", 
		"strictfp", "super", "synchronized", "this", 
		"throws", "throw", "transient", "try", "void", 
		"volatile"));
		
		if(type.contains(str))			return "type";
		//if(utilType.contains(str))	return "utilType";
		if(declare.contains(str))		return "declare";
		if(modifier.contains(str))		return "modifier";
		if(conditional.contains(str))	return "conditional";
		if(loop.contains(str))			return "loop";
		if(bool.contains(str))			return "boolean";
		if(branch.contains(str))		return "branch";
		if(other.contains(str))			return str;
		if(str.equals("void"))			return "return-type";
		if(str.contains("Exception"))	return "exception";
		
		//For multi-dimensional arrays
		String tempString = "";
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) != '[' && str.charAt(i) != ']'){
				tempString += str.charAt(i);
			}
		}
		if(type.contains(tempString)) return "type";
		
		/*Is str a comment?*/
		if(str.length() > 1){
			if(str.charAt(0) == '/' &&
			(str.charAt(1) == '/' || str.charAt(1) == '*')) return "comment";
		}
		
		/*Is str an annotation?*/
		if(str.charAt(0) == '@'){
			return "annotation";
		}
		
		switch(str){
			case "(": return "Lparen";
			case ")": return "Rparen";
			case "{": return "Lbrace";
			case "}": return "Rbrace";
			case "[": return "Lbracket";
			case "]": return "Rbracket";
			case ",": return "comma";
			case ".": return "dot_op";
			case ";": return "semi_colon";
			case ":": return "colon";
			case "&": return "ampersand";
			case "|": return "bar";
			default: break;
		}
		return "other";
	}
	
	public ArrayList<Token> lex(String write)throws FileNotFoundException, ClassNotFoundException{
		Format format = new Format();
		format.format(file, write);
		Lex lex = new Lex(write);
		lex.lexer();
		scanner.close();
		//Remove all comments from the list
		ArrayList<Token> newList = lex.list;
		for(int i = 0; i < newList.size(); i++){
			if(newList.get(i).getId().equals("comment")){
				comments.add(newList.get(i));
				newList.remove(i);
				i--;
			}
			newList.get(i).setIndex(i);
		}
		//ExtraLex extra = new ExtraLex(newList);
		//extra.firstPass();
		AltLex alt = new AltLex(newList);
		alt.pass();
		return lex.list;
	}
}