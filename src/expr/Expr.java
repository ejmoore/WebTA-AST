package expr;

import node.Node;
import lexer.Token;

public abstract class Expr extends Node {
	private String cast;
	
	public Expr(Token t) {
		super(t);
	}

	public void setCast(String c){
		cast = c;
	}
	
	public String getCast(){
		return cast;
	}
}
