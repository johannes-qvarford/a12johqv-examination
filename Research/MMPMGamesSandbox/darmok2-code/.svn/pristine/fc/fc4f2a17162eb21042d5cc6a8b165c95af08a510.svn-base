/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.learn.planlearning;

import d2.execution.planbase.GameStateSimilarity;
import org.jdom.Element;

import d2.execution.planbase.PlanBase;

import gatech.mmpm.ActionParameterType;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

public class GameStateComparisonCondition extends Sensor {

	GameState gs1;
	GameState gs2;
	
	public GameStateComparisonCondition() {
		gs1 = null;
		gs2 = null;
	}

	public GameStateComparisonCondition(GameState a_gs1,GameState a_gs2) {
		gs1 = a_gs1;
		gs2 = a_gs2;
	}
	
	public Object clone() {
		return new GameStateComparisonCondition(gs1,gs2);
	}

	public Object evaluate(int cycle, GameState gs, String player, gatech.mmpm.Context parameters) {
		
		if (GameStateSimilarity.gameStateSimilarity(gs, gs1, cycle, cycle, player,player,null)>=GameStateSimilarity.gameStateSimilarity(gs, gs2, cycle,cycle, player,player,null)) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}
	
	public ActionParameterType getType() {
		return ActionParameterType.BOOLEAN;
	}
	
	public void writeToXML(XMLWriter w) {
		w.tagWithAttributes("Sensor","type = \"" + this.getClass().getName() + "\"");
		if (gs1!=null) {
			w.tagWithAttributes("parameter", "name = \"gs1\"");
			gs1.writeToXML(w);
			w.tag("/parameter");
		}
		if (gs2!=null) {
			w.tagWithAttributes("parameter", "name = \"gs2\"");
			gs2.writeToXML(w);
			w.tag("/parameter");
		}
		w.tag("/Sensor");
	}
	
	public static Sensor loadFromXMLInternal(Element xml) {		
		GameStateComparisonCondition cond = new GameStateComparisonCondition();
		
		for(Object o:xml.getChildren("parameter")) {
			Element e = (Element)o;
			if (e.getAttributeValue("name").equals("gs1")) {
				cond.gs1 = GameState.loadFromXML(e.getChild("gamestate"), d2.core.Config.getDomain());
			}
			if (e.getAttributeValue("name").equals("gs2")) {
				cond.gs2 = GameState.loadFromXML(e.getChild("gamestate"), d2.core.Config.getDomain());
			}
		}
		
		return cond;
	}


}
