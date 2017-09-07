package node;

import lexer.Token;

public class Package extends Node {
	private String id;
	private Package next;
	
	public Package(Token t) {
		super(t);
	}
	
	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setNext(Package n){
		next = n;
		if(n != null){
			n.setParent(this);
		}
	}
	
	public Package getNext(){
		return next;
	}
}
