/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Prafulla Mahindrakar
												Santi Ontanon
****************************************************************************/
package bc.objects;


import java.awt.Graphics2D;
import java.io.IOException;

import bc.helpers.SpriteManager;

public class BCOBase extends BCPhysicalEntity {

	public BCOBase(String id,int ax,int ay,String aowner) {
		entityID = id;
		width = 32;
		length = 32;
		owner = aowner;
		x = ax;
		y = ay;			
	}

	public BCOBase()
	{
		width = 32;
		length = 32;
	}
	public BCOBase( BCOBase incoming )
	{
		super(incoming);
		width = 32;
		length = 32;
	}
	public Object clone() {
		BCOBase e = new BCOBase(this);
		return e;
	}


	public static boolean isActive() 
	{
		return false;
	}
	
	public void draw(Graphics2D g) throws IOException
	{
		if (m_lastTileUsed==null) m_lastTileUsed = SpriteManager.get("base");
		if (m_lastTileUsed!=null) g.drawImage(m_lastTileUsed,x,y,null);
	}
}