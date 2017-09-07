package expr;

import lexer.Token;

public abstract class StmtExpr extends Expr {
	private String op;
	
	public StmtExpr(Token t) {
		super(t);
	}
	
	public void setOp(String s){
		op = s;
	}
	
	public String getOp(){
		return op;
	}
}
