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

import java.awt.FlowLayout;

import consulo.internal.org.objectweb.asm.Type;
import consulo.internal.org.objectweb.asm.commons.Method;
import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;

/**
 * @author yole
 */
public class FlowLayoutCodeGenerator extends LayoutCodeGenerator
{
	private static final Type ourFlowLayoutType = Type.getType(FlowLayout.class);
	private static final Method ourConstructor = Method.getMethod("void <init>(int,int,int)");

	public void generateContainerLayout(final LwContainer lwContainer,
			final UIGeneratorAdapter generator,
			final int componentLocal)
	{
		generator.loadLocal(componentLocal);

		FlowLayout flowLayout = (FlowLayout) lwContainer.getLayout();
		generator.newInstance(ourFlowLayoutType);
		generator.dup();
		generator.push(flowLayout.getAlignment());
		generator.push(flowLayout.getHgap());
		generator.push(flowLayout.getVgap());
		generator.invokeConstructor(ourFlowLayoutType, ourConstructor);

		generator.invokeVirtual(ourContainerType, ourSetLayoutMethod);
	}

	public void generateComponentLayout(final LwComponent lwComponent,
			final UIGeneratorAdapter generator,
			final int componentLocal,
			final int parentLocal)
	{
		generator.loadLocal(parentLocal);
		generator.loadLocal(componentLocal);
		generator.invokeVirtual(ourContainerType, ourAddNoConstraintMethod);
	}
}
