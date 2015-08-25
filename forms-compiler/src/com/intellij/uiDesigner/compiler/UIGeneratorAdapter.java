package com.intellij.uiDesigner.compiler;

import org.jetbrains.org.objectweb.asm.ClassVisitor;
import org.jetbrains.org.objectweb.asm.Type;
import org.jetbrains.org.objectweb.asm.commons.GeneratorAdapter;
import org.jetbrains.org.objectweb.asm.commons.Method;

/**
 * @author VISTALL
 * @since 25.08.2015
 */
public class UIGeneratorAdapter extends GeneratorAdapter
{
	private static final Type ourJBUIType = Type.getType("com.intellij.util.ui.JBUI");
	private static final Method ourScaleIntMethod = Method.getMethod("int scale(int)");
	private static final Method ourScaleFloatMethod = Method.getMethod("float scale(float)");

	private final boolean myUseJBScaling;

	public UIGeneratorAdapter(int access, Method method, String signature, Type[] exceptions, ClassVisitor cv,
			boolean useJBScaling)
	{
		super(access, method, signature, exceptions, cv);
		myUseJBScaling = useJBScaling;
	}

	public void pushScaled(int value)
	{
		push(value);

		if(value > 0 && myUseJBScaling)
		{
			invokeStatic(ourJBUIType, ourScaleIntMethod);
		}
	}

	public void pushScaled(float value)
	{
		push(value);

		if(myUseJBScaling)
		{
			invokeStatic(ourJBUIType, ourScaleFloatMethod);
		}
	}
}
