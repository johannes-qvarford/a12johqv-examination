package towers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import towers.objects.PhysicalEntity;
import towers.objects.TBase;
import towers.objects.TPlayer;
import towers.objects.TTower;
import towers.objects.TUnit;
import towers.objects.TUpgradeGold;
import towers.objects.TUpgradeUnits;
import towers.objects.TWall;

public class TMap {
	public static final int TILE_WIDTH = 16;
	public static final int TILE_HEIGHT = 16;
	
	Towers m_game = null;
	int m_dx, m_dy;
	
    List<PhysicalEntity> m_running_objects = new LinkedList<PhysicalEntity>();
    List<PhysicalEntity> m_newly_added_running_objects = new LinkedList<PhysicalEntity>();
    List<PhysicalEntity> m_auxiliar_objects = new LinkedList<PhysicalEntity>();
    
    Paths m_paths = new Paths();

    int hex_value(char c) 
    {
    	if (c>='0' && c<='9') return c-'0';
    	if (c>='a' && c<='f') return 10+c-'a';
    	if (c>='A' && c<='F') return 10+c-'A';
    	return 0;
    }

	public TMap(Element map,Towers game) throws ClassNotFoundException {

		m_game = game;
		
		for(Object o:map.getChildren("entity")) {
			Element entity = (Element)o;

			String type=entity.getChildText("type");
			if (type.equals("map")) {
				Element background = entity.getChild("background");
				
				m_dx = Integer.parseInt(entity.getChildText("width"))*TILE_WIDTH;
				m_dy = Integer.parseInt(entity.getChildText("height"))*TILE_HEIGHT;
				
				String row;
				int y=0;
				int x;
				
				for(Object o2:background.getChildren()) {
					Element row_xml = (Element)o2;
					row = row_xml.getValue();
					for(x=0;x<row.length();x++) {
						
						// Add the map tiles
						if (row.charAt(x)=='w') {
							TWall e = new TWall("",
												"",
												x*TILE_WIDTH,
												y*TILE_HEIGHT);
							m_running_objects.add(e);						
						}
						
					} // for
					y++;
				} // while 
			} // if 

			// Add the objects:
			if (type.equals("TTower")) {
				TTower e = new TTower(entity.getAttributeValue("id"),
									  entity.getChildText("owner"),
									  Integer.parseInt(entity.getChildText("x"))*16,
									  Integer.parseInt(entity.getChildText("y"))*16);
				m_running_objects.add(e);
			}
			if (type.equals("TBase")) {
				TBase e = new TBase(entity.getAttributeValue("id"),
									entity.getChildText("owner"),
									Integer.parseInt(entity.getChildText("x"))*16,
									Integer.parseInt(entity.getChildText("y"))*16,
									Integer.parseInt(entity.getChildText("hitpoints")),
									Integer.parseInt(entity.getChildText("nextunit")));
				m_running_objects.add(e);
			}
			if (type.equals("TUpgradeGold")) {
				TUpgradeGold e = new TUpgradeGold(entity.getAttributeValue("id"),
												  entity.getChildText("owner"),
												  Integer.parseInt(entity.getChildText("x"))*16,
												  Integer.parseInt(entity.getChildText("y"))*16);
				m_running_objects.add(e);
			}
			if (type.equals("TUpgradeUnits")) {
				TUpgradeUnits e = new TUpgradeUnits(entity.getAttributeValue("id"),
												    entity.getChildText("owner"),
												    Integer.parseInt(entity.getChildText("x"))*16,
												    Integer.parseInt(entity.getChildText("y"))*16);
				m_running_objects.add(e);
			}

			
		} // while 
		
		m_paths.recomputePaths(this);
	}
	
	public void cycle(Towers game, List<Action> actions) throws IOException, ClassNotFoundException {
	    List<PhysicalEntity> to_delete = new LinkedList<PhysicalEntity>();

	    for(PhysicalEntity o:m_running_objects) {
	        if (!o.cycle(this, game, actions))
	            to_delete.add(o);
	    }

	    while (!to_delete.isEmpty()) {
	    	PhysicalEntity o = to_delete.remove(0);
	        m_running_objects.remove(o);
	    }

	    for(PhysicalEntity o:m_auxiliar_objects) {
	        if (!o.cycle(this, game, actions))
	            to_delete.add(o);
	    }

	    while (!to_delete.isEmpty()) {
	    	PhysicalEntity o = to_delete.remove(0);
	    	m_auxiliar_objects.remove(o);
	    }

		while(!m_newly_added_running_objects.isEmpty()) m_running_objects.add(m_newly_added_running_objects.remove(0));
	}


