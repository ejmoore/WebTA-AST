package stmt;

import lexer.Token;
//Empty statements only contain a semi-colon
public class EmptyStmt extends Stmt {

	public EmptyStmt(Token t) {
		super(t);
	}

}
