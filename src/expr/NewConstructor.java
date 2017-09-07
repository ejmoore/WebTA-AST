package expr;

import java.util.ArrayList;

import lexer.Token;

public class NewConstructor extends StmtExpr {
	private boolean isNew;
	private String id;
	private ArrayList<String> genericList;
	private ArrayList<Expr> arg;
	
	public NewConstructor(Token t) {
		super(t);
		genericList = new ArrayList<>();
	}
	
	public void setIsNew(boolean n){
		isNew = n;
	}
	
	public boolean getIsNew(){
		return isNew;
	}
	
	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setGenericList(ArrayList<String> g){
		genericList = g;
	}
	
	public ArrayList<String> getGenericList(){
		return genericList;
	}
	
	public void setArg(ArrayList<Expr> e){
		arg = e;
	}
	
	public ArrayList<Expr> getArg(){
		return arg;
	}

}
