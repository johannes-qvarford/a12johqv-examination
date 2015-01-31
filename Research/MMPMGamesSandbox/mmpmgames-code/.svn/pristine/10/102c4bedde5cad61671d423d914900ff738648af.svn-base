package towers;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import towers.helpers.Semaphore;
import towers.objects.Entity;
import towers.objects.PhysicalEntity;
import towers.objects.TBase;
import towers.objects.TPlayer;
import towers.objects.TTower;
import towers.objects.TUpgradeGold;
import towers.objects.TUpgradeUnits;

public class Towers {
	
	public static int TOWER_RANGE = 5;
	
	TMap m_map = null;
	List<Entity> m_entities = new LinkedList<Entity>();
	int m_cycle = 0;
	
	Semaphore m_gameStateSemaphore = new Semaphore(1);
	
    int hex_value(char c) 
    {
    	if (c>='0' && c<='9') return c-'0';
    	if (c>='a' && c<='f') return 10+c-'a';
    	if (c>='A' && c<='F') return 10+c-'A';
    	return 0;
    }

	
	public Towers(Element map_xml) throws ClassNotFoundException {
		// Load the players:
		
		for(Object o:map_xml.getChildren("entity")) {
			Element entity = (Element)o;

			String type=entity.getChildText("type");
			if (type.equals("TPlayer")) {
				// create a player:
				String color = entity.getChildText("color");
				int r = hex_value(color.charAt(0))*16+hex_value(color.charAt(1));
				int g = hex_value(color.charAt(2))*16+hex_value(color.charAt(3));
				int b = hex_value(color.charAt(4))*16+hex_value(color.charAt(5));
				
				TPlayer p = new TPlayer(entity.getChildText("owner"),
										Integer.parseInt(entity.getChildText("gold")),
										r,g,b);
				m_entities.add(p);
			}
		}
		
		m_map = new TMap(map_xml,this);
	}

	public TMap getMap() {
		return m_map;
	}

	public List<Entity> getObjects(String string) {
		List<Entity> l = new LinkedList<Entity>();
		for(Entity e:m_entities) if (e.getClass().getSimpleName().equals(string)) l.add(e);
		return l;
	}

	public int getCycle() {
		return m_cycle;
	}

	public synchronized List<Entity> getEntityList() {
		return this.m_entities;
	}
	
	public synchronized String saveToXML(int spaces) {
		String out="",tmp;
		
		m_gameStateSemaphore.WAIT();
		
		tmp = m_map.saveToXML(spaces+2);
				
		int i;
		for(i=0;i<spaces;i++) out+=" ";
		out+="<gamestate>\n";
		out+=tmp;
		for(Entity o:m_entities) {
			for(i=0;i<spaces+2;i++) out+=" ";
			out+=o.toXMLString() + "\n";
		}		
		for(i=0;i<spaces;i++) out+=" ";
		out+="</gamestate>\n";

		m_gameStateSemaphore.SIGNAL();

		return out;
	}

	public boolean cycle(List<Action> actions) throws ClassNotFoundException, IOException {
		m_cycle++;
		
		if (m_map==null) return false;

		m_gameStateSemaphore.WAIT();

		for(Entity e:m_entities) e.cycle(m_map, this, actions);
		m_map.cycle(this, actions);

		{
			List<PhysicalEntity> l_bases = m_map.getObjects(TBase.class);
			
			// Check if there is a winner:
			if (l_bases.size()==1 || l_bases.size()==0) {
				// Game over!

				m_gameStateSemaphore.SIGNAL();

				return false;
			} // if 
		}

		m_gameStateSemaphore.SIGNAL();

	    return true;
	}
	
	public TPlayer getPlayer(String player) {
		for(Entity e:getObjects("TPlayer")) {
			if (e.entityID.equals(player)) return (TPlayer)e;
		}
		return null;
	}

	public void draw(Graphics2D g) throws IOException {
		if (m_map!=null) m_map.draw(g);
	}
	
	public int cost(String building,String player) throws ClassNotFoundException {
		if (building.equals("towers.mmpm.entities.TTower")) {
			List<PhysicalEntity> l = m_map.getObjects(TTower.class);
			int n = 0;
			for(PhysicalEntity pe:l) if (pe.getowner().equals(player)) n++;
			return (10*(n+1));
		}
		if (building.equals("towers.mmpm.entities.TWall")) {
			return 2;
		}
		if (building.equals("towers.mmpm.entities.TUpgradeGold")) {
			List<PhysicalEntity> l = m_map.getObjects(TUpgradeGold.class);
			int n = 0;
			for(PhysicalEntity pe:l) if (pe.getowner().equals(player)) n++;
			return (int)(5*Math.pow(1.5,n));
		}
		if (building.equals("towers.mmpm.entities.TUpgradeUnits")) {
			List<PhysicalEntity> l = m_map.getObjects(TUpgradeUnits.class);
			int n = 0;
			for(PhysicalEntity pe:l) if (pe.getowner().equals(player)) n++;
			return (int)(5*Math.pow(1.5,n));
		}
		
		return 0;
	}
}
