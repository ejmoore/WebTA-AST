package stmt;

import lexer.Token;

public class BranchStmt extends Stmt {
	private String label; //For break and continue
	
	public BranchStmt(Token t) {
		super(t);
	}

	public void setLabel(String s){
		label = s;
	}
	
	public String getLabel(){
		return label;
	}
}
