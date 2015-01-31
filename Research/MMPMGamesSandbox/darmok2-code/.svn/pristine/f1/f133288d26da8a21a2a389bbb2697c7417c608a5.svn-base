/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.planexecution;

import d2.core.D2;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.adaptation.NothingPlanAdaptation;
import d2.execution.adaptation.PlanAdaptation;
import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlayerGameState;
import d2.execution.planner.Planner;
import d2.plans.ActionPlan;
import d2.plans.GoalPlan;
import d2.plans.Plan;
import gatech.mmpm.Action;
import gatech.mmpm.GameState;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

/*
 * TODO: Make it Turn-based: 
 *  - If an execution does not result in an action, then backtrack:
 *  	- implement a backtrack that retracts plans from the current plan so that the planner looks for other plans
 *  	- implement something in the planner so that it does not retrieve a case that has already failed
 *  - If all backtracks fail, then abort...
 */

public class TurnBasedPlanExecution extends PlanExecution {	
	public static final int MAX_BACKTRACKING_RETRIES = 3; 
	public static final int MAX_RESTART_RETRIES = 3; 
	
	public TurnBasedPlanExecution(String player, PlanAdaptation parameterAdapter, PlanAdaptation planAdapter, D2 d2)
	{
		super(player,parameterAdapter,planAdapter, d2);
	}
			
