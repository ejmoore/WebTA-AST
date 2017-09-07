package stmt;

import java.util.ArrayList;

import expr.Parameter;
import lexer.Token;

public class CatchStmt extends BodyStmt {
	private ArrayList<String> exception;
	private Parameter param;
	private CatchStmt next;
	
	public CatchStmt(Token t) {
		super(t);
	}
	
	public void setException(ArrayList<String> ex){
		exception = ex;
	}
	
	public ArrayList<String> getException(){
		return exception;
	}
	
	public void setParam(Parameter p){
		param = p;
		if(p != null){
			p.setParent(this);
		}
	}
	
	public Parameter getParam(){
		return param;
	}
	
	public void setNext(CatchStmt c){
		next = c;
		if(c != null){
			c.setParent(this);
		}
	}
	
	public CatchStmt getNext(){
		return next;
	}
}
