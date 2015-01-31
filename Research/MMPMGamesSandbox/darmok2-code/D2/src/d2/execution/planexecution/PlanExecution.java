/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.planexecution;

import d2.core.D2;
import d2.core.D2Module;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.adaptation.PlanAdaptation;
import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlayerGameState;
import d2.execution.planner.Planner;
import d2.plans.ActionPlan;
import d2.plans.FailureTransition;
import d2.plans.GoalPlan;
import d2.plans.PetriNetElement;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanPreconditionFailedState;
import d2.plans.PlanState;
import d2.plans.PreFailureTransition;
import d2.plans.PreTransition;
import d2.plans.State;
import d2.plans.SuccessTransition;
import d2.plans.Transition;
import gatech.mmpm.Action;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

public abstract class PlanExecution extends D2Module {

    PlanAdaptation m_parameterAdapter = null;
    PlanAdaptation m_planAdapter = null;
    D2 m_d2 = null;
    String currentPlayer = null;
    public static int DEBUG = 0;
    public static int EXECUTION_DEBUG = 0;
    public static final int PLAN_WAITING = 0;
    public static final int PLAN_FINISHED = 1;
    public static final int PLAN_ADVANCED = 2;
    List<Pair<Plan, Plan>> plansThatCouldntStart = new LinkedList<Pair<Plan, Plan>>();
    
    // This list keeps track of all the units who are currently executing actions, which have yet 
    // to succeed or fail:
    List<String> unitsInUse = new LinkedList<String>();

    public void setD2(D2 d2) {
        m_d2 = d2;
        m_parameterAdapter.setD2(d2);
        m_planAdapter.setD2(d2);
    }

    public void setPlanAdaptation(PlanAdaptation pa) throws Exception {
        if (pa == null) throw new Exception("D2 being initialized with a null plan adaptation method!");
        m_planAdapter = pa;
    }

    public void setParameterAdaptation(PlanAdaptation ppa) {
        m_parameterAdapter = ppa;
    }

    public PlanAdaptation getPlanAdaptation() {
        return m_planAdapter;
    }

    public PlanAdaptation getParameterAdaptation() {
        return m_parameterAdapter;
    }

    public PlanExecution(String player, PlanAdaptation parameterAdapter, PlanAdaptation planAdapter, D2 d2) {
        currentPlayer = player;
        m_parameterAdapter = parameterAdapter;
        m_planAdapter = planAdapter;
        m_d2 = d2;
        if (m_parameterAdapter!=null) m_parameterAdapter.setD2(m_d2);
        if (m_planAdapter!=null) m_planAdapter.setD2(m_d2);
    }

    public List<String> getUnitsInUse() {
        return unitsInUse;
    }

    public void setPlayer(String player) {
        currentPlayer = player;
    }

    //returns true if plan is executed completely
    //else returns false
    public abstract boolean execute(Planner planner, List<Action> a_actionsToExecute, PlayerGameState pgs);

    public abstract void saveToXML(XMLWriter w);

    public int executePlan(Plan topLevelPlan, Plan a_plan, List<Plan> a_plansToExecute, List<Plan> a_plansToRemove, PlayerGameState pgs) {
        int retval = PLAN_FINISHED;
        if (EXECUTION_DEBUG >= 2) {
            System.out.println("executePlan: " + a_plan);
        }

        if (a_plan instanceof ActionPlan) {
            retval = executeAction((ActionPlan) a_plan, a_plansToExecute, a_plansToRemove, pgs);
        } else if (a_plan instanceof GoalPlan) {
            retval = executeGoal(topLevelPlan, (GoalPlan) a_plan, a_plansToExecute, a_plansToRemove, pgs);
        } else if (a_plan instanceof PetriNetPlan) {
            retval = executePetriNetUntilAction(topLevelPlan, (PetriNetPlan) a_plan, a_plansToExecute, pgs);
//            retval = executePetriNetFAST(topLevelPlan, (PetriNetPlan) a_plan, a_plansToExecute, a_plansToRemove, pgs);
        }

        if ((retval & PLAN_FINISHED) == PLAN_FINISHED) {
            a_plansToRemove.add(a_plan);
        }
        return retval;
    }

