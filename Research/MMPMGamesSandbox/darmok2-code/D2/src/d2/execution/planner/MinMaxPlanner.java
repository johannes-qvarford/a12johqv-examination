/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

/*TODO:
 * - Consider "MAX_TREE_LEVELS", etc.
 * - Clone the "complete" plan and not just the sub part
 * - do the mini-max
 * - Test
 * - To multi-thread this so that simulations happen in parallel
*/
package d2.execution.planner;

import d2.core.D2;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.adaptation.PlanAdaptation;
import d2.execution.planbase.PlanBase;
import d2.execution.planexecution.PlanExecution;
import d2.plans.GoalPlan;
import d2.plans.PetriNetElement;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanState;
import d2.plans.State;
import d2.plans.Transition;
import d2.worldmodel.WorldModel;
import gatech.mmpm.GameState;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

public class MinMaxPlanner extends Planner {

	public int NO_OF_BRANCHES = 3;
	public int TOTAL_LOOKAHEAD_CYCLES = 10;
	public int NO_OF_SIMULATIONS = 2;
	public int GRANULARITY = 10;
	public int MAX_TREE_LEVELS = 4;
	public long timeOut = 1000;
	List <MinMaxElement> rootNodeList;
	protected int treeLevel = 0;

	public MinMaxPlanner(Plan a_plan, D2 d2, String a_playerID, PlanExecution pe, PlanAdaptation pa) {
		super(a_plan,d2,a_playerID,pe,pa);
		rootNodeList = new ArrayList <MinMaxElement>();
	}

	public MinMaxPlanner(List<Plan> a_plans, D2 d2, String a_playerID, PlanExecution pe, PlanAdaptation pa) {
		super(a_plans,d2,a_playerID,pe,pa);
		rootNodeList = new ArrayList <MinMaxElement>();
	}

	boolean expandPlan(Plan p, int cycle, GameState gs, String player) {
		MinMaxElement rootMaxNode = new MinMaxElement(p);
		rootNodeList.add(rootMaxNode);
		return expandPlan(p,cycle, gs,player,rootMaxNode,0);		
	}

	private boolean expandPlan(Plan p, int cycle, GameState gs, String player, MinMaxElement maxNode,int currentLevel) {
		if (DEBUG>=2) System.out.println("expandPlan: " + p.toString());
		List <Plan> returnedPlans = null;
		if (p instanceof GoalPlan) 
		{
			//This is a special case as there's just one subgoal now that has to be filled - This one
			GoalPlan gp = (GoalPlan)p;
			if (DEBUG>=4) System.out.println("expandPlan: found a GoalPlan!");
			
			if (gp.getExpandedGoalPlan()==null) {
				returnedPlans = m_d2.getPlanBase().retrieveNPlans(gp.getGoal(), cycle, gs, player, NO_OF_BRANCHES);
			} else {
				return expandPlan(gp.getExpandedGoalPlan(),cycle, gs,player, maxNode,currentLevel);
			}

		} 
		else
		{
			if (p instanceof PetriNetPlan) 
			{
				if (DEBUG>=4) System.out.println("expandPlan: found a PetriNetPlan!");
				for(PetriNetElement pe:((PetriNetPlan)p).getPetriNetElements()) {
					if (pe instanceof PlanState) {
						List<Pair<PlanState,Plan>> retrievedPlans = getImmediateOpenSubGoals((PlanState)pe,cycle, gs, player);
						if (retrievedPlans != null)
							returnedPlans = createNFullPlansUsingImmediateSubGoalPlans((PetriNetPlan)p,retrievedPlans,NO_OF_BRANCHES);
					}
				}
			}
		}
		if (returnedPlans != null) {
			simulateToCreateSubTree(returnedPlans,cycle, gs,player, maxNode);
			return true;
		}
		return false;
	}
	

