package node;

import expr.Parameter;
import lexer.Token;

public class ConstructorDecl extends Node {
	private ConstructorDecl next;
	private String id;
	private String modifier;
	private Parameter param;
	private Body body;
	private Annotation annotation;
	
	public ConstructorDecl(Token t) {
		super(t);
	}
	
	public void setNext(ConstructorDecl n){
		next = n;
		if(n != null){
			n.setParent(this);
		}
	}
	
	public ConstructorDecl getNext(){
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
