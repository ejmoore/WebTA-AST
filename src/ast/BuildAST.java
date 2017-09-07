package ast;
import java.util.ArrayList;

import expr.Assign;
import expr.Expr;
import expr.MethodCall;
import expr.NewConstructor;
import expr.Parameter;
import expr.StmtExpr;
import expr.UnaryOp;
import expr.Variable;
import stmt.BranchStmt;
import stmt.CaseStmt;
import stmt.CatchStmt;
import stmt.DoWhileStmt;
import stmt.EmptyStmt;
import stmt.ExprStmt;
import stmt.FinallyStmt;
import stmt.ForEachStmt;
import stmt.ForStmt;
import stmt.IfStmt;
import stmt.PrintStmt;
import stmt.Stmt;
import stmt.SwitchStmt;
import stmt.TryStmt;
import stmt.VarDecl;
import stmt.VarDeclArray;
import stmt.WhileStmt;
import node.*;
import node.Package;
import lexer.*;

public class BuildAST {
	ArrayList<Token> lex; 				//The list of tokens this AST is built on
	ArrayList<Annotation> annotation; 	//Keeps track of the list of annotations for a method or class
	BuildStmtHelper stmtHelper;			//Instance of BuildStmtHelper used to store statement builders
	BuildHelper helper;					//Instance of BuildHelper used to store many useful methods
	
	/**
	 * Constructor
	 * @param list The list of tokens used to build the AST.
	 */
	public BuildAST(ArrayList<Token> list) {
		lex = list;
		annotation = new ArrayList<>();
		stmtHelper = new BuildStmtHelper(list);
		helper = new BuildHelper(list);
	}
	
	/**
	 * Finds, and builds a node for, each class declaration (non-nested),
	 * annotation, package, and import.
	 * @return The root of the AST
	 */
	public Root build(){
		Root node = new Root(new Token("", "", 0, "")); 		//root node
		ArrayList<ClassDecl> classDeclList = new ArrayList<>();
		ArrayList<Package> packageList = new ArrayList<>();
		ArrayList<Import> importList = new ArrayList<>();
		ArrayList<Other> otherList = new ArrayList<>();
		String id;
		for(int i = 0; i < lex.size(); i++){
			Token t = lex.get(i);
			id = t.getId();
			if(id.contains("class")){							//Class Declaration
				ClassDecl current = new ClassDecl(t);
				classDeclList.add(current);
				i = buildClassDecl(current, i);
				i--;
			}
			else if(id.equals("annotation")){					//Annotation
				Annotation anno = new Annotation(t);
				helper.buildAnnotation(anno);
				annotation.add(anno);
			}
			else if(id.equals("package")){						//Package
				Package pack = new Package(t);
				String str = "";
				i++;
				for(; !lex.get(i).getId().equals("semi_colon"); i++){
					str += lex.get(i).getWord();
				}
				pack.setId(str);
				packageList.add(pack);
			}
			else if(id.equals("import")){						//Import
				Import imp = new Import(t);
				String str = "";
				i++;
				for(; !lex.get(i).getId().equals("semi_colon"); i++){
					str += lex.get(i).getWord();
				}
				imp.setId(str);
				importList.add(imp);
			}
			else{												//Other
				otherList.add(new Other(t));
			}
		}
		node.setClassDecl(Link.linkClass(classDeclList));
		node.setPackage(Link.linkPackage(packageList));
		node.setImport(Link.linkImport(importList));
		node.setOther(Link.linkOther(otherList));
		return node;
	}
	
