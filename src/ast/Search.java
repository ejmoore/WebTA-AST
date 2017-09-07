package ast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import node.*;
import node.Package;
import expr.*;
import stmt.*;
import lexer.Lex;
import lexer.Token;

public class Search {
	private AST ast;
	private Root root;
	private ArrayList<Node> allNode;
	
	public Search(String sourceFile, String formattedFile)throws FileNotFoundException, ClassNotFoundException{
		Lex lex = new Lex(sourceFile);
		ArrayList<Token> list = lex.lex(formattedFile);
		ast = new AST(list);
		root = ast.getRoot();
		allNode = getAllNode();
	}

	/**
	 * Makes a list of all the variable declarations in the AST
	 * @return The list of all variable declarations
	 */
	private ArrayList<Node> getAllNode(){
		ArrayList<Node> nodeList = new ArrayList<>();
		Package pack = root.getPackage();
		while(pack != null){
			nodeList.add(pack);
			pack = pack.getNext();
		}
		Import imp = root.getImport();
		while(imp != null){
			nodeList.add(imp);
			imp = imp.getNext();
		}
		ClassDecl classDecl = root.getClassDecl();
		while(classDecl != null){
			nodeList.add(classDecl);
			nodeList.addAll(getAllNodeFromClassDecl(classDecl));
			classDecl = classDecl.getNext();
		}
		return nodeList;
	}
	
	public ArrayList<Node> getAllNodeFromClassDecl(ClassDecl classDecl){
		ArrayList<Node> nodeList = new ArrayList<>();
		ClassBody cBody = classDecl.getClassBody();
		//checking for variable declarations in classDecl
		if(cBody.getVarDecl() != null) nodeList.addAll(addAllNext(cBody.getVarDecl()));
		//checking all constructor declarations in classDecl
		ConstructorDecl conDecl = cBody.getConstructorDecl();
		while(conDecl != null){
			nodeList.add(conDecl);
			nodeList.addAll(getAllNodeFromConstructorDecl(conDecl));
			conDecl = conDecl.getNext();
		}
		//checking all method declarations in classDecl
		MethodDecl methodDecl = cBody.getMethodDecl();
		while(methodDecl != null){
			nodeList.add(methodDecl);
			nodeList.addAll(getAllNodeFromMethodDecl(methodDecl));
			methodDecl = methodDecl.getNext();
		}
		return nodeList;
	}
	
	public ArrayList<Node> getAllNodeFromConstructorDecl(ConstructorDecl conDecl){
		ArrayList<Node> nodeList = new ArrayList<>();
		//checking all statements in classDecl
		Stmt stmt = conDecl.getBody().getStmt();
		while(stmt != null){
			if(stmt instanceof BodyStmt) nodeList.addAll(getAllNodeFromBodyStmt((BodyStmt)stmt));
			else nodeList.add(stmt);
			stmt = stmt.getNext();
		}
		return nodeList;
	}
	
	public ArrayList<Node> getAllNodeFromMethodDecl(MethodDecl methodDecl){
		ArrayList<Node> nodeList = new ArrayList<>();
		Body methodBody = methodDecl.getBody();
		//checking all statements in classDecl
		Stmt stmt = methodBody.getStmt();
		while(stmt != null){
			if(stmt instanceof BodyStmt) nodeList.addAll(getAllNodeFromBodyStmt((BodyStmt)stmt));
			else nodeList.add(stmt);
			stmt = stmt.getNext();
		}
		//checking all class declarations in methodDecl
		ClassDecl classDecl = methodBody.getClassDecl();
		while(classDecl != null){
			nodeList.add(classDecl);
			nodeList.addAll(getAllNodeFromClassDecl(classDecl));
			classDecl = classDecl.getNext();
		}
		return nodeList;
	}
	
	public ArrayList<Node> getAllNodeFromBodyStmt(BodyStmt bodyStmt){
		ArrayList<Node> nodeList = new ArrayList<>();
		//checking all statements in bodyStmt
		nodeList.add(bodyStmt);
		Stmt stmt = bodyStmt.getBody().getStmt();
		while(stmt != null){
			if(stmt instanceof BodyStmt) nodeList.addAll(getAllNodeFromBodyStmt((BodyStmt)stmt));
			else nodeList.add(stmt);
			stmt = stmt.getNext();
		}
		//checking all class declarations in bodyStmt
		ClassDecl classDecl = bodyStmt.getBody().getClassDecl();
		while(classDecl != null){
			nodeList.add(classDecl);
			nodeList.addAll(getAllNodeFromClassDecl(classDecl));
			classDecl = classDecl.getNext();
		}
		return nodeList;
	}
	
