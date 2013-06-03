package org.consulo.lombok.handler;

import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Modifier;

import org.consulo.lombok.annotations.ApplicationService;
import org.consulo.lombok.annotations.ModuleService;
import org.consulo.lombok.annotations.ProjectService;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.Pretty;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

/**
 * @author VISTALL
 * @since 16:44/03.06.13
 */
public class HandleQService
{
	private static final String NOT_NULL_ANNOTATION = "org.jetbrains.annotations.NotNull";

	public static enum ServiceType
	{
		Application,
		Project
				{
					@Override
					public List<JCTree.JCVariableDecl> getArguments(TreeMaker treeMaker, JavacNode javacNode)
					{
						final JCTree.JCExpression jcExpression = chainDotsString(javacNode, "com.intellij.openapi.project.Project");

						final JCTree.JCVariableDecl varDec = treeMaker.VarDef(createModifierListWithNotNull(treeMaker, javacNode, 0), javacNode.toName("project"), jcExpression, null);
						return List.of(varDec);
					}

					@Override
					public List<JCTree.JCExpression> preAppendArguments(List<JCTree.JCExpression> expressions, TreeMaker treeMaker, JCTree.JCClassDecl classDecl, JavacNode classNode)
					{
						expressions = expressions.append(chainDotsString(classNode, "project"));
						expressions = super.preAppendArguments(expressions, treeMaker, classDecl, classNode);
						return expressions;
					}
				},
		Module
				{
					@Override
					public List<JCTree.JCVariableDecl> getArguments(TreeMaker treeMaker, JavacNode javacNode)
					{
						final JCTree.JCExpression jcExpression = chainDotsString(javacNode, "com.intellij.openapi.module.Module");

						final JCTree.JCVariableDecl varDec = treeMaker.VarDef(createModifierListWithNotNull(treeMaker, javacNode, 0), javacNode.toName("module"), jcExpression, null);
						return List.of(varDec);
					}

					@Override
					public List<JCTree.JCExpression> preAppendArguments(List<JCTree.JCExpression> expressions, TreeMaker treeMaker, JCTree.JCClassDecl classDecl, JavacNode classNode)
					{
						expressions = expressions.append(chainDotsString(classNode, "module"));
						expressions = super.preAppendArguments(expressions, treeMaker, classDecl, classNode);
						return expressions;
					}

					@Override
					public String getServiceManagerQName()
					{
						return "com.intellij.openapi.module.ModuleServiceManager.getService";
					}
				};

		public List<JCTree.JCVariableDecl> getArguments(TreeMaker treeMaker, JavacNode javacNode)
		{
			return List.nil();
		}

		public List<JCTree.JCExpression> preAppendArguments(List<JCTree.JCExpression> expressions, TreeMaker treeMaker, JCTree.JCClassDecl classDecl, JavacNode classNode)
		{
			return expressions.append(getClassAccess(treeMaker, classDecl, classNode));
		}

		public String getServiceManagerQName()
		{
			return "com.intellij.openapi.components.ServiceManager.getService";
		}
	}

	public static class HandleApplicationService extends JavacAnnotationHandler<ApplicationService>
	{
		@Override
		public void handle(AnnotationValues<ApplicationService> annotationValues, JCTree.JCAnnotation jcAnnotation, JavacNode node)
		{
			make0(node, ServiceType.Application);
		}
	}

	public static class HandleProjectService extends JavacAnnotationHandler<ProjectService>
	{
		@Override
		public void handle(AnnotationValues<ProjectService> annotationValues, JCTree.JCAnnotation jcAnnotation, JavacNode node)
		{
			make0(node, ServiceType.Project);
		}
	}

	public static class HandleModuleService extends JavacAnnotationHandler<ModuleService>
	{
		@Override
		public void handle(AnnotationValues<ModuleService> annotationValues, JCTree.JCAnnotation jcAnnotation, JavacNode node)
		{
			make0(node, ServiceType.Module);
		}
	}

