package bc.ai;


import java.io.IOException;
import java.util.List;

import bc.BattleCity;
import bc.PlayerInput;
import bc.helpers.VirtualController;
import bc.mmpm.entities.BCOBullet;
import bc.objects.BCOBase;
import bc.objects.BCOTank;
import bc.objects.BCPhysicalEntity;

public class AIFollowerGreedy extends AIRandom {

	public AIFollowerGreedy(String playerID) {
		super(playerID);
	}
	
	public void game_cycle(VirtualController vc,BattleCity game,BCOTank tank) throws IOException, ClassNotFoundException {
		int move_command=-1;
		boolean fire_command = false;

		vc.m_joystick[PlayerInput.DIRECTION_UP]=false;
		vc.m_joystick[PlayerInput.DIRECTION_DOWN]=false;
		vc.m_joystick[PlayerInput.DIRECTION_LEFT]=false;
		vc.m_joystick[PlayerInput.DIRECTION_RIGHT]=false;
		vc.m_button[0]=false;
	    
	    if (tank.readyToFire()) {
			int dir = tank.getdirection();
			if (move_command!=-1) dir = move_command;
			BCPhysicalEntity o=on_sight(dir,game.getMap(),"BCOBase",tank);
			
			if (o==null ||
				!((BCOBase)o).getowner().equals(m_playerID)) fire_command=true;	
			
			if (fire_command) vc.m_button[0]=true;
		} // if     
			
		if (tank.readyToMove()) {
	        List<BCPhysicalEntity> l = game.getMap().getObjects("BCOPlayerTank");
	        BCPhysicalEntity closest=null;
	        
	        if (l==null || l.size()==1) l = game.getMap().getObjects("BCOEnemyTank");

	        int min_distance = -1;
	        
	        for(BCPhysicalEntity o:l) {
	            if (o!=tank) {
	                int d = (tank.getx()-o.getx())*(tank.getx()-o.getx()) + (tank.gety()-o.gety())*(tank.gety()-o.gety());
	                if (min_distance==-1 || d<min_distance) {
	                    closest = o;
	                    min_distance = d;
	                }
	            }
	        } // while
	        	        
	        if (closest!=null) {
	            int xoff[]={0,8,0,-8};
	            int yoff[]={-8,0,8,0};
	            int direction1 = -1;
	            int direction2 = -1;
	            int offsx = tank.getx()-closest.getx();
	            int offsy = tank.gety()-closest.gety();
	            
	            if (Math.abs(offsx)>Math.abs(offsy)) {
	                if (offsx>0) direction1 = PlayerInput.DIRECTION_LEFT;
	                        else direction1 = PlayerInput.DIRECTION_RIGHT; 
	                if (offsy>0) direction2 = PlayerInput.DIRECTION_UP;
	                        else direction2 = PlayerInput.DIRECTION_DOWN; 
	            } else {
	                if (offsy>0) direction1 = PlayerInput.DIRECTION_UP;
	                        else direction1 = PlayerInput.DIRECTION_DOWN; 
	                if (offsx>0) direction2 = PlayerInput.DIRECTION_LEFT;
	                        else direction2 = PlayerInput.DIRECTION_RIGHT; 
	            }
	            
	            if (!game.getMap().collisionExcludingObject(tank,xoff[direction1],yoff[direction1],BCOBullet.class)) {
	                move_command = direction1;
	            } else {
	                if (!game.getMap().collisionExcludingObject(tank,xoff[direction2],yoff[direction2],BCOBullet.class)) {
	                    move_command = direction2;
	                }
                }
	        }
	        
			if (move_command!=-1) vc.m_joystick[move_command]=true;
		} // if 
	}
}
