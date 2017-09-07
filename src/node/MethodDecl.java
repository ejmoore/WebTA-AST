package node;

import expr.Parameter;
import lexer.Token;

public class MethodDecl extends Node {
	private MethodDecl next;			//Next MethodDecl
	private String id;				//Name of method 
	private String modifier;			//Modifiers of method (public, static, ...)
	private Parameter param;			//Parameters of method
	private String retType;			//Return type of method
	private Body body;				//Body of method
	private Annotation annotation;	//Annotation(s) associated with method
	
	public MethodDecl(Token t) {
		super(t);
	}
	
	public void setNext(MethodDecl n){
		next = n;
		if(n != null){
			n.setParent(this);
		}
	}
	
	public MethodDecl getNext(){
		return next;
	}
	
	public void setId(String i){
		id = i;
	}
	
	public String getId(){
		return id;
	}
	
	public void setModifier(String m){
		modifier = m;
	}
	
	public String getModifier(){
		return modifier;
	}

	public void setParam(Parameter p){
		param = p;
		if(p != null){
			p.setParent(this);
		}
	}
	
	public Parameter getParam(){
		return param;
	}

	public void setRetType(String r){
		retType = r;
	}
	
	public String getRetType(){
		return retType;
	}
	
	public void setBody(Body b){
		body = b;
		if(b != null){
			b.setParent(this);
		}
	}
	
	public Body getBody(){
		return body;
	}
	
	public void setAnnotation(Annotation n){
		annotation = n;
	}
	
	public Annotation getAnnotation(){
		return annotation;
	}
}
