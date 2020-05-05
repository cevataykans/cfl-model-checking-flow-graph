import java.util.*;

public class Automaton<State, Sym> {

    protected TransMap<State, Sym> trans = new TransMap<State, Sym>();
    protected State initial;
    protected Set<State> accepting = new HashSet<State>();


    public Automaton() {
    }

    public Automaton(ArrayList<String> lines) {

        for (String line : lines) {

            String[] parts = line.split("-");

            // get the sections related to source and destinations, and transition labels
            String srcState = parts[0];
            String method = parts[1];
            String dstState = parts[2].replaceFirst(">", "");

            // if its the line denoting the initial state, set the initial state and clean the string
            if (srcState.charAt(0) == '=') {
                srcState = srcState.replaceFirst("=>", "");
                this.initial = (State) srcState.substring(1, srcState.length() - 1);
            }

            // extract source and destination states
            State src = (State) srcState.substring(1, srcState.length() - 1);
            State dst = (State) dstState.substring(1, dstState.length() - 1);

            // add transition
            this.addTransition(src, dst, (Sym) method);

            // if any of the states are enclosed in (), add them as accepting states
            if (srcState.contains("("))
                this.addAcceptingState(src);

            if (dstState.contains("("))
                this.addAcceptingState(dst);
        }

    }

    public Automaton(Automaton<State, Sym> a) {
        initial = a.initial;
        accepting.addAll(a.accepting);
        for (State src : a.trans.keySet())
            for (State dst : a.trans.get(src).keySet())
                addTransitions(src, dst, a.trans.get(src).get(dst));
    }


    public void addTransition(State src, State dst, Sym c) {
        addTransitions(src, dst, Collections.singleton(c));
    }


    public void addTransitions(State src, State dst, Collection<Sym> cs) {
        if (!trans.containsKey(src))
            trans.put(src, new HashMap<State, Set<Sym>>());

        Map<State, Set<Sym>> stateTransitions = trans.get(src);
        if (!stateTransitions.containsKey(dst))
            stateTransitions.put(dst, new HashSet<Sym>());

        stateTransitions.get(dst).addAll(cs);
    }

    public void complement() {
        Set<State> allStates = this.getStates();
        Set<State> accepting = this.getAcceptingStates();

        allStates.removeAll(accepting);

        this.accepting = allStates;
    }

    public TransMap<State, Sym> getTransitions() {
        return trans;
    }


    public void addAcceptingState(State acc) {
        accepting.add(acc);
    }


    public void setInitialState(State initial) {
        this.initial = initial;
    }


    public State getInitialState() {
        return initial;
    }

    public Set<State> getAcceptingStates() {
        return accepting;
    }

    public State getFirstAcceptingState() {
        return accepting.iterator().next();
    }


    public Set<State> getSuccessors(State src, Sym sym) {
        Set<State> successors = new HashSet<State>();
        if (trans.containsKey(src))
            for (State dst : trans.get(src).keySet())
                if (trans.get(src).get(dst).contains(sym))
                    successors.add(dst);
        return successors;
    }

    // Returns the input symbols for the automaton
    protected Set<Sym> getSymbols() {
        Set<Sym> symbols = new HashSet<Sym>();

        for (Map<State, Set<Sym>> transition : trans.values()) {
            for(Set<Sym> vals : transition.values()) {
                for(Sym s: vals) {
                    symbols.add(s);
                }
            }
        }
        return symbols;
    }

    /*
        Checks whether the automaton accepts a
        substring of the given input string
     */
    public boolean containsPattern(String input) {
        Set<Sym> inputSymbols = this.getSymbols();

        for (int i = 0; i < input.length(); i++) {
            if (inputSymbols.contains(input.charAt(i))) {
                boolean patternFound = this.searchPattern(input.substring(i));

                if (patternFound) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
        Searches for a matching pattern in the given substring
     */
    private boolean searchPattern(String word) {
        Set<Sym> inputSymbols = this.getSymbols(); // the symbols that the automaton can receive as input
        Set<State> toBeProcessed = new HashSet<State>(); // holds the current state

        for (int i = 0; i < word.length(); i++) {
            Character current = word.charAt(i);

            if (i == 0) {
                // if this is the first iteration, add the destination from the initial state
                // on the given input symbol
                toBeProcessed.addAll(this.getSuccessors(this.initial, (Sym) current));
            } else {
                Set<State> successors = new HashSet<State>(); // holds the next state

                // Find the states that can be reached from the current state on current input symbol
                for (State src : toBeProcessed)
                    successors.addAll(this.getSuccessors(src, (Sym) current));

                // If no transitions labeled with the input symbol, no match
                if (successors.isEmpty())
                    return false;

                // clear the current set
                toBeProcessed.clear();
                // add the items in the successors set to it
                toBeProcessed.addAll(successors);
            }

            // after completing the iteration, if we obtain
            // any accepting state, it's a match
            if (containsAnyAccepting(toBeProcessed))
                return true;

        }
        return false;
    }

    // Checks whether the given set of states has any accepting states
    private boolean containsAnyAccepting(Set<State> other) {
        for (State cur : other) {
            if (this.accepting.contains(cur)) {
                return true;
            }
        }
        return false;
    }

    public void mergeStates(State s1, State s2) {

        if (s1.equals(s2))
            return;

        // copy all s2-ingoing to s1-ingoing
        for (State src : getStates())
            if (trans.containsKey(src) && trans.get(src).containsKey(s2))
                addTransitions(src, s1, trans.get(src).get(s2));

        // copy all a2-outgoing to a1-outgoing
        if (trans.containsKey(s2))
            for (State dst : getStates())
                if (trans.get(s2).containsKey(dst))
                    addTransitions(s1, dst, trans.get(s2).get(dst));

        // remove a2.
        trans.remove(s2);
        for (State s : trans.keySet())
            trans.get(s).remove(s2);

        if (initial.equals(s2))
            initial = s1;

        if (accepting.remove(s2))
            accepting.add(s1);
    }



    // Returns the largest state explicitly mentioned in this automaton.
    public Set<State> getStates() {
        Set<State> states = new HashSet<State>();
        states.add(initial);
        states.addAll(accepting);
        for (State src : trans.keySet())
            for (State dst : trans.get(src).keySet())
                if (!trans.get(src).get(dst).isEmpty()) {
                    states.add(src);
                    states.add(dst);
                }
        return states;
    }

    public void printGV() {

        String acc = "";
        for (State q : accepting)
            acc += stateStringRep(q) + " ";

        System.out.println("digraph finite_state_machine {");
        System.out.println("    rankdir=LR;");
        System.out.println("    size=\"10,10\";");
        System.out.println("    node [shape = doublecircle]; " + acc + ";");
        System.out.println("    node [shape = circle];");

        for (State src : trans.keySet()) {
            for (State dst : trans.get(src).keySet()) {
                Set<Sym> syms = trans.get(src).get(dst);
                System.out.println(
                        stateStringRep(src) + " -> " +
                                stateStringRep(dst) +
                                " [ label = \"" + symsStringRep(syms) + "\" ];");
            }
        }
        System.out.println("}");
    }

    protected String stateStringRep(State s) {
        return (String) s;
    }

    protected String symsStringRep(Set<Sym> syms) {
        return syms.toString().replaceAll("\\[|,|\\]", "").replace("\"", "");
    }
}


class TransMap<State, Sym> extends HashMap<State, Map<State, Set<Sym>>> {
}