    public int executeAction(ActionPlan a_plan, List<Plan> a_plansToExecute, List<Plan> a_plansToRemove, PlayerGameState pgs) {
        if (EXECUTION_DEBUG >= 2) {
            System.out.println("executeAction");
        }

        a_plansToExecute.add(a_plan);
        return PLAN_FINISHED;
    }

    public int executeGoal(Plan topLevelPlan, GoalPlan a_plan, List<Plan> a_plansToExecute, List<Plan> a_plansToRemove, PlayerGameState pgs) {
        Plan expandedPlan = a_plan.getExpandedGoalPlan();

        if (EXECUTION_DEBUG >= 2) {
            System.out.println("executeGoal");
        }

        if (expandedPlan == null) {
            return 0;
        } else {
            int status = executePlan(topLevelPlan, expandedPlan, a_plansToExecute, a_plansToRemove, pgs);
            if ((status & PLAN_FINISHED) == PLAN_FINISHED) {
                expandedPlan = null;
            }

            int retval = 0;
            if ((status & PLAN_ADVANCED) == PLAN_ADVANCED) {
                retval |= PLAN_ADVANCED;
            }
            if (a_plan.checkSuccessCondition(pgs)) {
                retval |= PLAN_FINISHED;
            }
            return retval;
        }
    }

/*    
    public int executePetriNetFAST(Plan topLevelPlan, PetriNetPlan a_plan, List<Plan> a_plansToExecute, List<Plan> a_plansToRemove, PlayerGameState pgs) {
        int planOver = PLAN_FINISHED;

        List<Integer> markedStates = new LinkedList<Integer>();

        if (EXECUTION_DEBUG >= 1) System.out.println("executePetriNetFAST");

        for (int i = 0; i < a_plan.getPetriNetElements().size(); i++) {
            PetriNetElement p = a_plan.getPetriNetElement(i);

            if (p instanceof State) {
                State s = (State) p;
                for (Pair<Integer, Transition> t_p : s.getNextTransitions()) {
                    if (s.getCurrentNumberOfTokens() >= t_p._a && !markedStates.contains(i)) {
                        planOver = 0;
                        markedStates.add(i);
                    }
                }
            }
        }

//		if markedStates is empty, return the planOver
        if (markedStates.size() == 0) return PLAN_FINISHED;

//		Go thru the list again, and this time hit all the markedStates and move tokens till you get an action
//		or are stuck at a condition
        if (EXECUTION_DEBUG >= 2) 
            System.out.println("executePetriNetFAST: Found " + markedStates.size() + " marked states!");

        for (int i = 0; i < a_plan.getPetriNetElements().size(); i++) {
            if (markedStates.contains(i)) {
                State s = (State) a_plan.getPetriNetElement(i);

                // TOOD: [santi]: Jai, are you sure this works. How can you assume that the
                //				  executeStateChainTillAction method does not change the value of "markedStates"?
                //	call to function that executes the chain till an action
                if (executeStateChainTillAction(topLevelPlan, a_plan, s, a_plansToExecute, pgs)) {
                    planOver |= PLAN_ADVANCED;
                }
            }
        }

        return planOver;
    }

    private boolean executeStateChainTillAction(Plan topLevelPlan, PetriNetPlan a_plan, State s, List<Plan> a_plansToExecute, PlayerGameState pgs) {
        boolean tokensMoving = false;
        Plan adaptedPlan = null, originalPlan = null;

        if (EXECUTION_DEBUG >= 2) {
            System.out.println("\texecutePetriNetFAST: executeStateChainTillAction: in state " + s.getElementID() + " tokens: " + s.getCurrentNumberOfTokens());
        }

        for (Pair<Integer, Transition> t_p : s.getNextTransitions()) {
            if (s.getCurrentNumberOfTokens() >= t_p._a) {

                originalPlan = t_p._b.getPlan();
                adaptedPlan = null;
                if (t_p._b instanceof PreTransition) {
                    // If we are about to start a subplan, adapt it!
                    if (EXECUTION_DEBUG >= 1) {
                        System.out.println("About to Adapt: " + originalPlan);
                        System.out.println("Units in use are: " + unitsInUse);
                    }
                    adaptedPlan = m_parameterAdapter.adapt(originalPlan, originalPlan.getOriginalGameState(), originalPlan.getOriginalPlayer(), null, pgs.cycle, pgs.gs, pgs.player, null,unitsInUse);
                    if (adaptedPlan == null) {
                        if (EXECUTION_DEBUG >= 1) {
                            System.out.println("executeStateChainTillAction: stopped, since the plan cannot be adapted.");
                        }
                    } else {
                        if (EXECUTION_DEBUG >= 1) System.out.println("Action successfully adapted");
                    }
                }

                // If adaptedPlan != null, that means that the transition is a PRETRANSITION, and the Sensor is thus
                // the precondition of the plan
                if ((adaptedPlan != null && adaptedPlan.checkPreCondition(pgs))
                    || (adaptedPlan == null && t_p._b instanceof SuccessTransition && t_p._b.getPlan().checkSuccessCondition(pgs))
                    || (adaptedPlan == null && t_p._b instanceof FailureTransition && t_p._b.getPlan().checkFailureCondition(pgs))
                    || (adaptedPlan == null && t_p._b instanceof PreFailureTransition && t_p._b.getPlan().checkPreFailureCondition(pgs))
                    || (adaptedPlan == null && t_p._b.getPlan() == null && (Float) t_p._b.getCondition().evaluate(pgs.cycle, pgs.gs, pgs.player, a_plan.getContext(pgs.cycle, pgs.gs, pgs.player)) >= Sensor.BOOLEAN_TRUE_THRESHOLD)) {

                    if (adaptedPlan != null) {
                        // Replace this plan in the planstate, pretransition, successtransition, and failuretransition
                        a_plan.replacePlan(t_p._b.getPlan(), adaptedPlan);
                        if (EXECUTION_DEBUG >= 1) {
                            System.out.println("originalPlan: " + originalPlan);
                            System.out.println("adaptedPlan: " + adaptedPlan);
                        }

                        if (adaptedPlan instanceof ActionPlan) {
                            unitsInUse.add(((ActionPlan)adaptedPlan).getAction().getEntityID());
                        }
                    }

                    if (t_p._b instanceof SuccessTransition ||
                        t_p._b instanceof FailureTransition) {
                        Plan p = t_p._b.getPlan();
                        if (p!=null && p instanceof ActionPlan) {
                            unitsInUse.remove(((ActionPlan)p).getAction().getEntityID());
                        }
                    }

//                  If it's a precondition transition, execute the plan:
//                  and its an action (or subgoalplan, or petrinetplan...whatever), so well...here goes the action...
                    System.out.println(pgs.cycle + " - " + t_p._b.getClass().getSimpleName() + " fired for plan " + t_p._b.getPlan());
                    if (EXECUTION_DEBUG >= 1) {
                        if (t_p._b instanceof PreFailureTransition
                            || t_p._b instanceof PreTransition
                            || t_p._b instanceof SuccessTransition
                            || t_p._b instanceof FailureTransition) {
                            System.out.println(t_p._b.getClass().getSimpleName() + " fired for plan " + t_p._b.getPlan());
                            System.out.println("With context: " + t_p._b.getPlan().getContext(pgs.cycle, pgs.gs, pgs.player));
                            if (t_p._b.getPlan() instanceof ActionPlan) {
                                System.out.println("Unit: " + pgs.gs.getEntity(((ActionPlan)t_p._b.getPlan()).getAction().getEntityID()));
                            }
                            System.out.println(t_p._b.getCondition());

                            // clear the potential fields for the action just executed:
                            if (t_p._b instanceof SuccessTransition
                                    || t_p._b instanceof FailureTransition) {
                                Plan p = t_p._b.getPlan();
                                GameState gs = p.getOriginalGameState();
                                gs.clearMetadata();
                            }

                            if (EXECUTION_DEBUG >= 2) {
                                System.out.println(" /- cycle " + pgs.cycle + " --------------------\\ ");
                                Planner.printPlanNice(topLevelPlan);
                                System.out.println(" \\-------------------------/ ");
                            }

                        }
                    }
                    
                    if (t_p._b instanceof PreTransition) {
                        if (EXECUTION_DEBUG >= 2) {
                            System.out.println("\texecutePetriNetFAST:Fired Transition" + t_p._b.getElementID() + " removed tokens from state " + s.getElementID() + " -> " + s.getCurrentNumberOfTokens() + " (condition: " + t_p._b.getCondition() + ", plan: " + t_p._b.getPlan() + ")");
                        }

                        if (reasoningTraceWriter!=null) {
                            reasoningTraceWriter.tagWithAttributes("PlanExecution.executePlan","cycle = \"" + pgs.cycle + "\"");
                            reasoningTraceWriter.tag("transitionFired",t_p._b.getClass().getSimpleName());
                            reasoningTraceWriter.tag("originalPlan");
                            originalPlan.writeToXML("-", reasoningTraceWriter);
                            reasoningTraceWriter.tag("/originalPlan");
                            reasoningTraceWriter.tag("adaptedPlan");
                            adaptedPlan.writeToXML("-", reasoningTraceWriter);
                            reasoningTraceWriter.tag("/adaptedPlan");
                            reasoningTraceWriter.tag("/PlanExecution.executePlan");
                            reasoningTraceWriter.flush();
                        }


                        if (t_p._b.getPlan() != null) {
                            a_plansToExecute.add(t_p._b.getPlan());
                        } else {
                            System.err.println("Plan null in petri net element! -> " + t_p._b.getElementID());
                        }
                        tokensMoving = true;
                        s.setCurrentNumberOfTokens(s.getCurrentNumberOfTokens() - t_p._a);
                        for (Pair<Integer, State> s_p : t_p._b.getNextStates()) {
                            s_p._b.setCurrentNumberOfTokens(s_p._b.getCurrentNumberOfTokens() + s_p._a);
                            if (EXECUTION_DEBUG >= 2) {
                                System.out.println("\texecutePetriNetFAST:Added " + t_p._a + " token to state " + s_p._b.getElementID());
                            }
                        }
                        return tokensMoving;
                    } else {
                        if (EXECUTION_DEBUG >= 2) {
                            System.out.println("\texecutePetriNetFAST:Fired Transition" + t_p._b.getElementID() + " removed tokens from state " + s.getElementID() + " -> " + s.getCurrentNumberOfTokens() + " (condition: " + t_p._b.getCondition() + ", plan: " + t_p._b.getPlan() + ")");
                        }
                        
                        if (reasoningTraceWriter!=null) {
                            reasoningTraceWriter.tagWithAttributes("PlanExecution.executePlan","cycle = \"" + pgs.cycle + "\"");
                            reasoningTraceWriter.tag("transitionFired",t_p._b.getClass().getSimpleName());
                            reasoningTraceWriter.tag("/PlanExecution.executePlan");
                        }

                        // Remove the tokens from the preceding state:
                        tokensMoving = true;
                        s.setCurrentNumberOfTokens(s.getCurrentNumberOfTokens() - t_p._a);
                        for (Pair<Integer, State> s_p : t_p._b.getNextStates()) {
                            s_p._b.setCurrentNumberOfTokens(s_p._b.getCurrentNumberOfTokens() + s_p._a);
                            executeStateChainTillAction(topLevelPlan, a_plan, s_p._b, a_plansToExecute, pgs);
                            if (s_p._b instanceof PlanPreconditionFailedState && 
                                s_p._b.getNextTransitions().size() == 0) {
                                // The preconditions are not satisfied, and the action has not been adapted before:
                                plansThatCouldntStart.add(new Pair<Plan, Plan>(s_p._b.getPlan(), a_plan));
                            }
                        }
                    }
                } else {
                    if (EXECUTION_DEBUG >= 2) {
                        if (adaptedPlan == null) {
                            System.out.println("AdaptedPlan = null");
                            System.out.println("OriginalPlan = " + originalPlan);
                            if (t_p._b instanceof SuccessTransition) {
                                Sensor sensor = t_p._b.getCondition();
                                System.out.println("executePetriNetFAST: waiting for SuccessCondition " + sensor + " [cycle: " + pgs.cycle + "]");
                                System.out.println("Which evaluates to: " + t_p._b.getPlan().checkSuccessCondition(pgs));
                            } else if (t_p._b instanceof FailureTransition) {
                                Sensor sensor = t_p._b.getCondition();
                                System.out.println("executePetriNetFAST: waiting for FailureCondition " + sensor + " [cycle: " + pgs.cycle + "]");
                                System.out.println("Which evaluates to: " + t_p._b.getPlan().checkFailureCondition(pgs));
                            } else {
                                Sensor sensor = t_p._b.getCondition();
                                System.out.println("executePetriNetFAST: waiting for Sensor " + sensor + " [cycle: " + pgs.cycle + "]");
                                System.out.println("Which evaluates to: " + sensor.evaluate(pgs.cycle, pgs.gs, pgs.player));
                            }
                        } else {
                            Sensor sensor = adaptedPlan.getPreCondition();
                            System.out.println("executePetriNetFAST: waiting for PreCondition " + sensor + ", plan: " + adaptedPlan + " [cycle: " + pgs.cycle + "]");
                            System.out.println("Which evaluates to: " + adaptedPlan.checkPreCondition(pgs));
                        }
                    }
                }
            }
        }
        return tokensMoving;
    }
*/    
    
