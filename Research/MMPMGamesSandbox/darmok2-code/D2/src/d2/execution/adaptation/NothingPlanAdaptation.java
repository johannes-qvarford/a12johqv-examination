/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation;

import org.jdom.Element;

import d2.execution.planbase.PlanBase;
import d2.plans.Plan;

import gatech.mmpm.GameState;
import gatech.mmpm.Trace;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.tracer.TraceParser;
import gatech.mmpm.util.XMLWriter;
import java.util.List;

public class NothingPlanAdaptation extends PlanAdaptation {

	public NothingPlanAdaptation() {
		super();
	}

	public Plan adapt(Plan original_plan, GameState original_gs,String original_player,
			Sensor original_goal, int current_cycle, GameState current_gs, String current_player, 
			Sensor current_goal,List<String> usedIDs) {

		return original_plan;
	}
	
	public void adaptToAllow(Plan original_plan, GameState original_gs, String original_player,
                             Plan planToAllow, int current_cycle, GameState current_gs, String current_player,
                             PlanBase pb, PlanAdaptation parameterAdapter,List<String> usedIDs) {
		
	}


	public Trace getTrace(String traceName) {

		Trace temp = TraceParser.parse(traceName, d2.core.Config.getDomain());

		return temp;
	}


	public void saveToXML(XMLWriter w) {
		w.tag("type", this.getClass().getName());
	}

	public static PlanAdaptation loadFromXMLInternal(Element xml) {
		return new NothingPlanAdaptation();
	}

}
