/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.learn.planlearning;

import d2.core.D2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlanBaseEntry;
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

public class MonolithicPlanLearning extends PlanLearning {

    public static int DEBUG = 0;
    protected Sensor m_winGameGoal;

    public MonolithicPlanLearning(IDomain domain) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
    }

    /**
     * Learn a trace for the given player.
     */
    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) {
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
        DummyState head_ds = new DummyState(); // each "block" has a head dummy
        // state

        // Remember the entry point of the petri net, to set the entry points in
        // the PlanBase:
        petrinet_head = head_ds;
        gamestate_head = entry_list.get(0).getGameState();
        pnp.addPetriNetElement(head_ds);

        for (Entry e : entry_list) {
            ArrayList<Action> actions = new ArrayList<Action>();

            for (Action a : e.getActions()) {
                if (a.getPlayerID().equals(playerName)) {
                    actions.add(a);
                }
            }

            // there are actions
            if (actions.size() > 0) {
                if (DEBUG>=1) printTimeStamp(e);
                DummyState next_ds = new DummyState();
                pnp.addPetriNetElement(next_ds);
                handleActionsBlock(pnp, head_ds, actions, next_ds, e.getGameState(), playerName);
                head_ds = next_ds;
            }
        }

        PlanBaseEntry pbe = new PlanBaseEntry(pnp, m_winGameGoal, gamestate_head, t.getEntries().get(0).getTimeStamp(), playerName);
        HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

        // Set only one entry point, which is the beginning of the petri net:
        tokensInStates.put(petrinet_head.getElementID(), 1);
        pbe.setEntryPoint(tokensInStates);
        learntPlans.add(pbe);

        return learntPlans;
//        new PlanVisualizer(pnp, 800,600, 0, petrinet_head);
    }

    /**
     * Handles all actions within an entry.
     * @param pnp
     * @param head_ds
     * @param actions
     * @param next_ds
     * @param pre_condition
     */
    public static void handleActionsBlock(PetriNetPlan pnp, DummyState head_ds,
            ArrayList<Action> actions, DummyState next_ds, GameState gs, String player) {

        DummyTransition distributor = new DummyTransition();
        head_ds.addNextTransition(1, distributor);
        pnp.addPetriNetElement(distributor);

        DummyState collector = new DummyState();
        DummyTransition collector_to_next = new DummyTransition();
        collector.addNextTransition(actions.size(), collector_to_next);
        collector_to_next.addNextState(1, next_ds);
        pnp.addPetriNetElement(collector);
        pnp.addPetriNetElement(collector_to_next);

        for (int i = 0; i < actions.size(); i++) {
            // first, create the action plan
            Action act = actions.get(i);
            ActionPlan ap = new ActionPlan();
            ap.setOriginalGameState(gs);
            ap.setOriginalPlayer(player);

            ap.setAction(act);
//			System.out.println("Added action " + act + " to plan.");

            // now, create the plan state for that action plan
            PlanState ps = new PlanState();
            ps.setPlan(ap);
            pnp.addPetriNetElement(ps); // plan state

            DummyState action_head = new DummyState();
            distributor.addNextState(1, action_head);
            pnp.addPetriNetElement(action_head);

            // now the transitions

            // first we create the pre transition from the token dummy state
            // (token_ds)
            PreTransition from_token_to_plan_state = new PreTransition(ap);
            from_token_to_plan_state.addNextState(1, ps);
            action_head.addNextTransition(1, from_token_to_plan_state);
            pnp.addPetriNetElement(from_token_to_plan_state); // pre_transition

            // next we create the success transition from plan state to the next
            // dummy state (next_ds)
            SuccessTransition from_plan_state_to_next_dummy = new SuccessTransition(ap);
            from_plan_state_to_next_dummy.addNextState(1, collector);
            ps.addNextTransition(1, from_plan_state_to_next_dummy);
            pnp.addPetriNetElement(from_plan_state_to_next_dummy); // success_transition

            // finally we deal with the failure transition, which requires that
            // we create a new "failure" dummy state
            // But we only do it if the action has a non-empty Failure Condition
            if (!(ap.getFailureCondition() instanceof False)) {
                DummyState fs = new DummyState("FAILURESTATE");
                FailureTransition from_plan_state_to_failure_state = new FailureTransition(ap);
                from_plan_state_to_failure_state.addNextState(1, fs);
                ps.addNextTransition(1, from_plan_state_to_failure_state);
                pnp.addPetriNetElement(from_plan_state_to_failure_state); // failure_transition
                pnp.addPetriNetElement(fs); // failure state
            }

            // If there is a preFailureCondition, add it:
            if (!(ap.getPreFailureCondition() instanceof False)) {
                PlanPreconditionFailedState fs = new PlanPreconditionFailedState(ap);
                Transition from_dummy_state_to_failure_state = new PreFailureTransition(ap);
                from_dummy_state_to_failure_state.addNextState(1, fs);
                action_head.addNextTransition(1, from_dummy_state_to_failure_state);
                pnp.addPetriNetElement(from_dummy_state_to_failure_state); // failure_transition
                pnp.addPetriNetElement(fs); // failure state
            }

            // add all the action specific elements to pnp
        }
    }

    /**
     * prints the time stamp of a given entry.
     * @param e the entry.
     */
    public static void printTimeStamp(Entry e) {
        System.out.println("Evaluating time_stamp: " + e.getTimeStamp() + " , which has " + e.getActions().size() + " actions.");
        for(Action a:e.getActions()) {
            System.out.println("    " + a);
        }
    }

}
