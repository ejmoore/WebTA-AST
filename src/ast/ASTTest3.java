package ast;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import lexer.Lex;
import lexer.Token;
import node.Body;
import node.ClassBody;

import org.junit.Before;
import org.junit.Test;

import expr.Expr;
import expr.MethodCall;
import expr.NewConstructor;
import expr.VariableArray;
import stmt.CaseStmt;
import stmt.ExprStmt;
import stmt.SwitchStmt;
import stmt.VarDeclArray;

public class ASTTest3 {
	Lex lex;
	ArrayList<Token> list;
	AST ast;
	Body body;
	Body body2;
	ClassBody classBody3;
	public ASTTest3() throws FileNotFoundException{
	}
	
	@Before
	public void before() throws FileNotFoundException, ClassNotFoundException{
		lex = new Lex("C:\\Users\\James\\workspace\\WebTA\\src\\lexer\\Hello.java");
		list = lex.lex("C:\\Users\\James\\workspace\\WebTA\\src\\lexer\\format");
		ast = new AST(list);
		body = ast.getRoot().getClassDecl().getClassBody().getMethodDecl().getBody();
		body2 = ast.getRoot().getClassDecl().getNext().getClassBody().getMethodDecl().getBody();
		classBody3 = ast.getRoot().getClassDecl().getNext().getClassBody().getClassDecl().getClassBody();
	}
	
	@Test
	public void test21() {
		String v = body.getStmt().getNext().getNext().getNext().getNext().getNext().getExpr().getToken().getWord();
		assertEquals("a", v);
	}
	
	@Test
	public void test22() {
		SwitchStmt sstmt = (SwitchStmt)body.getStmt().getNext().getNext().getNext().getNext().getNext();
		String v = sstmt.getCaseStmt().getExpr().getToken().getWord();
		assertEquals("1", v);
	}

	@Test
	public void test23() {
		SwitchStmt sstmt = (SwitchStmt)body.getStmt().getNext().getNext().getNext().getNext().getNext();
		String v = sstmt.getCaseStmt().getBody().getStmt().getToken().getWord();
		assertEquals("b", v);
	}

	@Test
	public void test24() {
		SwitchStmt sstmt = (SwitchStmt)body.getStmt().getNext().getNext().getNext().getNext().getNext();
		String v = sstmt.getCaseStmt().getNext().getBody().getStmt().getNext().getToken().getWord();
		assertEquals("break", v);
	}

	@Test
	public void test25() {
		SwitchStmt sstmt = (SwitchStmt)body.getStmt().getNext().getNext().getNext().getNext().getNext();
		CaseStmt c = sstmt.getCaseStmt().getNext().getNext();
		Expr v = c.getExpr();
		assertEquals(null, v);
	}

	@Test
	public void test26() {
		SwitchStmt sstmt = (SwitchStmt)body.getStmt().getNext().getNext().getNext().getNext().getNext();
		String v = sstmt.getCaseStmt().getNext().getNext().getBody().getStmt().getNext().getToken().getWord();
		assertEquals("break", v);
	}
	
	@Test
	public void test27() {
		VarDeclArray arr = (VarDeclArray)classBody3.getVarDecl();
		assertEquals("int[][]", arr.getType());
	}

	@Test
	public void test28() {
		VarDeclArray arr = (VarDeclArray)classBody3.getVarDecl();
		assertEquals("0", arr.getNewArray().getValue().get(2).getWord());
	}

	@Test
	public void test29() {
		VariableArray arr = (VariableArray)classBody3.getVarDecl().getNext().getExpr();
		String s = arr.getIndices().get(0).getToken().getWord();
		assertEquals("x", s);
	}

	@Test
	public void test30() {
		VariableArray arr = (VariableArray)classBody3.getVarDecl().getNext().getExpr();
		VariableArray arr2 = (VariableArray)arr.getIndices().get(0);
		String s = arr2.getIndices().get(0).getToken().getWord();
		assertEquals("0", s);
	}
	
