/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.plans;

import d2.execution.planbase.PlayerGameState;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;

import org.jdom.Element;


public class GoalPlan extends Plan 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5035152012232126763L;
	private Plan expandedGoalPlan = null; 
	
	private Sensor PreCondition;
	private Sensor PreFailureCondition;
	private Sensor SuccessCondition;
	private Sensor FailureCondition;
	private Sensor PostCondition;


	public GoalPlan() {
		expandedGoalPlan = null;

		PreCondition = new True();
		PreFailureCondition = new False();
		SuccessCondition = new True();
		FailureCondition = new False();
		PostCondition = new True();
	}

	public GoalPlan(Sensor g) {
		expandedGoalPlan = null;

		PreCondition = new True();
		PreFailureCondition = new False();
		SuccessCondition = g;
		FailureCondition = new False();
		PostCondition = new True();
	}

	public GoalPlan(Sensor g,Plan ep) {
		expandedGoalPlan = ep;

		PreCondition = new True();
		PreFailureCondition = new False();
		SuccessCondition = g;
		FailureCondition = new False();
		PostCondition = new True();
	}

	public Plan getExpandedGoalPlan() {
		return expandedGoalPlan;
	}

	public void setExpandedGoalPlan(Plan expandedGoalPlan) {
		this.expandedGoalPlan = expandedGoalPlan;
	}

	public Sensor getGoal() {
		return SuccessCondition;
	}

	public void setGoal(Sensor goal) {
		this.setSuccessCondition(goal);
	}
		
	public void writeToXML(String planID, XMLWriter w) {
		w.tagWithAttributes("plan","id = '" + planID + "' type='" + this.getClass().getSimpleName() + "'>");
		w.tag("conditions");
		w.tag("preCondition");
		getPreCondition().writeToXML(w);
		w.tag("/preCondition");

		w.tag("successCondition");
		getSuccessCondition().writeToXML(w);
		w.tag("/successCondition");

		w.tag("failureCondition");
		getFailureCondition().writeToXML(w);
		w.tag("/failureCondition");
		w.tag("/conditions");

		if (expandedGoalPlan!=null) {
			this.getExpandedGoalPlan().writeToXML("",w);
		}

		if (m_originalGameState!=null) {
			m_originalGameState.writeToXML(w);
		}
        if (m_originalPlayer!=null) {
            w.tag("player", m_originalPlayer);
        }

		w.tag("/plan");
	}


	public static Plan loadFromXMLInternal(Element xml, GameState referenceGameState) {
		GoalPlan ret = new GoalPlan();

		Element conds_e = xml.getChild("conditions");
		Element pc_e = conds_e.getChild("preCondition");
		ret.setPreCondition(Sensor.loadFromXML(pc_e.getChild("Sensor")));
		Element sc_e = conds_e.getChild("successCondition");
		ret.setSuccessCondition(Sensor.loadFromXML(sc_e.getChild("Sensor")));
		Element fc_e = conds_e.getChild("failureCondition");
		ret.setFailureCondition(Sensor.loadFromXML(fc_e.getChild("Sensor")));

		Element egp_e = xml.getChild("Plan");
		if (egp_e !=null) ret.setExpandedGoalPlan(Plan.loadFromXML(egp_e,referenceGameState));

		Element gs_e = xml.getChild("gamestate");
		if (gs_e !=null) {
			ret.setOriginalGameState(GameState.loadFromXML(gs_e,d2.core.Config.getDomain()));
		}
        Element p_e = xml.getChild("player");
        if (p_e!=null) ret.setOriginalPlayer(p_e.getValue());

		return ret;
	}
	
	
	public String toString() {
		return "GoalPlan(" + SuccessCondition.toString() + ")";
	}

	public Object clone(HashMap<Object,Object> alreadyCloned) {	
		if (alreadyCloned.get(this)!=null) return alreadyCloned.get(this);
		Sensor g;
		if ( SuccessCondition != null )
			g = (Sensor) SuccessCondition.clone();
		else
			g = new True();
		Plan ep = (Plan)(expandedGoalPlan==null ? null:expandedGoalPlan.clone());
		GoalPlan p = new GoalPlan(g,ep);
		alreadyCloned.put(this,p);
		p.setOriginalGameState(m_originalGameState);
        p.setOriginalPlayer(m_originalPlayer);

		return p;
	}

	public boolean isPlanCompletelyExecuted() {
		if (expandedGoalPlan != null)
			return expandedGoalPlan.isPlanCompletelyExecuted();
		return false;	// Santi: false by default, the planner should detect this as an open goal and retrieve a plan anyway.
//		return true; //True by default if there's no expandedPlan. This would force the planner to retrieve a new plan to set here
	}

	public boolean checkFailureCondition(PlayerGameState pgs) {
		
//		if(!onFailureConditionCalled)
//			onFailureCondition();
		return ((Float)FailureCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player)) 
				>= Sensor.BOOLEAN_TRUE_THRESHOLD);
	}

	public boolean checkPostCondition(PlayerGameState pgs) {
//		if(!onPostConditionCalled)
//		onPostCondition();
		return ((Float)PostCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player)) 
				>= Sensor.BOOLEAN_TRUE_THRESHOLD);
	}

	public boolean checkPreCondition(PlayerGameState pgs) {
//		if(!onPreConditionCalled)
//		onPreCondition();
		return ((Float)PreCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player)) 
				>= Sensor.BOOLEAN_TRUE_THRESHOLD);
	}

	public boolean checkPreFailureCondition(PlayerGameState pgs) {
//		if(!onPreFailureConditionCalled)
//		onPreFailureCondition();
		return ((Float)PreFailureCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player)) 
				>= Sensor.BOOLEAN_TRUE_THRESHOLD);
	}

	public boolean checkSuccessCondition(PlayerGameState pgs) {
//		if(!onSuccessConditionCalled)
//		onSuccessCondition();
		return ((Float)SuccessCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player)) 
				>= Sensor.BOOLEAN_TRUE_THRESHOLD);
	}

	public Sensor getFailureCondition() {
		return FailureCondition;
	}

	public Sensor getPostCondition() {
		return PostCondition;
	}

	public Sensor getPreCondition() {
		return PreCondition;
	}

	public Sensor getPreFailureCondition() {
		return PreFailureCondition;
	}

	public Sensor getSuccessCondition() {
		return SuccessCondition;
	}

	public void setFailureCondition(Sensor s) {
		FailureCondition = s;
	}

	public void setPostCondition(Sensor s) {
		PostCondition = s;
	}

	public void setPreCondition(Sensor s) {
		PreCondition = s;
	}

	public void setPreFailureCondition(Sensor s) {
		PreFailureCondition = s;
	}

	public void setSuccessCondition(Sensor s) {
		SuccessCondition = s;
	}
}
