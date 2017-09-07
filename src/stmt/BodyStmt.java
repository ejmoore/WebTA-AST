package stmt;

import node.Body;
import lexer.Token;
//For Stmt's with a body
public abstract class BodyStmt extends Stmt {
	private Body body;
	
	public BodyStmt(Token t) {
		super(t);
	}
	
	public void setBody(Body b){
		body = b;
		b.setParent(this);
	}
	
	public Body getBody(){
		return body;
	}
}
