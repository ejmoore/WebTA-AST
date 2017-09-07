package ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import expr.Assign;
import expr.BinaryOp;
import expr.Constant;
import expr.Expr;
import expr.MethodCall;
import expr.NewArray;
import expr.NewConstructor;
import expr.Parameter;
import expr.StmtExpr;
import expr.UnaryOp;
import expr.Variable;
import expr.VariableArray;
import lexer.Token;
import node.*;

public class BuildHelper {
	ArrayList<Token> lex;				//The list of tokens this AST is built on (same one that's in BuildAST)
	ArrayList<String> type;				//List of mostly primitive types used for identifying constants and variables
	ArrayList<String> arrayType;		//List of array types used for identifying arrays
	ArrayList<Annotation> annotation;	//Keeps track of the list of annotations for a method or class
	
	public BuildHelper(ArrayList<Token> list) {
		lex = list;
		type = new ArrayList<String>(
			Arrays.asList("byte", "short", "int", "long", "float",
			"double", "boolean", "char", "String", "Object"));
		arrayType = new ArrayList<String>(
			Arrays.asList("byte[]", "short[]", "int[]", "long[]", "float[]",
			"double[]", "boolean[]", "char[]", "String[]"));
		annotation = new ArrayList<>();
	}
	
	/**
	 * Fills in the fields for the Annotation object given
	 * 
	 * @param anno The Annotation object to be built
	 */
	public void buildAnnotation(Annotation anno){
		String word = anno.getToken().getWord();
		String id = "", element = "";		
		int i = 1; 												//skip @
		for(; i < word.length(); i++){
			char c = word.charAt(i);
			if(c == '(') break;
			id += "" + c;
		}
		for(i = i + 1; i < word.length() - 1; i++){				//avoid parentheses
			char c = word.charAt(i);
			element += "" + c;
		}
		anno.setId(id);
		anno.setElement(element);
	}
	
	/**
	 * Builds the node for assign expressions (e.g. foo = 5)
	 * 
	 * @param assign The root node of this assign expression
	 * 
	 * @param index The index of assign's token in lex
	 * 
	 * @return The index at the end of this expression
	 */
	public int buildAssign(Assign assign, int index){
		Token t = lex.get(index);
		String id = t.getId();
		Variable var = new Variable(t);
		assign.setLValue(var);
		assign.setOp(lex.get(index + 1).getWord());
		index += 2; 										//skip the assign operator
		int parenCount = 0;
		ArrayList<Token> list = new ArrayList<>();
		t = lex.get(index);
		id = t.getId();
		parenCount += (id.equals("Lparen")) ? 1 : 0;
		parenCount -= (id.equals("Rparen")) ? 1 : 0;
		while(!id.equals("semi_colon") && !(id.equals("comma") && parenCount == 0) 	//Second condition expressly for for loops
				&& !(id.equals("Rparen") && parenCount == -1)){
			list.add(t);
			index++;
			t = lex.get(index);
			id = t.getId();
			parenCount += (id.equals("Lparen")) ? 1 : 0;
			parenCount -= (id.equals("Rparen")) ? 1 : 0;
		}
		assign.setExpr(buildExpr(list));
		return index;
	}
	
