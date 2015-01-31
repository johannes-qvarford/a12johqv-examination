/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.planbase;

import d2.core.D2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.worldmodel.ConditionMatcher;
import d2.plans.ActionPlan;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.State;
import gatech.mmpm.Context;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.composite.Invocation;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;
import java.util.Random;

public class SimilarityRetrieval extends PlanBase {
    static public int DEBUG = 0;
    Random r = new Random();

    public SimilarityRetrieval(D2 d2) {
        super(d2);
    }

    public void setWeights(HashMap<String,FeatureStats> a_weights) {
        featureStats = a_weights;
    }
    
    public Plan retrievePlan(Sensor g, int cycle, GameState gs, String player) {
        List<Plan> l = retrieveNPlans(g, cycle, gs, player, 1);
        if (l.isEmpty()) return null;
        return l.get(0);
    }

    
    public List<Plan> retrieveNPlans(Sensor g, int cycle, GameState gs, String player, int N) {
        if (DEBUG >= 1) {
            System.out.println("Inside SimilarityRetrieval:retrieveNPlans: " + m_plans.size() + " planBaseEntries");
        }
        List<Pair<PlanBaseEntry, Double>> bestPlans = new ArrayList<Pair<PlanBaseEntry, Double>>();

        double similarity;
       
        if (reasoningTraceWriter!=null) {
            reasoningTraceWriter.tagWithAttributes("SimilarityRetrieval.retrievePlan","cycle = \"" + cycle + "\" , goal = \"" + g.toSimpleString() + "\"");
            reasoningTraceWriter.tag("planLibrarySize", m_plans.size());
        }        

        for (PlanBaseEntry pbe : m_plans) {
            double match = m_d2.getWorldModel().getConditionMatcher().match((Context) null, pbe.m_goal, cycle, gs, player,
                null, g, 0, pbe.m_gameState, pbe.m_originalPlayer);
            if (DEBUG >= 2) System.out.println("matching: " + g + " with " + pbe.m_goal + " -> " + match);
            if (match > 0.0) {
                similarity = similarity(pbe, gs, cycle, player, featureStats);
                addPlan(pbe, similarity, bestPlans, N);
                
                if (reasoningTraceWriter!=null) {
                    reasoningTraceWriter.rawXML("<similarity id = '" + pbe.m_ID + "' match = '" + match + "' gs = '" + similarity + "'\\>");
                }
            } else {
                if (reasoningTraceWriter!=null) {
                    reasoningTraceWriter.rawXML("<similarity id = '" + pbe.m_ID + "' match = '" + match + "'\\>");
                }  
                
            }
        }
        List<Plan> returnedPlansList = new ArrayList<Plan>();

        if (DEBUG >= 2) {
            System.out.println("Sorted list of plans:");
            for (Pair<PlanBaseEntry, Double> bPlan : bestPlans) {
                System.out.println(bPlan._b);
            }
        }
/*
        // These debug statements is only for the SingleActionsPlanLearning module:
        System.out.println("Sorted list of plans:");
        for (Pair<PlanBaseEntry, Double> bPlan : bestPlans) {
            System.out.println(bPlan._b + " -> " + ((ActionPlan)(((PetriNetPlan)(bPlan._a.m_plan)).getAllPlanStates().get(0).getPlan())).getAction().toSimpleString());
        }
*/

        for (Pair<PlanBaseEntry, Double> bPlan : bestPlans) {
            //Clone the plan
            PetriNetPlan clone = (PetriNetPlan) (bPlan._a.m_plan.clone());

            // Apply entry point:
            for (String s : bPlan._a.m_entryPoint.keySet()) {
                State state = (State) clone.getPetriNetElement(s);
                state.setCurrentNumberOfTokens(bPlan._a.m_entryPoint.get(s));
            }
            
            if (DEBUG >= 1) System.out.println("retrieveNPlans: bestPlan with similarity " + bPlan._b);

            returnedPlansList.add(clone);
        }

        if (reasoningTraceWriter!=null) {
            for (Pair<PlanBaseEntry, Double> bPlan : bestPlans) {
                reasoningTraceWriter.tagWithAttributes("retrievedPlan","id = \"" + bPlan._a.m_ID + "\"");
                reasoningTraceWriter.tag("match",bPlan._b);
                reasoningTraceWriter.tag("/retrievedPlan");
            }
            reasoningTraceWriter.tag("/SimilarityRetrieval.retrieveNPlans");
            reasoningTraceWriter.flush();
        }

        return returnedPlansList;
    }
    
    
    public void addPlan(PlanBaseEntry pbe, double similarity, List<Pair<PlanBaseEntry, Double>> current, int N) {
        if (current.isEmpty()) {
            current.add(new Pair<PlanBaseEntry, Double>(pbe, similarity));
        } else {
            for(int i = 0;i<current.size();i++) {
                Pair<PlanBaseEntry, Double> tmp = current.get(i);
                if (similarity>=tmp._b) {
                    if (Math.abs(similarity - tmp._b)<0.000001) {   // if they are identical, roll a dice
                        if (r.nextBoolean()) {
                            current.add(i, new Pair<PlanBaseEntry, Double>(pbe, similarity));
                            while(current.size()>N) current.remove(N);
                            return;
                        }
                    } else {
                        current.add(i, new Pair<PlanBaseEntry, Double>(pbe, similarity));
                        while(current.size()>N) current.remove(N);
                        return;
                    }
                }
            }
            if (current.size()<N) current.add(new Pair<PlanBaseEntry, Double>(pbe, similarity));
            return;
        }
    }
    
    
    public static double similarity(PlanBaseEntry pbe, GameState gs, int cycle, String player, HashMap<String,FeatureStats> weights) {
        GameStateFeatures gsf_cb = pbe.getGameStateFeatures();
        GameStateFeatures gsf = (GameStateFeatures) gs.getMetaData("GameStateFeatures");
        if (gsf == null) {
            gsf = new GameStateFeatures(cycle, gs, player);
            gs.addMetaData("GameStateFeatures", gsf);
        }
        double similarity = GameStateSimilarity.gameStateSimilarity(gsf_cb, gsf, weights);
        
        if (DEBUG >= 2) System.out.println("SimilarityRetrieval: similarity " + similarity);

        return similarity;
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
        SimilarityRetrieval ret = new SimilarityRetrieval(d2);

        Element plans = xml.getChild("plans");
        List plan_l = plans.getChildren();
        for (Object o : plan_l) {
            Element plan_e = (Element) o;
            ret.m_plans.add(PlanBaseEntry.loadFromXML(plan_e, domain));
        }


        return ret;
    }

