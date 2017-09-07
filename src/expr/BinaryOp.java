package expr;

import lexer.Token;

public class BinaryOp extends Expr {
	private String op;
	private Expr expr1;
	private Expr expr2;
	
	public BinaryOp(Token t) {
		super(t);
	}
	
	public void setOp(String str){
		op = str;
	}
	
	public String getOp(){
		return op;
	}
	
	public void setExpr1(Expr e){
		expr1 = e;
		e.setParent(this);
	}
	
	public Expr getExpr1(){
		return expr1;
	}
	
	public void setExpr2(Expr e){
		expr2 = e;
		e.setParent(this);
	}
	
	public Expr getExpr2(){
		return expr2;
	}
}
