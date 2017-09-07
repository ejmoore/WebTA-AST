package ast;

import java.util.ArrayList;
import java.util.Arrays;

import expr.Expr;
import expr.NewArray;
import stmt.BranchStmt;
import stmt.CaseStmt;
import stmt.PrintStmt;
import stmt.TypeDecl;
import stmt.VarDecl;
import stmt.VarDeclArray;
import lexer.Token;
import node.*;

public class BuildStmtHelper {
	ArrayList<Token> lex;				//The list of tokens this AST is built on (same one that's in BuildAST)
	ArrayList<String> type;				//List of mostly primitive types used for identifying constants and variables
	ArrayList<String> arrayType;		//List of array types used for identifying arrays
	ArrayList<Annotation> annotation;	//Keeps track of the list of annotations for a method or class
	BuildHelper helper;					//Instance BuildHelper used to store many useful methods
	
	public BuildStmtHelper(ArrayList<Token> list) {
		lex = list;
		type = new ArrayList<String>(
			Arrays.asList("byte", "short", "int", "long", "float",
			"double", "boolean", "char", "String", "Object"));
		arrayType = new ArrayList<String>(
			Arrays.asList("byte[]", "short[]", "int[]", "long[]", "float[]",
			"double[]", "boolean[]", "char[]", "String[]"));
		annotation = new ArrayList<>();
		helper = new BuildHelper(list);
	}
	
	/**
	 * Builds the node for return, break, and continue statements
	 * @param branch The root node of this branch statement
	 * @param index The index of branch's token in lex
	 * @return The index at the end of this statement
	 */
	public int buildBranchStmt(BranchStmt branch, int index){
		index += 1;
		if(branch.getToken().getWord().equals("return")){			//for return stmt
			ArrayList<Token> expList = new ArrayList<>();
			for(; !lex.get(index).getId().equals("semi_colon"); index++){
				expList.add(lex.get(index));
			}
			branch.setExpr(helper.buildExpr(expList));
		}
		else if(lex.get(index).getId().equals("label")){		//for break and continue stmt
			branch.setLabel(lex.get(index).getWord());
			index++;	//move on to next token, the semi_colon
		}
		return index;
	}
	
	/**
	 * Builds the node that holds the case expression and its body
	 * @param stmt The root node of this case
	 * @param index The index of stmt's token in lex
	 * @return The index at the end of this statement (the colon)
	 */
	public int buildCaseStmt(CaseStmt stmt, int index){
		ArrayList<Token> tokenList = new ArrayList<>();
		index++;
		Token t = lex.get(index);
		String id = t.getId();
		for(; !id.equals("colon"); index++, t = lex.get(index), id = t.getId()) tokenList.add(t);
		stmt.setExpr(helper.buildExpr(tokenList));
		return index;
	}
	
	public int buildTypeDecl(TypeDecl type, int index){
		
		
		return index;
	}
	
	/**
	 * Basic implementation of accepting print statements.
	 * It knows the kind of print statement and what's being printed.
	 * 
	 * @param print The type of print statement (print, println, printf, etc)
	 * 
	 * @param index The index of print's token in lex
	 * 
	 * @return The index at the end of this statement
	 */
	public int buildPrintStmt(PrintStmt print, int index){
		index += 2; 										//skip '('
		ArrayList<Token> tokenList = new ArrayList<>();
		int parenCount = 0;
		Token t = lex.get(index);
		String id = t.getId();
		while(!(id.equals("comma") && parenCount == 0) && !(id.equals("Rparen") && parenCount == -1)){
			tokenList.add(t);
			index++;
			t = lex.get(index);
			id = t.getId();
			parenCount += (id.equals("Lparen")) ? 1 : 0;
			parenCount -= (id.equals("Rparen")) ? 1 : 0;
		}
		print.setExpr(helper.buildExpr(tokenList));
		if(print.getToken().getWord().contains("printf")){		//for printf statements
			ArrayList<Expr> arg = new ArrayList<>();
			tokenList = new ArrayList<>();
			parenCount = 0;
			t = lex.get(index + 1);
			id = t.getId();
			while(!(id.equals("Rparen") && parenCount == -1)){
				index++;
				t = lex.get(index);
				id = t.getId();
				while(!(id.equals("comma") && parenCount == 0) && !(id.equals("Rparen") && parenCount == -1)){
					tokenList.add(t);
					index++;
					t = lex.get(index);
					id = t.getId();
					parenCount += (id.equals("Lparen")) ? 1 : 0;
					parenCount -= (id.equals("Rparen")) ? 1 : 0;
				}
				arg.add(helper.buildExpr(tokenList));
				tokenList = new ArrayList<>();
				
			}
			print.setArg(arg);
		}
		index++;	//move on to next token, the semi_colon
		return index;
	}
	