	/**
	 * Builds the body for methods, loops, conditionals, and others.
	 * @param body The root of this body. Its token is '{'.
	 * @param index The index of body's token in lex
	 * @param caseBody Indicates if this is being run for a case statement's body or a
	 * 		regular body. Used throughout buildBody() for special cases of the case body
	 * @return The index at the end of the body
	 */
	public int buildBody(Body body, int index, boolean caseBody){
		ArrayList<Stmt> stmtList = new ArrayList<>();	//Linked together at end of method
		ArrayList<ClassDecl> classDeclList = new ArrayList<>();	//Linked together at end of method
		if(body.getToken().getId().equals("semi_colon")){	//For empty bodies
			return index;
		}
		Token t = lex.get(index);
		String id = t.getId();
		String ogContext = body.getToken().getContext();
		String context = ogContext;
		for(; context.contains(ogContext) && (!id.equals("case") || !caseBody) && 
			(!lex.get(index).getId().equals("default") || !caseBody); 
			index++, t = lex.get(index), id = t.getId(), context = t.getContext()){
			
			Stmt stmt = null;
			//Variable Declaration
			index = (lex.get(index + 1).getWord().equals("<")) ? helper.getToEndingMarker("<", ">", index + 1) : index; //In case the object has generics
			if(id.equals("type") && (lex.get(index + 1).getId().contains("var") || lex.get(index + 1).getId().equals("Lbracket"))){
				index += 1; //trying to find the variable name
				//variable is an array
				if(lex.get(index).getId().equals("Lbracket") || lex.get(index).getType().contains("[]")){
					while(lex.get(index).getId().contains("bracket")) index++;
					stmt = new VarDeclArray(lex.get(index));
					index = stmtHelper.buildVarDeclArray((VarDeclArray)stmt, index);
				}
				else{
					stmt = new VarDecl(lex.get(index));
					index = stmtHelper.buildVarDecl((VarDecl)stmt, index);
				}
			}
			//Pre Increment/Decrement
			else if(lex.get(index + 1).getId().contains("var") && id.equals("pre-unary_op")){
				UnaryOp un = new UnaryOp(t);
				un.setOp(t.getWord());
				un.setExpr(new Variable(lex.get(index + 1)));
				ExprStmt expr = new ExprStmt(t);
				expr.setExpr(un);
				stmtList.add(expr);
				index += 2;	//Increment to semi_colon
			}
			//Post Increment/Decrement
			else if(id.contains("var") && lex.get(index + 1).getId().equals("post-unary_op")){
				UnaryOp un = new UnaryOp(t);
				un.setOp(lex.get(index + 1).getWord());
				un.setExpr(new Variable(t));
				ExprStmt expr = new ExprStmt(t);
				expr.setExpr(un);
				stmtList.add(expr);
				index += 2;	//Increment to semi_colon
			}
			//Assign
			else if(id.contains("var") && lex.get(index + 1).getId().equals("assign_op")){
				try{
					Assign assign = new Assign(lex.get(index));
					index = helper.buildAssign(assign, index);
					ExprStmt exprStmt = new ExprStmt(assign.getToken());
					exprStmt.setExpr(assign);
					stmtList.add(exprStmt);
				}
				catch(Exception e){
					Assign assign = new Assign(lex.get(index));
					ExprStmt exprStmt = new ExprStmt(assign.getToken());
					stmtList.add(exprStmt);
				}
				
			}
			//MethodCall from a variable
			//Should be merged with the other MethodCall condition when we know this one works
			else if(id.contains("var") && lex.get(index + 1).getId().equals("dot_op") && lex.get(index + 2).getId().equals("method")){
				ArrayList<Token> tokenList = new ArrayList<>();
				for(; index < lex.size() && !lex.get(index).getId().equals("semi_colon"); index++){
					tokenList.add(lex.get(index));
				}
				index--; //so we don't go past the semi_colon
				MethodCall method = (MethodCall)helper.buildExpr(tokenList);
				ExprStmt expr = new ExprStmt(method.getToken());
				expr.setExpr(method);
				stmtList.add(expr);
			}
			//Regular MethodCall - can be combined with NewConstructor with slight modifications
			else if(id.equals("method")){
				ArrayList<Token> tokenList = new ArrayList<>();
				for(; index < lex.size() && !lex.get(index).getId().equals("semi_colon"); index++){
					tokenList.add(lex.get(index));
				}
				index--; //so we don't go past the semi_colon
				MethodCall method = (MethodCall)helper.buildExpr(tokenList);
				ExprStmt expr = new ExprStmt(method.getToken());
				expr.setExpr(method);
				stmtList.add(expr);
			}
			//NewConstructor
			else if(id.equals("new") && lex.get(index + 1).getId().equals("constructor")){
				ArrayList<Token> tokenList = new ArrayList<>();
				for(; index < lex.size() && !lex.get(index).getId().equals("semi_colon"); index++){
					tokenList.add(lex.get(index));
				}
				index--; //so we don't go past the semi_colon
				
				NewConstructor newCon = (NewConstructor)helper.buildExpr(tokenList);
				ExprStmt expr = new ExprStmt(newCon.getToken());
				expr.setExpr(newCon);
				stmtList.add(expr);
			}
			//Switch
			else if(id.equals("conditional") && t.getWord().equals("switch")){
				stmt = new SwitchStmt(t);
				index = buildSwitchStmt((SwitchStmt)stmt, index);
			}
			//Conditional
			else if(id.equals("conditional")){
				stmt = new IfStmt(t);
				index = buildIfStmt((IfStmt)stmt, index);
			}
			//Loop
			else if(id.equals("loop")){
				if(t.getWord().equals("for")){
					for(int j = index; j < lex.size(); j++){	//Look for ; or : to determine if it's a for or for-each loop
						if(lex.get(j).getId().equals("semi_colon")){
							stmt = new ForStmt(t);
							index = buildForStmt((ForStmt)stmt, index);
							break;
						}
						else if(lex.get(j).getId().equals("colon")){
							stmt = new ForEachStmt(t);
							index = buildForEachStmt((ForEachStmt)stmt, index);
							break;
						}
					}
				}
				else if(t.getWord().equals("while")){
					stmt = new WhileStmt(t);
					index = buildWhileStmt((WhileStmt)stmt, index);
				}
				else if(t.getWord().equals("do")){
					stmt = new DoWhileStmt(t);
					index = buildDoWhileStmt((DoWhileStmt)stmt, index);
				}
			}
			//Annotation
			else if(id.equals("annotation")){
				Annotation anno = new Annotation(t);
				helper.buildAnnotation(anno);
				annotation.add(anno);
			}
			//Branch Statement
			else if(id.equals("branch")){
				BranchStmt branch = new BranchStmt(t);
				index = stmtHelper.buildBranchStmt(branch, index);
				stmtList.add(branch);
				if(caseBody) break;
			}
			//Empty Statement
			else if((lex.get(Math.max(0, index - 1)).getId().equals("semi_colon") || 
					lex.get(Math.max(0, index - 1)).getId().contains("brace")) && id.equals("semi_colon")){
				stmt = new EmptyStmt(t);
			}
			//Print Statement
			else if(lex.get(index).getWord().contains("print")){
				stmt = new PrintStmt(t);
				index = stmtHelper.buildPrintStmt((PrintStmt)stmt, index);
			}
			//Try-Catch-Finally Statement
			else if(lex.get(index).getId().equals("try")){
				stmt = new TryStmt(t);
				index = buildTryStmt((TryStmt)stmt, index);
			}
			//Class Declaration
			else if(id.contains("class")){
				ClassDecl innerClass = new ClassDecl(t);
				index = buildClassDecl(innerClass, index);
				classDeclList.add(innerClass);
			}
			
			if(stmt != null) stmtList.add(stmt);
			
			if(!lex.get(index).getContext().contains(ogContext) ||
				(caseBody && lex.get(index).getId().equals("case"))) break;	//So i doesn't increment one too far
		}
		if(caseBody && (lex.get(index).getId().equals("case") || 
			lex.get(index).getId().equals("default"))) index--;	//Must end right before the next case statement
		body.setStmt(Link.linkStmt(stmtList));
		body.setClassDecl(Link.linkClass(classDeclList));
		return index;
	}
	
