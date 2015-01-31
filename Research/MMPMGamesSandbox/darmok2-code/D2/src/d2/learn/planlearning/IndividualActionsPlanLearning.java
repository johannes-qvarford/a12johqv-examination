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
import d2.util.plancreator.PetriNetHelper;
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

public class IndividualActionsPlanLearning extends PlanLearning {

    public static int DEBUG = 0;
    public static int nextID = 0;
    protected Sensor m_winGameGoal;

    public IndividualActionsPlanLearning(IDomain domain) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
    }

    /**
     * Learn a trace for the given player.
     */
    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) {
        List<PlanBaseEntry> learntPlans = new LinkedList<PlanBaseEntry>();
        String previous_ID = null;
        
        List<Entry> entry_list = t.getEntries();

        t.cleanUpAbortedActions();

        for (Entry e : entry_list) {
            ArrayList<Action> actions = new ArrayList<Action>();

            for (Action a : e.getActions()) {
                if (a.getPlayerID().equals(playerName)) {
                    actions.add(a);
                }
            }

            // there are actions
            if (actions.size() > 0) {
                String ID = "Case" + (nextID++);
                // create petri net plan with a single action:
                PetriNetPlan pnp = new PetriNetPlan();
                pnp.setPreCondition(new True());
                pnp.setFailureCondition(new False());
                pnp.setSuccessCondition(new True());
                pnp.setOriginalGameState(e.getGameState());
                pnp.setOriginalPlayer(playerName);

                DummyState petrinet_head = new DummyState();
                pnp.addPetriNetElement(petrinet_head);

                if (DEBUG>=1) printTimeStamp(e);
                DummyState next_ds = new DummyState();
                pnp.addPetriNetElement(next_ds);

                /*
                ActionPlan ap = new ActionPlan();
                ap.setOriginalGameState(e.getGameState());
                ap.setOriginalPlayer(playerName);
                ap.setAction(a);
                */

                MonolithicPlanLearning.handleActionsBlock(pnp, petrinet_head, actions, next_ds, e.getGameState(), playerName);
//                    PetriNetHelper.handlePlanBlock(pnp, petrinet_head, ap, next_ds, null, e.getGameState(), playerName);
                PlanBaseEntry pbe = new PlanBaseEntry(pnp, m_winGameGoal, e.getGameState(), e.getTimeStamp(), playerName, ID, previous_ID);
                HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

                // Set only one entry point, which is the beginning of the petri net:
                tokensInStates.put(petrinet_head.getElementID(), 1);
                pbe.setEntryPoint(tokensInStates);
                learntPlans.add(pbe);
                
                previous_ID = ID;
            }
        }

        return learntPlans;
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
