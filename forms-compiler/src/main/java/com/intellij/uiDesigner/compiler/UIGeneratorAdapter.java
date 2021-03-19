package com.intellij.uiDesigner.compiler;

import consulo.internal.org.objectweb.asm.ClassVisitor;
import consulo.internal.org.objectweb.asm.Opcodes;
import consulo.internal.org.objectweb.asm.Type;
import consulo.internal.org.objectweb.asm.commons.GeneratorAdapter;
import consulo.internal.org.objectweb.asm.commons.Method;

/**
 * @author VISTALL
 * @since 25.08.2015
 */
public class UIGeneratorAdapter extends GeneratorAdapter
{
	private static final Type ourJBUIType = Type.getObjectType("com/intellij/util/ui/JBUI");
	private static final Method ourScaleIntMethod = Method.getMethod("int scale(int)");
	private static final Method ourScaleFloatMethod = Method.getMethod("float scale(float)");

	private final boolean myUseJBScaling;

	public UIGeneratorAdapter(int access,
			Method method,
			String signature,
			Type[] exceptions,
			ClassVisitor cv,
			boolean useJBScaling)
	{
		super(Opcodes.API_VERSION, cv.visitMethod(access, method.getName(), method.getDescriptor(), signature,
				getInternalNames(exceptions)), access, method.getName(), method.getDescriptor());
		myUseJBScaling = useJBScaling;
	}

	private static String[] getInternalNames(Type[] types)
	{
		if(types == null)
		{
			return null;
		}
		String[] names = new String[types.length];
		for(int i = 0; i < names.length; i++)
		{
			names[i] = types[i].getInternalName();
		}
		return names;
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
