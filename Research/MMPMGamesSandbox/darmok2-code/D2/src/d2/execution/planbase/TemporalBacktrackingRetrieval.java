/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.planbase;

import d2.core.D2;
import d2.learn.plansimilarity.EditDistancePlanSimilarity;
import d2.learn.plansimilarity.PlanSimilarity;
import d2.plans.*;
import gatech.mmpm.*;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jdom.Element;

public class TemporalBacktrackingRetrieval extends SimilarityRetrieval {
    static public int DEBUG = 0;
    static public double actionSimilarityThreshold = 0.5;
    static public int backtrackLimit = 10;
    static public double currentStateThreshold = 0.9;
    static public double pastActionThreshold = 0.5;
    static public double pastStateThreshold = 0.7;
    EditDistancePlanSimilarity ps = new EditDistancePlanSimilarity();

    // statistics:
    static public int nTimesOutofThreshold = 0;
    static public int maxBacktracking = 0;
    static public int averageBacktracking = 0;
    static public int totalretrievals = 0;
    
    public TemporalBacktrackingRetrieval(D2 d2) {
        super(d2);
    }

    public void saveToXML(XMLWriter w) throws IOException {
        w.tag("PlanBase");
        w.tag("type", this.getClass().getSimpleName());
        w.tag("plans");
        for (PlanBaseEntry pbe : m_plans) {
            pbe.saveToXML(w);
        }
        w.tag("/plans");
        w.tag("/PlanBase");
    }

    public static PlanBase loadFromXMLInternal(Element xml, String domain, D2 d2) {
        TemporalBacktrackingRetrieval ret = new TemporalBacktrackingRetrieval(d2);

        Element plans = xml.getChild("plans");
        List plan_l = plans.getChildren();
        for (Object o : plan_l) {
            Element plan_e = (Element) o;
            ret.addPlan(PlanBaseEntry.loadFromXML(plan_e, domain));
        }

        return ret;
    }
    
    
    /*
     * if "stateOrAction"==0 uses state
     * if "stateOrAction"==1 uses action
     * 
     * actions can only be compared for past states, not for current state
     */
    public double similarityWithTemporalBackTracking(PlanBaseEntry pbe, GameState gs, int cycle, String player, int stepsBack, int stateOrAction) 
    {
        List<PlanBaseEntry> case_list = new LinkedList<PlanBaseEntry>();
        List<PlayerGameState> current_list = new LinkedList<PlayerGameState>();
        List<List<Plan>> current_actions_list = new LinkedList<List<Plan>>();
        
        case_list.add(pbe);
        current_list.add(new PlayerGameState(gs, cycle, player));
        current_actions_list.add(null);
        
        PlanBaseEntry pbe_tmp = pbe;
        Trace trace = m_d2.getTrace();
        for(int i = 0;i<stepsBack;i++) {
            if (pbe_tmp!=null) {
                pbe_tmp = getPlan(pbe_tmp.m_previous_ID);
                if (pbe_tmp!=null) {
                    case_list.add(pbe_tmp);
                }
            }
            
            int idx = (trace.getEntries().size()-1)-i;
            if (idx>=0) {
                Entry te = trace.getEntries().get(idx);
                current_list.add(new PlayerGameState(te.getGameState(),te.getTimeStamp(), player));
                List<Plan> tmp = new LinkedList<Plan>();
                for(Action a:te.getActions()) tmp.add(new ActionPlan(a));
                current_actions_list.add(tmp);
            }
        }
        
        if (DEBUG>=2 && stepsBack>0) System.out.println("backtracking similarity with traces of sizes " + case_list.size() + " " + current_list.size());
        if (stateOrAction==0) {
            if (case_list.size()>stepsBack && current_list.size()>stepsBack) {
                double sim = SimilarityRetrieval.similarity(case_list.get(stepsBack), 
                                                            current_list.get(stepsBack).gs, current_list.get(stepsBack).cycle, player,
                                                            featureStats );
                if (DEBUG>=2 && stepsBack>0) System.out.println("sim = " + sim);
                if (sim == 1.0) {
                    System.out.println("*** Case game state:");
                    System.out.println(case_list.get(stepsBack).m_gameState.toString());
                    System.out.println("*** Current game state:");
                    System.out.println(current_list.get(stepsBack).gs.toString());
                }
                return sim;
            } else {
                return 0;
            }
        } else {
            // compare actions:
            if (case_list.size()>stepsBack && current_list.size()>stepsBack) {
                PetriNetPlan pnp = case_list.get(stepsBack).m_plan;
                List<Plan> subplans = new ArrayList<Plan>();
                for(PlanState ps:pnp.getAllPlanStates()) subplans.add(ps.getPlan());
                double sim = ps.similarity(subplans, current_actions_list.get(stepsBack));
                if (DEBUG>=2 && stepsBack>0) System.out.println("sim (action) = " + sim);
                return sim;
            } else {
                return 0;
            }
        }
    }
    

