/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.learn.planlearning;

import d2.execution.adaptation.ExpandConditionMatcher;
import d2.plans.PlanDependencyGraph;
import d2.execution.planbase.GameStateFeatures;
import d2.execution.planbase.GameStateSimilarity;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
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
public class SimilarityBasedChunkingWithASDGPlanLearning extends PlanLearning {
    public static int DEBUG = 0;
    protected Sensor m_winGameGoal;
    double m_threshold = 0.95;

    public SimilarityBasedChunkingWithASDGPlanLearning(IDomain domain) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
    }

    
    public SimilarityBasedChunkingWithASDGPlanLearning(IDomain domain, double threshold) {
        super(domain);
        m_winGameGoal = domain.getWinGoal();
        m_threshold = threshold;
    }

    
    /**
     * Learn a trace for the given player.
     */
    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) throws Exception {
        List<PlanBaseEntry> learntPlans = new LinkedList<PlanBaseEntry>();
        ExpandConditionMatcher ecm = new ExpandConditionMatcher(wm.getConditionMatcher());

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
            HashMap<Action,PlanSpan> actions = new HashMap<Action,PlanSpan>();
            
            boolean endOfCase = true;
            do{
                Entry e = entry_list.get(idx);
                ArrayList<Action> entry_actions = new ArrayList<Action>();
                for (Action a : e.getActions()) 
                    if (a.getPlayerID().equals(playerName)) {
                        actions.put(a,new PlanSpan(a,e.getTimeStamp(),-1, true));
                        entry_actions.add(a);
                    }

                if (entry_actions.size()>0) {
                    GameStateFeatures gsf = new GameStateFeatures(entry_list.get(idx).getTimeStamp(), entry_list.get(idx).getGameState(), playerName);
                    double similarity = m_threshold;
                    if (last_gsf!=null) similarity = GameStateSimilarity.gameStateSimilarity(last_gsf, gsf, null);
                    if (similarity<m_threshold) {
                        // end of case:
                        endOfCase = true;
                    } else {
                        // add to the case:
                        if (entry_actions.size() > 0) {
                            if (DEBUG>=1) ActionSpanMonolithicPlanLearning.printTimeStamp(e);;
                            if (DEBUG>=2) {
                                for(Action a:entry_actions) System.out.println("  " + e.getTimeStamp() + " - " + a);
                            }
                            DummyState next_ds = new DummyState();
                            pnp.addPetriNetElement(next_ds);
                            MonolithicPlanLearning.handleActionsBlock(pnp, head_ds, entry_actions, next_ds, e.getGameState(), playerName);
                            head_ds = next_ds;
                            nActions += entry_actions.size();
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
                               
                PlanDependencyGraph pdg = new PlanDependencyGraph(pnp, ecm, entry_list.get(0).getTimeStamp(), gamestate_head, playerName);

                // remove the dependencies that cannot be according to the action span analysis:
                for(int i = 0;i<pdg.m_subPlans.size();i++) {
                    for(int j = 0;j<pdg.m_subPlans.size();j++) {
                        if (i!=j) {
                            if (pdg.m_dependencies[i][j]!=null) {
                                ActionPlan a1 = (ActionPlan) pdg.m_subPlans.get(i);
                                ActionPlan a2 = (ActionPlan) pdg.m_subPlans.get(j);
                                PlanSpan as1 = actions.get(a1.getAction());
                                PlanSpan as2 = actions.get(a2.getAction());

                                if (as1.m_end==-1 || as1.m_end>as2.m_start) {
                                    pdg.m_dependencies[i][j] = null;
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

                PlanBaseEntry pbe = pdg.generateFlexiblePlan(new PlayerGameState(gamestate_head, cycle_head, playerName), m_winGameGoal);                              
/*
                PlanBaseEntry pbe = new PlanBaseEntry(pnp, m_winGameGoal, gamestate_head, cycle_head, playerName);
                HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

                // Set only one entry point, which is the beginning of the petri net:
                tokensInStates.put(petrinet_head.getElementID(), 1);
                pbe.setEntryPoint(tokensInStates);
*/
                learntPlans.add(pbe);            
            }
        }while(idx<entry_list.size());
       

        return learntPlans;
//        new PlanVisualizer(pnp, 800,600, 0, petrinet_head);
    }
}
