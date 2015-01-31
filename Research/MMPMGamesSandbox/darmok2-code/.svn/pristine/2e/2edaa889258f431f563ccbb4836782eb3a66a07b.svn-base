/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.plans;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import d2.execution.adaptation.ExpandConditionMatcher;
import d2.execution.adaptation.ExpandConditionMatcher;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
import d2.learn.planlearning.PlanSpan;
import d2.plans.*;
import d2.util.plancreator.PetriNetHelper;
import gatech.mmpm.Action;
import gatech.mmpm.GameState;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.Pair;
import java.util.*;

/*
 * This class contains a Plan Dependency Graph, a directed graph where the nodes are subplans and the directed edges are 
 * conditions that represent the precondition/postcondition dependencies among these subplans.
 * 
 * The main difference of this class with respect to the compact matrix representation used by the 
 * "DependencyGraphPlanAdaptation" class is that this class keeps a record of which were the exact conditions that
 * matched. That allows the plan adaptation to infer many more things than with a plan matrix of 1 and 0s.
 * 
 */
public class PlanDependencyGraph {

    static int DEBUG = 0;
    static boolean safe = false; // runs inegrity tests after each dangerous operation (only needed for debugging)

    public static class Dependency {

        public Plan m_previousPlan, m_postPlan;
        public List<ExpandConditionMatcher.Match> m_matches;

        public Dependency(Plan prev, Plan post, List<ExpandConditionMatcher.Match> matches) {
            m_previousPlan = prev;
            m_postPlan = post;
            m_matches = matches;
        }

        public Dependency(Dependency d) {
            m_previousPlan = d.m_previousPlan;
            m_postPlan = d.m_postPlan;
            m_matches = new LinkedList<ExpandConditionMatcher.Match>();
            m_matches.addAll(d.m_matches);
        }
    }
    
    public List<Plan> m_subPlans;
    public Dependency[][] m_dependencies;

    public PlanDependencyGraph(PlanDependencyGraph p) {
        m_subPlans = new LinkedList<Plan>();
        m_subPlans.addAll(p.m_subPlans);

        int n_subPlans = m_subPlans.size();
        m_dependencies = new Dependency[n_subPlans][n_subPlans];
        for (int i = 0; i < n_subPlans; i++) {
            for (int j = 0; j < n_subPlans; j++) {
                if (p.m_dependencies[i][j]!=null) m_dependencies[i][j] = new Dependency(p.m_dependencies[i][j]);
            }
        }
    }

    public PlanDependencyGraph(PetriNetPlan pp, ExpandConditionMatcher conditionMatcher, int cycle, GameState gs, String player) throws Exception {

        List<PlanState> planStates = pp.getAllPlanStates();
        int n_subPlans = planStates.size();
        m_subPlans = new ArrayList<Plan>();
        for (PlanState ps : planStates) {
            m_subPlans.add(ps.getPlan());
        }
        m_dependencies = new Dependency[planStates.size()][planStates.size()];

        if (DEBUG >= 1) {
            for (int i = 0; i < m_subPlans.size(); i++) {
                System.out.println(i + " - " + m_subPlans.get(i));
            }
        }

        for (int i = 0; i < n_subPlans; i++) {
            for (int j = 0; j < n_subPlans; j++) {
                m_dependencies[i][j] = null;
            }
        }

        int[][] pathGraph = findAllPaths(pp);
        int i = 0, j = 0;
        for (PlanState current : planStates) {
            j = 0;
            for (PlanState current_previous : planStates) {
                if (i != j) {
//						System.out.println("Checking Matching for Actions " + j + " -> " + i);
                    if (pathGraph[j][i] == 1) {
                        List<ExpandConditionMatcher.Match> ret = conditionMatcher.matchSuccessWithPreconditionsDetailed(
                                current_previous.getPlan(), cycle, current_previous.getPlan().getOriginalGameState(), current_previous.getPlan().getOriginalPlayer(),
                                current.getPlan(), cycle, current.getPlan().getOriginalGameState(), current.getPlan().getOriginalPlayer());
                        if (!ret.isEmpty()) {
                            Dependency d = new Dependency(current_previous.getPlan(),current.getPlan(),ret);
                            m_dependencies[j][i] = d;
                        }
                    }
                }
                j++;
            }
            i++;
        }

        if (safe) checkIntegrity();
    }

