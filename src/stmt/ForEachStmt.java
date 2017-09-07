package stmt;

import expr.Variable;
import lexer.Token;

public class ForEachStmt extends BodyStmt {
	private VarDecl arg1;
	private Variable arg2;
	
	public ForEachStmt(Token t) {
		super(t);
	}
	
	public void setArg1(VarDecl v){
		arg1 = v;
	}
	
	public VarDecl getArg1(){
		return arg1;
	}
	
	public void setArg2(Variable v){
		arg2 = v;
	}
	
	public Variable getArg2(){
		return arg2;
	}
}
