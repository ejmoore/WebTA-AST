package stmt;

import lexer.Token;

public class IfStmt extends BodyStmt {
	private IfStmt elseStmt;
	
	public IfStmt(Token t) {
		super(t);
	}
	
	public void setElseStmt(IfStmt t){
		elseStmt = t;
		t.setParent(this);
	}
	
	public IfStmt getElseStmt(){
		return elseStmt;
	}
}
