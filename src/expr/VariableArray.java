package expr;

import java.util.ArrayList;

import lexer.Token;

public class VariableArray extends Expr {
	private String id;
	private ArrayList<Expr> indices;
	
	public VariableArray(Token t) {
		super(t);
		setId(t.getWord());
		indices = new ArrayList<>();
	}

	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setIndices(ArrayList<Expr> in){
		indices = in;
	}
	
	public ArrayList<Expr> getIndices(){
		return indices;
	}
}
