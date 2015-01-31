package bc.objects;


import java.io.IOException;
import java.util.List;

import bc.Action;
import bc.BCMap;
import bc.BattleCity;
import bc.PlayerInput;
import bc.ai.AIDefense;
import bc.ai.AIFollower;
import bc.ai.AIMeek;
import bc.ai.AIRandom;
import bc.helpers.VirtualController;

public class BCOEnemyTank extends BCOTank {
	public int ai_type;

	AIRandom ai = null;

	public BCOEnemyTank(int ax,int ay,int adirection,int atype) {
		entityID = "e" + (next_id++);
		width = 32;
		length = 32;
		x = ax;
		y = ay;			
		direction = adirection;
		ai_type = atype;
		
		if (ai_type==0) ai = new AIMeek("");
		if (ai_type==1) ai = new AIRandom("");
		if (ai_type==2) ai = new AIDefense("");
		if (ai_type==3) ai = new AIFollower("");
	}

	
	public BCOEnemyTank(String id, int ax,int ay,int adirection,int atype) {
		entityID = id;
		width = 32;
		length = 32;
		x = ax;
		y = ay;			
		direction = adirection;
		ai_type = atype;
		
		if (ai_type==0) ai = new AIMeek("");
		if (ai_type==1) ai = new AIRandom("");
		if (ai_type==2) ai = new AIDefense("");
		if (ai_type==3) ai = new AIFollower("");
	}
	
	public BCOEnemyTank( BCOEnemyTank incoming )
	{
		super(incoming);
		this.direction = incoming.direction;
		this.color = incoming.color;
		this.next_shot_delay = incoming.next_shot_delay;
		width = 32;
		length = 32;
		ai_type = incoming.ai_type;
		
		if (ai_type==0) ai = new AIMeek("");
		if (ai_type==1) ai = new AIRandom("");
		if (ai_type==2) ai = new AIDefense("");
		if (ai_type==3) ai = new AIFollower("");
	}
	
	public int getai_type()
	{
		return ai_type;
	}


	public boolean cycle(List<VirtualController> l_vc, BCMap map, BattleCity game, List<Action> actions) throws IOException, ClassNotFoundException {
		int m_old_x=x,m_old_y=y;

		if (!super.cycle(l_vc,map,game,actions)) return false;
		
		VirtualController selected_vc = new VirtualController();
		selected_vc.reset();
		selected_vc.m_button[0]=false;
		ai.game_cycle(selected_vc,game,this);

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

	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
					 "  <type>" + getClass().getSimpleName() + "</type>\n";				
		
		out+="  <x>" + x + "</x>\n";
		out+="  <y>" + y + "</y>\n";
		out+="  <owner>" + owner + "</owner>\n";
		out+="  <direction>" + direction + "</direction>\n";
		out+="  <next_shot_delay>" + next_shot_delay + "</next_shot_delay>\n";
		out+="  <next_move_delay>" + Math.max(Math.abs(m_moving_x),Math.abs(m_moving_y)) + "</next_move_delay>\n";
					
		return out + "</entity>\n";
	}	

}
