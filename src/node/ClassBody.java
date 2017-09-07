package node;

import stmt.VarDecl;
import lexer.Token;

public class ClassBody extends Node {
	private VarDecl var;
	private ConstructorDecl constructorDecl;
	private MethodDecl methodDecl;
	private ClassDecl classDecl;
	
	public ClassBody(Token t) {
		super(t);
	}
	
	public void setVarDecl(VarDecl v){
		var = v;
		if(v != null){
			v.setParent(this);
		}
	}
	
	public VarDecl getVarDecl(){
		return var;
	}
	
	public void setConstructorDecl(ConstructorDecl m){
		constructorDecl = m;
		if(m != null){
			m.setParent(this);
		}
	}
	
	public ConstructorDecl getConstructorDecl(){
		return constructorDecl;
	}
	
	public void setMethodDecl(MethodDecl m){
		methodDecl = m;
		if(m != null){
			m.setParent(this);
		}
	}
	
	public MethodDecl getMethodDecl(){
		return methodDecl;
	}
	
	public void setClassDecl(ClassDecl c){
		classDecl = c;
		if(c != null){
			c.setParent(this);
		}
	}
	
	public ClassDecl getClassDecl(){
		return classDecl;
	}
	
}
