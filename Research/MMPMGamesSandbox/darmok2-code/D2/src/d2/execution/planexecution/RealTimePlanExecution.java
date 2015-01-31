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

public class RealTimePlanExecution extends PlanExecution {

    public RealTimePlanExecution(String player, PlanAdaptation parameterAdapter, PlanAdaptation planAdapter, D2 d2) {
        super(player, parameterAdapter, planAdapter, d2);
    }

    public RealTimePlanExecution() {
        super(null, null, null, null);
    }

    //returns true if plan is executed completely
    //else returns false
    public boolean execute(Planner planner, List<Action> a_actionsToExecute, PlayerGameState pgs) {
        boolean plansOver = true;
        List<Plan> toDelete = new LinkedList<Plan>();
        List<Plan> globalToAdd = new LinkedList<Plan>();
        List<Plan> toAdd = new LinkedList<Plan>();
        Plan mainPlan = null;

        if (planner.getPlans().size() > 0) {
            mainPlan = planner.getPlans().get(0);
        }

        if (EXECUTION_DEBUG >= 2) {
            System.out.println("execute: start (" + planner.getPlans().size() + ")");
        }

        //		System.out.println(" /- " + cycle + " -------------------\\ ");
        //		System.out.println(a_gs.toString());
        //		printPlanNice(m_plans.get(0));
        //		System.out.println(" \\-------------------------/ ");

        for (Plan p : planner.getPlans()) {

            //			System.out.println(" -> executing " + m_plans.indexOf(p));

            toAdd.clear();
            int status = executePlan(mainPlan, p, toAdd, toDelete, pgs);
            if ((status & PLAN_FINISHED) == 0) {
                plansOver = false;
            }

            for (Plan newPlan : toAdd) {
                if (newPlan instanceof ActionPlan) {
                    if (EXECUTION_DEBUG >= 2) {
                        System.out.println("Planner.execute: " + newPlan);
                    }
                    a_actionsToExecute.add(((ActionPlan) newPlan).getAction());
                } else {
                    if (newPlan instanceof GoalPlan) {
                        // Clear the plan once the goal has been achieved
                        ((GoalPlan) newPlan).setExpandedGoalPlan(null);
                    }
                    globalToAdd.add(newPlan);
                }
            }
        }

        for (Plan p : toDelete) {
            removeStillUsedUnitIDs(p);
            planner.removePlanTree(p);
        }
        for (Plan p : globalToAdd) {
            planner.addPlan(p);
        }

        while (!plansThatCouldntStart.isEmpty()) {
            Pair<Plan, Plan> p = plansThatCouldntStart.remove(0);
            m_planAdapter.adaptToAllow(p._b, p._b.getOriginalGameState(), p._b.getOriginalPlayer(), 
                                       p._a, pgs.cycle, pgs.gs, pgs.player, m_d2.getPlanBase(), m_parameterAdapter,unitsInUse);
        }

        if (EXECUTION_DEBUG >= 2) {
            System.out.println("value of PlansOver " + plansOver);
        }
        if (EXECUTION_DEBUG >= 2) {
            System.out.println("execute: end");
        }


        return plansOver;
    }

    public void saveToXML(XMLWriter w) {
        w.tag("type", this.getClass().getName());
        w.tag("parameter-adaptation");
        m_parameterAdapter.saveToXML(w);
        w.tag("/parameter-adaptation");
        w.tag("plan-adaptation");
        m_planAdapter.saveToXML(w);
        w.tag("/plan-adaptation");
    }

    public static PlanExecution loadFromXMLInternal(Element xml, String a_player, D2 d2) {
        PlanAdaptation pa = null, pa2 = null;
        pa = PlanAdaptation.loadFromXML(xml.getChild("parameter-adaptation"));
        if (xml.getChild("plan-adaptation") != null) {
            pa2 = PlanAdaptation.loadFromXML(xml.getChild("plan-adaptation"));
        } else {
            pa2 = new NothingPlanAdaptation();
        }
        return new RealTimePlanExecution(a_player, pa, pa2, d2);
    }
}