	/**
	 * Converts the given list of expressions to reverse polish notation,
	 * then it reformats the list of expressions as a tree that can be
	 * properly evaluated.
	 * 
	 * @param list The list of expressions to be reformatted
	 * 
	 * @return The root node of the expression tree created from list
	 */
	public Expr buildExpr(ArrayList<Token> list){
		if(list.isEmpty()){
			return null;
		}
		
		Stack<Token> stack = new Stack<>();
		Stack<Integer> precedence = new Stack<>();
		ArrayList<Token> out = new ArrayList<>();
		String id, word, varType;
		ArrayList<VariableArray> vararr = new ArrayList<>(); //for array variables in the list of tokens
		ArrayList<Expr> newList = new ArrayList<>(); //for new arrays and constructor calls in the list of tokens
		for(int i = 0; i < list.size(); i++){
			Token t = list.get(i);
			id = t.getId();
			word = t.getWord();
			varType = t.getType();
			
			if(id.contains("method")){
				stack.push(t);
			}
			/*
			 * I don't think arrays can be evaluated using RPN, so I came up with this.
			 * When we find an array, we take everything related to it, it's brackets and indices,
			 * and evaluate them in a separate method. We take out from the list everything except
			 * the array so buildExpr() doesn't evaluate it. Then, when building the expression,
			 * when we find the array we replace it with the expression we built separately.
			 */
			else if((id.contains("var") || id.contains("pvar")) && varType.contains("[]")){
				vararr.add(new VariableArray(t));
				out.add(t);
				i = buildVariableArray(list, vararr.get(vararr.size() - 1), i);
			}
			else if(id.equals("const") || id.contains("var") || id.contains("pvar")){
				out.add(t);
			}
			else if(id.equals("new")){
				if(list.get(i + 2).getId().equals("Lbracket") || list.get(i + 2).getType().contains("[]")){
					NewArray newArray = new NewArray(t);
					out.add(t);
					i += buildNewArray(newArray, t.getIndex()) - t.getIndex();
					newList.add(newArray);
				}
				else if(list.get(i + 1).getId().equals("constructor")){
					NewConstructor newCon = new NewConstructor(t);
					out.add(t);
					i += buildNewConstructor(newCon, t.getIndex()) - t.getIndex();
					newList.add(newCon);
				}
			}
			else if(id.equals("comma")){
				while(!stack.peek().getId().equals("Lparen")){
					out.add(stack.pop());
					if(stack.isEmpty()){
						System.out.println("Misplaced comma or unbalanced parentheses. Line " + out.get(out.size() - 1).getLine());
					}
				}
			}
			else if(id.contains("op")){
				int prec;
				String assoc;
				
				if(id.equals("dot_op")){
					prec = 16;
					assoc = "left";
				}
				else if((id.contains("post") && id.contains("unary_op")) || word.equals("~")){
					prec = 15;
					assoc = "left";
				}
				else if(id.contains("unary_op")){
					prec = 14;
					assoc = "right";
				}
				//else if cast or new X (not yet implemented)
				else if(word.equals("*") || word.equals("/") || word.equals("%")){
					prec = 13;
					assoc = "left";
				}
				else if(word.equals("+") || word.equals("-")){
					prec = 12;
					assoc = "left";
				}
				else if(word.equals("<<") || word.equals(">>") || word.equals(">>>")){
					prec = 11;
					assoc = "left";
				}
				else if(id.equals("compare_op")){
					prec = 10;
					assoc = "left";
				}
				else if(id.equals("equality_op")){
					prec = 9;
					assoc = "left";
				}
				else if(word.equals("&")){
					prec = 8;
					assoc = "left";
				}
				else if(word.equals("^")){
					prec = 7;
					assoc = "left";
				}
				else if(word.equals("|")){
					prec = 6;
					assoc = "left";
				}
				else if(word.equals("&&")){
					prec = 5;
					assoc = "left";
				}
				else if(word.equals("||")){
					prec = 4;
					assoc = "left";
				}
				else if(word.equals("?")){
					prec = 3;
					assoc = "right";
				}
				else if(id.equals("assign_op")){
					prec = 2;
					assoc = "right";
				}
				else{
					prec = 1;
					assoc = "left";
				}
				//Set associativity
				if(stack.isEmpty() || !stack.peek().getId().contains("op") || prec > precedence.peek()){
					stack.push(t);
					precedence.push(prec);
				}
				else{
					while(!stack.isEmpty() && stack.peek().getId().contains("op") 
					&& ((assoc.equals("left") && prec <= precedence.peek()) || assoc.equals("right") && prec < precedence.peek())){
						out.add(stack.pop());
						precedence.pop();
					}
					stack.push(t);
					precedence.push(prec);
				}
			}
			else if(id.equals("Lparen")){
				stack.push(t);
			}
			else if(id.equals("Rparen")){
				while(!stack.peek().getId().equals("Lparen")){
					out.add(stack.pop());
					if(stack.isEmpty()){						//Unbalanced Parentheses
						System.out.println("Mismatched parentheses. Line " + out.get(out.size() - 1).getLine());
					}
				}
				stack.pop();									//pop the Lparen
				if(!stack.isEmpty() && stack.peek().getId().contains("method")) out.add(stack.pop()); //pop the method
			}
			//a period means a method or variable is coming up
			//else if(id.equals("dot_op")){
			//	out.add(t); //???
			//}
			else{
				System.out.println("Unreadable token: " + t);
			}
		}
		while(!stack.isEmpty()){								//pop the remaining operators
			if(stack.peek().getId().equals("Lparen")){
				System.out.println("Unbalanced parentheses. Line " + stack.peek().getLine());
				return null;
			}
			else{
				out.add(stack.pop());
			}
		}
		
/*Finish building the expression that is now in Reverse Polish Notation*/
		Stack<Expr> operand = new Stack<>();
		for(int i = 0; i < out.size(); i++){
			Token t = out.get(i);
			id = t.getId();
			varType = t.getType();
			if(id.equals("const")){
				Constant con = new Constant(t);
				operand.push(con);
			}
			else if(varType.contains("[]")){
				operand.push(vararr.remove(0));
				//model after method
				//only problem is we don't know how many
				//elements are indices of the array variable
				//maybe make a list to keep track of the
				//number of array and nested array indices
			}
			else if(id.equals("new")){
				operand.push(newList.remove(0));
			}
			else if(id.contains("var")){
				Variable var = new Variable(t);
				var.setId(t.getWord());
				operand.push(var);
			}
			else if(id.equals("pvar")){
				Parameter param = new Parameter(t);
				param.setId(t.getWord());
				operand.push(param);
			}
			else if(id.equals("method") || id.equals("constructor")){
				String[] paramArr = t.getParam().split("-");
				 //Find number of parameters in method, special case needed for 0 parameters because of split()
				int paramCount = (paramArr.length == 1 && paramArr[0].equals("")) ? 0 : paramArr.length;
				//Should I verify the arguments are the right type?
				Expr[] exp = new Expr[paramCount];				//Add arguments to method
				for(int j = paramCount - 1; j >= 0; j--){
					exp[j] = operand.pop();
				}
				MethodCall method = new MethodCall(t);
				method.setId(t.getWord());
				method.setArg(exp);
				operand.push(method);
			}
			else{												//Operator
				if(id.contains("unary_op")){
					UnaryOp un;
					if(id.contains("post")) un = new UnaryOp(operand.peek().getToken());
					else un = new UnaryOp(t);
					
					un.setOp(t.getWord());
					un.setExpr(operand.pop());
					operand.push(un);
				}
				//will need tweaking when adding variables
				else if(id.equals("dot_op")){
					if(operand.peek() instanceof MethodCall){
						MethodCall methodCall = (MethodCall)operand.pop(), temp = methodCall;
						while(temp.getCaller() != null) temp = (MethodCall)temp.getCaller();
						temp.setCaller(operand.pop());
						operand.push(methodCall);
					}
					//else if not a method (variable?)
				}
				else{
					BinaryOp bin = new BinaryOp(t);
					bin.setOp(t.getWord());
					bin.setExpr2(operand.pop());
					bin.setExpr1(operand.pop());
					operand.push(bin);
				}
			}
		}
		return operand.get(0);									//Operand should have only one element in it, the head of the expression
	}
	