	public void draw(Graphics2D g) throws IOException {	    
		
		// Draw the influence maps:
	    for(PhysicalEntity o:m_running_objects) {
	    	if (o instanceof TTower ||
	    		o instanceof TBase) {
	    		TPlayer player = m_game.getPlayer(o.getowner());
	    		int cx = o.getx()/TILE_WIDTH;
	    		int cy = o.gety()/TILE_HEIGHT;
	    		for(int i=-Towers.TOWER_RANGE;i<=Towers.TOWER_RANGE;i++) {
		    		for(int j=-Towers.TOWER_RANGE;j<=Towers.TOWER_RANGE;j++) {
		    			
		    			if (cx+j>=0 && cx+j<m_dx/TILE_WIDTH &&
		    				cy+i>=0 && cy+i<m_dy/TILE_HEIGHT &&
		    				(i*i)+(j*j)<=Towers.TOWER_RANGE*Towers.TOWER_RANGE) {
		    				
		    				Composite originalComposite = g.getComposite();
		    				g.setColor(new Color(player.get_r(),player.get_g(),player.get_b()));
		    				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			    			g.fillRect((cx+j)*TILE_WIDTH, (cy+i)*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
		    				g.setComposite(originalComposite);		    				
		    			}
		    		}	    			
	    		}
	    	}
	    }
		
		// Draw the grid
		g.setColor(Color.DARK_GRAY);
		for(int i=0;i<=m_dy/TILE_HEIGHT;i++) g.drawLine(0, i*TILE_HEIGHT, m_dx, i*TILE_HEIGHT);
		for(int i=0;i<=m_dx/TILE_WIDTH;i++) g.drawLine(i*TILE_WIDTH, 0, i*TILE_WIDTH, m_dy);
		
	    for(PhysicalEntity o:m_running_objects) o.draw(g);
	    for(PhysicalEntity o:m_auxiliar_objects) o.draw(g);
	}

	
	public List<PhysicalEntity> getObjects(Class c) throws ClassNotFoundException {
	    List<PhysicalEntity> res = new LinkedList<PhysicalEntity>();
	   	    
	    for(PhysicalEntity o:m_running_objects) {
	        if (c.isInstance (o)) res.add(o);
	    }
	            
	    return res;
	}

	public void removeObject(PhysicalEntity o) {
		m_running_objects.remove(o);
		m_newly_added_running_objects.remove(o);
	}

	public gatech.mmpm.Map toD2Map() {
		int dx=m_dx/16, dy=m_dy/16;
		gatech.mmpm.TwoDMap ret = new gatech.mmpm.TwoDMap(dx, dy, 16, 16);
		
		// Only put in the D2 map the walls. The
		// m_running_objects entities are NOT be store in the
		// D2 map.
		
		for(PhysicalEntity o:m_running_objects) {
			if (o instanceof TWall)
				ret.addEntity((gatech.mmpm.PhysicalEntity)o.toD2Entity());
		}
		
		return ret;
	}
	
	public List<PhysicalEntity> getMapEntities() {
		return m_running_objects;
	}
	
	public synchronized String saveToXML(int spaces) {
		String tmp="";	// something big enough :)
		int i,j;
		int dx=m_dx/16, dy=m_dy/16;
		
		for(i=0;i<spaces;i++) tmp+=" ";
		tmp+="<entity id=\"0\">\n";

		for(i=0;i<spaces+2;i++) tmp+=" ";
		tmp+="<type>map</type>\n";

		for(i=0;i<spaces+2;i++) tmp+=" ";
		tmp+="<width>" + dx + "</width>\n";

		for(i=0;i<spaces+2;i++) tmp+=" ";
		tmp+="<height>" + dy + "</height>\n";

		for(i=0;i<spaces+2;i++) tmp+=" ";
		tmp+="<cell-width>16</cell-width>\n";

		for(i=0;i<spaces+2;i++) tmp+=" ";
		tmp+="<cell-height>16</cell-height>\n";

		for(i=0;i<spaces+2;i++) tmp+=" ";
		tmp+="<background>\n";
		{
			char bg[] = new char[dx*dy];
			char bgtmp[] = new char[dx];

			for(i=0;i<dx*dy;i++) bg[i]='.';

			for(PhysicalEntity o:m_running_objects) {
				if (o instanceof TWall) {
					j = (o.getx()/TILE_WIDTH)+(o.gety()/TILE_HEIGHT)*dx;
					bg[j] = 'w';
				}
			}

			for(j=0;j<dy;j++) {
				for(i=0;i<spaces+4;i++) tmp+=" ";
				tmp+="<row>";
				System.arraycopy(bg, j*dx, bgtmp, 0, dx);
//				bgtmp[dx]=0;
				tmp+=new String(bgtmp);
				tmp+="</row>\n";
			} // for
		}
		for(i=0;i<spaces+2;i++) tmp+=" ";
		tmp+="</background>\n";

		for(i=0;i<spaces;i++) tmp+=" ";
		tmp+="</entity>\n";

		for(PhysicalEntity o:m_running_objects) {
			if (!(o instanceof TWall)) tmp+=o.toXMLString() + "\n";
		}
		
		return tmp;
	}
	
	class BCMapTilePlace
	{
        public String m_tile_name;
		Image m_tile_cache = null;
        public int m_x, m_y;
	}

	class BCObjectPlace
	{
		public BCObjectPlace(String id) {
			m_id = id;
		}
		
		public String m_id;
		public String m_object_name;
		public String m_player_id;
		public int m_x, m_y;
		public List<Integer> m_parameters = new LinkedList<Integer>();
	}

	public boolean collision(PhysicalEntity o2) throws IOException {

	    for(PhysicalEntity o:m_running_objects) 
	        if (o != o2 && o2.collision(o))
	            return true;

	    return false;
	}

	
	public boolean collisionExcludingTile(PhysicalEntity o2,String tileName) throws IOException {

	    for(PhysicalEntity o:m_running_objects) 
	        if (o != o2 && o2.collision(o))
	            return true;

	    return false;
	}

	
	public void addObject(PhysicalEntity o) {		
		m_newly_added_running_objects.add(o);
	}

	public PhysicalEntity collisionWithObject(PhysicalEntity o2) {
	    for(PhysicalEntity o:m_running_objects) 
	        if (o != o2 && o2.collision(o))
	            return o;

	    return null;
	}


	public boolean collisionExcludingObject(PhysicalEntity o2, int offsx, int offsy, String type) throws IOException, ClassNotFoundException {

	    for(PhysicalEntity o:m_running_objects) {
	    	o2.setx(o2.getx()+offsx);
	    	o2.sety(o2.gety()+offsy);
	    	
	    	if (Class.forName("battlecity.objects." + type).isInstance(o) && 
	    		o != o2 && o2.collision(o)) {
		    	o2.setx(o2.getx()-offsx);
		    	o2.sety(o2.gety()-offsy);
	            return true;
	        }
	    	o2.setx(o2.getx()-offsx);
	    	o2.sety(o2.gety()-offsy);
	    }

	    return false;
	}

	public PhysicalEntity getObjectAt(int cell_x, int cell_y) {

		for(PhysicalEntity pe:m_running_objects) {
			if (pe.getx()==cell_x*TILE_WIDTH &&
				pe.gety()==cell_y*TILE_HEIGHT) return pe;
		}

		return null;
	}
	
	public PhysicalEntity getClosestObjectOfPlayer(int cell_x,int cell_y,String player) {
		int best_distance = 0;
		PhysicalEntity closest = null;
		
		for(PhysicalEntity pe:m_running_objects) {
			if (pe.getowner().equals(player)) {
				int dx = pe.getx()-cell_x*TILE_WIDTH;
				int dy = pe.gety()-cell_y*TILE_HEIGHT;
				
				int distance = dx*dx+dy*dy;
					
				if (closest==null || distance<best_distance) {
					best_distance = distance;
					closest = pe;
				}
			}
		}

		return closest;
	}
	
	
	public double getDistancetoClosestTowerOfPlayer(int cell_x,int cell_y,String player) {
		int best_distance = 0;
		PhysicalEntity closest = null;
		
		for(PhysicalEntity pe:m_running_objects) {
			if (pe.getowner().equals(player) &&
				(pe instanceof TTower ||
				 pe instanceof TBase)) {
				int dx = (pe.getx()/TILE_WIDTH-cell_x);
				int dy = (pe.gety()/TILE_HEIGHT-cell_y);
				
				int distance = dx*dx+dy*dy;
					
				if (closest==null || distance<best_distance) {
					best_distance = distance;
					closest = pe;
				}
			}
		}

		return Math.sqrt(best_distance);
	}
	
	
	public TUnit getClosestTargetUnit(int cell_x,int cell_y,String player, double maxDistance) {
		int best_distance = 0;
		TUnit closest = null;
		
		maxDistance*= TILE_WIDTH;
		maxDistance = maxDistance*maxDistance;
		
		for(PhysicalEntity pe:m_running_objects) {
			if (pe instanceof TUnit &&
				!pe.getowner().equals(player)) {
				int dx = pe.getx()-cell_x*TILE_WIDTH;
				int dy = pe.gety()-cell_y*TILE_HEIGHT;
				
				int distance = dx*dx+dy*dy;
					
				if (distance<maxDistance && closest==null || distance<best_distance) {
					best_distance = distance;
					closest = (TUnit)pe;
					
				}
			}
		}

		return closest;
	}


	public List<PhysicalEntity> getAllObjects() {
		List<PhysicalEntity> l = new LinkedList<PhysicalEntity>();
		
		l.addAll(m_running_objects);
		l.addAll(m_newly_added_running_objects);
		return l;
	}
	
	
	public Paths getPaths() {
		return m_paths;
	}
	
	public int getDx() {
		return m_dx;
	}
	
	public int getDy() {
		return m_dy;
	}
	
	

}
