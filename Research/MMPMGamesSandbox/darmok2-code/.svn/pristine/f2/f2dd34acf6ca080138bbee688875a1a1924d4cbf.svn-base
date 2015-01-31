/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.planner;

import d2.core.D2;
import java.util.HashMap;
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
import d2.worldmodel.WorldModel;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

public class SimpleExpandPlanner extends Planner {

    public int MAX_NUMBER_OF_ATTEMPTS = 2;
    HashMap<GoalPlan, Integer> m_numberOfAttempts = new HashMap<GoalPlan, Integer>();

    public SimpleExpandPlanner(Plan a_plan, D2 d2, String player, PlanExecution pe, PlanAdaptation pa) {
        super(a_plan, d2, player, pe, pa);
    }

    public SimpleExpandPlanner(List<Plan> a_plans, D2 d2, String player, PlanExecution pe, PlanAdaptation pa) {
        super(a_plans, d2, player, pe, pa);
    }

    public SimpleExpandPlanner() {
        super((Plan) null, null, null, null, null);
    }

    boolean expandPlan(Plan p, int cycle, GameState gs, String player) {

        if (DEBUG >= 2) {
            System.out.println("expandPlan: " + p.toString());
        }

        if (p instanceof GoalPlan) {
            GoalPlan gp = (GoalPlan) p;

            if (DEBUG >= 2) {
                System.out.println("expandPlan: found a GoalPlan!");
            }

            if (gp.getExpandedGoalPlan() == null) {

                if ((Float) gp.getGoal().evaluate(cycle, gs, player) >= Sensor.BOOLEAN_TRUE_THRESHOLD) {
                    if (DEBUG >= 2) {
                        System.out.println("expandPlan: goal already achieved.");
                    }
                } else {
                    Integer pastAttempts = m_numberOfAttempts.get(gp);
                    // We don't count number of attempts for the main plan:
                    if (gp != m_plans.get(0)) {
                        if (pastAttempts == null) {
                            m_numberOfAttempts.put(gp, 1);
                        } else {
                            if (pastAttempts >= MAX_NUMBER_OF_ATTEMPTS) {
                                // We have attempted a subgoal too many times! Abort...
                                if (DEBUG >= 1) {
                                    System.out.println("expandPlan: goal " + gp + " attempted too many times... giving up.");
                                }
                                m_numberOfAttempts.put(gp, pastAttempts + 1);
                                return false;
                            } else {
                                m_numberOfAttempts.put(gp, pastAttempts + 1);
                            }
                        }
                    }

                    if (reasoningTraceWriter!=null) {
                        reasoningTraceWriter.tagWithAttributes("SimpleExpandPlanner.expandPlan","cycle = \"" + cycle + "\" , goal = \"" + gp.getGoal().toSimpleString() + "\"");
                    }

                    List<Plan> p2_l = m_d2.getPlanBase().retrieveNPlans(gp.getGoal(), cycle, gs, player, 3);
                    if (p2_l.size() > 0) {
                        Plan p2 = p2_l.remove(0);
                        Plan adaptedp2 = m_planAdaptation.adapt(p2, p2.getOriginalGameState(), p2.getOriginalPlayer(), null, cycle, gs, player, gp.getGoal(), m_planExecution.getUnitsInUse());
                        if (DEBUG >= 1) {
                            System.out.println("expandPlan: Expanding " + gp.getGoal() + " with: " + adaptedp2.toString());
                        }
                        gp.setExpandedGoalPlan(adaptedp2);

                        if (p2_l.size() > 0) {
                            if (DEBUG >= 1) {
                                System.out.println("Adding " + p2_l.size() + " backtracking options.");
                            }
                            m_backtrackingOptions.put(gp, p2_l);
                        }

                        if (reasoningTraceWriter!=null) {
                            reasoningTraceWriter.tag("/SimpleExpandPlanner.expandPlan");
                        }

                        return true;
                    } else {
                        System.err.println("SimpleExpandPlanner: Couldn't retrieve any case!!");

                        if (reasoningTraceWriter!=null) {
                            reasoningTraceWriter.tag("/SimpleExpandPlanner.expandPlan");
                        }
                        
                        return false;
                    }
                }
            } else {
                if (DEBUG >= 2) {
                    System.out.println("expandPlan: already had a plan: " + gp.getExpandedGoalPlan());
                }

                //check to see if the plan was completely executed or not.
                //If it was, then the Goal hasn't been satisfied yet, so set the expandedPlan to null, and retrieve again
                if (gp.getExpandedGoalPlan().isPlanCompletelyExecuted()) {
                    if (DEBUG >= 1) {
                        System.out.println("expandPlan: Plan executed without goal being satisfied! " + gp.getGoal());
                    }
                    gp.setExpandedGoalPlan(null);

                    if (reasoningTraceWriter!=null) {
                        reasoningTraceWriter.tagWithAttributes("SimpleExpandPlanner.expandPlan","cycle = \"" + cycle + "\" , goal = \"" + gp.getGoal().toSimpleString() + "\"");
                        reasoningTraceWriter.tag("result","planFinishedWithoutGoalSatisfaction");
                        reasoningTraceWriter.tag("/SimpleExpandPlanner.expandPlan");
                    }

                    return true;
                } else {
                    return expandPlan(gp.getExpandedGoalPlan(), cycle, gs, player);
                }
            }
        } else if (p instanceof PetriNetPlan) {
            boolean anychange = false;
            for (PetriNetElement pe : ((PetriNetPlan) p).getPetriNetElements()) {
                if (pe instanceof State) {
                    if (((State) pe).getCurrentNumberOfTokens() > 0) {
                        // there are tokens in this state, thus, any pretransitions comming out of this states are ready to be executed:
                        if (DEBUG >= 2) {
                            System.out.println("State " + pe.getElementID() + " has tokens!");
                        }
                        if (pe instanceof PlanState) {
                            if (pe.getPlan() != null) {
                                if (expandPlan(pe.getPlan(), cycle, gs, player)) {
                                    anychange = true;
                                }
                                if (m_numberOfAttempts.get(pe.getPlan()) != null) {
                                    if (m_numberOfAttempts.get(pe.getPlan()) > MAX_NUMBER_OF_ATTEMPTS) {
                                        // A Goal has been attempted too many times, give up:
                                        if (DEBUG >= 1) {
                                            System.out.println("expandPlan: goal " + pe.getPlan() + " attempted too many times... giving up (continuation).");
                                        }

                                        // Remove the tokens from the state, that will make the plan fail:
                                        ((PlanState) pe).setCurrentNumberOfTokens(0);
                                    }
                                }
                            }
                        }
                        for (Pair<Integer, Transition> pe2 : ((State) pe).getNextTransitions()) {
                            if (pe2._b instanceof PreTransition) {
                                if (DEBUG >= 2) {
                                    System.out.println("PreTransition " + pe2._b.getElementID() + " is ready to be fired!");
                                }
                                Plan p2 = ((PreTransition) pe2._b).getPlan();
                                if (p2 != null) {
                                    if (expandPlan(p2, cycle, gs, player)) {
                                        anychange = true;
                                    }
                                }
                            } else {
                                if (DEBUG >= 2) {
                                    System.out.println("Transition " + pe2._b.getElementID() + " is not a PreTransition");
                                }
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
        w.tag("type", this.getClass().getName());
        w.tag("plan-execution");
        m_planExecution.saveToXML(w);
        w.tag("/plan-execution");
        w.tag("plan-adaptation");
        m_planAdaptation.saveToXML(w);
        w.tag("/plan-adaptation");
        w.tag("max-no-of-attemts", MAX_NUMBER_OF_ATTEMPTS);
        w.tag("/Planner");
    }

    public static Planner loadFromXMLInternal(Element xml, D2 d2, String player) {
        PlanExecution pe = null;
        PlanAdaptation pa = null;
        pe = PlanExecution.loadFromXML(xml.getChild("plan-execution"), player, d2);
        pa = PlanAdaptation.loadFromXML(xml.getChild("plan-adaptation"));


        SimpleExpandPlanner ret = new SimpleExpandPlanner(new LinkedList<Plan>(), d2, player, pe, pa);

        if (xml.getChildText("max-no-of-attemts") != null) {
            ret.MAX_NUMBER_OF_ATTEMPTS = Integer.parseInt(xml.getChildText("max-no-of-attemts"));
        }

        return ret;
    }
}
