package node;

import lexer.Token;

public class Other extends Node {
	private Other next;
	
	public Other(Token t) {
		super(t);
	}
	
	public void setNext(Other n){
		next = n;
		n.setParent(this);
	}
	
	public Other getNext(){
		return next;
	}
}
