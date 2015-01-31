/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.planner;

import d2.core.D2;
import d2.core.D2Module;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.adaptation.PlanAdaptation;
import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
import d2.execution.planexecution.PlanExecution;
import d2.plans.ActionPlan;
import d2.plans.GoalPlan;
import d2.plans.PetriNetElement;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanState;
import d2.plans.State;
import d2.plans.Transition;
import d2.worldmodel.WorldModel;
import gatech.mmpm.Action;
import gatech.mmpm.GameState;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

public abstract class Planner extends D2Module {

    D2 m_d2;
    List<Plan> m_plans;	// List of all current plans being executed
    // It is a list, since while executing a PetriNet, other plans might be spawned.
    // The assumption that the first one in the list is the root plan should be valid.
    public static int DEBUG = 0;
    PlanExecution m_planExecution = null;
    PlanAdaptation m_planAdaptation = null;
    String currentPlayer = null;
    // This table contains available options for backtracking. The planners are responsible for filling them, with
    // as many options as wanted. If no entry in this table is created, then no backtracking can be performed, and
    // as soon as there is a problem, Darmok will fail.
    HashMap<GoalPlan, List<Plan>> m_backtrackingOptions = new HashMap<GoalPlan, List<Plan>>();
    HashMap<GoalPlan, PlanBaseEntry> m_currentUsedCases = new HashMap<GoalPlan, PlanBaseEntry>();

    public void setD2(D2 d2) {
        m_d2 = d2;
        m_planExecution.setD2(d2);
    }

    public void setPlanExecution(PlanExecution pe) {
        m_planExecution = pe;
    }

    public void setPlanAdaptation(PlanAdaptation pa) {
        m_planAdaptation = pa;
    }

    public Planner(String player, PlanExecution pe, PlanAdaptation planAdaptation, D2 d2) {
        currentPlayer = player;
        m_planExecution = pe;
        m_planAdaptation = planAdaptation;
        m_d2 = d2;
        m_planAdaptation.setD2(m_d2);
    }

    public Planner(Plan a_plan, D2 d2, String player, PlanExecution pe, PlanAdaptation planAdaptation) {
        m_plans = new LinkedList<Plan>();
        m_plans.add(a_plan);
        m_d2 = d2;

        currentPlayer = player;
        m_planExecution = pe;
        m_planAdaptation = planAdaptation;
        if (m_planAdaptation!=null) m_planAdaptation.setD2(m_d2);
    }

    public Planner(List<Plan> a_plans, D2 d2, String player, PlanExecution pe, PlanAdaptation planAdaptation) {
        m_plans = new LinkedList<Plan>();
        m_plans.addAll(a_plans);
        m_d2 = d2;

        currentPlayer = player;
        m_planExecution = pe;
        m_planAdaptation = planAdaptation;
        m_planAdaptation.setD2(m_d2);
    }

    public boolean plan(int cycle, GameState gs, String player) {
        if (m_plans.size() > 0) {
            // Only the first plan has to be expanded (the rest are subplans of it)
            if (DEBUG >= 2) {
                System.out.println("plan: started!!!!");
            }
//            printPlanNiceTokens(m_plans.get(0),0);
            if (expandPlan(m_plans.get(0), cycle, gs, player)) {
                if (DEBUG >= 1) {
                    printPlanNiceComplete(m_plans.get(0));
                }
                return true;
            }
        } else {
            System.err.println("D2 has no plans in its active plan list...");
        }
        return false;
    }

    /* This method should return true if the plan was expanded, and false if no modification was done */
    abstract boolean expandPlan(Plan p, int cycle, GameState gs, String player);

    public void setPlayer(String player) {
        currentPlayer = player;
        m_planExecution.setPlayer(player);
    }

    public void addPlan(Plan plan) {
        m_plans.add(plan);
    }

    //returns true if plan is executed completely
    //else returns false
    public boolean execute(List<Action> a_actionsToExecute, int cycle, GameState a_gs, String player) {
        return m_planExecution.execute(this, a_actionsToExecute, new PlayerGameState(a_gs, cycle, player));
    }

