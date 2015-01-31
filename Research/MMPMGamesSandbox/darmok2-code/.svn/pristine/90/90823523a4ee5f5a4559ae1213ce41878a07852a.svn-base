/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation;

import d2.core.D2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.jdom.Element;

import d2.execution.adaptation.parameters.ParameterAdaptation;
import d2.execution.planbase.PlanBase;
import d2.execution.planner.Planner;
import d2.plans.ActionPlan;
import d2.plans.DummyTransition;
import d2.plans.FailureTransition;
import d2.plans.GoalPlan;
import d2.plans.PetriNetElement;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanState;
import d2.plans.PreTransition;
import d2.plans.State;
import d2.plans.SuccessTransition;
import d2.plans.Transition;
import d2.worldmodel.ConditionMatcher;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

/*
 * TODO: Consider SubGoals. For now it is hardcoded that if there are subgoals in the plan, they will never be removed!
 * Santi: I've renamed lots of variables in this file, since variable names such as "index", "flag", etc. do not mean anything.
 *        Please use meaningful names for functions and variables. It is important when the code is shared with other people!
 */

public class DependencyGraphPlanAdaptation extends PlanAdaptation {

	ParameterAdaptation m_parameterAdaptor = null;
	ExpandConditionMatcher m_conditionMatcher = null;
	
	static final int DEBUG = 0;

	public DependencyGraphPlanAdaptation(D2 d2) {
		super();
		m_parameterAdaptor = new ParameterAdaptation();
		m_conditionMatcher = new ExpandConditionMatcher(d2.getWorldModel().getConditionMatcher());
	}

	public Plan adapt(Plan original_plan, GameState original_gs, String original_player, 
			Sensor original_goal, int current_cycle, GameState current_gs, String current_player,
			Sensor current_goal,List<String> usedIDs) {
		
		if (DEBUG>=1) System.out.println("DependencyGraphPlanAdaptation.adapt: starting adaptation...");
		
		if (original_plan instanceof PetriNetPlan) {
			List<Plan> plans = new ArrayList<Plan>();
			for(PlanState s:((PetriNetPlan)original_plan).getAllPlanStates()) plans.add(s.getPlan());			
			
			if (DEBUG>=3) {
				System.out.println("DependencyGraphPlanAdaptation.adapt: original plan");
				Planner.printPlanNiceComplete(original_plan);
			}

			// Create the plan graph
			int[][] planGraph =  createDependencyGraph(original_plan, current_cycle, current_gs, current_player);
	
			// Remove unnecessary actions
			int[][] newActions = removeUnnecessaryActions(planGraph, current_goal, current_cycle, current_gs,current_player,plans);
//			int[][] newActions = approximateRemoveUnnecessaryActions(planGraph, current_goal, current_cycle, current_gs,currentPlayer,original_plan);
			
			if (DEBUG>=1) displayMatrix(newActions, newActions.length);

			Plan newPlan = getUpdatedPetriNetPlan((PetriNetPlan)original_plan.clone(), newActions);
	
			// TODO: Adapt for goal parameters
			// ...
				
			if (DEBUG>=3) {
				System.out.println("DependencyGraphPlanAdaptation.adapt: new plan");
				Planner.printPlanNiceComplete(newPlan);
			}
			if (DEBUG>=1) System.out.println("DependencyGraphPlanAdaptation.adapt: adaptation complete");

			return newPlan;
		} else {
			// If the plan is not a PetriNetPlan, this class will do nothing to it
			return original_plan;
		}
	}
	
	public void adaptToAllow(Plan original_plan, GameState original_gs, String original_player,
                             Plan planToAllow, int current_cycle, GameState current_gs, String current_player,
                             PlanBase pb, PlanAdaptation parameterAdapter,List<String> usedIDs) {
		// TODO: ...
	}

