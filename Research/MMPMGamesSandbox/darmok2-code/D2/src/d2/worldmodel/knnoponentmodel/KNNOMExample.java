/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.knnoponentmodel;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.planbase.GameStateFeatures;

import gatech.mmpm.Action;
import gatech.mmpm.util.XMLWriter;

public class KNNOMExample {
	public List<Action> m_actions;
	public GameStateFeatures m_features;
	
	public KNNOMExample(GameStateFeatures gsf,List<Action> actions) {
		m_features = gsf;
		m_actions = actions;
	}
	
	public String toString() {
		String features = m_features.toString();
		String actions = m_actions.toString();
		return features + " - " + actions;
	}
	
	public void savetoXML(XMLWriter w) {
		w.tag("KNNOMExample");
		for(Action a:m_actions) a.writeToXML(w);
		m_features.writeToXMLOnlyFeatures(w);
		w.tag("/KNNOMExample");
	}

	public void saveDifferenceToXML(XMLWriter w, KNNOMExample prev_example) {
		w.tag("KNNOMExample");
		for(Action a:m_actions) a.writeToXML(w);
		m_features.writeDifferenceToXML(w,prev_example.getM_features());
		w.tag("/KNNOMExample");
	}

	public static KNNOMExample loadfromXML(Element e) {
		List<Action> actions = new LinkedList<Action>();
		GameStateFeatures features = null;

		for(Object action_o:e.getChildren("Action")) {
			Action a = Action.loadFromXML((Element)action_o);
			actions.add(a);			
		}
		
		features = GameStateFeatures.loadFromXML(e.getChild("GameStateFeatures"));
		
		return new KNNOMExample(features, actions);
	}
	
	public static KNNOMExample loadDifferenceFromXML(Element e, KNNOMExample prev_ex) {
		List<Action> actions = new LinkedList<Action>();
		GameStateFeatures features = null;

		for(Object action_o:e.getChildren("Action")) {
			Action a = Action.loadFromXML((Element)action_o);
			actions.add(a);			
		}
		
		features = GameStateFeatures.loadDifferenceFromXML(e.getChild("GameStateFeatures"),prev_ex.getM_features());
		
		return new KNNOMExample(features, actions);
	}


	public GameStateFeatures getM_features() {
		return m_features;
	}
	
}
