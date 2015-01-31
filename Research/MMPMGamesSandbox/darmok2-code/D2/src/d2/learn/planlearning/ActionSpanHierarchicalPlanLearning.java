/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package d2.learn.planlearning;

import d2.core.Config;
import d2.core.D2;
import d2.execution.adaptation.ExpandConditionMatcher;
import d2.plans.PlanDependencyGraph;
import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
import d2.plans.ActionPlan;
import d2.plans.DummyState;
import d2.plans.GoalPlan;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.util.plancreator.PetriNetHelper;
import d2.worldmodel.WorldModel;
import gatech.mmpm.Action;
import gatech.mmpm.Entity;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.IDomain;
import gatech.mmpm.Trace;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.SensorLibrary;
import gatech.mmpm.sensor.constant.True;
import gatech.mmpm.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Santiago Ontanon
 *
 * This class works the same as the HierarchicalPlanLearning, but it builds a dependency graph
 * in order to generate petri nets which are not sequential, but that capture the real dependencies
 * among actionSpan.
 *
 */
public class ActionSpanHierarchicalPlanLearning extends HierarchicalPlanLearning {

    public static int DEBUG = 1;
    List<PlanDependencyGraph> dependencyGraphs[] = null;

    public ActionSpanHierarchicalPlanLearning(IDomain domain) {
        super(domain);
    }

    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) throws Exception {

        if (DEBUG>=1) System.out.println("HierarchicalPlanLearning.learnFromTrace: " + t.getEntries().size() + " entries, learning from player " + playerName);
//		System.out.println("First state:");
//		System.out.println(t.getEntries().get(0).toString());
//		System.out.println("Last state:");
//		System.out.println(t.getEntries().get(t.getEntries().size()-1).toString());

        t.cleanUpAbortedActions();

        // record all the actionSpan in the trace:
        HashMap<Action,PlanSpan> actionSpan = new HashMap<Action,PlanSpan>();
        for (Entry e : t.getEntries()) {
            ArrayList<Action> entry_actions = new ArrayList<Action>();

            for(PlanSpan as:actionSpan.values()) {
                if (as.m_end==-1) {
                    // check if the action is over already:
                    if (as.m_action.checkSuccessCondition(e.getTimeStamp(), e.getGameState(), playerName)) {
                        as.m_end = e.getTimeStamp();
                    } else {
                        if (as.m_action.checkFailureCondition(e.getTimeStamp(), e.getGameState(), playerName)) {
                            as.m_end = e.getTimeStamp();
                            as.m_succeeded = false;
                        }
                    }
                }
            }

            for (Action a : e.getActions()) {
                if (a.getPlayerID().equals(playerName)) {
                    actionSpan.put(a,new PlanSpan(a,e.getTimeStamp(),-1, true));
                    entry_actions.add(a);
                }
            }
        }

        if (DEBUG>=1) {
            for(PlanSpan as:actionSpan.values()) {
                if (!as.m_succeeded) {
                    System.out.println("[FAILED] " + as.m_start + " - " + as.m_end + " : " + as.m_action.toSimpleString());
                } else {
                    System.out.println(as.m_start + " - " + as.m_end + " : " + as.m_action.toSimpleString());
                }
            }
        }

        createActionLists(t,playerName);

        if (DEBUG>=2) {
            System.out.println("Before subgoal substitution:");
            printActionList();
        }

        if (DEBUG>=1) System.out.println("About to perform goal substitutions:");
        findSubstitutions(playerName, wm, actionSpan);

        if (DEBUG>=1) System.out.println("Calculating individual petrinets:");
        return createIndividualPetriNets(playerName);
    }


    void findSubstitutions(String playerName, WorldModel wm, HashMap<Action,PlanSpan> actions) throws Exception {
        ExpandConditionMatcher ecm = new ExpandConditionMatcher(wm.getConditionMatcher());

        // Here we will record, for each goal, the shortest plan time span we can find:
        HashMap<Sensor,PlanSpan> goalSpan = new HashMap<Sensor,PlanSpan>();
        
        dependencyGraphs = new LinkedList[_goalActionList.length];

        // Construct the plan dependency graphs (including action-span analysis):
        for (int i = 0; i < _goalActionList.length; i++) {
            Sensor goal = _goalActionList[i]._a;
            dependencyGraphs[i] = new LinkedList<PlanDependencyGraph>();
            
            for (List<Pair<PlayerGameState, Plan>> al : _goalActionList[i]._b) {
                if (DEBUG>=1) System.out.println("**** Processing goal: " + goal + " ****");
                
                PlanBaseEntry pbe = PetriNetHelper.createPlanBaseEntryFromPlanSequence(al, goal, playerName);
                PlanDependencyGraph pdg = new PlanDependencyGraph(pbe.m_plan, ecm, 0,al.get(0)._a.gs, playerName);
                
                if (DEBUG>=1) System.out.println("Plan estimated length:" + planDurationEstimation(pdg, actions, goalSpan));

                // remove the dependencies that cannot be according to the action span analysis:
                for(int j = 0;j<pdg.m_subPlans.size();j++) {
                    for(int k = 0;k<pdg.m_subPlans.size();k++) {
                        if (j!=k) {
                            if (pdg.m_dependencies[j][k]!=null) {
                                ActionPlan a1 = (ActionPlan) pdg.m_subPlans.get(j);
                                ActionPlan a2 = (ActionPlan) pdg.m_subPlans.get(k);
                                PlanSpan as1 = actions.get(a1.getAction());
                                PlanSpan as2 = actions.get(a2.getAction());

                                if (as1.m_end==-1 || as1.m_end>as2.m_start) {
                                    pdg.m_dependencies[j][k] = null;
                                    if (DEBUG>=1) {
                                        System.out.println("Dependency removed due to action span analysis:");
                                        System.out.println(as1.m_action.toSimpleString() + "[" + as1.m_start + " - " + as1.m_end + "]  ->  " +
                                                           as2.m_action.toSimpleString() + "[" + as2.m_start + " - " + as2.m_end + "]");
                                    }
                                }
                            }
                        }
                    }
                }

                if (DEBUG>=1) System.out.println("Plan estimated length (after action span analysis):" + planDurationEstimation(pdg, actions, goalSpan));
                if (DEBUG>=1) pdg.print();
                dependencyGraphs[i].add(pdg);

                // remove the actionSpan from the list which are not required:
                // 1) find important actionSpan:
                GoalPlan gp = new GoalPlan(goal);
                List<Plan> importantActions = new LinkedList<Plan>();
                if (DEBUG>=1) System.out.println("Important actions for goal: " + goal);
                for(Plan p:pdg.m_subPlans) {
                    if (ecm.matchSuccessWithPreconditions(p, 0, p.getOriginalGameState(), playerName,
                                                          gp, 0, p.getOriginalGameState(), playerName)) {
                        importantActions.add(p);
                        if (DEBUG>=1) System.out.println(p);
                    }
                }
                // 2) find all the actionSpan required for those:
                List<Plan> IndirectImportantActions = new LinkedList<Plan>();
                for(Plan p:pdg.m_subPlans) {
                    for(Plan p2:importantActions) {
                        if (pdg.before(p, p2)) {
                            IndirectImportantActions.add(p);
                        }
                    }
                }

                // 3) remove the rest:
                List<Plan> toDelete = new LinkedList<Plan>();
                for(Plan p:pdg.m_subPlans) {
                    if (!IndirectImportantActions.contains(p)) {
                        // remove action:
                        if (DEBUG>=1) System.out.println("removing " + p);
                        
                        Pair<PlayerGameState,Plan> found = null;
                        for(Pair<PlayerGameState,Plan> pair:al) {
                            if (pair._b == p) {
                                found = pair;
                                break;
                            }
                        }
                        toDelete.add(p);
                        al.remove(found);
                    }
                }
                for(Plan p:toDelete) pdg.removePlan(p);

                {
                    int duration = planDurationEstimation(pdg, actions, goalSpan);
                    PlanSpan span = goalSpan.get(goal);
                    if (span==null || span.m_end>duration) {
                        goalSpan.put(goal,new PlanSpan(goal,0,duration,true));
                    }

                    if (DEBUG>=1) System.out.println("Plan estimated length (after action elimination):" + duration);                
                }
            }
        }

        if (DEBUG>=1) {
            System.out.println("Goal Spans:");
            for(PlanSpan ps:goalSpan.values()) {
                System.out.println(ps.m_goal + " -> " + ps.m_start + " - " + ps.m_end);
            }
        }

        // find substitutions:
        if (DEBUG>=1) System.out.println("Finding dependency-graph-based substitutions:");
        for (int i = 0; i < _goalActionList.length; i++) {
            Sensor goal1 = _goalActionList[i]._a;

            for (List<Pair<PlayerGameState, Plan>> a1 : _goalActionList[i]._b) {

                int candidate_goal = -1;
                List<Pair<PlayerGameState, Plan>> candidate_al = null;
                List<Plan> candidate_planList = null;
                PlanDependencyGraph pdg1 = dependencyGraphs[i].get(_goalActionList[i]._b.indexOf(a1));

                if (a1.size()==0) {
                    System.err.println("Plan for " + _goalActionList[i]._a + " has 0 actions!!");
                    System.err.println("Most likely the ConditionMatcher for this domain contains some errors...");
                } else {

                    for (int j = 0; j < _goalActionList.length; j++) {
                        if (i!=j) {
                            Sensor goal2 = _goalActionList[j]._a;
                            for (List<Pair<PlayerGameState, Plan>> a2 : _goalActionList[j]._b) {
                                if (a2.size()==0) {
                                    System.err.println("Plan for " + _goalActionList[j]._a + " has 0 actions!!");
                                    System.err.println("Most likely the ConditionMatcher for this domain contains some errors...");
                                } else {
                                    PlanDependencyGraph pdg2 = dependencyGraphs[j].get(_goalActionList[j]._b.indexOf(a2));
                                    List<Plan> planList = new LinkedList<Plan>();
                                    for(Pair<PlayerGameState, Plan> tmp:a2) planList.add(tmp._b);
                                    if (DEBUG>=1) System.out.println(goal2 + " (" + a2.size() + ") subgoal of " + goal1 + "(" + a1.size() + ")?");
                                    if (candidate_planList==null ||
                                        candidate_planList.size()<planList.size()) {
                                        if (pdg1.mergeable(planList)) {
                                            // test if the merge would introduce too much extra time:
                                            GoalPlan gp = new GoalPlan(_goalActionList[j]._a);
                                            gp.setOriginalGameState(a2.get(0)._a.gs);
                                            PlanDependencyGraph tmp_pdg = new PlanDependencyGraph(pdg1);
                                            int durationBefore = planDurationEstimation(tmp_pdg, actions, goalSpan);
                                            tmp_pdg.merge(planList, gp);
                                            int durationAfter = planDurationEstimation(tmp_pdg, actions, goalSpan);
                                            if (DEBUG>=1) {
                                                System.out.println("Before merging, duration estimation is: " + durationBefore);
                                                System.out.println("After merging, duration estimation is: " + durationAfter);
                                            }
                                            // If we don't introduce an excessive increase in time in the plan, then merge!
                                            if (durationAfter<durationBefore*1.1) {
                                                candidate_goal = j;
                                                candidate_planList = planList;  // "candidate_planList" is a candidate to be replaed by "candidate_goal" in "planList"
                                                candidate_al = a2;
                                                if (DEBUG>=1) System.out.println(goal2 + " is a candidate subgoal of " + goal1);
                                            } else {
                                                if (DEBUG>=1) System.out.println(goal2 + " is NOT a candidate subgoal of " + goal1 + " becauseit will introduce too much extra time");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (candidate_goal!=-1) {
                    // perform substitution:
                    GoalPlan gp = new GoalPlan(_goalActionList[candidate_goal]._a);
                    gp.setOriginalGameState(candidate_al.get(0)._a.gs);
                    pdg1.merge(candidate_planList, gp);
                }
            }
        }   
    }


    // compute the estimated duration of a plan based on the action spans observed in the trace:
    int planDurationEstimation(PlanDependencyGraph pdg, HashMap<Action,PlanSpan> actionSpans, HashMap<Sensor,PlanSpan> goalSpans) {
        boolean advanced = false;
        // estimation of when the actionSpan will end:
        HashMap<Object,Integer> estimation = new HashMap<Object,Integer>();
        int absoluteMax = 0;
        do{
            advanced = false;
            for(int i = 0;i<pdg.m_subPlans.size();i++) {
                Plan p1 = pdg.m_subPlans.get(i);
                Object ai = null;
                if (p1 instanceof ActionPlan) {
                    ai = ((ActionPlan)p1).getAction();
                } else {
                    ai = ((GoalPlan)p1).getGoal();
                }
                if (estimation.get(ai)==null) {
                    PlanSpan aias = null;
                    if (ai instanceof Action) aias = actionSpans.get((Action)ai);
                                         else aias = goalSpans.get((Sensor)ai);
                    boolean allEstimated = true;
                    int maxEstimate = 0;
                    if (aias.m_succeeded) maxEstimate = (aias.m_end-aias.m_start);
                    for(int j = 0;j<pdg.m_subPlans.size();j++) {
                        if (i!=j && pdg.m_dependencies[j][i]!=null) {
                            Plan p2 = pdg.m_subPlans.get(j);
                            Object aj = null;
                            if (p2 instanceof ActionPlan) {
                                aj = ((ActionPlan)p2).getAction();
                            } else {
                                aj = ((GoalPlan)p2).getGoal();
                            }
                            if (estimation.get(aj)!=null) {
                                int estimate = 0;
                                if (aias.m_succeeded) {
                                    estimate = estimation.get(aj) + (aias.m_end-aias.m_start);
                                } else {
                                    estimate = estimation.get(aj);
                                }
                                if (estimate>maxEstimate) maxEstimate = estimate;
                            } else {
                                allEstimated = false;
                                break;
                            }
                        }
                    }
                    if (allEstimated) {
                        estimation.put(ai, maxEstimate);
                        if (maxEstimate > absoluteMax) absoluteMax = maxEstimate;
                        advanced = true;
                    }
                }
            }
        }while(advanced);

        return absoluteMax;
    }


    List<PlanBaseEntry>  createIndividualPetriNets(String playerName) throws Exception {
        List<PlanBaseEntry> learntPlans = new LinkedList<PlanBaseEntry>();
        
        for (int i = 0; i < _goalActionList.length; i++) {
            Sensor m_goal = _goalActionList[i]._a;
            for (List<Pair<PlayerGameState, Plan>> al : _goalActionList[i]._b) {
                if (al.size()>0) {
                    PlanDependencyGraph pdg = dependencyGraphs[i].get(_goalActionList[i]._b.indexOf(al));

                    PlanBaseEntry pbe2 = pdg.generateFlexiblePlan(al.get(0)._a, m_goal);
                    learntPlans.add(pbe2);
                }
            }

            // Remove the action lists, so that we don't create behaviors twice when more than one trace is used
            _goalActionList[i]._b.clear();
        }

        return learntPlans;
    }
}
