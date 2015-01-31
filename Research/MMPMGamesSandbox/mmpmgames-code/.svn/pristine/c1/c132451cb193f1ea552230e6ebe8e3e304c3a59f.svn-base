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

import bc.PlayerInput;
import bc.helpers.SpriteManager;

public class BCOTank extends BCPhysicalEntity {
	public int direction;
	public String color;
	public int next_shot_delay= 0;
	
	public int m_moving_x = 0, m_moving_y = 0;
	
	public BCOTank(int ax,int ay,int adirection) {
		width = 32;
		length = 32;
		x = ax;
		y = ay;			
		direction = adirection;
	}


	public BCOTank()
	{
		width = 32;
		length = 32;
	}
	public BCOTank( BCOTank incoming )
	{
		super(incoming);
		this.direction = incoming.direction;
		this.color = incoming.color;
		this.next_shot_delay = incoming.next_shot_delay;
		width = 32;
		length = 32;
	}
	public Object clone() {
		BCOTank e = new BCOTank(this);
		return e;
	}


	public static boolean isActive() 
	{
		return true;
	}

	public int getdirection()
	{
		return direction;
	}

	public String getcolor()
	{
		return color;
	}

	public int getnext_shot_delay()
	{
		return next_shot_delay;
	}
	
	public int getmoving_x() {
		return m_moving_x;
	}

	public int getmoving_y() {
		return m_moving_y;
	}

	public void setdirection( int incoming )
	{
		this.direction = incoming;
	}

	public void setdirection( String incoming )
	{
		this.direction = Integer.parseInt(incoming);
	}

	public void setcolor( String incoming )
	{
		this.color = incoming;
	}

	public void setnext_shot_delay( int incoming )
	{
		this.next_shot_delay = incoming;
	}

	public void setnext_shot_delay( String incoming )
	{
		this.next_shot_delay = Integer.parseInt(incoming);
	}

	public void draw(Graphics2D g) throws IOException
	{
		switch(direction) {
		case PlayerInput.DIRECTION_UP: m_lastTileUsed = SpriteManager.get("player-tank-up");
		break;
		case PlayerInput.DIRECTION_DOWN: m_lastTileUsed = SpriteManager.get("player-tank-down");
		break;
		case PlayerInput.DIRECTION_LEFT: m_lastTileUsed = SpriteManager.get("player-tank-left");
		break;
		case PlayerInput.DIRECTION_RIGHT: m_lastTileUsed = SpriteManager.get("player-tank-right");
		break;
		} // switch
		if (m_lastTileUsed!=null) g.drawImage(m_lastTileUsed,x,y,null);
	}

	public boolean readyToFire() {
		return next_shot_delay==0;
	}


	public boolean readyToMove() {
		return m_moving_x==0 && m_moving_y==0; 
	}

}
