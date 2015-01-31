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
import towers.Paths;
import towers.TMap;
import towers.Towers;
import towers.helpers.SpriteManager;
import towers.helpers.TinterComposite;


public class TUnit extends PhysicalEntity {

	int cycle = 0;
	String target = null;
	int hp = 5;
	int hit_timmer = 0;

	public TUnit(String id, String owner,int x,int y,String a_target,int hitpoints) {
		super(id, owner);
		coords[0]=x;
		coords[1]=y;
		target = a_target;
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
			m_lastTileUsed = SpriteManager.get("unit");
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

		cycle++;

		if (hp<=0) return false;

		if ((cycle%25)==0) {
			Integer direction = map.getPaths().direction(getx()/TMap.TILE_WIDTH, gety()/TMap.TILE_HEIGHT, target);
			int nextx=getx();
			int nexty=gety();

			if (direction==null) {
				// Change target:
				target=null;
				List<PhysicalEntity> l = map.getObjects(TBase.class);
				if (l.size()>1) {	
					while(target==null) {
						Random r = new Random();
						Entity e = l.get(r.nextInt(l.size()));
						if (!e.getowner().equals(getowner())) {
							target = e.getowner();
						}
					}
				}				
			} else {
				if (direction==Paths.DIRECTION_DOWN) nexty=gety()+TMap.TILE_HEIGHT;
				if (direction==Paths.DIRECTION_UP) nexty=gety()-TMap.TILE_HEIGHT;
				if (direction==Paths.DIRECTION_RIGHT) nextx=getx()+TMap.TILE_WIDTH;
				if (direction==Paths.DIRECTION_LEFT) nextx=getx()-TMap.TILE_WIDTH;

				PhysicalEntity pe = map.getObjectAt(nextx/TMap.TILE_WIDTH, nexty/TMap.TILE_HEIGHT);
				if (pe!=null && pe.getowner().equals(target)) {
					if (pe instanceof TBase) {
						((TBase)pe).hit();
						return false;
					}
					if (pe instanceof TUnit) {
						((TUnit)pe).hit();
					}
				}

				setx(nextx);
				sety(nexty);

				if (getx()<0 || getx()>=map.getDx() ||
						gety()<0 || gety()>=map.getDy()) {
					return false;
				}
			}
		}

		return true;
	}

	public void hit() {
		hp--;
		hit_timmer = 10;
	}

	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
     	"  <owner>" + owner + "</owner>\n" +			
		"  <type>" + getClass().getSimpleName() + "</type>\n" +			
		"  <hitpoints>" + hp + "</hitpoints>\n" +
		"  <target>" + target + "</target>\n" + 
		"  <x>" + coords[0] + "</x>\n" +	
		"  <y>" + coords[1] + "</y>\n";	
		return out + "</entity>";
	}

	public gatech.mmpm.Entity toD2Entity() {
		towers.mmpm.entities.TUnit ret;
		
		ret = new towers.mmpm.entities.TUnit(entityID, owner);
		ret.setHitpoints(hp);
		ret.setTarget(target);
		ret.setx(coords[0]);
		ret.sety(coords[1]);
		return ret;
	}

	public Object clone() {
		return null;
	}	

}