	@Test
	public void test31() {
		Body methodBody = classBody3.getMethodDecl().getNext().getBody();
		MethodCall method = (MethodCall)methodBody.getStmt().getExpr();
		NewConstructor newC = (NewConstructor)method.getArg()[0];
		String s = newC.getId();
		assertEquals("There", s);
	}
	
	@Test
	public void test32() {
		Body methodBody = classBody3.getMethodDecl().getNext().getBody();
		MethodCall method = (MethodCall)methodBody.getStmt().getExpr();
		NewConstructor newC = (NewConstructor)method.getArg()[0];
		String s = newC.getGenericList().toString();
		assertEquals("[Integer, Double]", s);
	}

	@Test
	public void test33() {
		Body methodBody = classBody3.getMethodDecl().getNext().getBody();
		MethodCall method = (MethodCall)methodBody.getStmt().getExpr();
		NewConstructor newC = (NewConstructor)method.getArg()[0];
		MethodCall method2 = (MethodCall)newC.getArg().get(0);
		String s = method2.getToken().getType();
		assertEquals("int", s);
	}

	@Test
	public void test34() {
		Body methodBody = classBody3.getMethodDecl().getNext().getBody();
		MethodCall method = (MethodCall)methodBody.getStmt().getExpr();
		NewConstructor newC = (NewConstructor)method.getArg()[0];
		MethodCall method2 = (MethodCall)newC.getArg().get(0);
		String s = method2.getArg()[0].getToken().getType();
		assertEquals("There", s);
	}

	@Test
	public void test35() {
		Body methodBody = classBody3.getMethodDecl().getNext().getBody();
		MethodCall method = (MethodCall)methodBody.getStmt().getExpr();
		NewConstructor newC = (NewConstructor)method.getArg()[0];
		boolean boo = newC.getIsNew();
		assertTrue(boo);
	}
	
	@Test
	public void test36() {
		Body methodBody = classBody3.getMethodDecl().getNext().getNext().getBody();
		ExprStmt expr = (ExprStmt)methodBody.getStmt().getNext();
		MethodCall method = (MethodCall)expr.getExpr();
		String type = method.getToken().getType();
		assertEquals("Class", type);
	}
	
	@Test
	public void test37() {
		Body methodBody = classBody3.getMethodDecl().getNext().getNext().getBody();
		ExprStmt expr = (ExprStmt)methodBody.getStmt().getNext();
		MethodCall method = (MethodCall)expr.getExpr();
		MethodCall m2 = (MethodCall)method.getCaller();
		String word = m2.getToken().getWord();
		assertEquals("get", word);
	}
	
	@Test
	public void test38() {
		Body methodBody = classBody3.getMethodDecl().getNext().getNext().getBody();
		ExprStmt expr = (ExprStmt)methodBody.getStmt().getNext().getNext();
		MethodCall method = (MethodCall)expr.getExpr();
		String param = method.getToken().getParam();
		assertEquals("String", param);
	}
	
	@Test
	public void test39() {
		Body methodBody = classBody3.getMethodDecl().getNext().getNext().getBody();
		ExprStmt expr = (ExprStmt)methodBody.getStmt().getNext().getNext();
		MethodCall method = (MethodCall)expr.getExpr();
		MethodCall m2 = (MethodCall)method.getArg()[0];
		String type = m2.getToken().getType();
		assertEquals("String", type);
	}
	
	@Test
	//won't work because Class node has not been implemented yet
	public void test40() {
		Body methodBody = classBody3.getMethodDecl().getNext().getNext().getBody();
		ExprStmt expr = (ExprStmt)methodBody.getStmt().getNext().getNext().getNext();
		MethodCall method = (MethodCall)expr.getExpr();
		String caller = method.getCaller().getToken().getWord();
		assertEquals("Class", caller);
	}
}