    // The action is assumed to be retrieved to satisfy a particular Sensor "g" with is a precondition of "target"
    public ActionPlan retrieveAction(Sensor raw_g, Plan target, int cycle, GameState gs, String player) {
        List<ActionPlan> actions = getAllActions();

        if (DEBUG >= 1) System.out.println("retrieveAction: candidate actions: " + m_allActions.size());
        Sensor g = raw_g;
        if (g instanceof Invocation) {
            g = ((Invocation) raw_g).getSensor();
        }

        ActionPlan best = null;
        double best_score = 0;
        GameStateFeatures gsf = new GameStateFeatures(cycle, gs, player);
        GameStateFeatures gsf_cb = null;

        double match;
        double similarity;
        double score;
        
        if (reasoningTraceWriter!=null) {
            reasoningTraceWriter.tagWithAttributes("SimilarityRetrieval.retrieveAction","cycle = \"" + cycle + "\" , goal = \"" + raw_g.toSimpleString() + "\"");
            reasoningTraceWriter.tag("planLibrarySize", m_plans.size());
        }        

        for (ActionPlan a : actions) {
            List<Sensor> cl = new LinkedList<Sensor>();
            d2.execution.adaptation.ExpandConditionMatcher.expandCondition(a.getPostCondition(), cl);
            d2.execution.adaptation.ExpandConditionMatcher.expandCondition(a.getSuccessCondition(), cl);
            if (DEBUG >= 1) System.out.println("Action: " + a);
            for (Sensor c : cl) {
                if (DEBUG >= 1) System.out.println("Matching: " + c + " vs " + g);
                match = m_d2.getWorldModel().getConditionMatcher().match(a, c, 0, a.getOriginalGameState(), a.getOriginalPlayer(),
                                                                         target, g, cycle, gs, player);
                if (match > 0.0) {
                    gsf_cb = (GameStateFeatures) a.getOriginalGameState().getMetaData("GameStateFeatures");
                    if (gsf_cb == null) {
                        gsf_cb = new GameStateFeatures(cycle, a.getOriginalGameState(), a.getOriginalPlayer());
                        a.getOriginalGameState().addMetaData("GameStateFeatures", gsf_cb);
                    }
                    similarity = GameStateSimilarity.gameStateSimilarity(gsf_cb, gsf, null);
                    score = 0.33 * similarity + 0.67 * match;
                    if (DEBUG >= 1) System.out.println("Match!!!!: " + match + " " + similarity + " -> " + score);
                    if (best == null || score > best_score) {
                        best = a;
                        best_score = score;
                    }
                if (reasoningTraceWriter!=null) {
                    reasoningTraceWriter.tag("similarity");
                    reasoningTraceWriter.tag("match",match);
                    reasoningTraceWriter.tag("gs",similarity);
                    reasoningTraceWriter.tag("/similarity");
                }
            } else {
                if (reasoningTraceWriter!=null) {
                    reasoningTraceWriter.tag("similarity");
                    reasoningTraceWriter.tag("match",match);
                    reasoningTraceWriter.tag("/similarity");
                }                  }
            }
        }

        if (reasoningTraceWriter!=null) {
            reasoningTraceWriter.tag("retrievedAction");
            reasoningTraceWriter.tag("match",best_score);
            reasoningTraceWriter.tag("/retrievedAction");
            reasoningTraceWriter.tag("/SimilarityRetrieval.retrieveAction");
            reasoningTraceWriter.flush();
        }

        return best;
    }
}
