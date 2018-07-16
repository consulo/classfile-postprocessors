/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.uiDesigner.compiler;

import java.awt.Dimension;

import consulo.internal.org.objectweb.asm.Type;
import consulo.internal.org.objectweb.asm.commons.Method;

/**
 * @author yole
 * @noinspection HardCodedStringLiteral
 */
public class DimensionPropertyCodeGenerator extends PropertyCodeGenerator
{
	private static final Type ourDimensionType = Type.getType(Dimension.class);
	private static final Method ourInitMethod = Method.getMethod("void <init>(int,int)");

	public void generatePushValue(final UIGeneratorAdapter generator, final Object value)
	{
		Dimension dimension = (Dimension) value;
		generator.newInstance(ourDimensionType);
		generator.dup();
		generator.pushScaled(dimension.width);
		generator.pushScaled(dimension.height);
		generator.invokeConstructor(ourDimensionType, ourInitMethod);
	}
}