	public int[][] createDependencyGraph(Plan p, int cycle, GameState gs, String player) {
		int[][] DependencyGraph = null;

		if (p instanceof PetriNetPlan) {
			// System.out.println("PetrinetPlan");
			PetriNetPlan pp = (PetriNetPlan) p;

			List<PlanState> planStates = pp.getAllPlanStates();
			DependencyGraph = new int[planStates.size()][planStates.size()];
			
			if (DEBUG>=1) for(int i = 0;i<planStates.size();i++) System.out.println(i + " - " + planStates.get(i).getPlan());

			for (int i = 0; i < planStates.size(); i++) {
				for (int j = 0; j < planStates.size(); j++) {
					DependencyGraph[i][j] = 0;
				}

			}

			int[][] pathGraph = findAllPaths(p);
			int i = 0, j = 0;
			for (PlanState current : planStates) {
				j = 0;
				for (PlanState current_previous : planStates) {
					if (i != j) {
//						System.out.println("Checking Matching for Actions " + j + " -> " + i);
						if (pathGraph[j][i] == 1) {
							if (m_conditionMatcher
									.matchSuccessWithPreconditions(
											current_previous.getPlan(), cycle, gs, player, 
                                            current.getPlan(), cycle, gs, player)) {
								DependencyGraph[j][i] |= 1;
							}
						}

						if (pathGraph[j][i] == 1) {
							if (m_conditionMatcher
									.matchFailureWithPreconditions(
											current_previous.getPlan(), cycle, gs, player,
                                            current.getPlan(), cycle, gs, player)) {
								DependencyGraph[j][i] |= 2;

							}
						}

//						System.out.println(current_previous.getPlan() + " - " + current.getPlan() + " -> " + DependencyGraph[j][i]);

					}
					j++;
				}
				i++;
			}
		}

		else {

			// System.out.println("Not a petrinetPlan");
		}
		return DependencyGraph;

	}

	public void displayMatrix(int[][] dependencyGraph, int size) {

		for (int i = 0; i < size; i++) {
			String output = "{";
			for (int j = 0; j < size; j++) {
				output = output + dependencyGraph[i][j] + ",";
			}
			output = output.substring(0, output.length() - 1) + "}";
			System.out.println(output);
		}

	}

	
	/***
	 * This function returns a matrix showing all the possible paths in a PetriNetPlan p 
	 * @param p
	 * @return
	 */

	public int[][] findAllPaths(Plan p) {
		if(p instanceof PetriNetPlan)
		{
		PetriNetPlan pp = (PetriNetPlan) p;
		
		List<PlanState> planStates = pp.getAllPlanStates();

		int[][] pathGraph = new int[planStates.size()][planStates.size()];

		for (int i = 0; i < planStates.size(); i++) {
			for (int j = 0; j < planStates.size(); j++) {
				pathGraph[i][j] = 0;
			}

		}

		for (int i = 0; i < planStates.size(); i++)
			for (int j = 0; j < planStates.size(); j++) {
				if (findPath(planStates.get(i), planStates.get(j))) {
					pathGraph[i][j] = 1;
				}
			}

		// displayMatrix(pathGraph,planStates.size());
		return pathGraph;
		}
		else
		{
			// System.out.println("Plan is not a PetriNetPlan , hence could not find the Paths in the Plan");
			return null ; 
		}
	}
	
	/***
	 * This function is used to find if there exists a path between any two given PetriNetElements 
	 * @param start
	 * @param target
	 * @return 
	 */

