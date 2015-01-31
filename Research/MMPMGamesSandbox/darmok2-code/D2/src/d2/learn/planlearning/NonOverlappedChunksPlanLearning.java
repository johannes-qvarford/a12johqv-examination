/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.learn.planlearning;

import d2.execution.planbase.PlanBaseEntry;
import d2.plans.*;
import d2.worldmodel.WorldModel;
import gatech.mmpm.*;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author santi
 */
public class NonOverlappedChunksPlanLearning extends PlanLearning {
    public static int DEBUG = 1;
    protected Sensor m_winGameGoal;
    int m_chunkSize = 3;

    public NonOverlappedChunksPlanLearning(IDomain domain) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
    }

    
    public NonOverlappedChunksPlanLearning(IDomain domain, double chunkSize) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
        m_chunkSize = (int)chunkSize;
    }

    
    /**
     * Learn a trace for the given player.
     */
    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) {
        List<PlanBaseEntry> learntPlans = new LinkedList<PlanBaseEntry>();

        List<Entry> entry_list = t.getEntries();

        t.cleanUpAbortedActions();

        for(int idx = 0;idx<entry_list.size();idx++) {
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
            int cycle_head = entry_list.get(idx).getTimeStamp();
            petrinet_head = head_ds;
            gamestate_head = entry_list.get(idx).getGameState();
            pnp.addPetriNetElement(head_ds);
            
            if (DEBUG>=1) System.out.println("NonOverlappedChunksPlanLearning, generating plan:");     
            int nActions = 0;
            
            for(int currentSize = 0;idx<entry_list.size() && currentSize<m_chunkSize;idx++) {
                Entry e = entry_list.get(idx);
                ArrayList<Action> actions = new ArrayList<Action>();

                for (Action a : e.getActions()) {
                    if (a.getPlayerID().equals(playerName)) {
                        actions.add(a);
                    }
                }

                // there are actions
                if (actions.size() > 0) {
                    if (DEBUG>=1) {
                        for(Action a:actions) System.out.println("  " + e.getTimeStamp() + " - " + a);
                    }
                    DummyState next_ds = new DummyState();
                    pnp.addPetriNetElement(next_ds);
                    MonolithicPlanLearning.handleActionsBlock(pnp, head_ds, actions, next_ds, e.getGameState(), playerName);
                    head_ds = next_ds;
                    nActions += actions.size();
                    currentSize++;
                }                
            }
            
            if (nActions>0) {
                if (DEBUG>=1) System.out.println("  Plan has " + nActions + " actions.");
                PlanBaseEntry pbe = new PlanBaseEntry(pnp, m_winGameGoal, gamestate_head, cycle_head, playerName);
                HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

                // Set only one entry point, which is the beginning of the petri net:
                tokensInStates.put(petrinet_head.getElementID(), 1);
                pbe.setEntryPoint(tokensInStates);
                learntPlans.add(pbe);            
            }
        }
       

        return learntPlans;
//        new PlanVisualizer(pnp, 800,600, 0, petrinet_head);
    }
}
