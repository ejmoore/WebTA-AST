package expr;

import lexer.Token;

public class UnaryOp extends StmtExpr {
	private String op;	//Unary Operator
	private Expr expr;	//Variable or Expression being operated on
	
	public UnaryOp(Token t) {
		super(t);
	}

	public void setOp(String str){
		op = str;
	}
	
	public String getOp(){
		return op;
	}
	
	public void setExpr(Expr e){
		expr = e;
		e.setParent(this);
	}
	
	public Expr getExpr(){
		return expr;
	}
}
