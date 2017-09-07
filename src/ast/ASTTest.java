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

import expr.Assign;
import expr.Expr;
import expr.MethodCall;
import expr.NewConstructor;
import expr.VariableArray;
import stmt.BodyStmt;
import stmt.CaseStmt;
import stmt.CatchStmt;
import stmt.ExprStmt;
import stmt.ForEachStmt;
import stmt.ForStmt;
import stmt.IfStmt;
import stmt.SwitchStmt;
import stmt.TryStmt;
import stmt.VarDecl;
import stmt.VarDeclArray;

public class ASTTest {
	Lex lex;
	ArrayList<Token> list;
	AST ast;
	Body body;
	Body body2;
	ClassBody classBody3;
	
	@Before
	public void before() throws FileNotFoundException, ClassNotFoundException{
		lex = new Lex("/home/ejmoore/webta/WebTA/src/lexer/Hello.java");
		list = lex.lex("/home/ejmoore/webta/WebTA/src/lexer/format");
		ast = new AST(list);
		body = ast.getRoot().getClassDecl().getClassBody().getMethodDecl().getBody();
		body2 = ast.getRoot().getClassDecl().getNext().getClassBody().getMethodDecl().getBody();
		classBody3 = ast.getRoot().getClassDecl().getNext().getClassBody().getClassDecl().getClassBody();
	}
	
	@Test
	public void test01() {
		String v = body.getStmt().getToken().getWord();
		assertEquals("a", v);
	}

	@Test
	public void test02() {
		String v = body.getStmt().getExpr().getToken().getWord();
		assertEquals("+", v);
	}

	@Test
	public void test03() {
		VarDecl var = (VarDecl)body.getStmt();
		String v = var.getNextVar().getToken().getWord();
		assertEquals("b", v);
	}
	
	@Test
	public void test04() {
		VarDecl var = (VarDecl)body.getStmt();
		String v = var.getNextVar().getExpr().getToken().getWord();
		assertEquals("+", v);
	}
	
	@Test
	public void test05() {
		String v = body.getStmt().getNext().getNext().getExpr().getToken().getWord();
		assertEquals("==", v);
	}
	
	@Test
	public void test06() {
		BodyStmt bstmt = (BodyStmt)body.getStmt().getNext().getNext();
		String v = bstmt.getBody().getStmt().getToken().getWord();
		assertEquals("b", v);
	}

	@Test
	public void test07() {
		IfStmt ifs = (IfStmt)body.getStmt().getNext().getNext();
		String v = ifs.getElseStmt().getExpr().getToken().getWord();
		assertEquals("!=", v);
	}

	@Test
	public void test08() {
		IfStmt ifs = (IfStmt)body.getStmt().getNext().getNext();
		String v = ifs.getElseStmt().getBody().getStmt().getToken().getWord();
		assertEquals("a", v);
	}

	@Test
	public void test09() {
		ForStmt f = (ForStmt)body.getStmt().getNext().getNext().getNext();
		String v = f.getArg1().getToken().getWord();
		assertEquals("int", v);
	}

	@Test
	public void test10() {
		BodyStmt bstmt = (BodyStmt)body.getStmt().getNext().getNext().getNext().getNext();
		String v = bstmt.getBody().getStmt().getToken().getWord();
		assertEquals("y", v);
	}
	
	@Test
	public void test11() {
		String v = ast.getRoot().getPackage().getId();
		assertEquals("lexer", v);
	}

	@Test
	public void test12() {
		String v = ast.getRoot().getImport().getId();
		assertEquals("java.util.ArrayList", v);
	}

	@Test
	public void test13() {
		TryStmt t = (TryStmt)body2.getStmt().getNext();
		ExprStmt e = (ExprStmt)t.getBody().getStmt();
		assertEquals("=", e.getExpr().getOp());
	}
	
	@Test
	public void test14() {
		TryStmt t = (TryStmt)body2.getStmt().getNext();
		CatchStmt c = t.getCatchStmt();
		assertEquals("NullPointerException", c.getException().get(0));
	}
	
	@Test
	public void test15() {
		TryStmt t = (TryStmt)body2.getStmt().getNext();
		ExprStmt c = (ExprStmt)t.getCatchStmt().getNext().getBody().getStmt();
		Assign a = (Assign)c.getExpr();
		assertEquals("\"too_far\"", a.getExpr().getToken().getWord());
	}
	
	@Test
	public void test16() {
		TryStmt t = (TryStmt)body2.getStmt().getNext();
		ExprStmt c = (ExprStmt)t.getFinallyStmt().getBody().getStmt();
		Assign a = (Assign)c.getExpr();
		assertEquals("str", a.getLValue().getToken().getWord());
	}

	@Test
	public void test17() {
		VarDecl v = ast.getRoot().getClassDecl().getNext().getClassBody().getClassDecl().getClassBody().getVarDecl();
		assertEquals("x", v.getId());
	}

	@Test
	public void test18() {
		ForEachStmt fe = (ForEachStmt)body2.getStmt().getNext().getNext();
		String v = fe.getArg1().getId();
		assertEquals("s", v);
	}

	@Test
	public void test19() {
		ForEachStmt fe = (ForEachStmt)body2.getStmt().getNext().getNext();
		String v = fe.getArg2().getId();
		assertEquals("args", v);
	}

	@Test
	public void test20() {
		BodyStmt bstmt = (BodyStmt)body2.getStmt().getNext().getNext();
		Body b = bstmt.getBody();
		String v = b.getClassDecl().getClassBody().getVarDecl().getId();
		assertEquals("x", v);
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
}
