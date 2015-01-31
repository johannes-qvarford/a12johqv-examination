package bc.ai;


import java.io.IOException;
import java.util.List;

import bc.BCMap;
import bc.BattleCity;
import bc.PlayerInput;
import bc.helpers.VirtualController;
import bc.objects.BCOTank;
import bc.objects.BCPhysicalEntity;

public class AIDefense extends AIRandom {

	public AIDefense(String playerID) {
		super(playerID);
	}
	
	public void game_cycle(VirtualController vc,BattleCity game,BCOTank tank) throws IOException, ClassNotFoundException {
		int move_command=-1;
		boolean fire_command = false;

		vc.m_joystick[PlayerInput.DIRECTION_UP]=false;
		vc.m_joystick[PlayerInput.DIRECTION_RIGHT]=false;
		vc.m_joystick[PlayerInput.DIRECTION_DOWN]=false;
		vc.m_joystick[PlayerInput.DIRECTION_LEFT]=false;
		vc.m_button[0]=false;
	    
	    
	    if (tank.readyToFire()) {
			int dir = tank.getdirection();
			if (move_command!=-1) dir = move_command;
			BCPhysicalEntity o=on_sight(dir,game.getMap(),"BCOBase",tank);
			
			if (o==null ||
				!((BCPhysicalEntity)o).getowner().equals(m_playerID)) fire_command=true;	
			
			if (fire_command) vc.m_button[0]=true;
		} // if     
		
		if (tank.readyToMove()) {
	        int min_distance = -1;
	        int d;
	        int i;
	        
	        for(i=0;i<4;i++) {
	            d = distanceOfClosestTankOrBullet(i,game.getMap(),tank);
	            if (d!=-1) {
	                if (move_command==-1 || d<min_distance) {
	                    move_command = i;
	                    min_distance = d;
	                }
	            }
	        }
	        
			if (move_command!=-1 && move_command!=tank.getdirection()) vc.m_joystick[move_command]=true;
		} // if 
	}

	int distanceOfClosestTankOrBullet(int direction,BCMap map,BCOTank tank) throws ClassNotFoundException
	{
		List<BCPhysicalEntity> l = map.getObjects("BCOPlayerTank");

		int min_distance = -1;

		for(BCPhysicalEntity o:l) {
			switch(direction) {
			case PlayerInput.DIRECTION_UP:if (o.getx()+32>=tank.getx() && o.getx()<tank.getx()+32 &&
					o.gety()<tank.gety()) {
				int d = tank.gety()-o.gety();
				if (min_distance==-1 || d<min_distance) min_distance = d;
			} // if 
			break;
			case PlayerInput.DIRECTION_DOWN:if (o.getx()+32>=tank.getx() && o.getx()<tank.getx()+32 &&
					o.gety()>tank.gety()) {
				int d = o.gety()-tank.gety();
				if (min_distance==-1 || d<min_distance) min_distance = d;
			} // if 
			break;
			case PlayerInput.DIRECTION_LEFT:if (o.gety()+32>=tank.gety() && o.gety()<tank.gety()+32 &&
					o.getx()<tank.getx()) {
				int d = tank.getx()-o.getx();
				if (min_distance==-1 || d<min_distance) min_distance = d;
			} // if 
			break;
			case PlayerInput.DIRECTION_RIGHT:if (o.gety()+32>=tank.gety() && o.gety()<tank.gety()+32 &&
					o.getx()>tank.getx()) {
				int d = o.getx()-tank.getx();
				if (min_distance==-1 || d<min_distance) min_distance = d;
			} // if 
			break;
			} // switch
		} // while 
		return min_distance;
	}


}