	/**
	 * Builds the node for variable declarations whether
	 * single or multiple declarations in one statement
	 * 
	 * @param var The root node of the variable declaration (holds the type as its token)
	 * 
	 * @param index The index of var's token in lex
	 * 
	 * @return The index at the end of this statement
	 */
	public int buildVarDecl(VarDecl var, int index){
		ArrayList<VarDecl> list = new ArrayList<>();
		list = buildVarDeclHelper(list, index); 				//First variable declared
		//Find the rest of the VarDecl's if they exist
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		while(!id.equals("semi_colon") && !id.equals("colon")){
			if(id.equals("comma")){
				index++; 											//get past comma
				list = buildVarDeclHelper(list, index);
			}
			index++;
			t = lex.get(index);
			id = t.getId();
		}
		//For some reason, var = Link.linkVar(list); doesn't work once we leave this method.
		//So I set each field manually.
		VarDecl temp = Link.linkVar(list);
		var.setId(temp.getId());
		var.setModifier(temp.getModifier());
		var.setType(temp.getType());
		var.setOp(temp.getOp());
		var.setExpr(temp.getExpr());
		var.setNextVar(temp.getNextVar());
		var.setAnnotation(temp.getAnnotation());
		return index;
	}
	
	/**
	 * Helper to buildVarDecl(). Used for multiple declarations in one statement.
	 * 
	 * @param list The list of variable declarations to be added to
	 * 
	 * @param index The index of the new variable declaration (where the variable name is) in lex
	 * 
	 * @return The list of variable declarations with the new one added to it
	 */
	public ArrayList<VarDecl> buildVarDeclHelper(ArrayList<VarDecl> list, int index){
		Token t = lex.get(index);
		String id = t.getId();
		index += 1;
		VarDecl var;
		if(list.isEmpty()){
			var = new VarDecl(t);
			var.setModifier(t.getModifier());
			var.setType(t.getType());
			var.setId(t.getWord());
			var.setAnnotation(Link.linkAnnotation(annotation));
			annotation = new ArrayList<>();
		}
		else{
			var = new VarDecl(t);
			var.setModifier(list.get(0).getModifier());
			var.setType(list.get(0).getType());
			var.setId(t.getWord());
			var.setAnnotation(list.get(0).getAnnotation());
		}
		
		t = lex.get(index);
		id = t.getId();
		for(; !id.equals("semi_colon") && !id.equals("colon") && !id.equals("comma"); index++, t = lex.get(index), id = t.getId()){
			if(id.equals("assign_op")){
				var.setOp(t.getWord());
				//Build Expr List
				ArrayList<Token> tokenList = new ArrayList<>();
				index++;
				t = lex.get(index);
				id = t.getId();
				int parenCount = 0; 							//For methods called in the expression
				while(!id.equals("semi_colon") && !id.equals("colon") && !(id.equals("comma") && parenCount == 0)){
					tokenList.add(t);
					index++;
					t = lex.get(index);
					id = t.getId();
					parenCount += (id.equals("Lparen")) ? 1 : 0;
					parenCount -= (id.equals("Rparen")) ? 1 : 0;
				}
				var.setExpr(helper.buildExpr(tokenList));				//Build Expr
				break; 											//only meant to read one VarDecl at a time, so may as well break after doing it
			}
		}
		
		list.add(var);
		return list;
	}
	
