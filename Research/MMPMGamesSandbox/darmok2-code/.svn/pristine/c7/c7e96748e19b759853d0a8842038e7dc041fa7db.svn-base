/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.plans;

import java.util.ArrayList;
import java.util.List;

import gatech.mmpm.util.Pair;

abstract public class State extends PetriNetElement implements Cloneable, java.io.Serializable {
	
	static final long serialVersionUID = 0x34324547;

	protected int currentNumberOfTokens = 0;
	protected ArrayList <Pair<Integer,Transition>> nextTransitions;

	public int getCurrentNumberOfTokens() {
		return currentNumberOfTokens;
	}

	public void setCurrentNumberOfTokens(int currentNumberOfTokens) {
		this.currentNumberOfTokens = currentNumberOfTokens;
	}

	public void setCurrentNumberOfTokens(String currentNumberOfTokens) {
		this.currentNumberOfTokens = Integer.parseInt(currentNumberOfTokens);
	}	


	public State()
	{
		nextTransitions = new ArrayList <Pair<Integer,Transition>>();
		//tokens = null;
	}	

	public void addNextTransition(int tokensRequired,Transition t)
	{
		for(Pair<Integer,Transition> p:nextTransitions) {
			if (p._b==t && p._a == tokensRequired) return;
		}
		nextTransitions.add(new Pair<Integer,Transition>(tokensRequired,t));
                t.addPreviousState(tokensRequired, this);
	}
		
	public List<Pair<Integer,Transition>> getNextTransitions() {
		return nextTransitions;
	}

	public void printContent()
	{
		System.out.println("State ---> " + getElementID());
//		for (Pair<Integer,Transition> t : nextTransitions)
//			System.out.println("\t\t" + t.m_b.getElementID() + "(" + t.m_a + ")");
	}
		
	//abstract public void writeToXML(String planID, Writer fw) throws IOException;

}
