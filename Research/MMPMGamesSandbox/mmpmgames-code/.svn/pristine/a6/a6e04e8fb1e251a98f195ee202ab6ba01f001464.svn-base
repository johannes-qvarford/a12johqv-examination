/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Prafulla Mahindrakar
												Santi Ontanon
 ****************************************************************************/
package bc.objects;


import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import bc.Action;
import bc.BCMap;
import bc.BattleCity;
import bc.PlayerInput;
import bc.helpers.SpriteManager;
import bc.helpers.TinterComposite;
import bc.helpers.VirtualController;

public class BCOPlayerTank extends BCOTank {

	float m_r,m_g,m_b;
	Image m_last_tile_used = null;
	Image m_tintedTiles[] = {null,null,null,null}; 

	BCOPlayerTank(String id, int ax, int ay, String aowner,int ar,int ag,int ab,int adirection) throws IOException
	{
		entityID = id;
		width = 32;
		length = 32;
		x = ax;
		y = ay;
		owner = aowner;
		direction = adirection;
		m_r=ar/255.0f;
		m_g=ag/255.0f;
		m_b=ab/255.0f;
		m_moving_x=0;
		m_moving_y=0;
		next_shot_delay = 0;
		m_last_tile_used = SpriteManager.get("player-tank-up");
	} /* BCOPlayerTank */ 


	public BCOPlayerTank()
	{
	}
	public BCOPlayerTank( BCOPlayerTank incoming )
	{
		super(incoming);
	}
	public Object clone() {
		BCOPlayerTank e = new BCOPlayerTank(this);
		return e;
	}


	public static boolean isActive() 
	{
		return true;
	}

	public float getr() {
		return m_r;
	}

	public float getg() {
		return m_g;
	}

	public float getb() {
		return m_b;
	}
	
	public boolean cycle(List<VirtualController> l_vc, BCMap map, BattleCity game, List<Action> actions) throws IOException, ClassNotFoundException {
		if (!super.cycle(l_vc,map,game,actions)) return false;

		int m_old_x=x,m_old_y=y;
		VirtualController selected_vc;
		
		selected_vc = null;
		
		for(VirtualController vc:l_vc) if (vc.m_id.equals(owner)) selected_vc = vc;

		if (selected_vc!=null && next_shot_delay==0 && selected_vc.m_button[0]) {
			switch(direction) {
				case PlayerInput.DIRECTION_UP: {
					BCOBullet b = new BCOBullet(x+8,y,PlayerInput.DIRECTION_UP);
					b.excludeForCollision(this);
					map.addObject(b);
					
					if (actions!=null) {
                                            Action a = new Action("Fire", owner, entityID);
                                            actions.add(a);
                                        }			
				}
					break;
				case PlayerInput.DIRECTION_DOWN: {
					BCOBullet b = new BCOBullet(x+8,y+16,PlayerInput.DIRECTION_DOWN);
					b.excludeForCollision(this);
					map.addObject(b);
					
					if (actions!=null) {
                                            Action a = new Action("Fire", owner, entityID);			
                                            actions.add(a);
                                        }
				}
					break;
				case PlayerInput.DIRECTION_LEFT: {
					BCOBullet b = new BCOBullet(x,y+8,PlayerInput.DIRECTION_LEFT);
					b.excludeForCollision(this);
					map.addObject(b);
					
					if (actions!=null) {
                                            Action a = new Action("Fire", owner, entityID);			
                                            actions.add(a);
                                        }
				}
					break;
				case PlayerInput.DIRECTION_RIGHT: {
					BCOBullet b = new BCOBullet(x+16,y+8,PlayerInput.DIRECTION_RIGHT);
					b.excludeForCollision(this);
					map.addObject(b);
					
                                        if (actions!=null) {
                                            Action a = new Action("Fire", owner, entityID);			
                                            actions.add(a);
                                        }
				}
					break;
			} // switch
			next_shot_delay = 16;
		} // if 	

		if (selected_vc!=null && m_moving_x==0 && m_moving_y==0) {
			if (selected_vc.m_joystick[PlayerInput.DIRECTION_UP] && m_moving_x==0 && m_moving_y==0) {
				direction = PlayerInput.DIRECTION_UP;
				m_moving_y=-8;			

                                if (actions!=null) {
                                    Action a = new Action("Move", owner, entityID);			
                                    a.addParameter("direction","" + PlayerInput.DIRECTION_UP);
                                    actions.add(a);
                                }
			} // if 
			if (selected_vc.m_joystick[PlayerInput.DIRECTION_DOWN] && m_moving_x==0 && m_moving_y==0) {
				direction = PlayerInput.DIRECTION_DOWN;
				m_moving_y=8;

                                if (actions!=null) {
                                    Action a = new Action("Move", owner, entityID);					
                                    a.addParameter("direction","" + PlayerInput.DIRECTION_DOWN);
                                    actions.add(a);
                                }
			} // if 
			if (selected_vc.m_joystick[PlayerInput.DIRECTION_LEFT] && m_moving_x==0 && m_moving_y==0) {
				direction = PlayerInput.DIRECTION_LEFT;
				m_moving_x=-8;

                                if (actions!=null) {
                                    Action a = new Action("Move", owner, entityID);				
                                    a.addParameter("direction","" + PlayerInput.DIRECTION_LEFT);
                                    actions.add(a);
                                }
			} // if 
			if (selected_vc.m_joystick[PlayerInput.DIRECTION_RIGHT] && m_moving_x==0 && m_moving_y==0) {
				direction = PlayerInput.DIRECTION_RIGHT;
				m_moving_x=8;

                                if (actions!=null) {
                                    Action a = new Action("Move", owner, entityID);			
                                    a.addParameter("direction","" + PlayerInput.DIRECTION_RIGHT);
                                    actions.add(a);
                                }
			} // if 
		} // if 

		if (m_moving_x>0) {
			m_moving_x--;
			x+=2;
			if ((x%16)==0) m_moving_x=0;
		} // if 
		if (m_moving_y>0) {
			m_moving_y--;
			y+=2;
			if ((y%16)==0) m_moving_y=0;
		} // if 
		if (m_moving_x<0) {
			m_moving_x++;
			x-=2;
			if ((x%16)==0) m_moving_x=0;
		} // if 
		if (m_moving_y<0) {
			m_moving_y++;
			y-=2;
			if ((y%16)==0) m_moving_y=0;
		} // if 

		if (next_shot_delay>0) next_shot_delay--;

		if (map.collision(this)) {
			x = ((m_old_x+8)&0xfff0);	
			y = ((m_old_y+8)&0xfff0);
			m_moving_x=0;
			m_moving_y=0;
		} // if
		
		return true;		
	}