	public boolean findPath(PetriNetElement start, PetriNetElement target) {

		Stack<PetriNetElement> visited = new Stack<PetriNetElement>();
		Stack<PetriNetElement> open = new Stack<PetriNetElement>();

		open.push(start);

		while (!open.isEmpty()) {

			PetriNetElement p = open.pop();

			if (p == target)
				return true;

			if (p instanceof State && !visited.contains(p)) {
				State state = (State) p;

				List<Pair<Integer, Transition>> nextTransitions = state.getNextTransitions();

				for (int i = 0; i < nextTransitions.size(); i++)
					open.push(nextTransitions.get(i)._b);
			}

			else if (p instanceof Transition && !visited.contains(p)) {
				Transition transition = (Transition) p;

				List<Pair<Integer, State>> nextStates = transition.getNextStates();

				for (int i = 0; i < nextStates.size(); i++)
					open.push(nextStates.get(i)._b);
			}
			visited.push(p);

		}

		return false;
	}
	
	
	/***
	 * This function is used for faster adaptation but it gives an approximate adaptation result as compared 
	 * to the removeUnnecessaryActions function
	 * @param planGraph : DependencyGraph 
	 * @param goal	
	 * @param cycle
	 * @param current_gameState
	 * @param player
	 * @param p
	 * @return
	 */
	
	public int[][] approximateRemoveUnnecessaryActions(int [][] planGraph,Sensor goal, int cycle,
			GameState current_gameState, String player , Plan p)
	{
		if (DEBUG>=1) System.out.println("approximateRemoveUnnecessaryActions started");
		if (p instanceof PetriNetPlan) {
			PetriNetPlan pp = (PetriNetPlan) p;
			List<PlanState> planStates = pp.getAllPlanStates();

			ArrayList<Plan> matchedPlan = new ArrayList<Plan>();
			ArrayList<Integer> matchedIndices = new ArrayList<Integer>();
			int[][] newGraph = new int[planStates.size() + 1][planStates.size() + 1];

			// Copying planGraph into newGraph

			for (int i = 0; i < planStates.size(); i++) {
				for (int j = 0; j < planStates.size(); j++) {
					newGraph[i][j] = planGraph[i][j];
				}
			}

			// Extra column added to newGraph
			for (int i = 0; i < planStates.size() + 1; i++) {
				newGraph[planStates.size()][i] = 0;
				newGraph[i][planStates.size()] = 0;
			}
			newGraph[planStates.size()][planStates.size()] = 1;

			/*
			 * The dummyPlan is just to encapsulate the goal into a plan so that
			 * it can be used with the conditionmatcher . Also , the goal is
			 * supposed to be the precondition of the plan, coz the PreCondition
			 */
			GoalPlan dummyPlan = new GoalPlan(new True());
			dummyPlan.setPreCondition(goal);
//			dummyPlan.setFailureCondition(new False());
//			dummyPlan.setSuccessCondition(new True());

			for (int i = 0; i < planStates.size(); i++) {
				Plan currentPlan = planStates.get(i).getPlan();

				if (m_conditionMatcher.matchSuccessWithPreconditions(
                        currentPlan, cycle, current_gameState, player,
                        dummyPlan, cycle, current_gameState, player)
					||
                    m_conditionMatcher.matchFailureWithPreconditions(
                        currentPlan, cycle, current_gameState, player,
                        dummyPlan, cycle, current_gameState, player)) {
					// System.out.println("Match found " + currentPlan.toString() + "i :" + i);
					matchedPlan.add(currentPlan);
					matchedIndices.add(i);
					newGraph[i][planStates.size()] = 1;
				}
			}

			
			/**
			 * Planner . printPlanNice will be useful to trace the new petriNet
			 * 
			 * */
			for (int k = planStates.size() - 1; k >= 0; k--) {
				{
					if ((Float)planStates.get(k).getPlan().getSuccessCondition()
							.evaluate(cycle, current_gameState, player) >= Sensor.BOOLEAN_TRUE_THRESHOLD)
					{	
						// System.out.println(k + " - " + planStates.get(k).getPlan().toString()+  " is Satisfied");							

						if (DEBUG>=1) System.out.println("removing plan " + k);
						for (int l = 0; l <= planStates.size(); l++) {
							newGraph[l][k] = -1;
						}

						for (int l = 0; l <= planStates.size(); l++) {
							newGraph[k][l] = -1;
						}

					}
					

					else
					{
						// System.out.println(k + " - " + planStates.get(k).getPlan().toString()+  " is not Satisfied");							

					}
				}
				
			}
			return newGraph;

		}
		
//		System.out.println("Plan is not a PetriNetPlan");
		return null ; 
		
	}

