package node;

import lexer.Token;

public class Root extends Node {
	private ClassDecl classDecl;
	private Package packages;
	private Import imports;
	
	public Root(Token t) {
		super(t);
		// TODO Auto-generated constructor stub
	}
	
	public void setClassDecl(ClassDecl cd){
		classDecl = cd;
		if(cd != null){
			cd.setParent(this);
		}
	}
	
	public ClassDecl getClassDecl(){
		return classDecl;
	}
	
	public void setPackage(Package p){
		packages = p;
		if(p != null){
			p.setParent(this);
		}
	}
	
	public Package getPackage(){
		return packages;
	}
	
	public void setImport(Import i){
		imports = i;
		if(i != null){
			i.setParent(this);
		}
	}
	
	public Import getImport(){
		return imports;
	}
}
