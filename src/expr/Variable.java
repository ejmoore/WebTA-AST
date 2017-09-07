package expr;

import lexer.Token;

public class Variable extends Expr {
	private String id;
	private String type;
	private String modifier;
	
	public Variable(Token t) {
		super(t);
		setId(t.getWord());
	}

	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setType(String t){
		type = t;
	}
	
	public String getType(){
		return type;
	}
	
	public void setModifier(String m){
		modifier = m;
	}
	
	public String getModifier(){
		return modifier;
	}
}