	private List<Plan> createNFullPlansUsingImmediateSubGoalPlans(PetriNetPlan pnp, List<Pair<PlanState, Plan>> retrievedPlans, int branches) {
		List <Plan> fullPlans = new ArrayList <Plan> ();
		List <PlanState> planStates = new LinkedList <PlanState> ();
		for (Pair<PlanState,Plan> plan : retrievedPlans) {
			if (!planStates.contains(plan._a))
				planStates.add((PlanState) plan._a.clone());
		}

		//Do this branches number of times
		for (int j=0; j<branches; j++) {
			PetriNetPlan pnpClone = (PetriNetPlan)pnp.clone();
			for ( int i=0; i<pnpClone.getPetriNetElements().size();i++)
			{
				PetriNetElement pne = pnpClone.getPetriNetElement(i);
				if (pne instanceof PlanState)
					if (planStates.contains(pne)) {
						PlanState pState = (PlanState) pne;
						pState.setPlan(retrievedPlans.get(planStates.indexOf(pState)+j)._b);
					}
			}
			fullPlans.add(pnpClone);
		}
		return fullPlans;
	}

	/**
	 * gets the immediate subgoals that are still open and have to be expanded so that 
	 * we can start simulation. Also, fill them up with plans, and return a list of these
	 * plans.
	 * As multiple subgoals are possible, we retrieve NO_OF_BRANCHES plans for each subgoal
	 * Now, if there are X subgoals, and  NO_OF_BRANCHES plans for each of them,
	 * total number of plans possible (for the whole) as a result of mixing and matching these
	 * plans is : NO_OF_BRANCHES ^ X
	 * We cannot possibly iterate through all of them.
	 * So, we select plans at random and return a total of NO_OF_BRANCHES number of total plans
	 * @param currentPlanState The current Plan state. This could be a PetriNetPlan, GoalPlan or an ActionPlan
	 * @param gs The current gameState
	 */
	private List<Pair<PlanState,Plan>> getImmediateOpenSubGoals(PlanState currentPlanState, int cycle, GameState gs, String player)
	{
		List <Pair<PlanState,Plan>> retrievedPlans = new ArrayList <Pair<PlanState,Plan>> ();
		if (currentPlanState.getPlan() != null ) {
			Plan currentPlan = currentPlanState.getPlan();
			if (currentPlan instanceof GoalPlan) {
				GoalPlan gp = (GoalPlan) currentPlan;
				List <Plan> returnedPlans = m_d2.getPlanBase().retrieveNPlans(gp.getGoal(), cycle, gs,player, NO_OF_BRANCHES);
				for ( Plan plan : returnedPlans) {
					retrievedPlans.add(new Pair<PlanState,Plan>(currentPlanState,plan));
				}
				return retrievedPlans;
			}
			if ( currentPlan instanceof PetriNetPlan) {
				PetriNetPlan pnp = (PetriNetPlan) currentPlan;
				List <Integer> markedStates = new LinkedList <Integer> ();

				for ( int i = 0; i<pnp.getPetriNetElements().size();i++)
				{
					PetriNetElement p = pnp.getPetriNetElement(i);
					if(p instanceof State)
					{
						State s = (State)p;
						for(Pair<Integer,Transition> t_p : s.getNextTransitions())
							if(s.getCurrentNumberOfTokens()>=t_p._a && !markedStates.contains(i))
							{
								markedStates.add(i);
							}
					}
				}

				//if markedStates is zero, return retrievedPlans, as there are no subgoals to fill up
				if (markedStates.size() == 0)
					return retrievedPlans;

				for ( int i=0; i<pnp.getPetriNetElements().size();i++)
				{
					if ( markedStates.contains(i))
					{
						State s = (State) pnp.getPetriNetElement(i);
						List <PlanState> retPlanStates = runTillSubGoalPlan(s);
						//Now, expand this goalPlan if its present
						if (retPlanStates != null) {
							for ( PlanState pState : retPlanStates) {
								List <Pair<PlanState,Plan>> retrievedImmSubGoalPlans = getImmediateOpenSubGoals(pState,cycle, gs, player);
								retrievedPlans.addAll(retrievedImmSubGoalPlans);
							}
						}
					}

				}

			}
		} //end if getPlan() != null
		return retrievedPlans;
	}

