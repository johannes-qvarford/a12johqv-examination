/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.traceanalysis;

import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.PhysicalEntity;

public class DisappearedEntityDifference extends Difference {
	Entity m_e;
	boolean map;
	String type;
	
	public DisappearedEntityDifference(Entity e,boolean m) {
		m_e = e;
		map = m;
		type = "disappearedEntity";
	}	
	
	public String toString() {
		return "" + m_e.toString() + " disappeared" + (map ? "in the map ":"");
	}
	
	public boolean equals(Object o) {
		if (o.getClass()!=getClass()) return false;
		DisappearedEntityDifference o2 = (DisappearedEntityDifference)o;
		
		if (map!=o2.map) return false;
		
		if (m_e.getentityID()==null) return false;
		if (m_e.getentityID().equals(o2.m_e.getentityID())) return true;
		
		return false;
	}

	public void apply(GameState gs) {
		if (map) {
			gs.getMap().deleteEntity((PhysicalEntity)m_e);
		} else {
			gs.deleteEntity(m_e);
		}
	}
	
	public String getType() {
		return type;
	}
	
	public boolean getMap() {
		return map;
	}
}