	/****
	 * This function is used for Systematic adaptation of the plan
     * It is slower as compared to the approximateRemoveUnnecessaryActions function , but the adaptation is accurate 
	 * @param planGraph
	 * @param goal
	 * @param cycle
	 * @param current_gameState
	 * @param player
	 * @param p
	 * @return
	 */
	
	public int[][] removeUnnecessaryActions(int[][] planGraph, Sensor goal,
			int cycle, GameState current_gameState, String player, List<Plan> plans) {

		int nPlans = plans.size();
		ArrayList<Plan> matchedPlan = new ArrayList<Plan>();
		ArrayList<Integer> matchedIndices = new ArrayList<Integer>();
		int[][] newGraph = new int[nPlans + 1][nPlans + 1];

		// Copying planGraph into newGraph

		for (int i = 0; i < nPlans; i++) {
			for (int j = 0; j < nPlans; j++) {
				newGraph[i][j] = planGraph[i][j];
			}
		}

		// Extra column added to newGraph
		for (int i = 0; i < nPlans + 1; i++) {
			newGraph[nPlans][i] = 0;
			newGraph[i][nPlans] = 0;
		}
		newGraph[nPlans][nPlans] = 1;

		/*
		 * The dummyPlan is just to encapsulate the goal into a plan so that
		 * it can be used with the conditionmatcher . Also , the goal is
		 * supposed to be the precondition of the plan, coz the PreCondition
		 */
		GoalPlan dummyPlan = new GoalPlan(new True());
		dummyPlan.setPreCondition(goal);
//		dummyPlan.setFailureCondition(new False());
//		dummyPlan.setSuccessCondition(new True());

		for (int i = 0; i < nPlans; i++) {
			Plan currentPlan = plans.get(i);

			if (m_conditionMatcher.matchSuccessWithPreconditions(
					currentPlan, cycle, current_gameState, player,
                    dummyPlan, cycle, current_gameState, player)
				||
                m_conditionMatcher.matchFailureWithPreconditions(
					currentPlan, cycle, current_gameState, player,
                    dummyPlan, cycle, current_gameState, player)) {
				// System.out.println("Match found " + currentPlan.toString() + "i :" + i);
				matchedPlan.add(currentPlan);
				matchedIndices.add(i);
				newGraph[i][nPlans] = 1;
			}
		}

		for (int k = nPlans - 1; k >= 0; k--) {
			if ((Float)plans.get(k).getSuccessCondition().evaluate(cycle, current_gameState, player) >= Sensor.BOOLEAN_TRUE_THRESHOLD)
			{	
				// System.out.println(k + " - " + planStates.get(k).getPlan().toString()+  " is Satisfied");							
				if (DEBUG>=1) {
					System.out.println("removing plan " + k + " since Sensor " + plans.get(k).getSuccessCondition() + " is already satisfied.");
					if (DEBUG>=2) {
						System.out.println(current_gameState);
					}
				}
				for (int l = 0; l <= nPlans; l++) newGraph[l][k] = -1;
				for (int l = 0; l <= nPlans; l++) newGraph[k][l] = -1;

				int size = nPlans + 1;
				int index = size - 1;
				newGraph = recursiveRemove(newGraph, size, index, plans);
			}
			else
			{
				// System.out.println(k + " - " + planStates.get(k).getPlan().toString()+  " is not Satisfied");
			}
		}
		/***
		 * If each of the plan in the planState is satisfied , then remove
		 * it from the planGraph
		 */

		return newGraph;
	}