	private static void make0(JavacNode node, ServiceType serviceType)
	{
		JavacNode typeNode = node.up();
		switch(typeNode.getKind())
		{
			case TYPE:
				make(node, serviceType);
				break;
			default:
				node.addError("@#Service annotation is applicable only to classes");
				break;
		}
	}

	private static void make(JavacNode node, ServiceType serviceType)
	{
		final TreeMaker treeMaker = node.getTreeMaker();
		final JavacNode classNode = node.up();
		final JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classNode.get();

		// if class is interface - make it abstract
		makeAbstractClassIfInterfaceFound(classDecl);

		final JCTree.JCModifiers modifiers = createModifierListWithNotNull(treeMaker, classNode, Modifier.PUBLIC | Modifier.STATIC);

		final JCTree.JCExpression methodCallQName = chainDotsString(classNode, serviceType.getServiceManagerQName());

		List<JCTree.JCExpression> arguments = List.nil();
		arguments = serviceType.preAppendArguments(arguments, treeMaker, classDecl, classNode);

		final JCTree.JCMethodInvocation methodCallDecl = treeMaker.Apply(List.<JCTree.JCExpression>nil(), methodCallQName, arguments);

		final JCTree.JCReturn returnDecl = treeMaker.Return(methodCallDecl);
		final JCTree.JCBlock blockDecl = treeMaker.Block(0, List.<JCTree.JCStatement>of(returnDecl));

		final JCTree.JCMethodDecl decl = treeMaker.MethodDef(modifiers, classNode.toName("getInstance"), JavacHandlerUtil.chainDotsString(classNode, classDecl.name.toString()), List.<JCTree.JCTypeParameter>nil(), serviceType.getArguments(treeMaker, classNode), List.<JCTree.JCExpression>nil(), blockDecl, null);

		JavacHandlerUtil.injectMethod(classNode, decl);
		try
		{
			StringWriter stringWriter = new StringWriter();
			Pretty pretty = new Pretty(stringWriter, false);

			pretty.print(classDecl);
			System.out.println(stringWriter);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private static JCTree.JCModifiers createModifierListWithNotNull(TreeMaker treeMaker, JavacNode classNode, long val)
	{
		final JCTree.JCAnnotation notNullAnnotationDecl = treeMaker.Annotation(chainDotsString(classNode, NOT_NULL_ANNOTATION), List.<JCTree.JCExpression>nil());
		return treeMaker.Modifiers(val, List.<JCTree.JCAnnotation>of(notNullAnnotationDecl));
	}

	private static JCTree.JCFieldAccess getClassAccess(TreeMaker treeMaker, JCTree.JCClassDecl classDecl, JavacNode node)
	{
		Name name = classDecl.name;
		return treeMaker.Select(treeMaker.Ident(name), node.toName("class"));
	}

	private static void makeAbstractClassIfInterfaceFound(JCTree.JCClassDecl jcClassDecl)
	{
		if((jcClassDecl.mods.flags & Modifier.INTERFACE) != 0)
		{
			jcClassDecl.mods.flags &= ~Modifier.INTERFACE;
			jcClassDecl.mods.flags |= Modifier.ABSTRACT;

			final List<JCTree> members = jcClassDecl.getMembers();
			for(JCTree member : members)
			{
				switch(member.getKind())
				{
					case METHOD:
						JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) member;
						methodDecl.mods.flags |= Modifier.ABSTRACT;
						methodDecl.mods.flags |= Modifier.PUBLIC;
						break;
					case VARIABLE:
						JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) member;
						variableDecl.mods.flags |= Modifier.PUBLIC;
						variableDecl.mods.flags |= Modifier.STATIC;
						variableDecl.mods.flags |= Modifier.FINAL;
						break;
					default:
						throw new IllegalArgumentException(member.getKind().name());
				}
			}
		}
	}
}