    public List<Plan> getPlans() {
        return m_plans;
    }

    public PlanExecution getPlanExecution() {
        return m_planExecution;
    }

    public PlanAdaptation getPlanAdaptation() {
        return m_planAdaptation;
    }

    public abstract void saveToXML(XMLWriter w);

    public static Planner loadFromXML(Element xml, D2 d2, String a_player) {
        Class<?> c;
        try {

            c = Class.forName(xml.getChildText("type"));
            Method m = c.getMethod("loadFromXMLInternal", new Class[]{Element.class, D2.class, String.class});
            return (Planner) m.invoke(c, xml, d2, a_player);
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

    public static Planner loadFromXMLInternal(Element xml, PlanBase a_pb, WorldModel a_w_model, String a_player) {
        return null;
    }

    public static void printPlanNice(Plan p) {
        printPlanNice(p, 0);
    }

    public static void printPlanNice(Plan p, int tabs) {
        if (p instanceof ActionPlan) {
            for (int i = 0; i < tabs; i++) {
                System.out.print("  ");
            }
            System.out.println(p);
        } else if (p instanceof GoalPlan) {
            for (int i = 0; i < tabs; i++) {
                System.out.print("  ");
            }
            System.out.println(p);
            if (((GoalPlan) p).getExpandedGoalPlan() != null) {
                printPlanNice(((GoalPlan) p).getExpandedGoalPlan(), tabs + 1);
            }
        } else if (p instanceof PetriNetPlan) {
            PetriNetPlan pnp = (PetriNetPlan) p;

            for (PetriNetElement pne : pnp.getAllElements()) {
                if (pne instanceof PlanState) {
                    for (int i = 0; i < tabs; i++) {
                        System.out.print("  ");
                    }
                    System.out.print("(" + pne.getElementID() + ") Tokens: " + ((PlanState) pne).getCurrentNumberOfTokens() + " ->");
                    for (Pair<Integer, Transition> pne2 : ((PlanState) pne).getNextTransitions()) {
                        for (Pair<Integer, State> pne3 : pne2._b.getNextStates()) {
                            System.out.print(" " + pne3._b.getElementID());


//							Plan p2 = m_pb.retrievePlan(gp.getGoal(), cycle, gs, player);
//							if (DEBUG>=2) System.out.println("expandPlan: Expanding " + gp.getGoal() + " with: " + p2.toString());
//							gp.setExpandedGoalPlan(p2);

                        }
                    }
                    System.out.println();
                    printPlanNice(pne.getPlan(), tabs + 1);
                } else if (pne instanceof State) {
                    if (((State) pne).getCurrentNumberOfTokens() != 0) {
                        for (int i = 0; i < tabs; i++) {
                            System.out.print("  ");
                        }
                        System.out.print("(" + pne.getElementID() + ") Tokens: " + ((State) pne).getCurrentNumberOfTokens() + " ->");
                        for (Pair<Integer, Transition> pne2 : ((State) pne).getNextTransitions()) {
                            for (Pair<Integer, State> pne3 : pne2._b.getNextStates()) {
                                System.out.print(" " + pne3._b.getElementID());
                            }
                        }
                        System.out.println();
                    }
                }
            }
        }
    }

    public static void printPlanNiceTokens(Plan p, int tabs) {
        System.out.println("/-----------------------------------\\");
        if (p instanceof ActionPlan) {
            for (int i = 0; i < tabs; i++) {
                System.out.print("  ");
            }
            System.out.println(p);
        } else if (p instanceof GoalPlan) {
            for (int i = 0; i < tabs; i++) {
                System.out.print("  ");
            }
            System.out.println(p);
            if (((GoalPlan) p).getExpandedGoalPlan() != null) {
                printPlanNiceTokens(((GoalPlan) p).getExpandedGoalPlan(), tabs + 1);
            }
        } else if (p instanceof PetriNetPlan) {
            PetriNetPlan pnp = (PetriNetPlan) p;
            List<PetriNetElement> toPrint = new LinkedList<PetriNetElement>();
            for (PetriNetElement pne : pnp.getAllElements()) {
                if (pne instanceof State) {
                    if (((State) pne).getCurrentNumberOfTokens() > 0) {
                        toPrint.add(pne);
                    }
                }
            }
            for (PetriNetElement pne : toPrint) {
                boolean insertNewLine = true;
                for (int i = 0; i < tabs; i++) {
                    System.out.print("  ");
                }
                System.out.print(pne.getClass().getSimpleName() + " " + pne.getElementID() + " -> ");
                if (pne instanceof State) {
                    System.out.print(((State) pne).getCurrentNumberOfTokens() + " -> ");
                    for (Pair<Integer, Transition> pne2 : ((State) pne).getNextTransitions()) {
                        System.out.print(pne2._b.getElementID() + " ");
                    }
                    if (pne.getPlan() != null) {
                        insertNewLine = false;
                        System.out.println("");
                        printPlanNiceComplete(pne.getPlan(), tabs + 1);
                    }
                }
                if (pne instanceof Transition) {
                    for (Pair<Integer, State> pne2 : ((Transition) pne).getNextStates()) {
                        System.out.print(pne2._b.getElementID() + " ");
                    }
                }
                if (insertNewLine) {
                    System.out.println("");
                }
            }
        }
        System.out.println("\\-----------------------------------/");
    }

    public static void printPlanNiceComplete(Plan p) {
        System.out.println("/-----------------------------------\\");
        printPlanNiceComplete(p, 0);
        System.out.println("\\-----------------------------------/");
    }

    public static void printPlanNiceComplete(Plan p, int tabs) {
        if (p instanceof ActionPlan) {
            for (int i = 0; i < tabs; i++) {
                System.out.print("  ");
            }
            System.out.println(p);
        } else if (p instanceof GoalPlan) {
            for (int i = 0; i < tabs; i++) {
                System.out.print("  ");
            }
            System.out.println(p);
            if (((GoalPlan) p).getExpandedGoalPlan() != null) {
                printPlanNiceComplete(((GoalPlan) p).getExpandedGoalPlan(), tabs + 1);
            }
        } else if (p instanceof PetriNetPlan) {
            PetriNetPlan pnp = (PetriNetPlan) p;
            List<PetriNetElement> toPrint = new LinkedList<PetriNetElement>();

            for (PetriNetElement pne : pnp.getAllElements()) {
                if (pne instanceof State) {
                    if (((State) pne).getCurrentNumberOfTokens() > 0) {
                        toPrint.add(pne);
                    }
                }
            }
            boolean change = true;
            while (change) {
                List<PetriNetElement> toPrintNew = new LinkedList<PetriNetElement>();
                change = false;
                for (PetriNetElement pne : toPrint) {
                    if (pne instanceof State) {
                        for (Pair<Integer, Transition> pne2 : ((State) pne).getNextTransitions()) {
                            if (!toPrint.contains(pne2._b) && !toPrintNew.contains(pne2._b)) {
                                change = true;
                                toPrintNew.add(pne2._b);
                            }
                        }
                    }
                    if (pne instanceof Transition) {
                        for (Pair<Integer, State> pne2 : ((Transition) pne).getNextStates()) {
                            if (!toPrint.contains(pne2._b) && !toPrintNew.contains(pne2._b)) {
                                change = true;
                                toPrintNew.add(pne2._b);
                            }
                        }
                    }
                }
                toPrint.addAll(toPrintNew);
            }

            for (PetriNetElement pne : toPrint) {
                boolean insertNewLine = true;
                for (int i = 0; i < tabs; i++) {
                    System.out.print("  ");
                }
                System.out.print(pne.getClass().getSimpleName() + " " + pne.getElementID() + " -> ");
                if (pne instanceof State) {
                    System.out.print(((State) pne).getCurrentNumberOfTokens() + " -> ");
                    for (Pair<Integer, Transition> pne2 : ((State) pne).getNextTransitions()) {
                        System.out.print(pne2._b.getElementID() + " ");
                    }
                    if (pne.getPlan() != null) {
                        insertNewLine = false;
                        System.out.println("");
                        printPlanNiceComplete(pne.getPlan(), tabs + 1);
                    }
                }
                if (pne instanceof Transition) {
                    for (Pair<Integer, State> pne2 : ((Transition) pne).getNextStates()) {
                        System.out.print(pne2._b.getElementID() + " ");
                    }
                }
                if (insertNewLine) {
                    System.out.println("");
                }
            }
        }
    }


    /*
     * This method backtracks one of the plans in the plan
     */
    public boolean backtrack() {

        Plan p = m_plans.get(0);

        return backtrack(p);
    }

    /*
     * Looks for a plan in a GoalPlan that is currently blocked and backtracks it
     * Returns true when it has been able to successfully backtrack a plan.
     */
    boolean backtrack(Plan p) {
        System.out.println("Planner.backtrack: " + p);
        if (p instanceof ActionPlan) {
            return false;
        } else if (p instanceof GoalPlan) {
            Plan p2 = ((GoalPlan) p).getExpandedGoalPlan();

            if (p2 != null) {
                if (!backtrack(p2)) {
                    // BACKTRACK A NODE!
                    ((GoalPlan) p).setExpandedGoalPlan(null);
                    removePlanTree(p2);

                    // Get backtracking options:
                    {
                        List<Plan> alternatives = m_backtrackingOptions.get(p);
                        if (alternatives != null) {
                            System.out.println("backtrack: alternatives " + alternatives.size());
                        }

                        if (alternatives != null && alternatives.size() > 0) {
                            Plan next = alternatives.remove(0);
                            if (DEBUG >= 2) {
                                System.out.println("backtrack: Expanding " + ((GoalPlan) p).getGoal() + " with: " + next.toString());
                            }
                            ((GoalPlan) p).setExpandedGoalPlan(next);
                            return true;
                        }
                    }
                }
                return false;
            } else {
                return false;
            }
        } else if (p instanceof PetriNetPlan) {
            boolean backtracked = false;
            for (PetriNetElement e : ((PetriNetPlan) p).getAllElements()) {
                if (e instanceof PlanState) {
                    Plan p2 = ((PlanState) e).getPlan();
                    if (p2 != null && ((PlanState) e).getCurrentNumberOfTokens() > 0) {
                        if (backtrack(p2)) {
                            backtracked = true;
                            break;
                        }
                    }
                }
            }
            return backtracked;
        }

        return false;
    }

    public void removePlanTree(Plan p) {
        m_plans.remove(p);

        m_backtrackingOptions.remove(p);

        if (p instanceof ActionPlan) {
        } else if (p instanceof GoalPlan) {
            Plan p2 = ((GoalPlan) p).getExpandedGoalPlan();
            if (p2 != null) {
                removePlanTree(p2);
            }
            m_currentUsedCases.remove(p);
        } else if (p instanceof PetriNetPlan) {
            for (PetriNetElement e : ((PetriNetPlan) p).getAllElements()) {
                if (e instanceof PlanState) {
                    Plan p2 = ((PlanState) e).getPlan();
                    if (p2 != null) {
                        removePlanTree(p2);
                    }
                }
            }
        }
    }

    public ActionPlan findValidAction(int cycle, GameState a_gs, PlanAdaptation parameterAdapter, List<String> unitsInUse) {
        List<ActionPlan> l = m_d2.getPlanBase().getAllActions();
        System.out.println("findValidAction: " + l.size() + " actions to try.");
        for (ActionPlan a : l) {
            System.out.println("findValidAction: trying " + a.getAction());
            Plan adaptedPlan = parameterAdapter.adapt(a, a.getOriginalGameState(), a.getOriginalPlayer(), null, cycle, a_gs, currentPlayer ,null,unitsInUse);
            if (adaptedPlan != null) {
                return (ActionPlan) adaptedPlan;
            }
        }

        return null;
    }
}
