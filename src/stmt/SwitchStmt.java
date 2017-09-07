package stmt;

import lexer.Token;

public class SwitchStmt extends BodyStmt {
	private CaseStmt caseStmt;
	
	public SwitchStmt(Token t) {
		super(t);
	}
	
	public void setCaseStmt(CaseStmt c){
		caseStmt = c;
	}
	
	public CaseStmt getCaseStmt(){
		return caseStmt;
	}
}