    /*
     * This method actually ignores 'N', since Floyd's temporal backtracking is designed for single case retrieval
     */
    public List<Plan> retrieveNPlans(Sensor g, int cycle, GameState gs, String player, int N) {
        int stepsBack = 0;
        int maxPoolSize = 32;
        int maxStepsBack = Math.min(m_d2.getTrace().getEntries().size(),backtrackLimit);
        boolean disagreement = true;

        if (DEBUG >= 1) System.out.println("Inside TemporalBacktrackingRetrieval.retrieveNPlans: " + m_plans.size() + " planBaseEntries N = " + N + " maxStepsBack = " + maxStepsBack);

        List<Pair<PlanBaseEntry, Double>> bestPlans = new ArrayList<Pair<PlanBaseEntry, Double>>();
               
        do{
            PlanBaseEntry bestSoFar = null;
            double bestSimilaritySoFar = 0;
            disagreement = false;
            if (DEBUG >= 1) System.out.println("Inside TemporalBacktrackingRetrieval.retrieveNPlans: backtracking level:" + stepsBack);

            if (stepsBack==0) {
                double similarity;
                // Retrieve an initial pool of plans:
                for (PlanBaseEntry pbe : m_plans) {
                    double match = m_d2.getWorldModel().getConditionMatcher().match((Context) null, pbe.m_goal, cycle, gs, player,
                        null, g, 0, pbe.m_gameState, pbe.m_originalPlayer);
                    if (DEBUG >= 2) System.out.println("matching: " + g + " with " + pbe.m_goal + " -> " + match);
                    if (match > 0.0) {
                        similarity = similarityWithTemporalBackTracking(pbe, gs, cycle, player, stepsBack, 0);
                        if (similarity>=currentStateThreshold) addPlan(pbe, similarity, bestPlans, maxPoolSize);
                        if (bestSoFar==null || similarity>bestSimilaritySoFar) {
                            bestSoFar = pbe;
                            bestSimilaritySoFar = similarity;
                        }
                    }
                }
            } else {
                // Reorder the current set of plans:
                List<Pair<PlanBaseEntry, Double>> bestPlans2 = new ArrayList<Pair<PlanBaseEntry, Double>>();
                double similarity;
                // Retrieve an initial pool of plans:
                for (Pair<PlanBaseEntry, Double> tmp : bestPlans) {
                    if (stepsBack%2==0) {
                        similarity = similarityWithTemporalBackTracking(tmp._a, gs, cycle, player, (stepsBack+1)/2, 0);
                        if (similarity>=pastStateThreshold) addPlan(tmp._a, similarity, bestPlans2, maxPoolSize);
                        if (bestSoFar==null || similarity>bestSimilaritySoFar) {
                            bestSoFar = tmp._a;
                            bestSimilaritySoFar = similarity;
                        }
                    } else {
                        similarity = similarityWithTemporalBackTracking(tmp._a, gs, cycle, player, (stepsBack+1)/2, 1);
                        if (similarity>=pastActionThreshold) addPlan(tmp._a, similarity, bestPlans2, maxPoolSize);
                        if (bestSoFar==null || similarity>bestSimilaritySoFar) {
                            bestSoFar = tmp._a;
                            bestSimilaritySoFar = similarity;
                        }
                    }
                }                
                bestPlans = bestPlans2;
            }
            
//            System.out.println(stepsBack + " -> " + bestPlans.size());
            
            if (bestPlans.isEmpty()) {
                if (stepsBack==0) nTimesOutofThreshold++;
                
                bestPlans.add(new Pair<PlanBaseEntry, Double>(bestSoFar, bestSimilaritySoFar));
                break;
            }
            
            if (DEBUG>=1) System.out.println("Retrieved " + bestPlans.size() + " plans");
            // Check if the actions agree:
            double minSimilarity = 1.0;
            for(int i = 0;i<bestPlans.size() && i<bestPlans.size();i++) {
                for(int j = 0;j<bestPlans.size() && j<bestPlans.size();j++) {
                    double sim = ps.similarity(bestPlans.get(i)._a.m_plan, bestPlans.get(j)._a.m_plan);
                    if (sim<minSimilarity) minSimilarity = sim;
                    if (DEBUG>=1) System.out.print(sim + "  ");
                }
                if (DEBUG>=1) System.out.println("");
            }

            if (minSimilarity<actionSimilarityThreshold) {
                if (DEBUG>=1) System.out.println("Actions are dissimilar enough (" + minSimilarity + "). Backtracking!");
                disagreement = true;
                stepsBack++;
            }
            
        }while(disagreement && stepsBack<=maxStepsBack);
        
        if (stepsBack>maxBacktracking) maxBacktracking = stepsBack;
        averageBacktracking+=stepsBack;
        totalretrievals++;
        
        List<Plan> returnedPlansList = new ArrayList<Plan>();
        for (int i = 0;i<N && i<bestPlans.size();i++) {
            Pair<PlanBaseEntry, Double> bPlan = bestPlans.get(i);
            PetriNetPlan clone = (PetriNetPlan) (bPlan._a.m_plan.clone());

            // Apply entry point:
            for (String s : bPlan._a.m_entryPoint.keySet()) {
                State state = (State) clone.getPetriNetElement(s);
                state.setCurrentNumberOfTokens(bPlan._a.m_entryPoint.get(s));
            }
            if (DEBUG >= 1) {
                System.out.println("TemporalBacktrackingRetrieval bestPlan (" + bPlan._a.m_ID + " -> " + bPlan._a.m_previous_ID + ") with similarity " + bPlan._b);
                System.out.println(bPlan._a.m_plan);
            }

            returnedPlansList.add(clone);
        }

        return returnedPlansList;
    }
        

}