	public void draw(Graphics2D g) throws IOException
	{		
		if (m_tintedTiles[direction]==null) {
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

			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			BufferedImage i = gc.createCompatibleImage(m_lastTileUsed.getWidth(null),m_lastTileUsed.getHeight(null),Transparency.BITMASK);
			Graphics2D g2 = i.createGraphics();
			Composite c = g2.getComposite();
			g2.setComposite(new TinterComposite(m_r,m_g,m_b));
			g2.drawImage(m_lastTileUsed, 0,0,null);
			g2.setComposite(c);
			m_tintedTiles[direction] = i;
			m_lastTileUsed = i;
		} else {
			m_lastTileUsed = m_tintedTiles[direction];
		}

		if (m_lastTileUsed!=null) g.drawImage(m_lastTileUsed,x,y,null);
	}
		
	public String rgbStringColor() {
		String out="";
		char hex[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		int v,v1,v2;
		
		v = (int)(m_r*255);
		v1 = v/16;
		v2 = v%16;		
		out+=hex[v1] + "" +  hex[v2];

		v = (int)(m_g*255);
		v1 = v/16;
		v2 = v%16;		
		out+=hex[v1] + "" +  hex[v2];
		
		v = (int)(m_b*255);
		v1 = v/16;
		v2 = v%16;		
		out+=hex[v1] + "" +  hex[v2];
		
		return out;
	}

	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
					 "  <type>" + getClass().getSimpleName() + "</type>\n";				
		
		out+="  <x>" + x + "</x>\n";
		out+="  <y>" + y + "</y>\n";
		out+="  <owner>" + owner + "</owner>\n";
		out+="  <direction>" + direction + "</direction>\n";
		out+="  <next_shot_delay>" + next_shot_delay + "</next_shot_delay>\n";
		out+="  <next_move_delay>" + Math.max(Math.abs(m_moving_x),Math.abs(m_moving_y)) + "</next_move_delay>\n";
		out+="  <color>" + rgbStringColor() + "</color>\n";
					
		return out + "</entity>\n";
	}	


}