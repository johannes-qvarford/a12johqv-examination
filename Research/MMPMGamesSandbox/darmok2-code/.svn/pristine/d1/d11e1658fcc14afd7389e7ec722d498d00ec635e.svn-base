/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.learn.planlearning;

import d2.core.D2;
import d2.execution.adaptation.ExpandConditionMatcher;
import d2.plans.PlanDependencyGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
import d2.plans.ActionPlan;
import d2.plans.DummyState;
import d2.plans.DummyTransition;
import d2.plans.FailureTransition;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanPreconditionFailedState;
import d2.plans.PlanState;
import d2.plans.PreFailureTransition;
import d2.plans.PreTransition;
import d2.plans.SuccessTransition;
import d2.plans.Transition;
import d2.util.planvisualizer.PlanVisualizer;
import d2.worldmodel.WorldModel;

import gatech.mmpm.Action;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.IDomain;
import gatech.mmpm.Trace;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.composite.AndCondition;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;
import java.util.LinkedList;

public class ActionSpanMonolithicPlanLearning extends PlanLearning {

    public static int DEBUG = 0;
    protected Sensor m_winGameGoal;

    public ActionSpanMonolithicPlanLearning(IDomain domain) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
    }

    /**
     * Learn a trace for the given player.
     */
    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) throws Exception {
        ExpandConditionMatcher ecm = new ExpandConditionMatcher(wm.getConditionMatcher());
        List<PlanBaseEntry> learntPlans = new LinkedList<PlanBaseEntry>();
        List<Entry> entry_list = t.getEntries();

        t.cleanUpAbortedActions();

        // create a basic petri net plan:
        PetriNetPlan pnp = new PetriNetPlan();
        pnp.setPreCondition(new True());
        pnp.setFailureCondition(new False()); // can't fail, for now
        pnp.setSuccessCondition(new True());
        pnp.setOriginalGameState(t.getEntries().get(0).getGameState());
        pnp.setOriginalPlayer(playerName);

        DummyState petrinet_head = null;
        GameState gamestate_head = entry_list.get(0).getGameState();
        int cycle_head = entry_list.get(0).getTimeStamp();
        DummyState head_ds = new DummyState(); // each "block" has a head dummy state
        
        for (Entry e : entry_list) {
            ArrayList<Action> entry_actions = new ArrayList<Action>();

            for (Action a : e.getActions()) {
                if (a.getPlayerID().equals(playerName)) {
                    entry_actions.add(a);
                }
            }

            // there are actions
            if (entry_actions.size() > 0) {
                if (DEBUG>=1) printTimeStamp(e);
                DummyState next_ds = new DummyState();
                pnp.addPetriNetElement(next_ds);
                MonolithicPlanLearning.handleActionsBlock(pnp, head_ds, entry_actions, next_ds, e.getGameState(), playerName);
                head_ds = next_ds;
            }
        }        
                

        // record all the actions in the trace:
        HashMap<Action,PlanSpan> actions = createPlanTimeSpanTable(entry_list,playerName);

        PlanDependencyGraph pdg = new PlanDependencyGraph(pnp, ecm, entry_list.get(0).getTimeStamp(), gamestate_head, playerName);
        pdg.removeDependenciesUsingTimeSpans(actions);

        PlanBaseEntry pbe = pdg.generateFlexiblePlan(new PlayerGameState(gamestate_head, cycle_head, playerName), m_winGameGoal);
        learntPlans.add(pbe);

        return learntPlans;
//        new PlanVisualizer(pnp, 800,600, 0, petrinet_head);
    }
    
    
    public static HashMap<Action,PlanSpan> createPlanTimeSpanTable(List<Entry> entry_list, String playerName) {
        HashMap<Action,PlanSpan> actions = new HashMap<Action,PlanSpan>();
        
        for (Entry e : entry_list) {
            ArrayList<Action> entry_actions = new ArrayList<Action>();

            for(PlanSpan as:actions.values()) {
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
                    actions.put(a,new PlanSpan(a,e.getTimeStamp(),-1, true));
                    entry_actions.add(a);
                }
            }
        }        
        
        if (DEBUG>=1) {
            for(PlanSpan as:actions.values()) {
                if (!as.m_succeeded) {
                    System.out.println("[FAILED] " + as.m_start + " - " + as.m_end + " : " + as.m_action.toSimpleString());
                } else {
                    System.out.println(as.m_start + " - " + as.m_end + " : " + as.m_action.toSimpleString());
                }
            }
        }        
        
        return actions;
    }

    /**
     * prints the time stamp of a given entry.
     * @param e the entry.
     */
    static void printTimeStamp(Entry e) {
        System.out.println("Evaluating time_stamp: " + e.getTimeStamp() + " , which has " + e.getActions().size() + " actions.");
        for(Action a:e.getActions()) {
            System.out.println("    " + a);
        }
    }
}
