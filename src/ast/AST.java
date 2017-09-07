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
	
	public static void main(String[] args)throws Exception{
		if(args.length == 0){
			args = new String[2];
			args[0] = "/home/ejmoore/webta/badcode.java";
			args[1] = "/home/ejmoore/webta/WebTA/src/lexer/format";
		}
		//Lex lex = new Lex("C:\\Users\\James\\workspace\\WebTA\\src\\ast\\Sample.java");
		Lex lex = new Lex(args[0]);
		ArrayList<Token> list = lex.lex(args[1]);
		for(Token t : lex.list) {
			System.out.println(t);
		}
		for(Token t : lex.comments) {
			System.out.println(t);
		}
		
		AST ast = new AST(list);
		Root r = ast.root;
		ClassBody c = r.getClassDecl().getClassBody();
		Body body = c.getMethodDecl().getBody();
		//System.out.println(body.getStmt().getNext().getNext().getNext());
		Stmt s = body.getStmt();
		int i = 1;
		while (s != null) {
			System.out.println("Statement " + i++);
			System.out.println(s.getToken());
			System.out.println(s.getExpr() + "\n");
			s = s.getNext();
		}
	}
}