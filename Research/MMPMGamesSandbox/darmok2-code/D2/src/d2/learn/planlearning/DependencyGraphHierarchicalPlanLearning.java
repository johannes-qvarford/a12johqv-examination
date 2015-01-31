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
 * among actions.
 *
 */
public class DependencyGraphHierarchicalPlanLearning extends HierarchicalPlanLearning {

    public static int DEBUG = 0;
    List<PlanDependencyGraph> dependencyGraphs[] = null;

    public DependencyGraphHierarchicalPlanLearning(IDomain domain) {
        super(domain);
    }

    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerID, WorldModel wm) throws Exception {

        if (DEBUG>=1) System.out.println("HierarchicalPlanLearning.learnFromTrace: " + t.getEntries().size() + " entries, learning from player " + playerID);
//		System.out.println("First state:");
//		System.out.println(t.getEntries().get(0).toString());
//		System.out.println("Last state:");
//		System.out.println(t.getEntries().get(t.getEntries().size()-1).toString());

        t.cleanUpAbortedActions();

        createActionLists(t,playerID);

        if (DEBUG>=2) {
            System.out.println("Before subgoal substitution:");
            printActionList();
        }

        if (DEBUG>=1) System.out.println("About to perform goal substitutions:");
        findSubstitutions(playerID, wm);

        if (DEBUG>=1) System.out.println("Calculating individual petrinets:");
        return createIndividualPetriNets(playerID);
    }


    void findSubstitutions(String playerName, WorldModel wm) throws Exception {
        ExpandConditionMatcher ecm = new ExpandConditionMatcher(wm.getConditionMatcher());

        dependencyGraphs = new LinkedList[_goalActionList.length];

        for (int i = 0; i < _goalActionList.length; i++) {
            Sensor m_goal = _goalActionList[i]._a;
            dependencyGraphs[i] = new LinkedList<PlanDependencyGraph>();
            
            for (List<Pair<PlayerGameState, Plan>> al : _goalActionList[i]._b) {
                PlanBaseEntry pbe = PetriNetHelper.createPlanBaseEntryFromPlanSequence(al, m_goal, playerName);

                PlanDependencyGraph pdg = new PlanDependencyGraph(pbe.m_plan, ecm, al.get(0)._a.cycle, al.get(0)._a.gs, playerName);
                if (DEBUG>=1) pdg.print();
                dependencyGraphs[i].add(pdg);

                // remove the actions from the list which are not required:
                // 1) find important actions:
                GoalPlan gp = new GoalPlan(m_goal);
                List<Plan> importantActions = new LinkedList<Plan>();
                if (DEBUG>=1) System.out.println("Important actions for goal: " + m_goal);
                for(Plan p:pdg.m_subPlans) {
                    if (ecm.matchSuccessWithPreconditions(p, 0, p.getOriginalGameState(), playerName,
                                                          gp, 0, p.getOriginalGameState(), playerName)) {
                        importantActions.add(p);
                        if (DEBUG>=1) System.out.println(p);
                    }
                }
                // 2) find all the actions required for those:
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
                                            candidate_goal = j;
                                            candidate_planList = planList;
                                            candidate_al = a2;

                                            if (DEBUG>=1) System.out.println(goal2 + " is a candidate subgoal of " + goal1);
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
