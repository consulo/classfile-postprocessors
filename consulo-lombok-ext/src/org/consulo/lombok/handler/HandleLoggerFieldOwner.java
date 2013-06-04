package org.consulo.lombok.handler;

import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;
import static lombok.javac.handlers.JavacHandlerUtil.fieldExists;
import static lombok.javac.handlers.JavacHandlerUtil.injectField;
import static lombok.javac.handlers.JavacHandlerUtil.recursiveSetGeneratedBy;

import org.consulo.lombok.annotations.LoggerFieldOwner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

/**
 * @author VISTALL
 * @since 11:42/03.06.13
 *
 * @see lombok.javac.handlers.HandleLog
 */
public class HandleLoggerFieldOwner extends JavacAnnotationHandler<LoggerFieldOwner>
{
	private static final String LOG_CLASS = "com.intellij.openapi.diagnostic.Logger";
	private static final String LOG_METHOD = LOG_CLASS + ".getInstance";
	private static final String LOG_FIELD_NAME = "LOGGER";

	@Override
	public void handle(AnnotationValues<LoggerFieldOwner> annotationValues, JCTree.JCAnnotation jcAnnotation, JavacNode javacNode)
	{
		processAnnotation(annotationValues,  javacNode);
	}

	public static void processAnnotation(AnnotationValues<?> annotation, JavacNode annotationNode)
	{
		deleteAnnotationIfNeccessary(annotationNode, LoggerFieldOwner.class);

		JavacNode typeNode = annotationNode.up();
		switch(typeNode.getKind())
		{
			case TYPE:
				if((((JCTree.JCClassDecl) typeNode.get()).mods.flags & Flags.INTERFACE) != 0)
				{
					annotationNode.addError("@LoggerFieldOwner is legal only on classes and enums.");
					return;
				}

				if(fieldExists(LOG_FIELD_NAME, typeNode) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS)
				{
					annotationNode.addWarning(String.format("Field '%s' already exists.", LOG_FIELD_NAME));
					return;
				}

				JCTree.JCFieldAccess loggingType = selfType(typeNode);
				createField(typeNode, loggingType, annotationNode.get());
				break;
			default:
				annotationNode.addError("@LoggerFieldOwner is legal only on types.");
				break;
		}
	}

	private static JCTree.JCFieldAccess selfType(JavacNode typeNode)
	{
		TreeMaker maker = typeNode.getTreeMaker();
		Name name = ((JCTree.JCClassDecl) typeNode.get()).name;
		return maker.Select(maker.Ident(name), typeNode.toName("class"));
	}

	private static boolean createField(JavacNode typeNode, JCTree.JCFieldAccess loggingType, JCTree source)
	{
		TreeMaker maker = typeNode.getTreeMaker();

		// private static final <loggerType> log = <factoryMethod>(<parameter>);
		JCTree.JCExpression loggerType = chainDotsString(typeNode, LOG_CLASS);
		JCTree.JCExpression factoryMethod = chainDotsString(typeNode, LOG_METHOD);

		JCTree.JCMethodInvocation factoryMethodCall = maker.Apply(List.<JCTree.JCExpression>nil(), factoryMethod, List.<JCTree.JCExpression>of(loggingType));

		JCTree.JCVariableDecl fieldDecl = recursiveSetGeneratedBy(maker.VarDef(maker.Modifiers(Flags.PRIVATE | Flags.FINAL | Flags.STATIC), typeNode.toName(LOG_FIELD_NAME), loggerType, factoryMethodCall), source);

		injectField(typeNode, fieldDecl);
		return true;
	}
}