    public List<Dependency> dependencies(Plan p) {
        List<Dependency> l = new LinkedList<Dependency>();
        int j = m_subPlans.indexOf(p);
        if (j == -1) {
            return l;
        }
        for (int i = 0; i < m_subPlans.size(); i++) {
            if (m_dependencies[i][j] != null) {
                l.add(m_dependencies[i][j]);
            }
        }

        return l;
    }

    /***
     * This function returns a matrix showing all the possible paths in a PetriNetPlan p
     * @param p
     * @return
     */
    public int[][] findAllPaths(Plan p) {
        if (p instanceof PetriNetPlan) {
            PetriNetPlan pp = (PetriNetPlan) p;

            List<PlanState> planStates = pp.getAllPlanStates();

            int[][] pathGraph = new int[planStates.size()][planStates.size()];

            for (int i = 0; i < planStates.size(); i++) {
                for (int j = 0; j < planStates.size(); j++) {
                    pathGraph[i][j] = 0;
                }

            }

            for (int i = 0; i < planStates.size(); i++) {
                for (int j = 0; j < planStates.size(); j++) {
                    if (findPath(planStates.get(i), planStates.get(j))) {
                        pathGraph[i][j] = 1;
                    }
                }
            }

            // displayMatrix(pathGraph,planStates.size());
            return pathGraph;
        } else {
            // System.out.println("Plan is not a PetriNetPlan , hence could not find the Paths in the Plan");
            return null;
        }
    }

    /***
     * This function is used to find if there exists a path between any two given PetriNetElements
     * @param start
     * @param target
     * @return
     */
    public boolean findPath(PetriNetElement start, PetriNetElement target) {

        HashSet<PetriNetElement> visited = new HashSet<PetriNetElement>();
        Stack<PetriNetElement> open = new Stack<PetriNetElement>();

        open.push(start);

        while (!open.isEmpty()) {

            PetriNetElement p = open.pop();

            if (p == target) {
                return true;
            }

            if (p instanceof State && !visited.contains(p)) {
                State state = (State) p;

                List<Pair<Integer, Transition>> nextTransitions = state.getNextTransitions();

                for (int i = 0; i < nextTransitions.size(); i++) {
                    open.push(nextTransitions.get(i)._b);
                }
            } else if (p instanceof Transition && !visited.contains(p)) {
                Transition transition = (Transition) p;

                List<Pair<Integer, State>> nextStates = transition.getNextStates();

                for (int i = 0; i < nextStates.size(); i++) {
                    open.push(nextStates.get(i)._b);
                }
            }
            visited.add(p);

        }

        return false;
    }

    public boolean before(Plan start, Plan end) {
        int i1 = m_subPlans.indexOf(start);
        int i2 = m_subPlans.indexOf(end);
        if (i1 == -1 || i2 == -1) {
            return false;
        }
        return before(i1, i2);
    }

    public boolean before(int start, int end) {
        boolean closed[] = new boolean[m_subPlans.size()];

        List<Integer> open = new LinkedList<Integer>();
        open.add(start);
        while (!open.isEmpty()) {
            int current = open.remove(0);
            closed[current] = true;
            for (int i = 0; i < m_subPlans.size(); i++) {
                if (m_dependencies[current][i] != null) {
                    if (!closed[i] && !open.contains(i)) {
                        open.add(i);
                    }
                }
            }
        }

        return closed[end];
    }

    public void removePlan(Plan p) throws Exception {
        if (safe) checkIntegrity();

        int index = m_subPlans.indexOf(p);
        if (index == -1) {
            return;
        }
        int len = m_subPlans.size();

        Dependency[][] dep = new Dependency[len - 1][len - 1];
        for (int i = 0; i < len; i++) {
            if (i != index) {
                for (int j = 0; j < len; j++) {
                    if (j != index) {
                        int i1 = (i < index ? i : i - 1);
                        int j1 = (j < index ? j : j - 1);
                        dep[i1][j1] = m_dependencies[i][j];
                    }
                }
            }
        }
        m_subPlans.remove(p);
        m_dependencies = dep;

        if (safe) checkIntegrity();
    }

