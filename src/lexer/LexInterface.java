package lexer;

import java.util.ArrayList;
import java.util.Arrays;

public interface LexInterface {
	final ArrayList<String> constType = new ArrayList<>(
			Arrays.asList("byte", "short", "int", 
				"long", "float", "double",
				"boolean", "char", "String"));
	public void lexer(); //reads in each word and uses getToken() to assign it a token ID
	public String getToken(String str); //returns the token ID of str
	//Uses ExtraLex in the main method to give the tokens more information
}
