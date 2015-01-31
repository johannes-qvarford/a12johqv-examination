/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.plans;

import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;

import gatech.mmpm.util.Pair;

public class PlanPreconditionFailedState extends State 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1790345822207539139L;

	public PlanPreconditionFailedState()
	{
		super();
		setElementID("PLANPRECONDITIONFAILEDSTATE" + getElementID());
	}

    public PlanPreconditionFailedState(Plan p)
	{
		super();
        setPlan(p);
		setElementID("PLANPRECONDITIONFAILEDSTATE" + getElementID());
	}

	
	public void writeToXML(String planID, XMLWriter w) {
		w.rawXML("<state id='" + this.getElementID() + "' type=\"PlanPreconditionFailedState\" nextPlan='" + planID + "'>");
		w.tag("nextTransitions");
		for(Pair<Integer,Transition> t : getNextTransitions()) {
			w.rawXML("<nextTransition id= '"+t._b.getElementID()+"' tokens='"+t._a+"'/>");
		}
		//w.rawXML(nextTrans);
		w.tag("/nextTransitions");
		w.rawXML("</state>");
	}
	
	public Object clone(HashMap<Object,Object> alreadyCloned)
	{
		if (alreadyCloned.get(this)!=null) return alreadyCloned.get(this);
		
		PlanPreconditionFailedState cloneS = new PlanPreconditionFailedState();
		alreadyCloned.put(this,cloneS);

		cloneS.elementID = elementID;
		cloneS.plan = (plan==null ? null:(Plan)plan.clone(alreadyCloned));

		cloneS.setCurrentNumberOfTokens(this.getCurrentNumberOfTokens());
		cloneS.currentNumberOfTokens = currentNumberOfTokens;
		for(Pair<Integer,Transition> p:nextTransitions) {
			cloneS.nextTransitions.add(new Pair<Integer,Transition>(new Integer((int)p._a),(Transition) p._b.clone(alreadyCloned)));
		}
		
		return cloneS;
	}
}
