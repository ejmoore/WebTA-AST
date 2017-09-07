package expr;

import java.util.ArrayList;
import lexer.Token;

public class NewArray extends Expr {
	private boolean isNew;			//describes if 'new' keyword is present
	private String type;			//Array type on the right side of the equation
	private ArrayList<Expr> length;	//length for declared array, it's a list in case it's multidimensional
	private ArrayList<Token> value;	//for declarations like: = {1, 2, 3};
	
	public NewArray(Token t) {
		super(t);
		isNew = false;
		length = new ArrayList<>();
		value = new ArrayList<>();
	}
	
	public void setIsNew(boolean n){
		isNew = n;
	}
	
	public boolean getIsNew(){
		return isNew;
	}
	
	public void setType(String r){
		type = r;
	}
	
	public String getType(){
		return type;
	}
	
	public void setLength(ArrayList<Expr> e){
		length = e;
	}
	
	public ArrayList<Expr> getLength(){
		return length;
	}
	
	public void setValue(ArrayList<Token> v){
		value = v;
	}
	
	public ArrayList<Token> getValue(){
		return value;
	}
}
