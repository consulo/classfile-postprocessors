import java.util.ArrayList;
import java.util.List;

import com.sun.tools.javac.Main;

/**
 * @author VISTALL
 * @since 21:33/02.06.13
 */
public class Compiler
{
	public static void main(String[] t) throws Exception
	{
		List<String> args = new ArrayList<String>();
		args.add("-cp");
		args.add("lib/lombok.jar;lib/lombok-consulo-ext.jar");
		args.add("-d");
		args.add("testOut");
		args.add("@myCompilerList");


		Main.main(args.toArray(new String[args.size()]));
	}
}
