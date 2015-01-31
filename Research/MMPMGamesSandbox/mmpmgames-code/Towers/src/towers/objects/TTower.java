package towers.objects;


import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import towers.Action;
import towers.TMap;
import towers.Towers;
import towers.helpers.SpriteManager;
import towers.helpers.TinterComposite;

public class TTower extends PhysicalEntity {

	int cycle = 0;
	
	public TTower(String id, String owner,int x,int y) {
		super(id, owner);
		coords[0]=x;
		coords[1]=y;
		width = 16;
		length = 16;
	}
	
	public void draw(Graphics2D g) throws IOException
	{
		if (m_lastTileUsed!=null) g.drawImage(m_lastTileUsed,coords[0],coords[1],null);
	}	
	
	
	public boolean cycle(TMap map, Towers game, List<Action> actions) throws IOException {
		if (m_lastTileUsed==null) {
			TPlayer player = game.getPlayer(owner);
			m_lastTileUsed = SpriteManager.get("tower");
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			BufferedImage i = gc.createCompatibleImage(m_lastTileUsed.getWidth(null),m_lastTileUsed.getHeight(null),Transparency.BITMASK);
			Graphics2D g2 = i.createGraphics();
			Composite c = g2.getComposite();
			g2.setComposite(new TinterComposite(player.get_r(),player.get_g(),player.get_b()));
			g2.drawImage(m_lastTileUsed, 0,0,null);
			g2.setComposite(c);
			m_lastTileUsed = i;					
		}
		
		cycle++;
		
		if ((cycle%20)==0) {
			// Fire!
			
			TUnit target = map.getClosestTargetUnit(getx()/TMap.TILE_WIDTH, gety()/TMap.TILE_HEIGHT, getowner(), 5);
			if (target!=null) target.hit();
		}
		
		return true;
	}
	
	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
					 "  <owner>" + owner + "</owner>\n" +			
					 "  <type>" + getClass().getSimpleName() + "</type>\n" +
					 "  <nextshot>" + ((cycle%20)==0 ? 0:20-(cycle%20)) + "</nextshot>\n" +
					 "  <x>" + coords[0] + "</x>\n" +	
					 "  <y>" + coords[1] + "</y>\n";	
		return out + "</entity>";
	}

	public gatech.mmpm.Entity toD2Entity() {
		towers.mmpm.entities.TTower ret;
		
		ret = new towers.mmpm.entities.TTower(entityID, owner);
		ret.setNextshot((cycle%20)==0 ? 0:20-(cycle%20));
		ret.setx(coords[0]);
		ret.sety(coords[1]);
		return ret;
	}
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}	


}
