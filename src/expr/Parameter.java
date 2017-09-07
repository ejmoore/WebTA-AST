package expr;

import lexer.Token;

public class Parameter extends Expr {
	private String type;
	private String id;
	private String modifier;
	private Parameter next;
	
	public Parameter(Token t) {
		super(t);
		setId(t.getWord());
	}

	public void setType(String t){
		type = t;
	}
	
	public String getType(){
		return type;
	}
	
	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setModifier(String m){
		modifier = m;
	}
	
	public String getModifier(){
		return modifier;
	}
	
	public void setNext(Parameter n){
		next = n;
		n.setParent(this);
	}
	
	public Parameter getNext(){
		return next;
	}
	
}