	private ArrayList<VarDecl> addAllNext(VarDecl v){
		ArrayList<VarDecl> next = new ArrayList<>();
		while(v != null){
			next.add(v);
			v = (VarDecl)v.getNext();
		}
		return next;
	}
	
	public ArrayList<Node> findPatternInNode(Node node, String missing){
		return null;
	}
	
	public ArrayList<VarDecl> getAllVarDeclWithoutExpr(){
		ArrayList<VarDecl> varList = new ArrayList<>();
		for(VarDecl v : getAllVarDecl()){
			if(v.getExpr() == null) varList.add(v);
		}
		return varList;
	}
	
	public ArrayList<VarDecl> getAllVarDecl(){
		ArrayList<VarDecl> varList = new ArrayList<>();
		for(Node n : allNode){
			if(n instanceof VarDecl) varList.add((VarDecl)n);
		}
		return varList;
	}
	
	public ArrayList<Stmt> getAllStmt(){
		ArrayList<Stmt> stmtList = new ArrayList<>();
		for(Node n : allNode){
			if(n instanceof Stmt) stmtList.add((Stmt)n);
		}
		return stmtList;
	}
	
	public ArrayList<BodyStmt> getAllBodyStmt(){
		ArrayList<BodyStmt> stmtList = new ArrayList<>();
		for(Node n : allNode){
			if(n instanceof BodyStmt) stmtList.add((BodyStmt)n);
		}
		return stmtList;
	}
	
	public ArrayList<Import> getAllImport(){
		ArrayList<Import> importList = new ArrayList<>();
		for(Node n : allNode){
			if(n instanceof Import) importList.add((Import)n);
		}
		return importList;
	}
	
	public ArrayList<BodyStmt> getBodyStmtWithOneLineAndBrace(){
		ArrayList<BodyStmt> bodyStmtList = new ArrayList<>();
		for(BodyStmt bodyStmt : getAllBodyStmt()){
			Stmt stmt = bodyStmt.getBody().getStmt();
			String brace = bodyStmt.getBody().getToken().getWord();
			if(stmt.getNext() == null && brace.equals("{")) bodyStmtList.add(bodyStmt);
		}
		return bodyStmtList;
	}
	
	public ArrayList<?> findGiven(String nodeName, String methodName, ArrayList<String> equals){
		switch(nodeName){
			case "Stmt": return getAllStmt();
			case "VarDecl": return getAllTheseVarDecl(methodName, equals);
			case "Import": return getAllTheseImport(methodName, equals);
			default: return null;
		}
	}
	
	public ArrayList<VarDecl> getAllTheseVarDecl(String methodName, ArrayList<String> equals){
		ArrayList<VarDecl> varList = new ArrayList<>();
		for(VarDecl v : getAllVarDecl()){
			if(methodName.equals("getExpr") && ((v.getExpr() == null && equals == null) /*|| equals.contains(v.getExpr().getToken().getWord())*/)) 
				varList.add(v);
		}
		return varList;
	}
	
	public ArrayList<Import> getAllTheseImport(String methodName, ArrayList<String> equals){
		ArrayList<Import> importList = new ArrayList<>();
		for(Import i : getAllImport()){
			if(methodName.equals("getId") && equals.contains(i.getId())) importList.add(i);
		}
		return importList;
	}
		
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException{
		Search search = new Search("C:\\Users\\James\\workspace\\WebTA\\src\\ast\\Sample.java", "C:\\Users\\James\\workspace\\WebTA\\src\\lexer\\format");
		System.out.println(search.findGiven("VarDecl", "getExpr", null));
		System.out.println(search.getBodyStmtWithOneLineAndBrace());
		ArrayList<String> equals = new ArrayList<>();
		equals.add("java.util.ArrayList");
		equals.add("java.util.*");
		System.out.println(search.findGiven("Import", "getId", equals));
	}
}