	/**
	 * Builds the class' body. Similar to buildBody(),
	 * but specifically for the body of the class.
	 * 
	 * @param body The root of this class' body. Its token is '{'.
	 * 
	 * @param index The index of body's token in lex
	 * 
	 * @return The index at the class' body ends
	 */
	public int buildClassBody(ClassBody body, int index){
		ArrayList<VarDecl> varDeclList = new ArrayList<>();
		ArrayList<ConstructorDecl> constructorDeclList = new ArrayList<>();
		ArrayList<MethodDecl> methodDeclList = new ArrayList<>();
		ArrayList<ClassDecl> classDeclList = new ArrayList<>();
		
		String ogContext = body.getToken().getContext();
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		String context = ogContext;
		for(; context.contains(ogContext) && index < lex.size(); index++, t = lex.get(index), id = t.getId(), context = t.getContext()){
			//Class variable declaration
			if(id.equals("type") && (lex.get(index + 1).getId().contains("var") || lex.get(index + 1).getId().equals("Lbracket"))){
				index += 1; //trying to find the variable name
				//variable is an array
				if(lex.get(index).getId().equals("Lbracket") || lex.get(index).getType().contains("[]")){
					while(lex.get(index).getId().contains("bracket")) index++;
					VarDeclArray v = new VarDeclArray(lex.get(index));
					index = stmtHelper.buildVarDeclArray(v, index);
					varDeclList.add(v);
				}
				else{
					VarDecl v = new VarDecl(lex.get(index));
					index = stmtHelper.buildVarDecl(v, index);
					varDeclList.add(v);
				}
			}
			else if(id.contains("constructor")){				//Constructor Declaration
				ConstructorDecl c = new ConstructorDecl(t);
				index = buildConstructorDecl(c, index);
				constructorDeclList.add(c);
			}
			else if(id.contains("method")){						//Method Declaration
				MethodDecl m = new MethodDecl(t);
				index = buildMethodDecl(m, index);
				methodDeclList.add(m);
			}
			else if(id.equals("annotation")){					//Annotation
				Annotation anno = new Annotation(t);
				helper.buildAnnotation(anno);
				annotation.add(anno);
			}
			else if(id.contains("class")){					//Class Declaration
				ClassDecl innerClass = new ClassDecl(t);
				index = buildClassDecl(innerClass, index);
				classDeclList.add(innerClass);
			}
		}
		body.setVarDecl(Link.linkVarDecl(varDeclList));
		body.setConstructorDecl(Link.linkConstructor(constructorDeclList));
		body.setMethodDecl(Link.linkMethod(methodDeclList));
		body.setClassDecl(Link.linkClass(classDeclList));
		return index;
	}
	
