package stmt;
import lexer.Token;

public class CaseStmt extends BodyStmt{
	private CaseStmt next;
	
	public CaseStmt(Token t) {
		super(t);
	}
	
	public void setNext(CaseStmt c){
		next = c;
		if(c != null){
			c.setParent(next);
		}
	}
	
	public CaseStmt getNext(){
		return next;
	}
}
