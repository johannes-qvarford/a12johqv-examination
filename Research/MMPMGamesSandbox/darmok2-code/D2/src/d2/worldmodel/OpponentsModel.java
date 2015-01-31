/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel;

import gatech.mmpm.Action;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.Trace;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom.Element;


public abstract class OpponentsModel {
	public OpponentsModel() {
		
	}
	
	public abstract void learn(Trace traces);
	public abstract void savetoXML(XMLWriter w);
	public abstract void saveDifferenceToXML(XMLWriter w);
	public abstract void loadfromXML(Element e);
	public abstract void loadDifferencefromXML(Element e);
	public void evaluateWithTrace(Trace t) {
		
		class Evaluation {
			public int additional = 0;
			public int missing = 0;
			public int correct = 0;
		}
		
		Set<String> players = new HashSet<String>();
		HashMap<String,Evaluation> results = new HashMap<String,Evaluation>();
		
		System.out.println("\nOpponentsModel: evaluation with trace\n");		
		
		for(Entry e:t.getEntries()) {
			for(Action a:e.getActions()) {
				players.add(a.getPlayerID());
			}
			
			List<Action> predicted = predictActions(e.getTimeStamp(), e.getGameState(),players);
			
			List<Action> additional = new LinkedList<Action>();
			List<Action> missing = new LinkedList<Action>();
			List<Action> correct = new LinkedList<Action>();
				
			for(Action a:e.getActions()) {
				missing.add(a);
			}
				
			for(Action a:predicted) {
				Action found = null;
				for(Action a2:missing) {
					if (a.equivalents(a2)) {
						found = a2;						
					}
				}
				if (found==null) {
					additional.add(a);
				} else {
					missing.remove(found);
					correct.add(a);
				}
			}
			
			{
				System.out.println("At cycle " + e.getTimeStamp());
				
				System.out.println("* Correct:");
				for(Action a:correct) System.out.println("  " + a);
				System.out.println("* Additional:");
				for(Action a:additional) System.out.println("  " + a);
				System.out.println("* Missed:");
				for(Action a:missing) System.out.println("  " + a);
				System.out.println("");
			}
			
			for(String player:players) {
				Evaluation eval = results.get(player);
				if (eval==null) {
					eval = new Evaluation();
					results.put(player,eval);
				}
				
				for(Action a:missing) 
					if (a.getPlayerID().equals(player)) eval.missing++;
				for(Action a:additional) 
					if (a.getPlayerID().equals(player)) eval.additional++;
				for(Action a:correct) 
					if (a.getPlayerID().equals(player)) eval.correct++;			
			}
		}
		
		for(String player:players) {
			Evaluation eval = results.get(player);
			
			if (eval!=null) {
				System.out.println("Results for player " + player);
				System.out.println("  - Correct: " + eval.correct);
				System.out.println("  - Additional: " + eval.additional);
				System.out.println("  - Missed: " + eval.missing);
				System.out.println("  - Total Accuracy: " + ((float)eval.correct/(float)(eval.correct+eval.missing+eval.additional)));
			}
		}
		
	}

	public abstract List<Action> predictActions(int cycle, GameState gs,Set<String> players);

}
