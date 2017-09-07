package ast;

import java.util.ArrayList;

import expr.Parameter;
import stmt.CaseStmt;
import stmt.CatchStmt;
import stmt.ExprStmt;
import stmt.IfStmt;
import stmt.Stmt;
import stmt.VarDecl;
import stmt.VarDeclArray;
import node.*;
import node.Package;

public class Link {

	//Link Annotation nodes
	public static Annotation linkAnnotation(ArrayList<Annotation> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				Annotation temp = list.get(i);
				Annotation temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link CaseStmt nodes
	public static CaseStmt linkCaseStmt(ArrayList<CaseStmt> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				CaseStmt temp = list.get(i);
				CaseStmt temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link CatchStmt nodes
	public static CatchStmt linkCatchStmt(ArrayList<CatchStmt> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				CatchStmt temp = list.get(i);
				CatchStmt temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link ClassDecl nodes
	public static ClassDecl linkClass(ArrayList<ClassDecl> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				ClassDecl temp = list.get(i);
				ClassDecl temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link ConstructorDecl nodes
		public static ConstructorDecl linkConstructor(ArrayList<ConstructorDecl> list){
			if(list.size() > 0){
				for(int i = 0; i < list.size() - 1; i++){
					ConstructorDecl temp = list.get(i);
					ConstructorDecl temp2 = list.get(i + 1);
					temp.setNext(temp2);
					list.set(i, temp);
					list.set(i + 1, temp2);
				}
				return list.get(0);
			}
			return null;
		}
	
	//linkExprStmt
	public static ExprStmt linkExprStmt(ArrayList<ExprStmt> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				ExprStmt temp = list.get(i);
				ExprStmt temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			System.out.println("link: " + list.get(0));
			return list.get(0);
		}
		return null;
	}
	
	//Link IfStmt nodes
	public static IfStmt linkIfStmt(ArrayList<IfStmt> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				IfStmt temp = list.get(i);
				IfStmt temp2 = list.get(i + 1);
				temp.setElseStmt(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link Import nodes
	public static Import linkImport(ArrayList<Import> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				Import temp = list.get(i);
				Import temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link MethodDecl nodes
	public static MethodDecl linkMethod(ArrayList<MethodDecl> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				MethodDecl temp = list.get(i);
				MethodDecl temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link Other nodes
	public static Other linkOther(ArrayList<Other> list){
		for(int i = 0; i < list.size() - 1; i++){
			Other temp = list.get(i);
			Other temp2 = list.get(i + 1);
			temp.setNext(temp2);
			list.set(i, temp);
			list.set(i + 1, temp2);
		}
		return list.get(0);
	}
	
	//Link Package nodes
	public static Package linkPackage(ArrayList<Package> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				Package temp = list.get(i);
				Package temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link Parameter nodes
	public static Parameter linkParam(ArrayList<Parameter> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				Parameter temp = list.get(i);
				Parameter temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * Links a list of Stmt's together. This includes BodyStmt's
	 * 
	 * @param list The list of Stmt's to be linked
	 * 
	 * @return The first Stmt in the list which can reach all other Stmt's
	 */
	public static Stmt linkStmt(ArrayList<Stmt> list){
		ArrayList<IfStmt> ifList = new ArrayList<>(); //For connecting ifStmt's
		if(!list.isEmpty()){
			for(int i = 0; i < list.size() - 1; i++){
				Stmt temp = list.get(i);
				Stmt temp2 = list.get(i + 1);
				if(temp instanceof IfStmt && ifList.isEmpty()){
					ifList.add((IfStmt)temp);
				}
				if(temp2.getToken().getWord().contains("else")){
					ifList.add((IfStmt)temp2);
					list.remove(i + 1);
					if(i == list.size() - 1){
						temp = linkIfStmt(ifList);
						temp.setNext(temp2);
						list.set(i, temp);
						ifList = new ArrayList<>();
					}
					i--;
				}
				else if(!ifList.isEmpty()){
					temp = linkIfStmt(ifList);
					temp.setNext(temp2);
					list.set(i, temp);
					list.set(i + 1, temp2);
					ifList = new ArrayList<>();
				}
				else{
					temp.setNext(temp2);
					list.set(i, temp);
					list.set(i + 1, temp2);
				}
			}
			return list.get(0);
		}
		return null;
	}
	
	/*Link Stmt nodes
	public static Stmt linkStmt2(ArrayList<Stmt> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				Stmt temp = list.get(i);
				Stmt temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	*/
	
	//Link VarDecl nodes declared on the same line
	public static VarDecl linkVar(ArrayList<VarDecl> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				VarDecl temp = list.get(i);
				VarDecl temp2 = list.get(i + 1);
				temp.setNextVar(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link VarDeclArray nodes declared on the same line
	public static VarDeclArray linkVarArray(ArrayList<VarDeclArray> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				VarDeclArray temp = list.get(i);
				VarDeclArray temp2 = list.get(i + 1);
				temp.setNextVar(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
	
	//Link VarDecl nodes (specifically for buildClassBody())
	public static VarDecl linkVarDecl(ArrayList<VarDecl> list){
		if(list.size() > 0){
			for(int i = 0; i < list.size() - 1; i++){
				VarDecl temp = list.get(i);
				VarDecl temp2 = list.get(i + 1);
				temp.setNext(temp2);
				list.set(i, temp);
				list.set(i + 1, temp2);
			}
			return list.get(0);
		}
		return null;
	}
}
