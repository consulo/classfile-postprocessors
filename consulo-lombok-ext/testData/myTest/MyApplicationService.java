package myTest;

import org.consulo.lombok.annotations.ApplicationService;

/**
 * @author VISTALL
 * @since 16:48/03.06.13
 */
@ApplicationService
public abstract class MyApplicationService
{
	public static final int TEST_VALUE = 1;

	public abstract void testCall();

	public abstract  int testCall2();
}
