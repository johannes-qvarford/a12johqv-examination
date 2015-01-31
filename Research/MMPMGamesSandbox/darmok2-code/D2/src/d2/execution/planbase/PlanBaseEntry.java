/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.planbase;

import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import gatech.mmpm.Context;


/*
 * Every member is public since this class is just a storage class with no functionality 
 */

public class PlanBaseEntry {
    
        public String m_ID;                 // This ID is used to allow a PlanBaseEntry to refer to another one (e.g. for temporal backtracking retrieval)
        public String m_previous_ID;  // ID of the PlanBaseEntry immediately before this one in the learning trace
	public PetriNetPlan m_plan;
	public Sensor m_goal;		// This is the goal that was observed from the trace
                                        // from were this plan was learned.
	public String m_originalPlayer;
        public int m_cycle;
	public GameState m_gameState;
	public HashMap<String,Integer> m_entryPoint;	// The entry point stores the configuration in 
                                                        // which the tokens are when the plan is to be
                                                        // started.

	public List<PlanBaseEpisode> m_episodes;

	public PlanBaseEntry(PetriNetPlan p,Sensor g, GameState gs, int cycle, String originalPlayer) {
                m_ID = null;
                m_previous_ID = null;
		m_plan = p;
		m_goal = g;
		m_originalPlayer = originalPlayer;
                m_cycle = cycle;
		m_gameState = gs;
		m_entryPoint = null;
		m_episodes = new LinkedList<PlanBaseEpisode>();
	}

	public PlanBaseEntry(PetriNetPlan p,Sensor g, GameState gs, int cycle, String originalPlayer, String ID) {
                m_ID = ID;
                m_previous_ID = null;
		m_plan = p;
		m_goal = g;
		m_originalPlayer = originalPlayer;
                m_cycle = cycle;
		m_gameState = gs;
		m_entryPoint = null;
		m_episodes = new LinkedList<PlanBaseEpisode>();
	}

	public PlanBaseEntry(PetriNetPlan p,Sensor g, GameState gs, int cycle, String originalPlayer, String ID, String previous_ID) {
                m_ID = ID;
                m_previous_ID = previous_ID;
		m_plan = p;
		m_goal = g;
		m_originalPlayer = originalPlayer;
                m_cycle = cycle;
		m_gameState = gs;
		m_entryPoint = null;
		m_episodes = new LinkedList<PlanBaseEpisode>();
	}
        
        
        public void setEntryPoint(HashMap<String,Integer> tokensInStates) {
		m_entryPoint = tokensInStates;
	}

	public void saveToXML(XMLWriter w) throws IOException {
		w.tag("PlanBaseEntry");
		if (m_ID!=null) w.tag("ID",m_ID);
		if (m_previous_ID!=null) w.tag("previous_ID",m_previous_ID);
		w.tag("original-player",m_originalPlayer);
		w.tag("cycle",m_cycle);
		m_gameState.writeToXML(w);
		m_plan.writeToXMLDifference("",m_gameState,w);
		w.tag("goal");
		m_goal.writeToXML(w);
		w.tag("/goal");
		w.tag("entry-point");
		for(String id:m_entryPoint.keySet()) {
			w.tag("state");
			w.tag("id",id);
			w.tag("tokens",m_entryPoint.get(id));
			w.tag("/state");
		}
		w.tag("/entry-point");
		w.tag("/PlanBaseEntry");
	}

	public static PlanBaseEntry loadFromXML(Element xml,String domain) {
                String ID = xml.getChildText("ID");
                String previous_ID = xml.getChildText("previous_ID");
		String originalPlayer = xml.getChildText("original-player");
		int cycle = Integer.parseInt(xml.getChildText("cycle"));
		GameState gs = GameState.loadFromXML(xml.getChild("gamestate"),d2.core.Config.getDomain());
		PetriNetPlan plan = (PetriNetPlan) Plan.loadFromXML(xml.getChild("plan"),gs);
		Sensor goal = Sensor.loadFromXML(xml.getChild("goal").getChild("Sensor"));
		HashMap<String,Integer> entryPoint = new HashMap<String,Integer>();

		{
			Element ep_element = xml.getChild("entry-point");
			List l = ep_element.getChildren();
			for(Object o:l) {
				Element e = (Element)o;
				entryPoint.put(e.getChildText("id"), Integer.parseInt(e.getChildText("tokens")));
			}
		}

		PlanBaseEntry ret = new PlanBaseEntry(plan,goal,gs, cycle, originalPlayer, ID, previous_ID);
		ret.setEntryPoint(entryPoint);

		return ret;
	}
        
    public GameStateFeatures getGameStateFeatures() {
        GameStateFeatures gsf = (GameStateFeatures) m_gameState.getMetaData("GameStateFeatures");
        if (gsf == null) {
            gsf = new GameStateFeatures(m_cycle, m_gameState, m_originalPlayer);
            m_gameState.addMetaData("GameStateFeatures", gsf);
        }
        return gsf;
    }
        
}
