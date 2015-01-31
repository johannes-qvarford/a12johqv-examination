/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.plans;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;


public abstract class PetriNetElement implements Cloneable, java.io.Serializable
{
	protected String elementID = new String();
	protected Plan plan;
	
	public PetriNetElement()
	{
		long elID = Counter.PetriNetElementcounter++;
		elementID = elID + "";
	}
		
	public void printContent()
	{
	}

	public String getElementID() {
		return elementID;
	}

	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}
		
	public Object clone() {
		return clone(new HashMap<Object,Object>());
	}
	public abstract Object clone(HashMap<Object,Object> alreadyCloned);
	abstract public void writeToXML(String planID, XMLWriter w);
		
}