	/**
	 * Builds the first and third arguments of a basic for loop
	 * if they are single or multiple statement expressions.
	 * 
	 * @param list The list of one or more StmtExpr's in the for loop argument
	 * 
	 * @param index The index of the first token in the for loop argument
	 * 
	 * @param arg 1 or 3, determines which argument is being read
	 * 
	 * @return The index at the last token read in.
	 */
	public int buildForArg(ArrayList<StmtExpr> list, int index, int arg){
		String end;
		if(arg == 1) end = "semi_colon";
		else if(arg == 3) end = "Rparen";
		else{
			System.out.println("Wrong input for buildForArg");
			end = "";
		}
		Token t = lex.get(index);
		String id = t.getId();
		Assign assign2;
		UnaryOp unary;
		for(; !id.equals(end); index++, t = lex.get(index), id = t.getId()){
			assign2 = null;
			unary = null;
			if(id.contains("unary_op")){
				unary = new UnaryOp(t);
				Variable v = new Variable(lex.get(index + 1));
				unary.setExpr(v);
				unary.setOp(t.getWord());
				list.add(unary);
				index++; 											//pass the variable right after the unary operator
			}
			else if((id.contains("var") || id.contains("pvar")) &&
					lex.get(index + 1).getId().contains("unary_op")){
				unary = new UnaryOp(t);
				Variable v = new Variable(t);
				unary.setExpr(v);
				unary.setOp(lex.get(index + 1).getWord());
				list.add(unary);
				index++; 											//pass the unary operator that we read ahead for
			}
			else if(id.contains("var") || id.contains("pvar")){
				assign2 = new Assign(t);
				index = buildAssign(assign2, index) - 1;				//So we don't miss the ')' at the end.
				list.add(assign2);					
			}
			else if(id.contains("method")){
				ArrayList<Token> methodList = new ArrayList<>();
				methodList.add(t);
				int parenCount = 0;
				do{
					index++;
					t = lex.get(index);
					id = t.getId();
					parenCount += (id.equals("Lparen")) ? 1 : 0;
					parenCount -= (id.equals("Rparen")) ? 1 : 0;
					methodList.add(t);
				}while(parenCount != 0);
				
				MethodCall m = (MethodCall)buildExpr(methodList);
				list.add(m);
			}
		}
		return index;
	}
	
