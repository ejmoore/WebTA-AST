package lexer;
public class Token{
	private String id;			//describes what it is
	private String word;		//actual word of the token
	private String modifier;	//modifiers attached to this token
	private String type;		//type of variable, or return type of a method
	private String param;		//for methods and constructors
	private int line;			//line the token is found on
	private String context;		//where the token is relative to classes, methods, loops, etc.
	private int index;			//where the token is in a list of all tokens in the program
	
	public Token(String w, String x, int y, String z){
		id = w;
		word = x;
		line = y;
		context = z;
		modifier = "";
		type = "";
		param = "";
		
	}
	
	//Getters
	public String getId(){
		return id;
	}
	public String getWord(){
		return word;
	}
	public String getModifier(){
		return modifier;
	}
	public String getType(){
		return type;
	}
	public String getParam(){
		return param;
	}
	public int getLine(){
		return line;
	}
	public String getContext(){
		return context;
	}
	public int getIndex(){
		return index;
	}
	
	//Setters
	public void setId(String i){
		id = i;
	}
	public void setWord(String w){
		word = w;
	}
	public void setModifier(String m){
		modifier = m;
	}
	public void setType(String t){
		type = t;
	}
	public void setParam(String p){
		param = p;
	}
	public void setLine(int num){
		line = num;
	}
	public void setContext(String con){
		context = con;
	}
	public void setIndex(int in){
		index = in;
	}
	
	//toString
	public String toString(){
		return "(ID:" + id + ", Word:" + word + ", Line:" + line + ", Context:" + context + ", Index:" + index + ")";
		
	}
}