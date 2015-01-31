package bc;


import java.awt.Graphics2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import bc.helpers.VirtualController;
import bc.objects.BCOBase;
import bc.objects.BCOPlayerTank;
import bc.objects.BCPhysicalEntity;

public class BattleCity {
	int m_current_zoom;
	int m_cycle;
	
	BCMap m_map;
	
	public BattleCity(Element map) throws Exception {
		m_cycle = 0;
		m_map = null;
		
		if (map==null) {
			throw new Exception("BattleCity: map = null!");
		} else {
			m_cycle = 0;

			m_map = new BCMap(map);
			m_map.reset();
		} // if 
	}
        
        
        public BattleCity(BattleCity bc) {
            m_current_zoom = bc.m_current_zoom;
            m_cycle = bc.m_cycle;
            m_map = new BCMap(bc.m_map);
        }

	
	public boolean cycle(List<VirtualController> l_vc,List<Action> m_actions) throws ClassNotFoundException, IOException {
		
		m_cycle++;
				
		if (m_map==null) return false;
			
		m_map.cycle(l_vc,this, m_actions);

		// Check for destroyed bases:
		{
			List<BCPhysicalEntity> l_tanks = m_map.getObjects("BCOPlayerTank");
			List<BCPhysicalEntity> l_enemy_tanks = m_map.getObjects("BCOEnemyTank");
			List<BCPhysicalEntity> l_bases = m_map.getObjects("BCOBase");
			List<BCPhysicalEntity> l_generators = m_map.getObjects("BCOTankGenerator");
			BCPhysicalEntity selected_base;

			List<BCPhysicalEntity> to_delete = new LinkedList<BCPhysicalEntity>();

			for(BCPhysicalEntity tank:l_tanks) {
				selected_base=null;
				for(BCPhysicalEntity base:l_bases) {
					if (((BCOBase)base).getowner().equals(((BCOPlayerTank)tank).getowner())) selected_base=base;
				} // while 

				if (selected_base==null) to_delete.add(tank);
			} // while 

			while(!to_delete.isEmpty()) {
				BCPhysicalEntity tank = to_delete.remove(0);
				l_tanks.remove(tank);
				m_map.removeObject(tank);
			} // while 
			
			// Check if there is a winner:
			if ((l_tanks.size()==1 && l_generators.size()==0 && l_enemy_tanks.size()==0) ||
				l_tanks.size()==0) {
				// Game over!

				return false;
			} // if 
		}
		
	    return true;
	}
	
	public void draw(Graphics2D g) throws IOException {
		if (m_map!=null) m_map.draw(g);
	}
	
	public int getCycle() {
		return m_cycle;
	}

        public void setCycle(int cycle) {
		m_cycle = cycle;
	}

        
	public String saveToXML(int spaces) {
		String out="",tmp;
		
		tmp = m_map.saveToXML(spaces+2);
				
		int i;
		for(i=0;i<spaces;i++) out+=" ";
		out+="<gamestate>\n";
		out+=tmp;
		for(i=0;i<spaces;i++) out+=" ";
		out+="</gamestate>\n";
		
		return out;
	}
	
	public BCMap getMap() {		
		return m_map;
	}
}
