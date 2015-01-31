package towers.objects;


import java.awt.Graphics2D;
import java.io.IOException;
import java.util.List;

import towers.Action;
import towers.TMap;
import towers.Towers;
import towers.helpers.SpriteManager;

public class TWall extends PhysicalEntity {

	public TWall(String owner, String id,int x,int y) {
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
			m_lastTileUsed = SpriteManager.get("wall");
		}
		return true;
	}

	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toXMLString() {
		// TODO Auto-generated method stub
		return null;
	}

	public gatech.mmpm.Entity toD2Entity() {
		towers.mmpm.entities.TWall ret = new towers.mmpm.entities.TWall(entityID, owner);
		ret.setx(coords[0]);
		ret.sety(coords[1]);
		return ret;
	}
}
