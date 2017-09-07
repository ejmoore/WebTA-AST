package stmt;

import expr.StmtExpr;
import lexer.Token;

public class ExprStmt extends Stmt {
	private StmtExpr stmtExpr;
	
	public ExprStmt(Token t) {
		super(t);
	}
	
	public void setExpr(StmtExpr s){
		stmtExpr = s;
	}
	
	public StmtExpr getExpr(){
		return stmtExpr;
	}
}
