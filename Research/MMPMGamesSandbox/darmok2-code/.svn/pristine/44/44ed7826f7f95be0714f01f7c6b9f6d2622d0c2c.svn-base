/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.util.planvisualizer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class PlanFrame {
	public int m_x = 0, m_y = 0;
	public int m_width = 100,m_height = 100;
	public HashMap<Object,Location> m_elements = new HashMap<Object,Location>();
	public List<LocationLink> m_links = new LinkedList<LocationLink>();	
	
	public void move(int offsetx,int offsety) {
		m_x+=offsetx;
		m_y+=offsety;
		for(Location l:m_elements.values()) {
			l.m_x+=offsetx;
			l.m_y+=offsety;
			if (l.m_element instanceof PlanFrame) {
				((PlanFrame)l.m_element).move(offsetx, offsety);
			}
		}
	}
}