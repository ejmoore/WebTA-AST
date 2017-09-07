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
import stmt.BodyStmt;
import stmt.CatchStmt;
import stmt.ExprStmt;
import stmt.ForEachStmt;
import stmt.ForStmt;
import stmt.IfStmt;
import stmt.TryStmt;
import stmt.VarDecl;

public class ASTTest2 {
	Lex lex;
	ArrayList<Token> list;
	AST ast;
	Body body;
	Body body2;
	ClassBody classBody3;
	public ASTTest2() throws FileNotFoundException{
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
}
