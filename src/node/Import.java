package node;

import lexer.Token;

public class Import extends Node {
	private String id;
	private Import next;
	
	public Import(Token t) {
		super(t);
	}
	
	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setNext(Import n){
		next = n;
		if(n != null){
			n.setParent(this);
		}
	}
	
	public Import getNext(){
		return next;
	}
}
