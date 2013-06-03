package org.consulo.lombok.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author VISTALL
 * @since 16:43/03.06.13
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface ModuleService
{
}
