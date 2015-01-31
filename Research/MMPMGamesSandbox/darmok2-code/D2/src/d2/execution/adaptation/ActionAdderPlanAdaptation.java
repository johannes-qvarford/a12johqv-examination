/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.adaptation;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.planbase.PlanBase;
import d2.plans.ActionPlan;
import d2.plans.DummyState;
import d2.plans.DummyTransition;
import d2.plans.PetriNetElement;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanPreconditionFailedState;
import d2.plans.PlanState;
import d2.plans.PreFailureTransition;
import d2.plans.PreTransition;
import d2.plans.State;
import d2.util.plancreator.PetriNetHelper;

import gatech.mmpm.GameState;
import gatech.mmpm.Trace;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;
import gatech.mmpm.tracer.TraceParser;
import gatech.mmpm.util.XMLWriter;

public class ActionAdderPlanAdaptation extends PlanAdaptation {

    public int DEBUG = 0;

    public ActionAdderPlanAdaptation() {
        super();
    }

    public Plan adapt(Plan original_plan, GameState original_gs, String original_player, Sensor original_goal,
            int current_cycle, GameState current_gs, String current_player, Sensor current_goal,
            List<String> usedIDs) {
        if (DEBUG >= 1) {
            System.out.println("----");
            System.out.println("ActionAdderPlanAdaptation.adapt: goal " + current_goal);
            System.out.println("ActionAdderPlanAdaptation.adapt: plan " + original_plan);
        }

        return original_plan;
    }

