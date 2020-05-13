import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Lab2
{
	public static void main( String[] args) throws FileNotFoundException {

		FG fg = new FG();
		ReadSimpleTestCase( fg);

		System.out.println( "**********************");
		System.out.println( "**********************");
		System.out.println( "**********************");
		Automaton<String, String> test = new Automaton<String, String>("simple.spec"); //EvenOdd1b
		CFG cfg = new CFG( fg, test);
		//cfg.PrintProductTable();

		/*System.out.println("\n\n---------- Testing EvenOdd1a ----------\n\n");
		ArrayList<String> lines = readDfaSpecFile("EvenOdd1a.spec");
		Automaton<String, String> test = new Automaton<String, String>(lines);
		test.printGV();
		System.out.println("Initial State: " + test.getInitialState());

		System.out.println("\n\n---------- Testing EvenOdd1b ----------\n\n");
		lines = readDfaSpecFile("EvenOdd1b.spec");
		test = new Automaton<String, String>(lines);
		test.printGV();
		System.out.println("Initial State: " + test.getInitialState());

		System.out.println("\n\n---------- Testing simple ----------\n\n");
		lines = readDfaSpecFile("simple.spec");
		test = new Automaton<String, String>(lines);
		test.printGV();
		System.out.println("Initial State: " + test.getInitialState());

		System.out.println("\n\n---------- Testing Vote_v ----------\n\n");
		lines = readDfaSpecFile("Vote_v.spec");
		test = new Automaton<String, String>(lines);
		test.printGV();
		System.out.println("Initial State: " + test.getInitialState());

		System.out.println("\n\n---------- Testing Vote_gv ----------\n\n");
		lines = readDfaSpecFile("Vote_gv.spec");
		test = new Automaton<String, String>(lines);
		test.printGV();
		System.out.println("Initial State: " + test.getInitialState());
		*/
		System.exit( 0);
	}

	public static void ReadSimpleTestCase( FG toStore)
	{
		FG test = new FG();

		/*test.AddNodePair( "eps", "v0", "v1");
		test.AddNodePair( "a", "v0", "v2");
		test.AddNodePair( "eps", "v3", "v4");
		test.AddNodePair( "a", "v3", "v5");

		test.AddNodeType( "main",  "v0", Node.ENTRY);
		test.AddNodeType( "main", "v1", Node.RET);
		test.AddNodeType( "main", "v2", Node.RET);
		test.AddNodeType( "a",  "v3", Node.ENTRY);
		test.AddNodeType( "a", "v4", Node.RET);
		test.AddNodeType( "a", "v5", Node.RET);

		test.PrintFG();
		test = null;

		System.out.println( "***********");
		System.out.println( "***********");
		System.out.println( "***********");*/

		try
		{
			Scanner scan = new Scanner( new File( "FGtestcases/simple.cfg") ); //EvenOdd
			String[] arguments = new String[ 4];
			while ( scan.hasNextLine() )
			{
				String line = scan.nextLine(); // Get the current line in the cfg file
				int curArg = 0;
				int prevIndex = 0;
				for ( int i = 0; i < line.length(); i++)
				{
					if ( line.charAt( i) == ' ')
					{
						arguments[ curArg++] = line.substring( prevIndex, i);
						prevIndex = i + 1;
					}
					else if ( i + 1 == line.length() )
					{
						arguments[ curArg] = line.substring( prevIndex, i + 1);
					}
				}

				if ( arguments[ 0].equals( "node"))
				{
					arguments[ 2] = GetMethodName( arguments[ 2]);
					if ( arguments[ 3] == null)
					{
						arguments[ 3] = NodeType.NONE;
					}

					toStore.AddNodeType( arguments[ 2], arguments[ 1], arguments[ 3] );
				}
				else
				{
					toStore.AddNodePair( arguments[ 3], arguments[ 1], arguments[ 2]);
				}

				arguments[ 0] = null;
				arguments[ 1] = null;
				arguments[ 2] = null;
				arguments[ 3] = null;
			}
			scan.close();
		}
		catch ( IOException e)
		{
			System.out.println( e);
			System.out.println( "FILE DOES NOT EXIST!");
			return;
		}
	}

	public static String GetMethodName( String method)
	{
		return method.substring( 5, method.length() - 1);
	}
}
