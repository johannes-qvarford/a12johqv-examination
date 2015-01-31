/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.learn.planlearning;

import d2.execution.planbase.GameStateFeatures;
import d2.execution.planbase.GameStateSimilarity;
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
public class SimilarityBasedChunkingPlanLearning extends PlanLearning {
    public static int DEBUG = 0;
    protected Sensor m_winGameGoal;
    double m_threshold = 0.95;

    public SimilarityBasedChunkingPlanLearning(IDomain domain) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
    }

    
    public SimilarityBasedChunkingPlanLearning(IDomain domain, double threshold) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
        m_threshold = threshold;
    }

    
    /**
     * Learn a trace for the given player.
     */
    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) {
        List<PlanBaseEntry> learntPlans = new LinkedList<PlanBaseEntry>();

        List<Entry> entry_list = t.getEntries();

        t.cleanUpAbortedActions();

        int idx = 0;
        do {
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
            
            if (DEBUG>=1) System.out.println("SimilarityBasedChunkingPlanLearning, generating plan:");     
            GameStateFeatures last_gsf = null;
            int nActions = 0;
            
            boolean endOfCase = true;
            do{
                Entry e = entry_list.get(idx);
                ArrayList<Action> actions = new ArrayList<Action>();
                for (Action a : e.getActions()) 
                    if (a.getPlayerID().equals(playerName)) actions.add(a);

                if (actions.size()>0) {
                    GameStateFeatures gsf = new GameStateFeatures(entry_list.get(idx).getTimeStamp(), entry_list.get(idx).getGameState(), playerName);
                    double similarity = m_threshold;
                    if (last_gsf!=null) similarity = GameStateSimilarity.gameStateSimilarity(last_gsf, gsf, null);
                    if (similarity<m_threshold) {
                        // end of case:
                        endOfCase = true;
                    } else {
                        // add to the case:
                        if (actions.size() > 0) {
                            if (DEBUG>=2) {
                                for(Action a:actions) System.out.println("  " + e.getTimeStamp() + " - " + a);
                            }
                            DummyState next_ds = new DummyState();
                            pnp.addPetriNetElement(next_ds);
                            MonolithicPlanLearning.handleActionsBlock(pnp, head_ds, actions, next_ds, e.getGameState(), playerName);
                            head_ds = next_ds;
                            nActions += actions.size();
                        }                

                        last_gsf = gsf;
                        endOfCase = false;
                        idx++;
                    }
                } else {
                    endOfCase = false;
                    idx++;
                }
                
            }while(idx<entry_list.size() && !endOfCase);
            
            if (nActions>0) {
                if (DEBUG>=1) System.out.println("  Plan has " + nActions + " actions.");
                PlanBaseEntry pbe = new PlanBaseEntry(pnp, m_winGameGoal, gamestate_head, cycle_head, playerName);
                HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

                // Set only one entry point, which is the beginning of the petri net:
                tokensInStates.put(petrinet_head.getElementID(), 1);
                pbe.setEntryPoint(tokensInStates);
                learntPlans.add(pbe);            
            }
        }while(idx<entry_list.size());
       

        return learntPlans;
//        new PlanVisualizer(pnp, 800,600, 0, petrinet_head);
    }
}
