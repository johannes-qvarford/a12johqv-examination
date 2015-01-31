/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.planbase;

import d2.core.D2;
import d2.core.D2Module;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import d2.plans.ActionPlan;
import d2.plans.Plan;
import d2.plans.PlanState;

public abstract class PlanBase extends D2Module {

    D2 m_d2;
    protected List<PlanBaseEntry> m_plans;
    protected HashMap<String, PlanBaseEntry> m_plansByID;
    List<ActionPlan> m_allActions = null;
    HashMap<String,FeatureStats> featureStats = null;
    static public int DEBUG = 0;

    public PlanBase(D2 d2) {
        m_d2 = d2;
        m_plans = new LinkedList<PlanBaseEntry>();
        m_plansByID = new HashMap<String, PlanBaseEntry>();
    }

    public void setD2(D2 d2) {
        m_d2 = d2;
    }

    public D2 getD2() {
        return m_d2;
    }

    public void addPlan(PlanBaseEntry p) {
        m_plans.add(p);
        if (p.m_ID != null) {
            m_plansByID.put(p.m_ID, p);
        }
    }

    public void addPlans(List<PlanBaseEntry> l) {
        for (PlanBaseEntry pbe : l) {
            addPlan(pbe);
        }
    }

    public PlanBaseEntry getPlan(int index) {
        return m_plans.get(index);
    }

    public PlanBaseEntry getPlan(String ID) {
        return m_plansByID.get(ID);
    }

    public String toString() {
        return "PlanBase, size = " + m_plans.size() + ".";
    }

    public abstract Plan retrievePlan(Sensor g, int cycle, GameState gs, String player);

    public abstract List<Plan> retrieveNPlans(Sensor g, int cycle, GameState gs, String player, int N);

    // This method goes over all the actions in the plans in the base, and tries to find one that
    // individually satisfied a particular goal.
    public abstract ActionPlan retrieveAction(Sensor g, Plan target, int cycle, GameState gs, String player);

    public List<Plan> getPlans() {
        List<Plan> l = new LinkedList<Plan>();
        for (PlanBaseEntry pbe : m_plans) {
            l.add(pbe.m_plan);
        }
        return l;
    }
    
    public List<PlanBaseEntry> getPlanBaseEntries() {
        return m_plans;
    }
    

    public abstract void saveToXML(XMLWriter w) throws IOException;

    public static PlanBase loadFromXML(Element xml, String domain, D2 d2) {
        String classString = xml.getChildText("type");

        // TODO: this is a hack, find a better way:
        if (classString.equals("SimilarityRetrieval")) {
            return SimilarityRetrieval.loadFromXMLInternal(xml, domain, d2);
        } else if (classString.equals("TemporalBacktrackingRetrieval")) {
            return TemporalBacktrackingRetrieval.loadFromXMLInternal(xml, domain, d2);
        }

        return null;
    }

    public List<ActionPlan> getAllActions() {
        if (m_allActions == null) {
            m_allActions = new LinkedList<ActionPlan>();
            for (PlanBaseEntry pbe : m_plans) {
                for (PlanState ps : pbe.m_plan.getAllPlanStates()) {
                    if (ps.getPlan() instanceof ActionPlan) {
                        m_allActions.add((ActionPlan) (ps.getPlan()));
                    }
                }
            }
        }
        return m_allActions;
    }
    
    
    public HashMap<String, FeatureStats> analyzeFeatures() {
        featureStats = new HashMap<String, FeatureStats>();
        
        for (PlanBaseEntry pbe : m_plans) {
            GameStateFeatures gsf_cb = pbe.getGameStateFeatures();
            for(String feature:gsf_cb.getFeatureNames()) {
                Double v = gsf_cb.getFeature(feature);
                FeatureStats s = featureStats.get(feature);
                if (s==null) s = new FeatureStats();
                s.update(v);
                featureStats.put(feature,s);
            }        
        }
        
        if (DEBUG>=1) {
            for(String feature:featureStats.keySet()) {
                FeatureStats s = featureStats.get(feature);
                System.out.println(feature + ": [" + s.min + "," + s.max + "]");
            }
        }
        
        return featureStats;
    }
}
