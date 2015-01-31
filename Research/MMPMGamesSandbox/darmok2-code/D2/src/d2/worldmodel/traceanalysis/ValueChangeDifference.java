/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.traceanalysis;

import gatech.mmpm.Entity;
import gatech.mmpm.GameState;

public class ValueChangeDifference extends Difference {
	Entity m_e;
	String m_feature;
	Object m_new_value;
	String type;
	
	public ValueChangeDifference(Entity e,String feature, Object nv) 
	{
		m_e = e;
		m_feature = feature;
		m_new_value = nv;	
		type = "changedEntity";
	}
	
	public String toString() {
		return "feature of " + m_e.toString() + " changed, " + m_feature + " -> " + m_new_value;
	}
	
	public boolean equals(Object o) {
		if (o.getClass()!=getClass()) return false;
		ValueChangeDifference o2 = (ValueChangeDifference)o;

		if (m_e.getentityID().equals(o2.m_e.getentityID()) &&
			m_feature.equals(o2.m_feature) &&
			m_new_value.equals(o2.m_new_value)) return true;
		
		return false;
	}
	
	public void apply(GameState gs) {
		Entity e = gs.getEntity(m_e.getentityID());
		
//		System.out.println("Applying: " + this);
//		System.out.flush();
		
		if (e!=null) {
			e.setFeatureValue(m_feature, m_new_value.toString());
		}
	}
	
	public String getType() {
		return type;
	}
}
