package stmt;

import lexer.Token;

public class TryStmt extends BodyStmt {
	private CatchStmt catchStmt;
	private FinallyStmt finallyStmt;
	
	public TryStmt(Token t) {
		super(t);
	}

	public void setCatchStmt(CatchStmt c){
		catchStmt = c;
		if(c != null){
			c.setParent(this);
		}
	}
	
	public CatchStmt getCatchStmt(){
		return catchStmt;
	}
	
	public void setFinallyStmt(FinallyStmt f){
		finallyStmt = f;
		if(f != null){
			f.setParent(this);
		}
	}
	
	public FinallyStmt getFinallyStmt(){
		return finallyStmt;
	}
}