	/**
	 * Builds a new array (new int[5] or new int[]{1, 2, 3} for example)
	 * @param array The node to be built
	 * @param index Where the new array begins, either at 'new' or a left brace
	 * @return The index of the last right brace or bracket ending the expression
	 */
	public int buildNewArray(NewArray array, int index){
		Token t = lex.get(index);
		boolean isNew = false;
		if(t.getId().equals("new")){
			isNew = true;
			t = lex.get(++index);
		}
		String type = ""; //documents the type of the array, int, double, String, etc.
		ArrayList<Expr> length = new ArrayList<>(); //for the length field of new array
		if(t.getId().equals("type")){
			ArrayList<Token> expr = new ArrayList<>(); //to be evaluated by buildExpr() then added to length
			type = t.getWord();
			int bracketCount = (lex.get(index + 1).getId().equals("Lbracket")) ? 1 : 0;
			for(index += 2; index < lex.size() && (bracketCount != 0 || lex.get(index).getId().equals("Lbracket")); index++){
				t = lex.get(index);
				if(t.getId().equals("Lbracket") && bracketCount == 0) bracketCount++;
				else if(t.getId().equals("Rbracket") && bracketCount == 1){
					bracketCount--;
					length.add(buildExpr(expr));
					expr = new ArrayList<>();
					type += "[]";
				}
				else if(t.getId().equals("Lbracket")){
					bracketCount++;
					expr.add(t);
				}
				else if(t.getId().equals("Rbracket")){
					bracketCount--;
					expr.add(t);
				}
				else expr.add(t);
			}
			t = lex.get(index); //moves it to either a semi_colon or left brace
		}
		ArrayList<Token> value = new ArrayList<Token>();
		if(t.getId().equals("Lbrace")){
			int braceCount = 1;
			value.add(t);
			for(index += 1; index < lex.size() && braceCount != 0; index++){
				t = lex.get(index);
				braceCount += (t.getId().equals("Lbrace")) ? 1 : 0;
				braceCount -= (t.getId().equals("Rbrace")) ? 1 : 0;
				value.add(t);
			}
		}
		
		array.setIsNew(isNew);
		array.setType(type);
		array.setLength(length);
		array.setValue(value);
		return index - 1;
	}
	
