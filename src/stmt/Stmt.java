package stmt;

import expr.Expr;
import node.Node;
import lexer.Token;

public abstract class Stmt extends Node {
	private Expr expr;
	private Stmt next; //The next statement, could be null
	
	public Stmt(Token t) {
		super(t);
	}
	
	public void setExpr(Expr e){
		expr = e;
		if(e != null){
			e.setParent(this);
		}
	}
	
	public Expr getExpr(){
		return expr;
	}
	
	public void setNext(Stmt n){
		next = n;
		if(n != null){
			n.setParent(this);
		}
	}
	
	public Stmt getNext(){
		return next;
	}

}
