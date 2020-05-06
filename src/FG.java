import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FG
{
	// Variables
	HashMap< String, Set< NodePair< String> > > edgeTransition;
	HashMap< String, HashMap< String, Set<String> > > methodsToNodes;

	public FG()
	{
		edgeTransition = new HashMap<>();
		methodsToNodes = new HashMap<>();
		//NodePair<String> testNodePair = new NodePair<>("Hello", "World");
		//System.out.println(testNodePair.firstNode + " " + testNodePair.secondNode);
	}

	public void AddNodePair( String methodName, String firstNode, String secondNode)
	{
		NodePair<String> pair = new NodePair<>( firstNode, secondNode);

		if ( !edgeTransition.containsKey( methodName) )
		{
			edgeTransition.put( methodName, new HashSet<>() );
		}
		edgeTransition.get( methodName).add( pair);
	}

	public void AddNodeType( String methodName, String node, String type)
	{
		if ( !methodsToNodes.containsKey( methodName) )
		{
			methodsToNodes.put( methodName, new HashMap<>() );
		}

		if ( !methodsToNodes.get( methodName).containsKey( type) )
		{
			methodsToNodes.get( methodName).put( type, new HashSet<>());
		}

		methodsToNodes.get( methodName).get( type).add( node);
	}

	public Set<String> GetNodesOfType( String methodName, String type)
	{
		Set<String> nodes = methodsToNodes.get( methodName).get( type);
		for( String s : nodes)
		{
			System.out.println( s);
		}
		return nodes;
	}

	public Set<NodePair<String>> GetMethodTransitions(String name)
	{
		Set<NodePair<String>> nodePairs = edgeTransition.get( name);
		for ( NodePair p : nodePairs)
		{
			System.out.println( "From node " + p.firstNode + " -> to " + p.secondNode);
		}
		return nodePairs;
	}

	public void PrintFG()
	{
		System.out.print( '\n');
		System.out.println( "*****Printing node configurations****");
		for ( String key : methodsToNodes.keySet() )
		{
			System.out.println( "Method is: " + key);
			for ( String type : methodsToNodes.get( key).keySet() )
			{
				System.out.println( "Type of the nodes are: " + type);
				for ( String node : methodsToNodes.get( key).get( type) )
				{
					System.out.println( "Node is: " + node);
				}
			}
			System.out.println();
		}

		System.out.println( "*****Printing edge configurations*****");
		for ( String key : edgeTransition.keySet() )
		{
			System.out.println( "Method is: " + key);
			for ( NodePair pair : edgeTransition.get( key) )
			{
				System.out.println( "First Node is " + pair.firstNode + " and second node is " + pair.secondNode);
			}
			System.out.println();
		}
	}
}
