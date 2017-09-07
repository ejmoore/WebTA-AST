package stmt;

import java.util.ArrayList;

import node.Annotation;
import lexer.Token;

public class VarDecl extends Stmt {
	private String id;
	private String modifier;
	private String type;
	private String op;
	private VarDecl nextVar;
	private Annotation annotation;
	private String cast;
	private ArrayList<String> genericList;
	
	public VarDecl(Token t) {
		super(t);
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
	
	public void setType(String t){
		type = t;
	}
	
	public String getType(){
		return type;
	}
	
	public void setOp(String s){
		op = s;
	}
	
	public String getOp(){
		return op;
	}
	
	public void setNextVar(VarDecl v){
		nextVar = v;
		if(v != null){
			v.setParent(this);
		}
	}
	
	public VarDecl getNextVar(){
		return nextVar;
	}
	
	public void setAnnotation(Annotation n){
		annotation = n;
	}
	
	public Annotation getAnnotation(){
		return annotation;
	}
	
	
	public void setCast(String c){
		cast = c;
	}
	
	public String getCast(){
		return cast;
	}
	
	public void setGenericList(ArrayList<String> g){
		genericList = g;
	}
	
	public ArrayList<String> getGenericList(){
		return genericList;
	}
}