	/**
	 * Begins the process of building the entire class, 
	 * starting with its declaration.
	 * 
	 * @param cd The node containing the token of this class declaration
	 * 
	 * @param index The index of cd's token in lex
	 * 
	 * @return The index at end of this class
	 */
	public int buildClassDecl(ClassDecl cd, int index){
		ArrayList<ClassDecl> classDeclList = new ArrayList<>();
		cd.setId(cd.getToken().getWord());
		cd.setModifier(cd.getToken().getModifier());
		cd.setAnnotation(Link.linkAnnotation(annotation));
		annotation = new ArrayList<>();
		
		classDeclList.add(cd);
		for(; index < lex.size(); index++){
			Token t = lex.get(index);
			String id = t.getId();
			if(id.equals("Lbrace")){
				ClassBody body = new ClassBody(t);
				index = buildClassBody(body, index);
				cd.setClassBody(body);
				break;
			}
			else if(id.equals("generic")) cd.getGenericList().add(t.getWord());
		}
		return index;
	}
	
	/**
	 * Copied from buildMethodDecl
	 * @param conDecl The node to be built
	 * @param index Where the constructor's token is
	 * @return The index of the body's ending '}'
	 */
	public int buildConstructorDecl(ConstructorDecl conDecl, int index){
		Token token = conDecl.getToken();
		conDecl.setId(token.getWord());
		conDecl.setAnnotation(Link.linkAnnotation(annotation));
		conDecl.setModifier(token.getModifier());
		annotation = new ArrayList<>();
		Token t;
		String id;
		ArrayList<Parameter> parameterList = new ArrayList<>();
		index += 1;
		for(; index < lex.size(); index++){
			t = lex.get(index);
			id = t.getId();
			if(id.equals("pvar")){
				Parameter parameter = new Parameter(t);
				parameter.setId(t.getWord());
				parameter.setType(t.getType());
				parameter.setModifier(t.getModifier());
				parameterList.add(parameter);
			}
			else if(id.contains("Lbrace")){
				Body body = new Body(t);
				index = buildBody(body, index, false);
				conDecl.setBody(body);
				break;
			}
		}
		conDecl.setParam(Link.linkParam(parameterList));
		return index;
	}
	
