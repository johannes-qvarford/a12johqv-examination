/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.planner;

import d2.core.D2;
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
import d2.plans.PreTransition;
import d2.plans.State;
import d2.plans.Transition;
import gatech.mmpm.util.Pair;
import d2.worldmodel.WorldModel;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

public class SingleSimulatorBasedExpanderPlanner extends Planner {
	public int NO_OF_BRANCHES = 3;
	public int NO_OF_SIMULATIONS = 3;
	public int TOTAL_LOOKAHEAD_CYCLES = 200;
	public int GRANULARITY = 8;
	public SingleSimulatorBasedExpanderPlanner(Plan a_plan, D2 d2, String a_playerID, PlanExecution pe, PlanAdaptation pa) {
		super(a_plan,d2,a_playerID, pe, pa);
	}

	public SingleSimulatorBasedExpanderPlanner(List<Plan> a_plans, D2 d2, String a_playerID, PlanExecution pe, PlanAdaptation pa) {
		super(a_plans,d2,a_playerID, pe, pa);
	}

	boolean expandPlan(Plan p, int cycle, GameState gs, String player) {

		if (DEBUG>=2) System.out.println("SingleSimulatorBasedExpanderPlanner.expandPlan: " + p.toString());

		if (p instanceof GoalPlan) {
			GoalPlan gp = (GoalPlan)p;

			if (DEBUG>=2) System.out.println("expandPlan: found a GoalPlan!");

			if (gp.getExpandedGoalPlan()==null) {
				
				if ((Float)gp.getGoal().evaluate(cycle, gs, player) >= Sensor.BOOLEAN_TRUE_THRESHOLD) {
					if (DEBUG>=2) System.out.println("expandPlan: goal already achieved.");
				} else {
					long start_time = System.currentTimeMillis();
					//Plan p2 = m_pb.retrievePlan(gp.getGoal(), gs);
					List <Plan> retrievedPlans = m_d2.getPlanBase().retrieveNPlans(gp.getGoal(), cycle, gs, player, NO_OF_BRANCHES);
					Plan p2 = null;
					double best_GS_evaluation = 0;
					int i=0;
					System.out.println("Retrieved " + retrievedPlans.size() + "/" + NO_OF_BRANCHES + " plans for " + gp.getGoal());
					for ( Plan rPlan : retrievedPlans) {
						double predicted_GS_evaluation = 0.0, evaluation;
						long startTime = System.currentTimeMillis();
						for(int j=0;j<NO_OF_SIMULATIONS;j++) {
	//						List<GameState> log = w_model.logSimulation(gs, (Plan)rPlan.clone(), currentPlayer,TOTAL_LOOKAHEAD_CYCLES ,GRANULARITY , null);
	//						new StateSequenceVisualizer(log,640,480);
							
							// TODO: We cannot simulate just the piece of plan that we are inserting, we have to simulate the WHOLE plan!
							GameState predictedGS = m_d2.getWorldModel().simulate(cycle, gs, (Plan)rPlan.clone(), currentPlayer,TOTAL_LOOKAHEAD_CYCLES ,GRANULARITY , null);
							evaluation = (Float)gp.getGoal().evaluate(cycle, predictedGS, player);
							System.out.println(evaluation);
							predicted_GS_evaluation += evaluation;
						}
						predicted_GS_evaluation/=NO_OF_SIMULATIONS;
						System.out.println("Predicted performance for plan " + ++i + ": " + predicted_GS_evaluation + " (time taken: " + (System.currentTimeMillis()-startTime + ")"));
						if ( p2==null || predicted_GS_evaluation > best_GS_evaluation)
						{
							best_GS_evaluation = predicted_GS_evaluation;
							p2 = rPlan;
						}
					}
					long end_time = System.currentTimeMillis();
					System.out.println("(total time taken: " + (end_time-start_time) + ")");
					if (DEBUG>=2) System.out.println("expandPlan: Expanding with: " + p2.toString());
					gp.setExpandedGoalPlan(p2);
					return true;
				}
			} else {
				//check to see if the plan was completely executed or not.
				//If it was, then the Goal hasn't been satisfied yet, so set the expandedPlan to null, and retrieve again
				if (gp.getExpandedGoalPlan().isPlanCompletelyExecuted()) {
					if (DEBUG>=2) System.out.println("expandPlan: Plan executed without goal being satisfied!");
					gp.setExpandedGoalPlan(null);
					return true;
				} else {
					return expandPlan(gp.getExpandedGoalPlan(),cycle, gs,player);
				}				
			}
		} else if (p instanceof PetriNetPlan) {
			boolean anychange = false;
			for(PetriNetElement pe:((PetriNetPlan)p).getPetriNetElements()) {
				if (pe instanceof State) {
					if (((State)pe).getCurrentNumberOfTokens()>0) {
						// Expand the plan in the state itself:
						if (pe instanceof PlanState) {
							Plan p2 = ((PlanState)pe).getPlan();
							if (p2!=null && expandPlan(p2,cycle, gs, player)) anychange = true;
						}
						// there are tokens in this state, thus, any pretransitions comming out of this states are ready to be executed:
						for(Pair<Integer,Transition> pe2:((State)pe).getNextTransitions()) {
							if (pe2._b instanceof PreTransition) {
								Plan p2 = ((PreTransition)pe2._b).getPlan();
								if (p2!=null && expandPlan(p2,cycle, gs, player)) anychange = true;
								
							}
						}
					}
				}
			}
			return anychange;
		}	
		return false;
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
		w.tag("/Planner");
	}

	
	public static Planner loadFromXMLInternal(Element xml,D2 d2,String player) {
		PlanExecution pe = null;
		PlanAdaptation pa = null;
		pe = PlanExecution.loadFromXML(xml.getChild("plan-execution"),player, d2);
		pa = PlanAdaptation.loadFromXML(xml.getChild("plan-adaptation"));
		SingleSimulatorBasedExpanderPlanner ret =  new SingleSimulatorBasedExpanderPlanner(new LinkedList<Plan>(),d2,player,pe,pa);
		
		ret.NO_OF_BRANCHES = Integer.parseInt(xml.getChildText("no-of-branches"));
		ret.NO_OF_SIMULATIONS = Integer.parseInt(xml.getChildText("no-of-simulations"));
		ret.TOTAL_LOOKAHEAD_CYCLES = Integer.parseInt(xml.getChildText("total-lookahead-cycles"));
		ret.GRANULARITY = Integer.parseInt(xml.getChildText("granularity"));
		
		return ret;
	}

}