	public PetriNetPlan getUpdatedPetriNetPlan(PetriNetPlan p, int[][] newGraph) {

		List<Plan> plansToRemove = new LinkedList<Plan>();
		List<PetriNetElement> PNEToRemove = new LinkedList<PetriNetElement>();
		HashMap<Plan,DummyTransition> dummyTransitions = new HashMap<Plan,DummyTransition>();

		// Find plans to remove and created their corresponding dummy transitions
		for(int i = 0;i<p.getAllPlanStates().size();i++) {
			boolean toRemove = true;
			for(int j=0;j<newGraph.length;j++) {
				if (newGraph[i][j]!=-1) {
					toRemove = false;
					break;
				}
			}
			if (toRemove) {
				plansToRemove.add(p.getAllPlanStates().get(i).getPlan());
				if (DEBUG>=1) System.out.println("getUpdatedPetriNetPlan: removing plan " + p.getAllPlanStates().get(i).getPlan());
				dummyTransitions.put(p.getAllPlanStates().get(i).getPlan(), new DummyTransition());
			}
		}
		
		// Find PNE to remove, and link the dummy transitions to the appropriate next states
		for(PetriNetElement pne:p.getAllElements()) {
			if (pne instanceof PlanState) if (plansToRemove.contains(pne.getPlan())) PNEToRemove.add(pne);
			if (pne instanceof PreTransition) if (plansToRemove.contains(pne.getPlan())) PNEToRemove.add(pne);
			if (pne instanceof SuccessTransition) {
				if (plansToRemove.contains(pne.getPlan())) {
					PNEToRemove.add(pne);
					for(Pair<Integer,State> pair:((SuccessTransition)pne).getNextStates()) {
						dummyTransitions.get(pne.getPlan()).addNextState(pair._a, pair._b);
					}
				}
			}
			if (pne instanceof FailureTransition) if (plansToRemove.contains(((FailureTransition)pne).getPlan())) PNEToRemove.add(pne);
		}		

		// Find staes that point to the pretransitions that have to be removed and link them to the dummy transitions
		for(PetriNetElement pne:p.getAllElements()) {
			if (pne instanceof State) {
				for(Pair<Integer,Transition> pair:((State)pne).getNextTransitions()) {
					if (PNEToRemove.contains(pair._b)) {
						if (pair._b instanceof PreTransition) {
							((State)pne).getNextTransitions().remove(pair);
							((State)pne).getNextTransitions().add(new Pair<Integer,Transition>(1,dummyTransitions.get(pair._b.getPlan())));
						}
					}
				}
			}
		}
		
		// Remove all the unnecessary PNEs...
		for(PetriNetElement pne:PNEToRemove) p.getAllElements().remove(pne);
		for(Plan p2:dummyTransitions.keySet()) p.addPetriNetElement(dummyTransitions.get(p2));
		return p;
	}

	public int[][] recursiveRemove(int[][] planGraph, int size, int index, List<Plan> plans) {

		if (index == 0)
			return planGraph;

		else {
			boolean toBeRemoved = true;
			boolean removed = true;
			if (index<plans.size() && (plans.get(index) instanceof ActionPlan)) {
				for (int i = 0; i < size; i++) {
					if (planGraph[index][i] > 0) {
						toBeRemoved = false;
						// index--;
						break;
					}
					if (planGraph[index][i] != -1) {
						removed = false;
					}
				}
			} else {
				// TODO: For now we do not remove GoalPlans, design a method to do so
				toBeRemoved = false;
			}
			if (toBeRemoved && !removed) {

				if (DEBUG>=1) System.out.println("removing plan " + index);
				for (int k = 0; k < size; k++) {
					planGraph[k][index] = -1;
				}

				for (int k = 0; k < size; k++) {
					planGraph[index][k] = -1;

				}
				index = size - 1;
			} else {
				index--;
			}
		}

		return recursiveRemove(planGraph, size, index, plans);
	}
	
	public void saveToXML(XMLWriter w) {
		w.tag("type", this.getClass().getName());
	}

	public static PlanAdaptation loadFromXMLInternal(Element xml, D2 d2) {
		return new DependencyGraphPlanAdaptation(d2);
	}

}
