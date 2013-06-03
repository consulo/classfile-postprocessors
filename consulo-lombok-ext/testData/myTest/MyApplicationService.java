package myTest;

import org.consulo.lombok.annotations.ApplicationService;

/**
 * @author VISTALL
 * @since 16:48/03.06.13
 */
@ApplicationService
public interface MyApplicationService
{
	int TEST_VALUE = 1;

	void testCall();

	public int testCall2();
}