	/**
	 * Builds a node for the do-while statement.
	 * 
	 * @param dw The root of this do-while loop
	 * 
	 * @param index The index of the dw's token in lex
	 * 
	 * @return The index at end of this loop.
	 */
	public int buildDoWhileStmt(DoWhileStmt dw, int index){
		ArrayList<Token> tokenList = new ArrayList<>(); //For building the expression of the while condition
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		Body body = new Body(t);
		index = buildBody(body, index, false);
		
		//i++; //skip over 'while'
		index += 2; //skip over }while should just read in while instead
		int parenCount = 0;
		t = lex.get(index);
		id = t.getId();
		do{
			parenCount += (id.equals("Lparen")) ? 1 : 0;
			parenCount -= (id.equals("Rparen")) ? 1 : 0;
			tokenList.add(t);
			index++;
			t = lex.get(index);
			id = t.getId();
		}while(parenCount != 0);
		
		dw.setBody(body);
		dw.setExpr(helper.buildExpr(tokenList));
		return index;
	}
	
	/**
	 * Builds a node for a for-each loop
	 * 
	 * @param stmt The root of this for-each loop
	 * 
	 * @param index The index of stmt's token in lex
	 * 
	 * @return The index at the end of this loop
	 */
	public int buildForEachStmt(ForEachStmt stmt, int index){
		index += 1;
		VarDecl varDecl = null;							//Variable Declaration
		for(; !lex.get(index).getId().equals("colon"); index++){
			if(lex.get(index).getId().contains("var")){
				varDecl = new VarDecl(lex.get(index - 1));
				varDecl.setId(lex.get(index).getWord());
				varDecl.setModifier(lex.get(index).getModifier());
				varDecl.setType(lex.get(index).getType());
				varDecl.setExpr(null);
			}
		}
		index++; 											//skip colon
		Variable variable = new Variable(lex.get(index));	//Object to be iterated through
		index += 2; 										//get to Lbrace
		Body body = new Body(lex.get(index));				//Build Body
		index = buildBody(body, index, false);
		stmt.setArg1(varDecl);
		stmt.setArg2(variable);
		stmt.setBody(body);
		return index;
	}
	
