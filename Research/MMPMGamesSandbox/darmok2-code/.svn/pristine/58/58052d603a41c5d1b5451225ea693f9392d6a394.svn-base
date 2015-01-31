/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.learn.planlearning;

import gatech.mmpm.Action;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.Trace;
import gatech.mmpm.sensor.Sensor;


import java.util.ArrayList;
import java.util.List;


/**
 * Class to generate a two dimensional gameState versus Goal matrix used by the
 * learning component to learn a set of actions each entry in this matrix is set
 * of Actions.
 * 
 * @author Praful
 * 
 */
public class GoalGameStateMatrix {

	private Action actionMatrix[][];

	private List<Sensor> goals;

	private String playerID;

	/**
	 * variable for testing if a goal is complete. goals are complete at 100 (or
	 * 1.0), but this is stated as 0.99 to handle floating point arithmetic
	 * issues.
	 */
	private double COMPLETE = 0.99;

	public List<Sensor> getGoals() {
		return goals;
	}

	public void setGoals(List<Sensor> goals) {
		this.goals = goals;
	}

	/**
	 * public constructor.
	 * @param PID the player to watch for a trace
	 */
	public GoalGameStateMatrix(String PID) {
		playerID = PID;
		goals = new ArrayList<Sensor>();
		// Add goals to the arrayList
/*
 		Santi: The goals are not in the D2 project anymore, they are in the games, so this is not allowed anymore:
 
		goals.add(new WinGameGoal());
		goals.add(new GetInLineWithEnemyGoal());
		goals.add(new GetInLineWithEnemyBaseGoal());
		goals.add(new DestroyEnemyBaseGoal());
		goals.add(new DestroyEnemiesGoal());
*/
	}

	/**
	 * Analyzes a trace and creates a matrix of actions representing plans to achieve goals.
	 * 
	 * Creates a MxN matrix, where M is the number of entries in the trace and N is the number of goals.
	 * Actions are included only for the goal they achieved, and null elsewhere.
	 * Actions are considered to achieve a goal if they occurred after the start of game or finished the last goal, whichever is most recent.
	 * Actions may be null if only the opponent took an action at that entry.
	 * @param t the trace to analyze.
	 * @return the matrix of actions.
	 */
	public Action[][] generateGoalGameStateMatrix(Trace t, String player) {

		List<Entry> entries = t.getEntries();
		int entriesIterator = 0;
		// intialize actionMatrix
		actionMatrix = new Action[entries.size()][goals.size()];
		List<Action> tmpList = new ArrayList<Action>();
		boolean[] completed = new boolean[goals.size()];

		// init all to false, since no goals are completed at start time
		for (int i = 0; i < completed.length; i++) {
			completed[i] = false;
		}

		// search through all entries
		for (Entry entry : entries) {
			GameState gs = entry.getGameState();
			ArrayList<Action> actions = entry.getActions();
			Action playerAction = null;
			// only add actions that are for the current player.
			for (Action action : actions) {
				if (action.getPlayerID().equals(playerID)) {
					playerAction = action;
				}
			}
			

//			System.out.println(entriesIterator);
//			System.out.println(playerAction);
			tmpList.add(playerAction);

			int goalCounter = 0;
			boolean foundGoal = false;
			// Find whether any goal is satisfied in this gameState
			for (Sensor goal : goals) {

				if (!completed[goalCounter] && (Float)goal.evaluate(entry.getTimeStamp(), gs, player) > COMPLETE) {
					foundGoal = true;
					completed[goalCounter] = true;

					//number of actions since last goal completion
					int i = entriesIterator - tmpList.size() + 1;
					//add actions for that goal to the matrix
					for (Action action : tmpList) {
						actionMatrix[i][goalCounter] = action;
						i++;
					}
				}
				goalCounter++;
			}
			// clear list of actions for completed goal
			if (foundGoal) {
				foundGoal = false;
				tmpList = new ArrayList<Action>();
			}
			entriesIterator++;
		}
		return actionMatrix;
	}
	
	/**
	 * looks through the actionMatrix for the sequence of actions
	 * that were used to solve a subgoal.
	 * @param goal the goals whose plan we're searching for.
	 * @return the raw list of actions for the plan.
	 */
	private List<Action> findActionForGoal(Sensor goal) {
		List<Action> actions = new ArrayList<Action>();
		
		//find the location of the goal in the list.
		int goalPlace = 0;
		while (!goal.equals(goals.get(goalPlace))) {
			goalPlace++;
		}
		
		//collect the non-null actions for that goal
		for(int i = 0; i < actionMatrix.length; i++) {
			if (null != actionMatrix[i][goalPlace]) {
				actions.add(actionMatrix[i][goalPlace]);
			}
		}
		
		return actions;
	}
	
	/**
	 * Gets the list of all actions for each goal.
	 * @return the list of all action used to solve each goal.
	 */
	public List<List<Action>> getGoalLists() {
		List<List<Action>> goalList = new ArrayList<List<Action>>();
		for(Sensor goal : goals) {
			goalList.add(findActionForGoal(goal));
		}
		return goalList;
	}



}
