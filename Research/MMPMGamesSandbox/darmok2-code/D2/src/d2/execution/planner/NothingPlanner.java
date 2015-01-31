/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.planner;

import d2.core.D2;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.adaptation.PlanAdaptation;
import d2.execution.planbase.PlanBase;
import d2.execution.planexecution.PlanExecution;
import d2.plans.Plan;
import d2.worldmodel.WorldModel;

import gatech.mmpm.GameState;
import gatech.mmpm.util.XMLWriter;

public class NothingPlanner extends Planner {

	public NothingPlanner(Plan a_plan,D2 d2, String player, PlanExecution pe, PlanAdaptation pa) {
		super(a_plan,d2,player,pe,pa);
	}

	public NothingPlanner(List<Plan> a_plans, D2 d2, String player, PlanExecution pe, PlanAdaptation pa) {
		super(a_plans,d2,player,pe,pa);
	}

	public boolean expandPlan(Plan p, int cycle, GameState gs, String player) {
		return false;
	}
	
	public void saveToXML(XMLWriter w) {
		w.tag("Planner");
		w.tag("type",this.getClass().getName());
		w.tag("plan-execution");
		m_planExecution.saveToXML(w);
		w.tag("/plan-execution");
		w.tag("plan-adaptation");
		m_planAdaptation.saveToXML(w);
		w.tag("/plan-adaptation");
		w.tag("/Planner");
	}
	
	public static Planner loadFromXMLInternal(Element xml,D2 d2,String player) {
		PlanExecution pe = null;
		PlanAdaptation pa = null;
		pe = PlanExecution.loadFromXML(xml.getChild("plan-execution"),player, d2);
		pa = PlanAdaptation.loadFromXML(xml.getChild("plan-adaptation"));
		return new NothingPlanner(new LinkedList<Plan>(),d2, player,pe,pa);
	}


}
