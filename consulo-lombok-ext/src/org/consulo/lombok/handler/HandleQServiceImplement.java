package org.consulo.lombok.handler;

import com.sun.tools.javac.tree.JCTree;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;

/**
 * @author VISTALL
 * @since 12:05/04.06.13
 */
public class HandleQServiceImplement extends JavacASTAdapter
{
	@Override
	public void visitType(JavacNode typeNode, JCTree.JCClassDecl type)
	{

	}
}
