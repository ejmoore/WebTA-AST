package node;

import stmt.Stmt;
import lexer.Token;

public class Body extends Node {
	private Stmt stmt;
	private ClassDecl classDecl;
	
	public Body(Token t) {
		super(t);
	}
	
	public void setStmt(Stmt s){
		stmt = s;
		if(s != null){
			s.setParent(this);
		}
	}
	
	public Stmt getStmt(){
		return stmt;
	}
	
	public void setClassDecl(ClassDecl c){
		classDecl = c;
		if(c != null){
			c.setParent(this);
		}
	}
	
	public ClassDecl getClassDecl(){
		return classDecl;
	}
}