    public void adaptToAllow(Plan original_plan, GameState original_gs, String original_player,
            Plan originalPlanToAllow, int cycle, GameState gs, String player,
            PlanBase pb, PlanAdaptation parameterAdapter, List<String> usedIDs) {
        // Find the initial PlanPreconditionFailedState and the final PlanState for the action:
        if (!(original_plan instanceof PetriNetPlan)) {
            return;
        }
        PetriNetPlan pnp = (PetriNetPlan) original_plan;
        PlanPreconditionFailedState startState = null;
        PlanState endState = null;
        Plan planToAllow = originalPlanToAllow;

        if (DEBUG >= 1) {
            System.out.println("ActionAdderPlanAdaptation.adaptToAllow");
        }

        if (planToAllow instanceof ActionPlan) {
            // Try to adapt it:
            planToAllow = parameterAdapter.adapt(originalPlanToAllow, originalPlanToAllow.getOriginalGameState(), original_player, null, cycle, gs, player, null, usedIDs);

            // If it cannot, at least change the actor:
            if (planToAllow == null) {
                planToAllow = (Plan) originalPlanToAllow.clone();
                ((ActionPlan) planToAllow).getAction().setPlayerID(player);
            }
            //	((ActionPlan) planToAllow).getAction().initializeActionConditions(cycle, gs);
        }
        if (DEBUG >= 1) {
            System.out.println("ActionAdderPlanAdaptation.adaptToAllow: originalPlanToAllow " + originalPlanToAllow);
        }
        if (DEBUG >= 1) {
            System.out.println("ActionAdderPlanAdaptation.adaptToAllow: planToAllow " + planToAllow);
        }

        for (PetriNetElement pne : pnp.getAllElements()) {
            if (pne.getPlan() == originalPlanToAllow) {
                if (pne instanceof PlanState) {
                    endState = (PlanState) pne;
                }
                if (pne instanceof PlanPreconditionFailedState) {
                    startState = (PlanPreconditionFailedState) pne;
                }
                if (startState != null && endState != null) {
                    break;
                }
            }
        }
        if (startState == null || endState == null) {
            return;
        }

        // Find out which are the basic conditions that are not satisfied:
        List<Sensor> preConditions = new LinkedList<Sensor>();
        ExpandConditionMatcher.expandCondition(planToAllow.getPreCondition(), preConditions);

        List<Sensor> unsatisfiedPreConditions = new LinkedList<Sensor>();
        List<ActionPlan> selectedActions = new LinkedList<ActionPlan>();

        for (Sensor c : preConditions) {
            if (DEBUG >= 2) {
                System.out.println("ActionAdderPlanAdaptation.adaptToAllow: evaluating precondition " + c.toSimpleString());
                System.out.println("Returns: " + (Float) c.evaluate(cycle, gs, player, planToAllow.getContext(cycle, gs, player)));
                System.out.println("player: " + player);
                System.out.println(gs);
            }
            if ((Float) c.evaluate(cycle, gs, player, planToAllow.getContext(cycle, gs, player)) < Sensor.BOOLEAN_TRUE_THRESHOLD) {
                unsatisfiedPreConditions.add(c);
            }
        }

        if (DEBUG >= 1) {
            System.out.println("ActionAdderPlanAdaptation.adaptToAllow: conditions not satisfied:");
            for (Sensor c : unsatisfiedPreConditions) {
                System.out.println(c);
            }
        }

        if (reasoningTraceWriter!=null) {
            reasoningTraceWriter.tagWithAttributes("ActionAdderPlanAdaptation.adaptToAllow","cycle = \"" + cycle + "\" , goal = \"" + original_plan + "\"");
        }

        for (Sensor c : unsatisfiedPreConditions) {
            ActionPlan ap = pb.retrieveAction(c, planToAllow, cycle, gs, player);

            if (ap != null) {
                if (DEBUG >= 1) {
                    System.out.println("ActionPlan selected: " + ap);
                }
                selectedActions.add(ap);
                
            } else {
                if (DEBUG >= 1) {
                    System.out.println("No ActionPlan was found...");
                }

                if (reasoningTraceWriter!=null) {
                    reasoningTraceWriter.tag("unadaptablePrecondition",c.toSimpleString());
                    reasoningTraceWriter.tag("/ActionAdderPlanAdaptation.adaptToAllow");
                }

                return;
            }
        }

        if (reasoningTraceWriter!=null) {
            reasoningTraceWriter.tag("/ActionAdderPlanAdaptation.adaptToAllow");
        }


        // Create a petrinet with the selected actions, and link it to the current plan:
        {
            State head = startState;
            State next = null;
            for (ActionPlan ap : selectedActions) {
                if (DEBUG >= 1) {
                    System.out.println("Creating plan block for " + ap);
                }
                next = new DummyState();
                pnp.addPetriNetElement(head);
                pnp.addPetriNetElement(next);
                PetriNetHelper.handlePlanBlock(pnp, head, ap, next, new True(), ap.getOriginalGameState(), player);
                head = next;
            }

            // Link the block of actions back to the original action, and add an additional preFailureTransition, but with no chance of adaptation:
            PreTransition pt = new PreTransition(originalPlanToAllow);
            pnp.addPetriNetElement(pt);

            if (next == null) {
                System.out.flush();
                System.err.flush();
                System.err.println("adaptToAllow found null 'head'!");
                System.err.println("This might be an indication that there is a missing precondition in the definition of the planToAllow.");
                System.err.print("selectedActions: ");
                for (ActionPlan ap : selectedActions) {
                    System.err.print(ap + " ");
                }
                System.err.println("");
                System.err.print("unsatisfiedPreConditions: ");
                for (Sensor c : unsatisfiedPreConditions) {
                    System.err.print(c + " ");
                }
                System.err.println("");
                System.err.println("planToAllow: " + planToAllow);
                System.err.flush();
                return;
            }

            next.addNextTransition(1, pt);
            pt.addNextState(1, endState);

            if (planToAllow instanceof ActionPlan) {
                if (!(((ActionPlan) planToAllow).getPreFailureCondition() instanceof False)) {
                    DummyState ds = new DummyState();
                    PreFailureTransition dt = new PreFailureTransition(planToAllow);
                    pnp.addPetriNetElement(ds);
                    pnp.addPetriNetElement(dt);
                    next.addNextTransition(1, dt);
                    dt.addNextState(1, ds);
//					System.out.println("DS: " + ds.getElementID() + " , DT: " + dt.getElementID());
                }
            }

        }

//		Planner.printPlanNiceComplete(pnp);

    }

    public Trace getTrace(String traceName) {

        Trace temp = TraceParser.parse(traceName, d2.core.Config.getDomain());

        return temp;
    }

    public void saveToXML(XMLWriter w) {
        w.tag("type", this.getClass().getName());
    }

    public static PlanAdaptation loadFromXMLInternal(Element xml) {
        return new ActionAdderPlanAdaptation();
    }
}