	/**
	 * Builds a node for a for loop
	 * 
	 * @param stmt The root of this for loop
	 * 
	 * @param index The index of stmt's token in lex
	 * 
	 * @return The index at the end of this loop
	 */
	public int buildForStmt(ForStmt stmt, int index){
		//1st Argument
		ArrayList<StmtExpr> arg0 = new ArrayList<>();			//arg0 is one or more assigns if it exists
		Stmt arg1 = null;
		index += 2; 											//skip Lparen
		Token t = lex.get(index);
		String id = t.getId();
		if(id.contains("type")){								//arg1 is a variable declaration
			arg1 = new VarDecl(t);
		}
		else if(id.contains("var") || 
				id.contains("method") || 
				id.contains("unary_op")){						//arg1 is a statement expression
			index = helper.buildForArg(arg0, index, 1);
		}
		else if(id.equals("semi_colon")){						//arg1 is an empty statement
			arg1 = new EmptyStmt(t);
		}
		else{													//arg1 is something else
			arg1 = null;
		}
		index++; 													//skip semi_colon
		t = lex.get(index);
		id = t.getId();
		//2nd Argument
		ArrayList<Token> tokenList = new ArrayList<>();
		for(; !id.equals("semi_colon"); index++, t = lex.get(index), id = t.getId()){
			tokenList.add(t);
		}
		Expr arg2 = helper.buildExpr(tokenList);
		index++; 													//skip semi_colon
		t = lex.get(index);
		id = t.getId();
		//3rd Argument
		ArrayList<StmtExpr> arg3 = new ArrayList<>();
		index = helper.buildForArg(arg3, index, 3);
		index++; 													//skip ending parenthesis
		t = lex.get(index);
		//StmtExpr elem = (assign2 != null) ? assign2 : unary;
		//arg3.add(elem);
		
		//stmt.setExpr(buildExpr(list));
		Body body = new Body(t);
		index = buildBody(body, index, false);
		
		stmt.setArg0(arg0);
		stmt.setArg1(arg1);
		stmt.setArg2(arg2);
		stmt.setArg3(arg3);
		stmt.setBody(body);
		return index;
	}
	
	/**
	 * Builds a node for an if statement
	 * 
	 * @param stmt The root of this if statement
	 * 
	 * @param index The index of stmt's token in lex
	 * 
	 * @return The index at the end of this statement
	 */
	public int buildIfStmt(IfStmt stmt, int index){
		ArrayList<Token> tokenList = new ArrayList<>();
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		if(!stmt.getToken().getWord().equals("else")){
			int parenCount = 0;
			do{
				parenCount += (id.equals("Lparen")) ? 1 : 0;
				parenCount -= (id.equals("Rparen")) ? 1 : 0;
				tokenList.add(t);
				index++;
				t = lex.get(index);
				id = t.getId();
			}while(parenCount != 0);
			
			stmt.setExpr(helper.buildExpr(tokenList));
		}
		else{
			stmt.setExpr(null);
		}
		Body body = new Body(t);
		index = buildBody(body, index, false);
		stmt.setBody(body);
		return index;
	}
	
	/**
	 * Builds a node for a method declaration
	 * 
	 * @param method The root node for this method declaration
	 * 
	 * @param index The index of method's token in lex
	 * 
	 * @return The index at the end of this method declaration
	 */
	public int buildMethodDecl(MethodDecl method, int index){
		Token token = method.getToken();
		method.setId(token.getWord());
		method.setAnnotation(Link.linkAnnotation(annotation));
		method.setModifier(token.getModifier());
		method.setRetType(token.getType());
		annotation = new ArrayList<>();
		Token t;
		String id;
		ArrayList<Parameter> parameterList = new ArrayList<>();
		index += 1;
		for(; index < lex.size(); index++){
			t = lex.get(index);
			id = t.getId();
			if(id.equals("pvar")){
				Parameter parameter = new Parameter(t);
				parameter.setId(t.getWord());
				parameter.setType(t.getType());
				parameter.setModifier(t.getModifier());
				parameterList.add(parameter);
			}
			else if(id.contains("Lbrace")){
				Body body = new Body(t);
				index = buildBody(body, index, false);
				method.setBody(body);
				break;
			}
		}
		method.setParam(Link.linkParam(parameterList));
		return index;
	}
	