	public int buildNewConstructor(NewConstructor newCon, int index){
		Token t = lex.get(index);
		//Does the constructor have 'new' right before it?
		boolean isNew = false;
		if(t.getId().equals("new")){
			isNew = true;
			t = lex.get(++index);
		}
		String id = t.getWord();
		t = lex.get(++index);
		//Does the constructor have any generics? - May need to be modified for nested corner brackets
		ArrayList<String> genericList = new ArrayList<>();
		if(t.getWord().equals("<")){
			for(index += 1; index < lex.size() && !lex.get(index).getWord().equals(">"); index++){
				Token generic = lex.get(index);
				if(!generic.getId().equals("comma")) genericList.add(generic.getWord());
			}
			t = lex.get(++index);
		}
		
		ArrayList<Expr> exprList = new ArrayList<>();
		if(t.getId().equals("Lparen")){
			ArrayList<Token> expr = new ArrayList<>();
			int parenCount = 1, braceCount = 0, cornerCount = 0; //current token is Lparen so parenCount = 1
			for(index += 1; index < lex.size() && parenCount != 0; index++){
				t = lex.get(index);
				//arguments are separated by comma, but watch out for commas from method calls, arrays, etc
				if(t.getId().equals("comma") && parenCount == 1 && braceCount == 0 && cornerCount == 0){
					exprList.add(buildExpr(expr));
					expr = new ArrayList<>();
				}
				else if(t.getId().equals("Lparen")) parenCount++;
				else if(t.getId().equals("Rparen")) parenCount--;
				else if(t.getId().equals("Lbrace")) braceCount++;
				else if(t.getId().equals("Rbrace")) braceCount--;
				else if(t.getWord().equals("<")) cornerCount++;
				else if(t.getWord().equals(">")) cornerCount--;
				else expr.add(t);
			}
			exprList.add(buildExpr(expr)); //add last expr to exprList
		}
		newCon.setIsNew(isNew);
		newCon.setId(id);
		newCon.setGenericList(genericList);
		newCon.setArg(exprList);
		return index - 1;
	}
	
	/**
	 * Builds an array variable with its possible indices
	 * @param out The list of tokens already evaluated into expressions
	 * @param array The node to be built
	 * @param index The index where the variable identifier can be found
	 * @return The index of the last right bracket
	 */
	public int buildVariableArray(ArrayList<Token> out, VariableArray array, int index){
		array.setId(out.get(index).getWord());
		int bracketCount = 0;
		int i = index + 1;
		for(; i < out.size() && (out.get(i).getId().equals("Lbracket") || out.get(i).getId().equals("Rbracket") || bracketCount != 0); i++){
			if(out.get(i).getId().equals("Lbracket")){
				ArrayList<Token> expr = new ArrayList<>();
				int bracketCount2 = 1;
				for(i += 1; ; i++){
					bracketCount2 += (out.get(i).getId().equals("Lbracket")) ? 1 : 0;
					bracketCount2 -= (out.get(i).getId().equals("Rbracket")) ? 1 : 0;
					if(bracketCount2 == 0) break;
					expr.add(out.get(i));
				}
				array.getIndices().add(buildExpr(expr));
			}
		}
		return i;
	}
	
	/**
	 * Starts at a left marker and counts every instance of Lmarker as +1
	 * and Rmarker as -1. Keeps iterating until markCount == 0.
	 * right-parenthesis
	 * @param index The index of the first instance of Lmarker
	 * @return The index of the last matching Rmarker
	 */
	public int getToEndingMarker(String Lmarker, String Rmarker, int index){
		int markCount = (lex.get(index).getWord().equals(Lmarker)) ? 1 : 0;
		if(markCount == 0) return index; //Lmarker was not found at given index
		for(index += 1; index < lex.size() && markCount != 0; index++){
			markCount += (lex.get(index).getWord().equals(Lmarker)) ? 1 : 0;
			markCount -= (lex.get(index).getWord().equals(Rmarker)) ? 1 : 0;
		}
		return index - 1; //index - 1 because it increments one too far in the for loop
	}
}
