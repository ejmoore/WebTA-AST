package node;

import lexer.Token;

public class Annotation extends Node {
	private String id;
	private String element;
	private Annotation next;
	
	public Annotation(Token t) {
		super(t);
	}
	
	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setElement(String e){
		element = e;
	}
	
	public String getElement(){
		return element;
	}
	
	public void setNext(Annotation n){
		next = n;
	}
	
	public Annotation getNext(){
		return next;
	}
}
