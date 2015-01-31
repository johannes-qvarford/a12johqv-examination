/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.traceanalysis;

import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.PhysicalEntity;

public class NewEntityDifference extends Difference {
	Entity m_e;
	boolean map;
	String type;
	
	public NewEntityDifference(Entity e,boolean m) {
		m_e = e;
		type = "newEntity";
		map = m;
	}
	
	public String toString() {
		String out = "new entity " + (map ? "in map ":"") + m_e.toString();
		return out;
	}	
	
	public boolean equals(Object o) {
		if (o.getClass()!=getClass()) return false;
		NewEntityDifference o2 = (NewEntityDifference)o;
		
		if (map!=o2.map) return false;
		
		if ((m_e==null && o2.m_e!=null) ||
			(m_e!=null && o2.m_e==null)) return false;
		if (m_e==null && o2.m_e==null) return true;
		if (m_e.equivalents(o2.m_e)) return true;
		
		return false;
	}
	
	public void apply(GameState gs) {
		if (map) {
			gs.getMap().addEntity((PhysicalEntity)m_e);
		} else {
			gs.addEntity(m_e);
		}
	}
	
	public String getType() {
		return type;
	}
	
	public boolean getMap() {
		return map;
	}
}
