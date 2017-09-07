package lexer;
import java.util.*;
import java.io.*;

public class Format{
	
	public void format(String read, String write)throws FileNotFoundException{
		Scanner scanner = new Scanner(new File(read));
		PrintWriter writer = new PrintWriter(new File(write));
		
		ArrayList<Character> seperate = new ArrayList<Character>(
		Arrays.asList('(', ')', '{', '}', '[', ']', ',', '.', ';', ':', '+', 
		'-', '*', '/', '%', '=', '<', '>', '!', '&', '|', '^', '~', '?'));
		
		ArrayList<String> seperate2 = new ArrayList<String>(
		Arrays.asList("++", "--", "<<", ">>", ">>>", "+=", "-=", "*=", "/=", "%=",
		"<<=", ">>=", ">>>=", "==", "<=", ">=", "&=", "|=", "^=", "!=", "&&", "||"));
		
		String line;
		char c;
		int lineNum = 1;
		while(scanner.hasNextLine()){
			writer.write("#" + lineNum + " ");
			lineNum++;
			line = scanner.nextLine();
			for(int i = 0; i < line.length(); i++){
				c = line.charAt(i);
				String str = "", str2 = "", str3 = "";
				
				if(i < line.length() - 1){
					str = "" + c + line.charAt(i + 1);
					if(i < line.length() - 2){
						str2 = str + line.charAt(i + 2);
						if(i < line.length() - 3){
							str3 = str2 + line.charAt(i + 3);
						}
					}
				}
				//c is part of a comment
				if((i < line.length() - 1) && str.equals("//")){
					writer.write(" " + c);
					i++;
					while(i < line.length()){
						c = line.charAt(i);
						writer.append(c);
						i++;
					}
				}
				//c is part of a block comment
				else if((i < line.length() - 1) && str.equals("/*")){
					writer.write(" " + c);
					i++;
					while(i < line.length()){
						c = line.charAt(i);
						if(i < line.length() - 1){
							String tempStr = "" + c + line.charAt(i + 1);
							if(tempStr.equals("*/")){
								writer.write(tempStr + " ");
								i++;
								break;
							}
						}
						writer.append(c);
						i++;
						if(i == line.length()){
							line = scanner.nextLine();
							i = 0;
							writer.write("\n#" + lineNum + " ");
							lineNum++;
						}
					}
				}
				//c is part of an annotation
				else if(c == '@'){
					writer.write(" " + c);
					i++;
					int parenCount = 0;
					boolean startCount = false;
					while(i < line.length()){
						c = line.charAt(i);
						parenCount += (line.charAt(i) != '(') ? 1 : 0;
						parenCount -= (line.charAt(i) != ')') ? 1 : 0;
						if(startCount && parenCount == 0){
							writer.write(c + " ");
							i++;
							break;
						}
						writer.append(c);
						i++;
						if(i == line.length()){
							line = scanner.nextLine();
							i = 0;
							writer.write("\n#" + lineNum + " ");
							lineNum++;
						}
						//This means we reached the parentheses and can start counting
						if(parenCount != 0){
							startCount = true;
						}
					}
				}
				else if(seperate2.contains(str3)){
					writer.write(" " + str3 + " ");
					i += 3;
				}
				else if(seperate2.contains(str2)){
					writer.write(" " + str + " ");
					i += 2;
				}
				else if(seperate2.contains(str)){
					writer.write(" " + str + " ");
					i++;
				}
				
				//checking if it's a double
				else if(c == '.' && (Character.isDigit(line.charAt(Math.max(i - 1, 0)))
				|| Character.isDigit(line.charAt(Math.min(i + 1, line.length() - 1))))){
					writer.append(c);
				}
				else if(seperate.contains(c)){
					/*For when + and - are unary operators
					if(i > 0 && i < line.length() - 1 && (c == '+' || c == '-')){
						char pre = line.charAt(i - 1);
						if((pre == '+' && c == '-') || (pre == '-' && c == '+')) writer.append(c);
						else if(!Character.isDigit(pre) && !Character.isLetter(pre) && pre != ')') writer.write(" " + c);
						else writer.write(" " + c + " ");
					}
					*/
					//else{
						writer.write(" " + c + " ");
					//}
				}
				//c is part of a string
				else if(c == '"'){
					writer.append(c);
					i++;
					//c just has to not be " or '
					c = ' ';
					// i - 2 and i - 3 because of how i is incremented and c uses i
					while((c != '"' || (line.charAt(i - 2) == '\\' && line.charAt(i - 3) != '\\')) && i < line.length()){
						c = line.charAt(i);
						writer.append(c);
						i++;
					}
					i--;
					writer.write(' ');
				}
				else if(c == '\''){
					writer.append(c);
					i++;
					//c just has to not be " or '
					c = ' ';
					// i - 2 and i - 3 because of how i is incremented and c uses i
					while((c!= '\'' || (line.charAt(i - 2) == '\\' && line.charAt(i - 3) != '\\')) && i < line.length()){
						c = line.charAt(i);
						writer.append(c);
						i++;
					}
					i--;
					writer.append(' ');
				}
				else{
					writer.append(c);
				}
			}
			writer.append('\n');
		}
		scanner.close();
		writer.close();
	}
}