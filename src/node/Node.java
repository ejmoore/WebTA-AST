package node;
import lexer.Token;

public abstract class Node{
	private Token token;
	private Node parent;
	private Other other;
	
	//Constructor
	public Node(Token t){
		token = t;
	}
	
	public Token getToken(){
		return token;
	}
	
	public void setParent(Node p){
		parent = p;
	}
	
	public Node getParent(){
		return parent;
	}
	
	public void setOther(Other o){
		other = o;
		o.setParent(this);
	}
	
	public Other getOther(){
		return other;
	}
	
	public String toString(){
		return token.toString();
	}
}