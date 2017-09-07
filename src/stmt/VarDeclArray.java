package stmt;

import expr.NewArray;
import lexer.Token;

public class VarDeclArray extends VarDecl {
	private NewArray newArray;
	private VarDeclArray nextVar;
	
	public VarDeclArray(Token t) {
		super(t);
	}
	
	public void setNewArray(NewArray n){
		newArray = n;
	}
	
	public NewArray getNewArray(){
		return newArray;
	}
	
	public void setNextVar(VarDeclArray v){
		nextVar = v;
	}
	
	public VarDeclArray getNextVar(){
		return nextVar;
	}
}
