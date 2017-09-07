package expr;

import lexer.Token;

public class Assign extends StmtExpr {
	private Variable lValue;
	private Expr expr;
	
	public Assign(Token t) {
		super(t);
	}
	
	public void setLValue(Variable var){
		lValue = var;
		var.setParent(this);
	}
	
	public Variable getLValue(){
		return lValue;
	}
	
	public void setExpr(Expr e){
		expr = e;
	}
	
	public Expr getExpr(){
		return expr;
	}
}
