import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author VISTALL
 * @since 17:31/11.05.13
 */
public class IterationTest extends Assert
{
	@Test
	public void testElementIteration()
	{
		final Document document = createDocument();

		assertEquals(document.getRootElement().getName(), "element");
		for(Element element : document.getRootElement().getChildren())
		{
			assertEquals(element.getName(), "child-element");
		}
	}

	public Document createDocument()
	{
		Document document = new Document();
		final Element element = new Element("element");
		element.setAttribute("key", "value");
		element.addContent(new Element("child-element"));
		element.addContent(new Text("inner text"));

		document.addContent(element);
		return document;
	}
}