	/**
	 * Takes in a state which is one of the next-in-sequence states
	 * Runs through the PetriNet looking for a PlanState.
	 * On locating the first one, returns back
	 * @param s Starting state
	 */
	private List<PlanState> runTillSubGoalPlan(State s) {
		List <PlanState> retStates = new ArrayList <PlanState>();
		if (s instanceof PlanState) {
			retStates.add((PlanState)s);
			return retStates;
		}
		for(Pair<Integer,Transition> t_p : s.getNextTransitions()) {
			for(Pair<Integer,State> s_p : t_p._b.getNextStates()) {
				retStates.addAll(runTillSubGoalPlan(s_p._b));
			}
		}
		return retStates;
	}

	/**
	 * Takes in a maxNode, with the list of plans retrieved for all the immediate open subgoals
	 * runs the simulator on them for NO_OF_SIMULATOR_RUNS times, and creates a MinMax tree 
	 * out of the results. This small tree would be added to the larger tree as a part of
	 * prevMaxNode 
	 * @param retrievedPlans List of plans formed by retrieving plans for immediate subgoals 
	 * @param gs Current game state
	 * @param prevMaxNode the previous max node of the MinMax tree
	 */
	private void simulateToCreateSubTree(List<Plan> retrievedPlans, int cycle, GameState gs, String player, MinMaxElement prevMaxNode) {
		treeLevel++;
		for (Plan p :retrievedPlans) {
			MinMaxElement minNode = new MinMaxElement(p);
			for (int i=0; i<NO_OF_SIMULATIONS; i++) {
				GameState predictedGS = m_d2.getWorldModel().simulate(cycle, gs, (Plan)p.clone(), currentPlayer,TOTAL_LOOKAHEAD_CYCLES ,GRANULARITY , null);
				MinMaxElement maxNode = new MinMaxElement(p,predictedGS);
				minNode.addNextElement(maxNode);
			}
			prevMaxNode.addNextElement(minNode);
		}
		for (MinMaxElement minNode : prevMaxNode.getNextElements()) {
			for (MinMaxElement maxNode : minNode.getNextElements()) {
				// TODO: add the correct level
				expandPlan(maxNode.getChosenPlan(), cycle, maxNode.getReturnedGameState(),player,  maxNode,0);
			}
			if (treeLevel >= MAX_TREE_LEVELS) break;
		}
	}

	public void saveToXML(XMLWriter w) {
		w.tag("Planner");
		w.tag("type",this.getClass().getName());
		w.tag("plan-execution");
			m_planExecution.saveToXML(w);
		w.tag("/plan-execution");
		w.tag("plan-adaptation");
			m_planAdaptation.saveToXML(w);
		w.tag("/plan-adaptation");
		w.tag("no-of-branches",NO_OF_BRANCHES);
		w.tag("no-of-simulations",NO_OF_SIMULATIONS);
		w.tag("total-lookahead-cycles",TOTAL_LOOKAHEAD_CYCLES);
		w.tag("granularity",GRANULARITY);
		w.tag("max-tree-levels",MAX_TREE_LEVELS);
		w.tag("time-out",timeOut);
		w.tag("/Planner");
	}
	
	public static Planner loadFromXMLInternal(Element xml,D2 d2,String player) {
		PlanExecution pe = null;
		PlanAdaptation pa = null;
		pe = PlanExecution.loadFromXML(xml.getChild("plan-execution"),player, d2);
		pa = PlanAdaptation.loadFromXML(xml.getChild("plan-adaptation"));
		
		MinMaxPlanner ret =  new MinMaxPlanner(new LinkedList<Plan>(),d2,player,pe,pa);
		
		ret.NO_OF_BRANCHES = Integer.parseInt(xml.getChildText("no-of-branches"));
		ret.NO_OF_SIMULATIONS = Integer.parseInt(xml.getChildText("no-of-simulations"));
		ret.TOTAL_LOOKAHEAD_CYCLES = Integer.parseInt(xml.getChildText("total-lookahead-cycles"));
		ret.GRANULARITY = Integer.parseInt(xml.getChildText("granularity"));
		ret.MAX_TREE_LEVELS = Integer.parseInt(xml.getChildText("max-tree-levels"));
		ret.timeOut = Integer.parseInt(xml.getChildText("time-out"));
		
		return ret;
	}


}
