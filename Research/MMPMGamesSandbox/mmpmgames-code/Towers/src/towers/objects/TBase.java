package towers.objects;


import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import towers.Action;
import towers.TMap;
import towers.Towers;
import towers.helpers.SpriteManager;
import towers.helpers.TinterComposite;

public class TBase extends PhysicalEntity {
	
	int hp = 5;
	int cycle = 0;
	int hit_timmer = 0;
	
	public TBase(String owner, String id,int x, int y, int hitpoints, int nextUnit) {
		super(owner,id);
		width = 32;
		length = 32;
		coords[0] = x;
		coords[1] = y;
		hp = hitpoints;
		width = 16;
		length = 16;
	}
	
	public void draw(Graphics2D g) throws IOException
	{
		if (m_lastTileUsed!=null) g.drawImage(m_lastTileUsed,coords[0],coords[1],null);
	}	
	
	
	public boolean cycle(TMap map, Towers game, List<Action> actions) throws IOException, ClassNotFoundException {
		if (m_lastTileUsed==null || hit_timmer!=0) {
			TPlayer player = game.getPlayer(owner);
			m_lastTileUsed = SpriteManager.get("base");
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			BufferedImage i = gc.createCompatibleImage(m_lastTileUsed.getWidth(null),m_lastTileUsed.getHeight(null),Transparency.BITMASK);
			Graphics2D g2 = i.createGraphics();
			Composite c = g2.getComposite();
			float r,g,b;
			r = player.get_r();
			g = player.get_g();
			b = player.get_b();
			if (hit_timmer>0) {
				hit_timmer--;
				float f = hit_timmer/10.0f;
				if (f>1.0) f = 1.0f;
				r = 1.0f*f + r*(1.0f-f);
				g = 1.0f*f + g*(1.0f-f);
				b = 1.0f*f + b*(1.0f-f);
			}
			g2.setComposite(new TinterComposite(r,g,b));
			g2.drawImage(m_lastTileUsed, 0,0,null);
			g2.setComposite(c);
			m_lastTileUsed = i;					
		}
	
		if (hp<=0) return false;
		
		cycle++;
		
		if ((cycle%100)==0) {
			String target = null;
			List<PhysicalEntity> l = map.getObjects(TBase.class);
			List<PhysicalEntity> l2 = map.getObjects(TUpgradeUnits.class);
			int n_upgrades = 0;
			
			for(PhysicalEntity pe:l2) if (pe.getowner().equals(getowner())) n_upgrades++;
			
			
			if (l.size()>1) {	
				while(target==null) {
					Random r = new Random();
					Entity e = l.get(r.nextInt(l.size()));
					if (!e.getowner().equals(getowner())) {
						target = e.getowner();
					}
				}
				
				TUnit u = new TUnit("TUnit-" + getowner() + "-" + cycle,owner,getx(),gety(),target,(int)(5*Math.pow(1.5, n_upgrades)));
				map.addObject(u);
				
//				System.out.println("Unit from " + getowner() + " has target " + target);
			}
		}
		
		return true;
	}
	
	
	public void hit() {
		hp--;
		hit_timmer = 10;
	}

	public int getHP() {
		return hp;
	}
	
	public gatech.mmpm.Entity toD2Entity() {
		towers.mmpm.entities.TBase ret;
		ret = new towers.mmpm.entities.TBase(entityID, owner);
		ret.setHitpoints(hp);
		ret.setNextunit((cycle%100)==0 ? 0:100-(cycle%100));
		ret.setx(coords[0]);
		ret.sety(coords[1]);
		return ret;
	}
	
	public String toXMLString() {
		StringBuilder b = new StringBuilder();
		
		b.append("<entity id=\"");
		b.append(entityID);
		b.append("\">\n");
		
		b.append("  <owner>");
		b.append(owner);
		b.append("</owner>\n");
		
		b.append("  <type>");
		b.append(getClass().getSimpleName());
		b.append("</type>\n");
		
		b.append("  <hitpoints>");
		b.append(hp);
		b.append("</hitpoints>\n");
		
		b.append("  <nextunit>");
		b.append(((cycle%100)==0 ? 0:100-(cycle%100)));
		b.append("</nextunit>\n");	
		
		b.append("  <x>");
		b.append(coords[0]);
		b.append("</x>\n");
		
		b.append("  <y>");
		b.append(coords[1]);
		b.append("</y>\n");
		
		b.append("</entity>");

		return b.toString();
	}

	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}	


}