    public int executePetriNetUntilAction(Plan topLevelPlan, PetriNetPlan a_plan, List<Plan> a_plansToExecute, PlayerGameState pgs) {
        int retVal = PLAN_WAITING;

        if (EXECUTION_DEBUG >= 1) System.out.println("PlanExecution.executePetriNetUntilAction");

        int tmp;
        do{
            tmp = executePetriNetStep(topLevelPlan, a_plan, a_plansToExecute, pgs);
            if (tmp == PLAN_FINISHED) return PLAN_FINISHED;
            if (tmp == PLAN_ADVANCED) retVal = PLAN_ADVANCED;
        }while(tmp == PLAN_ADVANCED);
        
        return retVal;
    }
    
    
    public int executePetriNetStep(Plan topLevelPlan, PetriNetPlan a_plan, List<Plan> a_plansToExecute, PlayerGameState pgs) {
        int retVal = PLAN_FINISHED;
        
        if (EXECUTION_DEBUG >= 1) System.out.println("PlanExecution.executePetriNetStep");
        
        for (int i = 0; i < a_plan.getPetriNetElements().size(); i++) {
            PetriNetElement p = a_plan.getPetriNetElement(i);

            if (p instanceof Transition) {
                Transition t = (Transition)p;
                boolean readyToFire = true;
                for(Pair<Integer,State> ps:t.getPreviousStates()) {
                    if (ps._b.getCurrentNumberOfTokens()<ps._a) {
                        readyToFire = false;
                        break;
                    }
                }
                if (readyToFire) {
                    if (executePetriNetTransition(t, topLevelPlan, a_plan, a_plansToExecute, pgs)) {
                        return PLAN_ADVANCED;                
                    } else {
                        retVal = PLAN_WAITING;
                    }
                }
            }
        }
        
        return retVal;
    }
    
    
    public boolean executePetriNetTransition(Transition t, Plan topLevelPlan, PetriNetPlan a_plan, List<Plan> a_plansToExecute, PlayerGameState pgs) {
        boolean tokensMoving = false;
        Plan adaptedPlan = null, originalPlan = null;

        originalPlan = t.getPlan();
        adaptedPlan = null;
        if (t instanceof PreTransition) {
            // If we are about to start a subplan, adapt it!
            if (EXECUTION_DEBUG >= 1) {
                System.out.println("About to Adapt: " + originalPlan);
                System.out.println("Units in use are: " + unitsInUse);
            }
            adaptedPlan = m_parameterAdapter.adapt(originalPlan, originalPlan.getOriginalGameState(), originalPlan.getOriginalPlayer(), null, pgs.cycle, pgs.gs, pgs.player, null,unitsInUse);
            if (adaptedPlan == null) {
                if (EXECUTION_DEBUG >= 1) {
                    System.out.println("executeStateChainTillAction: stopped, since the plan cannot be adapted.");
                }
            } else {
                if (EXECUTION_DEBUG >= 1) System.out.println("Action successfully adapted");
            }
        }

        // If adaptedPlan != null, that means that the transition is a PRETRANSITION, and the Sensor is thus
        // the precondition of the plan
        if ((adaptedPlan != null && adaptedPlan.checkPreCondition(pgs))
            || (adaptedPlan == null && t instanceof SuccessTransition && t.getPlan().checkSuccessCondition(pgs))
            || (adaptedPlan == null && t instanceof FailureTransition && t.getPlan().checkFailureCondition(pgs))
            || (adaptedPlan == null && t instanceof PreFailureTransition && t.getPlan().checkPreFailureCondition(pgs))
            || (adaptedPlan == null && t.getPlan() == null && (Float) t.getCondition().evaluate(pgs.cycle, pgs.gs, pgs.player, a_plan.getContext(pgs.cycle, pgs.gs, pgs.player)) >= Sensor.BOOLEAN_TRUE_THRESHOLD)) {

            if (adaptedPlan != null) {
                // Replace this plan in the planstate, pretransition, successtransition, and failuretransition
                a_plan.replacePlan(t.getPlan(), adaptedPlan);
                if (EXECUTION_DEBUG >= 1) {
                    System.out.println("originalPlan: " + originalPlan);
                    System.out.println("adaptedPlan: " + adaptedPlan);
                }

                if (adaptedPlan instanceof ActionPlan) {
                    unitsInUse.add(((ActionPlan)adaptedPlan).getAction().getEntityID());
                }
            }

            if (t instanceof SuccessTransition ||
                t instanceof FailureTransition) {
                Plan p = t.getPlan();
                if (p!=null && p instanceof ActionPlan) {
                    unitsInUse.remove(((ActionPlan)p).getAction().getEntityID());
                }
            }

//                  If it's a precondition transition, execute the plan:
//                  and its an action (or subgoalplan, or petrinetplan...whatever), so well...here goes the action...
            System.out.println(pgs.cycle + " - " + t.getClass().getSimpleName() + " fired for plan " + t.getPlan());
            if (EXECUTION_DEBUG >= 1) {
                if (t instanceof PreFailureTransition
                    || t instanceof PreTransition
                    || t instanceof SuccessTransition
                    || t instanceof FailureTransition) {
                    System.out.println(t.getClass().getSimpleName() + " fired for plan " + t.getPlan());
                    System.out.println("With context: " + t.getPlan().getContext(pgs.cycle, pgs.gs, pgs.player));
                    if (t.getPlan() instanceof ActionPlan) {
                        System.out.println("Unit: " + pgs.gs.getEntity(((ActionPlan)t.getPlan()).getAction().getEntityID()));
                    }
                    System.out.println(t.getCondition());

                    // clear the potential fields for the action just executed:
                    if (t instanceof SuccessTransition
                            || t instanceof FailureTransition) {
                        Plan p = t.getPlan();
                        GameState gs = p.getOriginalGameState();
                        gs.clearMetadata();
                    }

                    if (EXECUTION_DEBUG >= 2) {
                        System.out.println(" /- cycle " + pgs.cycle + " --------------------\\ ");
                        Planner.printPlanNice(topLevelPlan);
                        System.out.println(" \\-------------------------/ ");
                    }

                }
            }

            if (t instanceof PreTransition) {
                if (reasoningTraceWriter!=null) {
                    reasoningTraceWriter.tagWithAttributes("PlanExecution.executePlan","cycle = \"" + pgs.cycle + "\"");
                    reasoningTraceWriter.tag("transitionFired",t.getClass().getSimpleName());
                    reasoningTraceWriter.tag("originalPlan");
                    originalPlan.writeToXML("-", reasoningTraceWriter);
                    reasoningTraceWriter.tag("/originalPlan");
                    reasoningTraceWriter.tag("adaptedPlan");
                    adaptedPlan.writeToXML("-", reasoningTraceWriter);
                    reasoningTraceWriter.tag("/adaptedPlan");
                    reasoningTraceWriter.tag("/PlanExecution.executePlan");
                    reasoningTraceWriter.flush();
                }

                if (t.getPlan() != null) {
                    a_plansToExecute.add(t.getPlan());
                } else {
                    System.err.println("Plan null in petri net element! -> " + t.getElementID());
                }
            } else {
                if (EXECUTION_DEBUG >= 2) {
                    System.out.println("\texecutePetriNetFAST:Fired Transition" + t.getElementID() + " (condition: " + t.getCondition() + ", plan: " + t.getPlan() + ")");
                }

                if (reasoningTraceWriter!=null) {
                    reasoningTraceWriter.tagWithAttributes("PlanExecution.executePlan","cycle = \"" + pgs.cycle + "\"");
                    reasoningTraceWriter.tag("transitionFired",t.getClass().getSimpleName());
                    reasoningTraceWriter.tag("/PlanExecution.executePlan");
                }
            }
            
            // Remove the tokens from the preceding state:
            tokensMoving = true;
            for(Pair<Integer,State> ps : t.getPreviousStates()) {
                ps._b.setCurrentNumberOfTokens(ps._b.getCurrentNumberOfTokens() - ps._a);
//                System.out.println("Taking " + ps._a + " tokens... now: " + ps._b.getCurrentNumberOfTokens());
            }
            for (Pair<Integer, State> s_p : t.getNextStates()) {
                s_p._b.setCurrentNumberOfTokens(s_p._b.getCurrentNumberOfTokens() + s_p._a);
//                System.out.println("Adding " + s_p._a + " tokens... now: " + s_p._b.getCurrentNumberOfTokens());
                if (s_p._b instanceof PlanPreconditionFailedState && 
                    s_p._b.getNextTransitions().size() == 0) {
                    // The preconditions are not satisfied, and the action has not been adapted before:
                    plansThatCouldntStart.add(new Pair<Plan, Plan>(s_p._b.getPlan(), a_plan));
                }
            }            
            return tokensMoving;

        } else {
            if (EXECUTION_DEBUG >= 2) {
                if (adaptedPlan == null) {
                    System.out.println("AdaptedPlan = null");
                    System.out.println("OriginalPlan = " + originalPlan);
                    if (t instanceof SuccessTransition) {
                        Sensor sensor = t.getCondition();
                        System.out.println("executePetriNetFAST: waiting for SuccessCondition " + sensor + " [cycle: " + pgs.cycle + "]");
                        System.out.println("Which evaluates to: " + t.getPlan().checkSuccessCondition(pgs));
                    } else if (t instanceof FailureTransition) {
                        Sensor sensor = t.getCondition();
                        System.out.println("executePetriNetFAST: waiting for FailureCondition " + sensor + " [cycle: " + pgs.cycle + "]");
                        System.out.println("Which evaluates to: " + t.getPlan().checkFailureCondition(pgs));
                    } else {
                        Sensor sensor = t.getCondition();
                        System.out.println("executePetriNetFAST: waiting for Sensor " + sensor + " [cycle: " + pgs.cycle + "]");
                        System.out.println("Which evaluates to: " + sensor.evaluate(pgs.cycle, pgs.gs, pgs.player));
                    }
                } else {
                    Sensor sensor = adaptedPlan.getPreCondition();
                    System.out.println("executePetriNetFAST: waiting for PreCondition " + sensor + ", plan: " + adaptedPlan + " [cycle: " + pgs.cycle + "]");
                    System.out.println("Which evaluates to: " + adaptedPlan.checkPreCondition(pgs));
                }
            }
        }        
        
        return false;
    }    
    
    
    public void removeStillUsedUnitIDs(Plan p) {
        if (p instanceof ActionPlan) {
            unitsInUse.remove(((ActionPlan)p).getAction().getEntityID());
        } else if (p instanceof GoalPlan) {
            if (((GoalPlan)p).getExpandedGoalPlan()!=null) {
                removeStillUsedUnitIDs(((GoalPlan)p).getExpandedGoalPlan());
            }
        } else if (p instanceof PetriNetPlan) {
            for(PlanState s:((PetriNetPlan)p).getAllPlanStates()) {
                if (s.getCurrentNumberOfTokens()>0) {
                    removeStillUsedUnitIDs(s.getPlan());
                }
            }
        }
    }

    public static PlanExecution loadFromXML(Element xml, String a_player, D2 d2) {
        Class<?> c;
        try {

            c = Class.forName(xml.getChildText("type"));
            Method m = c.getMethod("loadFromXMLInternal", new Class[]{Element.class, String.class, D2.class});
            return (PlanExecution) m.invoke(c, xml, a_player, d2);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PlanExecution loadFromXMLInternal(Element xml, String a_player, D2 d2) {
        return null;
    }
}