	//returns true if plan is executed completely
	//else returns false
	public boolean execute(Planner planner, List<Action> a_actionsToExecute,PlayerGameState pgs)
	{
		boolean plansOver = true, plansAdvancing = false;
		List<Plan> toDelete = new LinkedList<Plan>();
		List<Plan> globalToAdd = new LinkedList<Plan>();
		List<Plan> toAdd = new LinkedList<Plan>();
		Plan mainPlan = planner.getPlans().get(0);
		int current_backtrack_retries = 0;
		int current_restart_retries = 0;

		if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: start (" + planner.getPlans().size() + ")");

//		System.out.println(" /- " + cycle + " -------------------\\ ");
//		System.out.println(a_gs.toString());
//		printPlanNice(m_plans.get(0));
//		System.out.println(" \\-------------------------/ ");

		do {			
			plansAdvancing = false;
			if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: start cycle (" + planner.getPlans().size() + ")");
			
			for(Plan p:planner.getPlans()) 
			{	
	//			System.out.println(" -> executing " + m_plans.indexOf(p));
				
				toAdd.clear();		
				int status = executePlan(mainPlan,p,toAdd,toDelete,pgs);
				if ((status&PLAN_FINISHED)==0) plansOver = false;
//				if ((status&PLAN_FINISHED)==PLAN_FINISHED) toDelete.add(p);
				if ((status&PLAN_ADVANCED)==PLAN_ADVANCED) plansAdvancing = true;
	
				for(Plan newPlan:toAdd) {
					if (EXECUTION_DEBUG>=2) System.out.println("Planner.execute: " + newPlan);
					if (newPlan instanceof ActionPlan) {
						a_actionsToExecute.add(((ActionPlan)newPlan).getAction());
					} else {
						if (newPlan instanceof GoalPlan) {
							// Clear the plan once the goal has been achieved
							((GoalPlan) newPlan).setExpandedGoalPlan(null);
						}
						globalToAdd.add(newPlan);		
					}
				}
			}
	
			for(Plan p:toDelete) {
                removeStillUsedUnitIDs(p);
                planner.removePlanTree(p);
            }
			for(Plan p:globalToAdd) planner.addPlan(p);
			toDelete.clear();
			globalToAdd.clear();

			if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: plansThatCouldntStart " + plansThatCouldntStart.size());
			
			while(!plansThatCouldntStart.isEmpty()) {
				Pair<Plan,Plan> p = plansThatCouldntStart.remove(0);
                m_planAdapter.adaptToAllow(p._b, p._b.getOriginalGameState(), p._b.getOriginalPlayer(),
                                           p._a, pgs.cycle, pgs.gs, pgs.player, m_d2.getPlanBase(), m_parameterAdapter,unitsInUse);
			}
	
			if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: end cycle (" + planner.getPlans().size() + ")");
			if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: value of PlansOver " + plansOver);
			if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: value of plansAdvancing " + plansAdvancing);
			
			if (planner.getPlans().size()==1) {
				Plan p = planner.getPlans().get(0);
				if (p instanceof GoalPlan) {
					if (((GoalPlan)p).getExpandedGoalPlan()==null) {
						// The plan is empty! this means that the planner restarted...
						current_restart_retries++;
						if (current_restart_retries>=MAX_RESTART_RETRIES) {
							// Failure
							System.out.println("The planner cannot find a valid action to execute (too many restarts)! Trying a desperate option...");
							ActionPlan a = planner.findValidAction(pgs.cycle, pgs.gs, m_parameterAdapter,unitsInUse);
							if (a!=null) {
								a_actionsToExecute.add(a.getAction());
							} else {
								System.err.println("D2 cannot find a valid action to execute!!!!!!!!!");
								return plansOver;								
							}
						}
					}
				}
			}
			
			if (a_actionsToExecute.size()==0 && !plansAdvancing) {
				if (EXECUTION_DEBUG>=1) System.out.println("No actions to be executed, let's try to plan...");
				if (!planner.plan(pgs.cycle,pgs.gs,pgs.player)) {
					if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: unsuccessful, backtracking!!! ( " + planner.getPlans().size() + ")");
					if (!planner.backtrack()) {
						if (current_backtrack_retries<MAX_BACKTRACKING_RETRIES) {
							current_backtrack_retries++;
							if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: after unsuccessful backtracking ( " + planner.getPlans().size() + ")");
							if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: retries " + current_backtrack_retries + "/" + MAX_BACKTRACKING_RETRIES);
							planner.plan(pgs.cycle, pgs.gs, pgs.player);
						} else {
							// failure!
							System.out.println("The planner cannot find a valid action to execute (too many backtracks)! Trying a desperate option...");
							ActionPlan a = planner.findValidAction(pgs.cycle, pgs.gs, m_parameterAdapter,unitsInUse);
							if (a!=null) {
								a_actionsToExecute.add(a.getAction());
							} else {
								System.err.println("D2 cannot find a valid action to execute!!!!!!!!!");
								return plansOver;								
							}
						}
					} else {
						if (EXECUTION_DEBUG>=1) System.out.println("TurnBasedPlanExecution: after backtracking ( " + planner.getPlans().size() + ")");
						// After backtracking, we have to expand the plan again:
						planner.plan(pgs.cycle,pgs.gs,pgs.player);
					}
				} else {
					if (EXECUTION_DEBUG>=1) System.out.println("The planner changed something...");					
				}
			}
			
		} while (a_actionsToExecute.size()==0);
		
		if (EXECUTION_DEBUG>=2) System.out.println("TurnBasedPlanExecution: end");

		return plansOver;
	}	

	public void saveToXML(XMLWriter w) {
		w.tag("type",this.getClass().getName());
		w.tag("parameter-adaptation");
		m_parameterAdapter.saveToXML(w);
		w.tag("/parameter-adaptation");
		w.tag("plan-adaptation");
		m_planAdapter.saveToXML(w);
		w.tag("/plan-adaptation");
	}
	
	public static PlanExecution loadFromXMLInternal(Element xml,String a_player, D2 d2) {
		PlanAdaptation pa = null, pa2 = null;
		pa = PlanAdaptation.loadFromXML(xml.getChild("parameter-adaptation"));
		if (xml.getChild("plan-adaptation")!=null) {
			pa2 = PlanAdaptation.loadFromXML(xml.getChild("plan-adaptation"));
		} else {
			pa2 = new NothingPlanAdaptation();
		}
		return new TurnBasedPlanExecution(a_player,pa,pa2,d2);
	}

}
