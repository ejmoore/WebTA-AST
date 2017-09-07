package stmt;

import java.util.ArrayList;

import expr.*;
import lexer.Token;

public class ForStmt extends BodyStmt {
	private ArrayList<StmtExpr> arg0;
	private Stmt arg1;
	private Expr arg2;
	private ArrayList<StmtExpr> arg3;
	
	public ForStmt(Token t) {
		super(t);
	}
	
	public void setArg0(ArrayList<StmtExpr> s){
		arg0 = s;
	}
	
	public ArrayList<StmtExpr> getArg0(){
		return arg0;
	}
	
	public void setArg1(Stmt s){
		arg1 = s;
		if(s!= null){
			s.setParent(this);
		}
	}
	
	public Stmt getArg1(){
		return arg1;
	}
	
	public void setArg2(Expr e){
		arg2 = e;
		if(e != null){
			e.setParent(this);
		}
	}
	
	public Expr getArg2(){
		return arg2;
	}
	
	public void setArg3(ArrayList<StmtExpr> s){
		arg3 = s;
	}
	
	public ArrayList<StmtExpr> getArg3(){
		return arg3;
	}
}
