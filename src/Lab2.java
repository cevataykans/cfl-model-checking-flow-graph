import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class Lab2
{
	public static void main( String[] args)
	{
		ReadSimpleTestCase();

		System.exit( 0);
	}

	public static void ReadSimpleTestCase()
	{
		FG test = new FG();

		test.AddNodePair( "eps", "v0", "v1");
		test.AddNodePair( "a", "v0", "v2");
		test.AddNodePair( "eps", "v3", "v4");
		test.AddNodePair( "a", "v3", "v5");

		test.AddNodeType( "main", Node.ENTRY, "v0");
		test.AddNodeType( "main", Node.RET, "v1");
		test.AddNodeType( "main", Node.RET, "v2");
		test.AddNodeType( "a", Node.ENTRY, "v3");
		test.AddNodeType( "a", Node.RET, "v4");
		test.AddNodeType( "a", Node.RET, "v5");

		test.PrintFG();
		test = null;

		System.out.println( "***********");
		System.out.println( "***********");
		System.out.println( "***********");

		test = new FG();

		try
		{
			Scanner scan = new Scanner( new File( "FGtestcases/simple.cfg") );
			while ( scan.hasNextLine() )
			{
				System.out.println( scan.nextLine() );
			}
			scan.close();
		}
		catch ( Exception e)
		{
			System.out.println( e);
			System.out.println( "FUCK FILE DOES NOT EXIST!");
			return;
		}
	}
}