    public boolean mergeable(List<Plan> plans) throws Exception {
        // First check it's a subset:

        if (safe) checkIntegrity();

        for (Plan p2 : plans) {
            if (!m_subPlans.contains(p2)) {
                return false;
            }
        }

        // Make sure that the block of plans in 'plans' is a single collapsable block
        if (DEBUG >= 1) {
            System.out.print("Testing whether [ ");
            for (Plan p : plans) {
                System.out.print(m_subPlans.indexOf(p) + " ");
            }
            System.out.println("] are mergeable");
        }

        for (Plan p1 : m_subPlans) {
            if (!plans.contains(p1)) {
                int current_ordering = 0;
                int i1 = m_subPlans.indexOf(p1);
                for (Plan p2 : plans) {
                    int i2 = m_subPlans.indexOf(p2);
                    if (i2 == -1) {
                        return false;
                    }
                    if (before(i1, i2)) {
                        if (current_ordering == 1) {
                            return false;
                        }
                        current_ordering = -1;
                    } else {
                        if (before(i2, i1)) {
                            if (current_ordering == -1) {
                                return false;
                            }
                            current_ordering = 1;
                        }
                    }
                }
//                System.out.println(m_subPlans.indexOf(p1) + " is " + current_ordering);
            }
        }

        return true;
    }

    // merges a collection of plans, and substitutes them by 'newPlan':
    public boolean merge(List<Plan> a_plans, Plan newPlan) throws Exception {
        if (!mergeable(a_plans)) {
            return false;
        }

        List<Plan> plans = new LinkedList<Plan>();
        plans.addAll(a_plans);
        Plan pToKeep = plans.remove(0);

        if (safe) checkIntegrity();

        for (Plan p : plans) {
            // merge dependencies of 'p' to 'pToKeep':
            int ptki = m_subPlans.indexOf(pToKeep);
            int pi = m_subPlans.indexOf(p);
            for (int i = 0; i < m_subPlans.size(); i++) {
                if (m_dependencies[i][pi] != null) {
                    if (m_dependencies[i][ptki] != null) {
                        m_dependencies[i][ptki].m_matches.addAll(m_dependencies[i][pi].m_matches);
                        m_dependencies[i][pi] = null;
                    } else {
                        m_dependencies[i][ptki] = m_dependencies[i][pi];
                        m_dependencies[i][ptki].m_postPlan = pToKeep;
                        m_dependencies[i][pi] = null;
                    }
                }
                if (m_dependencies[pi][i] != null) {
                    if (m_dependencies[ptki][i] != null) {
                        m_dependencies[ptki][i].m_matches.addAll(m_dependencies[pi][i].m_matches);
                        m_dependencies[pi][i] = null;
                    } else {
                        m_dependencies[ptki][i] = m_dependencies[pi][i];
                        m_dependencies[ptki][i].m_previousPlan = pToKeep;
                        m_dependencies[pi][i] = null;
                    }
                }
            }

            // remove 'p':
            removePlan(p);
//            System.out.println("After removing one plan the graph looks like this:");
//            print();
        }

        // replace 'pToKeep' with 'new Plan':
        m_subPlans.set(m_subPlans.indexOf(pToKeep), newPlan);

        // change to "newPlan" also in all the depdendencies:
        for (int i = 0; i < m_subPlans.size(); i++) {
            int ptki = m_subPlans.indexOf(newPlan);
            if (m_dependencies[i][ptki] != null) {
                m_dependencies[i][ptki].m_postPlan = newPlan;
            }
            if (m_dependencies[ptki][i] != null) {
                m_dependencies[ptki][i].m_previousPlan = newPlan;
            }
        }
        
        if (safe) checkIntegrity();

        return true;
    }
    
    
    public void removeDependenciesUsingTimeSpans(HashMap<Action,PlanSpan> actions) {
        // remove the dependencies that cannot be according to the action span analysis:
        for(int i = 0;i<m_subPlans.size();i++) {
            for(int j = 0;j<m_subPlans.size();j++) {
                if (i!=j) {
                    if (m_dependencies[i][j]!=null) {
                        ActionPlan a1 = (ActionPlan) m_subPlans.get(i);
                        ActionPlan a2 = (ActionPlan) m_subPlans.get(j);
                        PlanSpan as1 = actions.get(a1.getAction());
                        PlanSpan as2 = actions.get(a2.getAction());

                        if (as1.getStart()==-1 || as1.getEnd()>as2.getStart()) {
                            m_dependencies[i][j] = null;
                            if (DEBUG>=1) {
                                System.out.println("Dependency removed due to action span analysis:");
                                System.out.println(as1.getAction().toSimpleString() + "[" + as1.getStart() + " - " + as1.getEnd() + "]  ->  " +
                                                   as2.getAction().toSimpleString() + "[" + as2.getStart() + " - " + as2.getEnd() + "]");
                            }
                        }
                    }
                }
            }
        }        
    }
    

