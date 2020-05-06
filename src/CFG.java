import java.util.*;

public class CFG
{
	private FG fg = null;
	private Automaton dfa = null;

	// Properties
	String startingVariable = "$";
	HashMap< String, List< AppearanceNode > > appearances;
	HashMap< String, Set< Production > > productTable;

	// Constructor
	public CFG( FG fg, Automaton dfa)
	{
		this.fg = fg;
		this.dfa = dfa;
		appearances = new HashMap<>();
		productTable = new HashMap<>();

		/*Production p = new Production();
		p.production.add( "B");
		p.count = 1;
		p.parentVariable = startingVariable;
		AddProduction( startingVariable, p);

		p = new Production();
		p.production.add( "C");
		p.production.add( "A");
		p.count = 2;
		p.parentVariable = "B";
		AddProduction( "B", p);

		p = new Production();
		p.production.add( "D");
		p.production.add( "c");
		p.production.add( "D");
		p.count = 2;
		p.parentVariable = "A";
		AddProduction( "A", p);

		p = new Production();
		p.production.add( "B");
		p.count = 1;
		p.parentVariable = "C";
		AddProduction( "C", p);

		p = new Production();
		p.production.add( "b");
		p.count = 0;
		p.parentVariable = "C";
		AddProduction( "C", p);

		p = new Production();
		p.production.add( "K");
		p.production.add( "L");
		p.production.add( "m");
		p.count = 2;
		p.parentVariable = "D";
		AddProduction( "D", p);

		p = new Production();
		p.production.add( "C");
		p.count = 1;
		p.parentVariable = "D";
		AddProduction( "D", p);

		p = new Production();
		p.production.add( "eps");
		p.count = 0;
		p.parentVariable = "K";
		AddProduction( "K", p);

		p = new Production();
		p.production.add( "eps");
		p.count = 0;
		p.parentVariable = "L";
		AddProduction( "L", p);

		PrintProductTable();*/
	}

	public void AddAppearance( String key, Production product, int indexInProduction )
	{
		AppearanceNode node = new AppearanceNode( product, indexInProduction);
		if ( !appearances.containsKey( key) )
		{
			appearances.put( key, new ArrayList<AppearanceNode>());
		}
		appearances.get( key).add( node);
	}

	public void AddProduction( String key, Production product)
	{
		if ( !productTable.containsKey( key) )
		{
			productTable.put( key, new HashSet<>());
		}
		productTable.get( key).add( product);
	}

	public void MultiplyFGDFA()
	{

	}

	public void PrintProductTable()
	{
		for ( String variable : productTable.keySet())
		{
			System.out.print( variable + " -> ");
			for ( Production p : productTable.get( variable))
			{
				for ( String symbol : p.production)
				{
					System.out.print( symbol + ".");
				}
				System.out.print( " and count is " + p.count);
				System.out.print( " and parent node is " + p.parentVariable);
				System.out.print( " |||| ");
			}
			System.out.println();
		}
	}
}


class AppearanceNode
{
	Production productionAppeardIn;
	int index;

	public AppearanceNode( Production production, int indexInProduction)
	{
		productionAppeardIn = production;
		index = indexInProduction;
	}

	public AppearanceNode( Production production)
	{
		productionAppeardIn = production;
		index = -1;
	}
}

class Production
{
	ArrayList<String> production;
	int count;
	String parentVariable;

	public Production()
	{
		production = new ArrayList<>();
		count = 0;
		parentVariable = null;
	}
}