	/**
	 * Builds the node for array variable declarations whether
	 * single or multiple declarations in one statement
	 * 
	 * @param var The root node of the array variable declaration (holds the type as its token)
	 * 
	 * @param index The index of var's token in lex
	 * 
	 * @return The index at the end of this statement
	 */
	public int buildVarDeclArray(VarDeclArray var, int index){
		ArrayList<VarDeclArray> list = new ArrayList<>();
		list = buildVarDeclArrayHelper(list, index);			//First variable declared
		//Find the rest of the VarDecl's if they exist
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		int braceCount = 0;
		while(!id.equals("semi_colon") && !id.equals("colon")){
			braceCount += (id.equals("Lbrace")) ? 1 : 0;
			braceCount -= (id.equals("Rbrace")) ? 1 : 0;
			if(id.equals("comma") && braceCount == 0){
				index++;											//get past comma
				list = buildVarDeclArrayHelper(list, index);
			}
			index++;
			t = lex.get(index);
			id = t.getId();
		}
		//For some reason, var = Link.linkVar(list); doesn't work once we leave this method.
		//So I set each field manually.
		VarDeclArray temp = Link.linkVarArray(list);
		var.setId(temp.getId());
		var.setModifier(temp.getModifier());
		var.setType(temp.getType());
		var.setOp(temp.getOp());
		var.setNewArray(temp.getNewArray());
		var.setExpr(temp.getExpr());
		var.setNextVar(temp.getNextVar());
		var.setAnnotation(temp.getAnnotation());
		return index;
	}
	
	/**
	 * Helper to buildVarDeclArray(). Used for multiple array declarations in one statement.
	 * 
	 * @param list The list of array variable declarations to be added to
	 * 
	 * @param index The index of the new array variable declaration (where the variable name is) in lex
	 * 
	 * @return The list of array variable declarations with the new one added to it
	 */
	public ArrayList<VarDeclArray> buildVarDeclArrayHelper(ArrayList<VarDeclArray> list, int index){
		Token t = lex.get(index);
		String id = t.getId();
		index += 1;
		VarDeclArray var;
		if(list.isEmpty()){
			var = new VarDeclArray(t);
			var.setType(t.getType());
			var.setModifier(t.getModifier());
			var.setId(t.getWord());
			var.setAnnotation(Link.linkAnnotation(annotation));
			annotation = new ArrayList<>();
		}
		else{
			var = new VarDeclArray(t);
			var.setType(list.get(0).getType());
			var.setModifier(list.get(0).getModifier());
			var.setId(t.getWord());
			var.setAnnotation(list.get(0).getAnnotation());
		}
		
		t = lex.get(index);
		id = t.getId();
		int braceCount = 0;
		NewArray newArray = null;
		//maybe make an expr specifically for arrays?
		for(; !id.equals("semi_colon") && !id.equals("colon") && !(id.equals("comma") && 
				braceCount == 0); index++, t = lex.get(index), id = t.getId()){
			
			if(id.equals("assign_op")) var.setOp(t.getWord());
			else if(id.equals("new") || id.equals("Lbrace")){
				newArray = new NewArray(t);
				index = helper.buildNewArray(newArray, index);
			}
			else if(id.contains("var")){
				ArrayList<Token> tokenList = new ArrayList<>();
				for(; !id.equals("semi_colon") && !id.equals("comma"); index++, id = lex.get(index).getId()) tokenList.add(lex.get(index));
				index--; //to go back to either the semi_colon or comma
				var.setExpr(helper.buildExpr(tokenList));
			}
		}
		var.setNewArray(newArray);
		list.add(var);
		return list;
	}
}

