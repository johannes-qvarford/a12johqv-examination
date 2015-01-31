/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel;

import gatech.mmpm.Action;
import gatech.mmpm.GameState;
import gatech.mmpm.Trace;
import gatech.mmpm.util.XMLWriter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;

import d2.worldmodel.traceanalysis.Difference;
import d2.worldmodel.traceanalysis.DifferenceTrace;
import d2.worldmodel.traceanalysis.DifferenceTraceEntry;


public abstract class ActionsModel {
	public ActionsModel() {

	}

	// Learning Methods:
	public abstract void learn(Trace traces);

	public abstract void savetoXML(XMLWriter w);

	public abstract void loadfromXML(Element e);

	// Simulation Methods:
	public abstract GameState simulate(GameState gs, List<Action> newActions, int cycles);

	/**
	 * Given an actual trace of the game, evaluate how closely the simulation
	 * and the actual trace matched. Prints output to the console.
	 * 
	 * @param t
	 *            the actual game trace.
	 */
	public void evaluateWithTrace(Trace t) {
		DifferenceTrace dt = new DifferenceTrace(t);
		GameState working_gs = (GameState) dt.getInitialGameState().clone();
		GameState temptative_gs = null;
		List<Action> nextActions = new LinkedList<Action>();
		int cycle = 0;

		int correct = 0;
		int incorrect_additional = 0;
		int incorrect_missed = 0;
		
		List<Difference> additionalDifferences = new LinkedList<Difference>();
		List<Difference> missingDifferences = new LinkedList<Difference>();

		// taking the difference between the last game state and the next game
		// state, simulate the cycles between. Then check for differences
		// between the simulated result and the actual result
		for (DifferenceTraceEntry e : dt.getEntries()) {

			System.out.println("- " + cycle + " -> " + e.getTimeStamp());

			if (e.getTimeStamp() > cycle) {
				printActions(nextActions);
				temptative_gs = simulate(working_gs, nextActions, e.getTimeStamp() - cycle);
				nextActions.clear();
				cycle = e.getTimeStamp();
			} else {
				temptative_gs = (GameState) working_gs.clone();
			}
			nextActions.addAll(e.getActions());

			// Evaluate if all the differences where properly predicted using
			// the temptative_gs:
			{
				List<Difference> l = DifferenceTrace.findDifferences(working_gs, temptative_gs);
				// Compare how many of the differences predicted by the rules
				// are the same than the ones in the trace:
				for (Difference d : l) {
					if (e.getDifferences().contains(d)) {
						// Got a difference right!
						System.out.println("Correct Difference (" + cycle + "): " + d);
						correct++;
					} else {
						// Predicted one difference that was not there:
						System.out.println("Additional Difference (" + cycle + "): " + d);
						incorrect_additional++;
						additionalDifferences.add(d);
					}
				}
				for (Difference d : e.getDifferences()) {
					if (!l.contains(d)) {
						// Missing a difference:
						System.out.println("Missing Difference (" + cycle + "): " + d);
						incorrect_missed++;
						missingDifferences.add(d);
					}
				}
			}

			// Apply all the differences to the working_gs:
			for (Difference d : e.getDifferences()) {
				d.apply(working_gs);
			}

			System.out.println("---");

		}

		printTotals(correct, incorrect_additional, incorrect_missed);
		System.out.println("\nAdditional Differences:");
		printSummary(additionalDifferences);
		System.out.println("\nMissing Differences:");
		printSummary(missingDifferences);
	}
	
	/**
	 * Prints out the count of types by difference in order.
	 * @param diffs the differences found.
	 */
	private void printSummary(List<Difference> diffs) {
		
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		List<Map.Entry<String, Integer>> entries = new LinkedList<Map.Entry<String, Integer>>();
		
		//count the differences
		for(Difference d : diffs) {
			String type = d.getType();
			if (count.containsKey(type)) {
				Integer amount = count.get(type);
				count.put(type, amount.intValue() + 1);
			} else {
				count.put(type, 1);
			}
		}
		
		//sort differences by count
		entries.addAll(count.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>(){

			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
			
		});
		
		//print differences in order
		for (Map.Entry<String, Integer> entry : entries) {
			System.out.println(entry.getKey() + "\t-\t" + entry.getValue());
		}
		
	}

	/**
	 * Prints the correct, additional, and incorrect entries, as well as an accuracy.
	 * @param correct
	 * @param incorrect_additional
	 * @param incorrect_missed
	 */
	private void printTotals(int correct, int incorrect_additional, int incorrect_missed) {
		System.out.println("Result of rule evaluation:");
		System.out.println("  - Correct: " + correct);
		System.out.println("  - Additional: " + incorrect_additional);
		System.out.println("  - Missed: " + incorrect_missed);
		System.out.println("  - Total Accuracy: "
				+ ((float) correct / (float) (correct + incorrect_additional + incorrect_missed)));
	}

	/**
	 * Prints the list of actions to the console.
	 * 
	 * @param nextActions
	 */
	private void printActions(List<Action> nextActions) {
		System.out.println("Actions:");
		for (Action a : nextActions) {
			System.out.println(a);
		}
	}

	public void visualizeWithTrace(Trace t) {
		// Visualizing the execution of the actions model:
		// ...
	}

}
