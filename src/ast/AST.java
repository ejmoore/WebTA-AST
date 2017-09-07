package ast;

import java.util.ArrayList;

import expr.MethodCall;
import stmt.ExprStmt;
import stmt.Stmt;
import node.*;
import lexer.*;

//AST is a tree where each node can have an
//arbitrary number of children
public class AST{
	private Root root;
	
	//Constructor
	public AST(ArrayList<Token> lex){
		BuildAST build = new BuildAST(lex);
		root = build.build();
	}
	
	public Root getRoot(){
		return root;
	}
	
	/*
	 * First arg is the file to be analyzed
	 * Second arg is the format file Located in /src/lexer/format)
	 */
	public static void main(String[] args)throws Exception{
		//If args were not set use local file for the AST
		if(args.length == 0){
			args = new String[2];
			args[0] = "/home/ejmoore/webta/badcode.java";
			args[1] = "/home/ejmoore/webta/WebTA/src/lexer/format";
		}
		
		Lex lex = new Lex(args[0]);
		ArrayList<Token> list = lex.lex(args[1]);
		
		//Print all tokens
		//This can be used for a lot of analytics
		for(Token t : list) {
			//System.out.println(t);
		}
		
		//For example you can get all integer type declarations
		int numInts = 0;
		for(Token t : list) {
			if(t.getId().equals("type") && t.getWord().equals("int")) {
				numInts++;
			}
		}
		System.out.println("There are " + numInts + " integers declarations in this code");
		
		//Print all tokens that are comments
		//The comments ArrayList was added for ease of use
		//
		//You could also loop through the entire list and pull out 
		//the tokens with an ID of "comment"
		for(Token t : lex.comments) {
			//System.out.println(t);
		}
		
		//If the code can be successfully used to create an AST
		//the rest of this will run and print out the statements
		//within the AST
		//
		//The most common reason code cannot be used to create an
		//AST is syntax errors in the code being analyzed
		
		AST ast = new AST(list);
		Root r = ast.root;
		ClassBody c = r.getClassDecl().getClassBody();
		Body body = c.getMethodDecl().getBody();
		Stmt s = body.getStmt();
		
		System.out.println("\n" + c.getMethodDecl().getId());
		
		int i = 1;
		while (s != null) {
			System.out.println("Statement " + i++);
			System.out.println(s.getToken());
			System.out.println(s.getExpr() + "\n");
			s = s.getNext();
		}
	}
}