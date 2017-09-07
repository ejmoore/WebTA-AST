package stmt;

import java.util.ArrayList;

import expr.Expr;
import lexer.Token;

public class PrintStmt extends Stmt {
	private ArrayList<Expr> arg; //List of arguments for printf
	public PrintStmt(Token t) {
		super(t);
	}
	
	public void setArg(ArrayList<Expr> a){
		arg = a;
	}
	
	public ArrayList<Expr> getArg(){
		return arg;
	}
}
