package lexer;
import java.util.ArrayList;

public class Hello{
	public static void main(String[] args) {
		int a = 6+5, b = +4;
		int x = 1, y = 2;
		if(b == a) b += 1;
		else if(a != b) a += 1;
		else a -= 1;
		for(int i = 0; i < 5; i++){
			x += b;
		}
		for(int i = 0; i < 5; i += 1) y += b;
		switch(a){
			case 1: b = a;
			case 2: b *= a;
					break;
			default:
					b = 0;
					break;
		}
	}
}

class Hi{
	public static void main(String[] args) {
		String str;
		try{
			str = args[1];
		}
		catch(NullPointerException n){
			str = "empty";
		}
		catch(IndexOutOfBoundsException in){
			str = "too_far";
		}
		finally{
			str = "finally";
		}
		for(String s : args){
			class Inner{
				int x;
			}
		}
	}
	class There<E, F>{
		int[] x[] = new int[][]{{0}, {1}, {2}};
		int y[] = x[x[0][0]];
		There there;
		public There(int x){
			
		}
		public int method(There there){
			return 0;
		}
		public int method2(){
			return method(new There<Integer, Double>(method(there)));
		}
		public void method3() throws ClassNotFoundException{
			ArrayList<String> str = new ArrayList<String>();
			str.get(0).getClass();
			str.add(str.get(0));
			Class.forName("java.util.ArrayList");
		}
	}
}