	/**
	 * Builds a node for the switch statement
	 * 
	 * @param stmt The root node of the switch statement
	 * 
	 * @param index	The index of stmt's token in lex
	 * 
	 * @return The index at the end of the switch's body
	 */
	public int buildSwitchStmt(SwitchStmt stmt, int index){
		ArrayList<CaseStmt> caseList = new ArrayList<>();
		ArrayList<Token> tokenList = new ArrayList<>();
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		String context = t.getContext();
		String ogContext = context;
		int parenCount = 0;
		do{
			parenCount += (id.equals("Lparen")) ? 1 : 0;
			parenCount -= (id.equals("Rparen")) ? 1 : 0;
			tokenList.add(t);
			index++;
			t = lex.get(index);
			id = t.getId();
		}while(parenCount != 0);
		stmt.setExpr(helper.buildExpr(tokenList));
		
		for(; context.contains(ogContext); index++, t = lex.get(index), id = t.getId(), context = t.getContext()){
			if(id.equals("case") || id.equals("default")){
				CaseStmt caseStmt = new CaseStmt(t);
				index = stmtHelper.buildCaseStmt(caseStmt, index);
				t = lex.get(index);
				Body body = new Body(t);
				index = buildBody(body, index, true);
				caseStmt.setBody(body);
				caseList.add(caseStmt);
			}
		}
		stmt.setCaseStmt(Link.linkCaseStmt(caseList));
		return index;
	}
	
	/**
	 * Builds a node to link the related try, catch, and finally statements
	 * 
	 * @param stmt The root node linking all these statements
	 * 
	 * @param index The index of stmt's token in lex
	 * 
	 * @return The index at the end of the finally or last catch statement
	 */
	public int buildTryStmt(TryStmt stmt, int index){
		ArrayList<CatchStmt> catchList = new ArrayList<>();
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		Body body = new Body(t);
		index = buildBody(body, index, false);
		stmt.setBody(body);
		index++;
		t = lex.get(index);
		id = t.getId();
		for(; id.equals("catch"); index++, t = lex.get(index), id = t.getId()){
			ArrayList<String> exceptionList = new ArrayList<String>();
			CatchStmt c = new CatchStmt(t);
			index += 2; 											//skip Lparen
			t = lex.get(index);
			id = t.getId();
			exceptionList.add(t.getWord());
			
			index++; 												//Move on to next token
			while(lex.get(index).getWord().equals("|")){ 			//Exceptions are separated by '|'
				index++; 											//Move past '|'
				exceptionList.add(lex.get(index).getWord());
				index++; 											//Move on to next token;
			}
			Parameter param = new Parameter(lex.get(index));		//The next token is the parameter
			param.setId(lex.get(index).getWord());
			index += 2; 											//Move past ')' to '{'
			
			t = lex.get(index);
			Body cBody = new Body(t);
			index = buildBody(cBody, index, false);
			
			c.setException(exceptionList);
			c.setParam(param);
			c.setBody(cBody);
			catchList.add(c);
			stmt.setCatchStmt(Link.linkCatchStmt(catchList));
		}
		if(id.equals("finally")){
			FinallyStmt f = new FinallyStmt(t);
			index++;
			t = lex.get(index);
			id = t.getId();
			Body fBody = new Body(t);
			index = buildBody(fBody, index, false);
			f.setBody(fBody);
			stmt.setFinallyStmt(f);
		}
		return index;
	}
	
	/**
	 * Builds a node for a while loop
	 * 
	 * @param stmt The root of this while loop
	 * 
	 * @param index The index of stmt's token in lex
	 * 
	 * @return The index at the end of this loop
	 */
	public int buildWhileStmt(WhileStmt stmt, int index){
		ArrayList<Token> tokenList = new ArrayList<>();
		index += 1;
		Token t = lex.get(index);
		String id = t.getId();
		int parenCount = 0;
		do{
			parenCount += (id.equals("Lparen")) ? 1 : 0;
			parenCount -= (id.equals("Rparen")) ? 1 : 0;
			tokenList.add(t);
			index++;
			t = lex.get(index);
			id = t.getId();
		}while(parenCount != 0);
		stmt.setExpr(helper.buildExpr(tokenList));
		Body body = new Body(t);
		index = buildBody(body, index, false);
		stmt.setBody(body);
		return index;
	}
}