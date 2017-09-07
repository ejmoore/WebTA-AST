package expr;

import lexer.Token;

public class MethodCall extends StmtExpr {
	private Expr caller; //The caller of the method: list.size(), Class.forName(fqn), returnsString().getCharAt(0), etc
	private String id;
	private Expr[] arg;
	
	public MethodCall(Token t) {
		super(t);
	}
	
	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setArg(Expr[] e){
		arg = e;
	}
	
	public Expr[] getArg(){
		return arg;
	}
	
	public void setCaller(Expr e){
		caller = e;
	}
	
	public Expr getCaller(){
		return caller;
	}
}
