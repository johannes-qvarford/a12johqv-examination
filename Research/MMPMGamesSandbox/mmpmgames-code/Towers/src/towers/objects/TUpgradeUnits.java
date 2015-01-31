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
public class TUpgradeUnits extends PhysicalEntity {

	public TUpgradeUnits(String owner, String id,int x,int y) {
		super(owner, id);
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
			m_lastTileUsed = SpriteManager.get("upgradeUnits");
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			BufferedImage i = gc.createCompatibleImage(m_lastTileUsed.getWidth(null),m_lastTileUsed.getHeight(null),Transparency.BITMASK);
			Graphics2D g2 = i.createGraphics();
			Composite c = g2.getComposite();
			g2.setComposite(new TinterComposite(player.get_r(),player.get_g(),player.get_b()));
			g2.drawImage(m_lastTileUsed, 0,0,null);
			g2.setComposite(c);
			m_lastTileUsed = i;					
		}
		return true;
	}


	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
		"  <owner>" + owner + "</owner>\n" +			
		"  <type>" + getClass().getSimpleName() + "</type>\n" +
		"  <x>" + coords[0] + "</x>\n" +	
		"  <y>" + coords[1] + "</y>\n";						 
		return out + "</entity>";
	}

	public gatech.mmpm.Entity toD2Entity() {
		towers.mmpm.entities.TUpgradeUnits ret;
		
		ret = new towers.mmpm.entities.TUpgradeUnits(entityID, owner);
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
