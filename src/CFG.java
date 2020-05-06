import org.w3c.dom.Node;

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
		MultiplyFGDFA();

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
		dfa.complement();

		// add the productions as stated in the assignment
		addStartingProductions();
		addEpsilonTransitionsOfFG();
		addTripledRules();
		addReturnNodeRules();
		addRulesFromDFA();
	}

	// Assignment 1
	private void addStartingProductions() {
		String initial = (String) dfa.getInitialState(); // q0
		Set<String> finals = dfa.getAcceptingStates(); // Qf of DFA
		Set<String> entryOfMain = fg.GetNodesOfType("main", NodeType.ENTRY);
		String entry = entryOfMain.toArray(new String[entryOfMain.size()])[0]; // v0

		for (String state : finals) {
			Production prod = new Production();
			prod.count = 1;
			prod.parentVariable = startingVariable;
			prod.production.add("[" + String.join("-", initial, entry, state) + "]");
			this.AddProduction(startingVariable, prod);
		}
	}

	// Assignment 2
	private void addEpsilonTransitionsOfFG() {
		Set<NodePair<String>> epsPairs = fg.GetMethodTransitions("eps");

		// get all pairs of state sequences
		ArrayList<String> stateSequences = this.getStateSequences(false);

		// for each eps transition edge
		for (NodePair<String> pair : epsPairs) {

			// for each q_a,q_b in Q^2
			for (String sequence : stateSequences) {
				String[] seq = sequence.split("-");
				String src = pair.firstNode; // v_i
				String dst = pair.secondNode; // v_j
				String qA = seq[0]; // q_a
				String qB = seq[1]; // q_b
				String head = "[" + String.join("-", qA, src, qB) + "]";

				Production prod = new Production();
				prod.parentVariable = head;
				prod.count = 1;
				prod.production.add("[" + String.join("-", qA, dst, qB) + "]");
				this.AddProduction(head, prod);
			}
		}
	}

	// Assignment 3
	private void addTripledRules() {
		// get all Q^4 => q_a,q_b,q_c,q_d
		ArrayList<String> quadSequences = this.getStateSequences(true);

		// get method names and remove eps
		Set<String> methods = fg.edgeTransition.keySet();
		methods.remove("eps");

		// for every call edge m
		for (String method : methods) {

			Set<String> entryNode = fg.GetNodesOfType(method, NodeType.ENTRY);
			String entry = entryNode.toArray(new String[entryNode.size()])[0]; // v_k
			Set<NodePair<String>> pairs = fg.GetMethodTransitions(method); // all transitions on m

			// for every v_i -m-> v_j
			for (NodePair<String> pair : pairs) {
				String src = pair.firstNode; // v_i
				String dst = pair.secondNode; // v_j

				// for every sequence q_A,q_b,q_c,q_d in Q^4
				for (String sequence : quadSequences) {
					// Create and add production
					Production prod = new Production();
					String[] stateSeq = sequence.split("-");

					String head = "[" + String.join("-", stateSeq[0], src, stateSeq[3]) + "]";
					prod.parentVariable = head;
					prod.count = 3;

					prod.production.add("[" + String.join("-", stateSeq[0], method, stateSeq[1]) + "]");
					prod.production.add("[" + String.join("-", stateSeq[1], entry, stateSeq[2]) + "]");
					prod.production.add("[" + String.join("-", stateSeq[2], dst, stateSeq[3]) + "]");
					this.AddProduction(head, prod);
				}
			}
		}
	}

	// Assignment 4
	private void addReturnNodeRules() {
		Set<String> methods = fg.edgeTransition.keySet();
		Set<String> states = dfa.getStates();

		for (String method : methods) {

			// get every return node of method
			Set<String> returnNodes = fg.GetNodesOfType(method, NodeType.RET);

			// for every return node v_i
			for (String node : returnNodes) {

				for (String state : states) {
					String head = "[" + String.join("-", state, node, state) + "]";

					Production prod = new Production();
					prod.parentVariable = head;
					prod.production.add("EPSILON");

					this.AddProduction(head, prod);
				}
			}
		}
	}

	// Assignment 5
	private void addRulesFromDFA() {
		HashMap<String, HashMap<String, Set<String>>> transitions = dfa.getTransitions();

		// iterator over the states of DFA
		Iterator states = transitions.entrySet().iterator();

		while (states.hasNext()) {
			Map.Entry pair = (Map.Entry) states.next();
			String src = (String) pair.getKey(); // q_i

			// Iterator over transitions from src
			HashMap<String, String> labels = (HashMap<String, String>) pair.getValue();
			Iterator trans = labels.entrySet().iterator();

			while (trans.hasNext()) {
				Map.Entry innerPair = (Map.Entry) trans.next();
				String dst = (String) innerPair.getKey(); // q_j
				Set<String> methods = (Set<String>) innerPair.getValue();

				// iterate over methods, excluding eps that trigger the transition
				for (String method : methods) {
					if (!method.equals("eqs")) {
						String head = "[" + String.join("-", src, method, dst) + "]";

						Production prod = new Production();
						prod.production.add(method);
						prod.parentVariable = head;
						this.AddProduction(head, prod);
					}
				}
			}
		}
	}

	// get either Q^2 or Q^4
	private ArrayList<String> getStateSequences(boolean quadrupled) {
		Set<String> allStates = (Set<String>) dfa.getStates();

		ArrayList<String> sequences = new ArrayList<>();

		for (String first : allStates) {

			for (String second : allStates) {
				// if we need a quad-sequence, Q^4
				if (quadrupled) {
					for (String third : allStates) {

						for (String fourth : allStates) {
							sequences.add(String.join("-", first, second, third, fourth));
						}
					}
				} else {
					sequences.add(String.join("-", first, second));
				}
			}
		}
		return sequences;
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
