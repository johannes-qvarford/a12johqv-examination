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
import d2.plans.PlanPreconditionFailedState;
import d2.plans.PlanState;
import d2.plans.PreFailureTransition;
import d2.plans.PreTransition;
import d2.plans.SuccessTransition;
import d2.plans.Transition;
import d2.util.planvisualizer.PlanVisualizer;
import d2.worldmodel.ConditionMatcher;
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

public class DependencyGraphMonolithicPlanLearning extends PlanLearning {

    public static int DEBUG = 0;
    protected Sensor m_winGameGoal;

    public DependencyGraphMonolithicPlanLearning(IDomain domain) {
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

        // create petri net plan
        PetriNetPlan pnp = new PetriNetPlan();
        pnp.setPreCondition(new True());
        pnp.setFailureCondition(new False()); // can't fail, for now
        pnp.setSuccessCondition(new True());
        pnp.setOriginalGameState(t.getEntries().get(0).getGameState());
        pnp.setOriginalPlayer(playerName);

        DummyState petrinet_head = null;
        GameState gamestate_head = null;
        DummyState head_ds = new DummyState(); // each "block" has a head dummy state

        // Remember the entry point of the petri net, to set the entry points in
        // the PlanBase:
        int cycle_head = entry_list.get(0).getTimeStamp();
        petrinet_head = head_ds;
        gamestate_head = entry_list.get(0).getGameState();

        for (Entry e : entry_list) {
            ArrayList<Action> actions = new ArrayList<Action>();

            for (Action a : e.getActions()) {
                if (a.getPlayerID().equals(playerName)) {
                    actions.add(a);
                }
            }


            DummyState next_ds = new DummyState();

            // there are actions
            if (actions.size() > 0) {
                if (DEBUG>=1) printTimeStamp(e);
                pnp.addPetriNetElement(head_ds); // we're done with head_ds
                MonolithicPlanLearning.handleActionsBlock(pnp, head_ds, actions, next_ds, e.getGameState(), playerName);
                head_ds = next_ds;
            }
        }

        pnp.addPetriNetElement(head_ds);

        PlanDependencyGraph pdg = new PlanDependencyGraph(pnp, ecm, entry_list.get(0).getTimeStamp(), gamestate_head, playerName);
        PlanBaseEntry pbe = pdg.generateFlexiblePlan(new PlayerGameState(gamestate_head, cycle_head, playerName), m_winGameGoal);
        learntPlans.add(pbe);

        return learntPlans;
//        new PlanVisualizer(pnp, 800,600, 0, petrinet_head);
    }

    /**
     * prints the time stamp of a given entry.
     * @param e the entry.
     */
    private void printTimeStamp(Entry e) {
        System.out.println("Evaluating time_stamp: " + e.getTimeStamp() + " , which has " + e.getActions().size() + " actions.");
        for(Action a:e.getActions()) {
            System.out.println("    " + a);
        }
    }

}