    public PlanBaseEntry generateFlexiblePlan(PlayerGameState pgs, Sensor goal) throws Exception {
        // 1.- Remove a restriction (a,b) if there exists a "c" such that (a,c) and (c,b)
        // 2.- For each action "a", require as many tokens as rependencies (·,a) exist

        if (DEBUG >= 1) {
            System.out.println("generateFlexiblePlan for goal:" + goal);
        }

        // Find a minimum set of dependencies:
        int n_subPlans = m_subPlans.size();

        int n_minimum_dependencies = 0;

        PlanDependencyGraph p = new PlanDependencyGraph(this);

        // Find minimum set:
        for (int i = 0; i < n_subPlans; i++) {
            for (int j = 0; j < n_subPlans; j++) {
                if (p.m_dependencies[i][j] != null) {
                    Dependency tmp = p.m_dependencies[i][j];
                    p.m_dependencies[i][j] = null;

                    if (p.before(i, j)) {
                        n_minimum_dependencies++;
                    } else {
                        // readd he dependency
                        p.m_dependencies[i][j] = tmp;
                    }
                }
            }
        }

        if (DEBUG >= 1) {
            System.out.println("Dependencies reduced in " + n_minimum_dependencies);
            p.print();
        }

        return PetriNetHelper.createPlanBaseEntryFromDependencyGraph(p, goal, pgs);
    }



    public void checkIntegrity() throws Exception {
        for(int i = 0;i<m_subPlans.size();i++) {
            for(int j = 0;j<m_subPlans.size();j++) {
                if (m_dependencies[i][j]!=null) {
                    if (m_dependencies[i][j].m_previousPlan!=m_subPlans.get(i) ||
                        m_dependencies[i][j].m_postPlan!=m_subPlans.get(j))
                        throw new Exception("Dependency Graph has lost integrity at position " + i + "," + j + "!");
                }
            }
        }
    }
    
    
   public void print() {
        System.out.println("PlanDependencyGraph:");
        System.out.println(m_subPlans.size() + " plans");
        for (int i = 0; i < m_subPlans.size(); i++) {
            System.out.println(i + " - " + m_subPlans.get(i));
        }

        System.out.println("Dependencies:");
        for (int j = 0; j < m_dependencies.length; j++) {
            System.out.print("[");
            for (int i = 0; i < m_dependencies[j].length; i++) {
                if (m_dependencies[i][j] != null) {
                    System.out.print(" " + i);
                }
            }
            System.out.println(" ] -> " + j);
        }
    }
   
   
   public void printDetailed() {
        System.out.println("PlanDependencyGraph:");
        System.out.println(m_subPlans.size() + " plans");
        for (int i = 0; i < m_subPlans.size(); i++) {
            System.out.println(i + " - " + m_subPlans.get(i));
        }

        System.out.println("Dependencies:");
        for (int j = 0; j < m_dependencies.length; j++) {            
            System.out.println(j + " depends on:");
            for (int i = 0; i < m_dependencies[j].length; i++) {
                if (m_dependencies[i][j] != null) {
                    System.out.print("  " + i + ": ");
                    for(ExpandConditionMatcher.Match m:m_dependencies[i][j].m_matches) {
                        if (m.preCondition!=null) {
                            System.out.print(m.preCondition.toSimpleString() + " ");
                        } else {
                            System.out.print("- ");
                        }
                    }
                    System.out.println();
                }
            }
        }
    }
   
}
