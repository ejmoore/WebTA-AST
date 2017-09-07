package node;

import java.util.ArrayList;

import lexer.Token;

public class ClassDecl extends Node {
	private ClassDecl next;
	private String id;
	private String modifier;
	private ClassBody body;
	private Annotation annotation;
	private ArrayList<String> genericList;
	
	public ClassDecl(Token t) {
		super(t);
		genericList = new ArrayList<>();
	}
	
	public void setNext(ClassDecl n){
		next = n;
		n.setParent(this);
	}
	
	public ClassDecl getNext(){
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
	
	public void setClassBody(ClassBody b){
		body = b;
		b.setParent(this);
	}
	
	public ClassBody getClassBody(){
		return body;
	}
	
	public void setAnnotation(Annotation n){
		annotation = n;
	}
	
	public Annotation getAnnotation(){
		return annotation;
	}
	
	public void setGenericList(ArrayList<String> g){
		genericList = g;
	}
	
	public ArrayList<String> getGenericList(){
		return genericList;
	}
}
