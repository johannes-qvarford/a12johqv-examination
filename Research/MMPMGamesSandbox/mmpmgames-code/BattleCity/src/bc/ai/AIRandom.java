package bc.ai;


import java.io.IOException;
import java.util.List;
import java.util.Random;

import bc.BCMap;
import bc.BattleCity;
import bc.BattleCityApp;
import bc.PlayerInput;
import bc.helpers.VirtualController;
import bc.mmpm.entities.BCOBullet;
import bc.objects.BCOBase;
import bc.objects.BCOPlayerTank;
import bc.objects.BCOTank;
import bc.objects.BCPhysicalEntity;

public class AIRandom implements AI {
	
	static Random rg = new Random();
	
	public String m_playerID;

	public AIRandom(String playerID) {
		m_playerID = playerID;
	}
	
	public void cycle(int application_state,BattleCity game,VirtualController vc) throws ClassNotFoundException, IOException {
		switch(application_state) {
		case BattleCityApp.STATE_INIT:	vc.m_button[0]=true;
									break;
		case BattleCityApp.STATE_GAME:	{
										List<BCPhysicalEntity> l = game.getMap().getObjects("BCOPlayerTank");
										BCPhysicalEntity tank=null;

										for(BCPhysicalEntity o:l) 
											if (((BCOPlayerTank)o).getowner().equals(m_playerID)) tank=o;
										if (tank!=null) game_cycle(vc,game,(BCOTank)tank);
									}
									break;
		case BattleCityApp.STATE_QUITTING:	vc.m_button[0]=true;
										break;
		} // switch

	}
	
	public void game_cycle(VirtualController vc,BattleCity game,BCOTank tank) throws IOException, ClassNotFoundException {
		int move_command=-1;
		boolean fire_command = false;

		vc.m_joystick[PlayerInput.DIRECTION_UP]=false;
		vc.m_joystick[PlayerInput.DIRECTION_RIGHT]=false;
		vc.m_joystick[PlayerInput.DIRECTION_DOWN]=false;
		vc.m_joystick[PlayerInput.DIRECTION_LEFT]=false;
		vc.m_button[0]=false;


		// RANDOM-CONTINUOUS MOVEMENT - FIRE WHEN THEY SEE PLAYER TANK
		if (tank.readyToFire()) {
			int dir = tank.getdirection();
			if (move_command!=-1) dir = move_command;
			BCPhysicalEntity o=on_sight(dir,game.getMap(),"BCOBase",tank);
			
			if (o==null ||
				!((BCOBase)o).getowner().equals(m_playerID)) fire_command=true;	
			
			if (fire_command) vc.m_button[0]=true;
		} // if 
		
		
		if (tank.readyToMove()) {
			boolean valid[]={false,false,false,false};
			int max_valids=0;

			
			if (!game.getMap().collisionExcludingObject(tank,8,0,BCOBullet.class)) {
				valid[PlayerInput.DIRECTION_RIGHT]=true;
				max_valids++;
			} // if 
			if (!game.getMap().collisionExcludingObject(tank,-8,0,BCOBullet.class)) {
				valid[PlayerInput.DIRECTION_LEFT]=true;
				max_valids++;
			} // if 
			if (!game.getMap().collisionExcludingObject(tank,0,8,BCOBullet.class)) {
				valid[PlayerInput.DIRECTION_DOWN]=true;
				max_valids++;
			} // if 
			if (!game.getMap().collisionExcludingObject(tank,0,-8,BCOBullet.class)) {
				valid[PlayerInput.DIRECTION_UP]=true;
				max_valids++;
			} // if 

			if (!valid[tank.getdirection()] || rg.nextInt(4)==0) {
				if (max_valids>0) {
					do{
						move_command = rg.nextInt(4);
					}while(!valid[move_command]);						
				} // if 
			} else {
				move_command = tank.getdirection();
			} // if 

			if (move_command!=-1) vc.m_joystick[move_command]=true;
		} // if 
	}

	BCPhysicalEntity on_sight(int direction, BCMap map, String target_type,BCOTank tank) throws ClassNotFoundException {
		List<BCPhysicalEntity> l = map.getObjects(target_type);

		for(BCPhysicalEntity o:l) {
			switch(direction) {
			case PlayerInput.DIRECTION_UP:if (o.getx()+32>=tank.getx() && o.getx()<tank.getx()+32 &&
								  o.gety()<tank.gety()) {
								  return o;
							  } // if 
							  break;
			case PlayerInput.DIRECTION_DOWN:if (o.getx()+32>=tank.getx() && o.getx()<tank.getx()+32 &&
									o.gety()>tank.gety()) {
								  return o;
							  } // if 
							  break;
			case PlayerInput.DIRECTION_LEFT:if (o.gety()+32>=tank.gety() && o.gety()<tank.gety()+32 &&
									o.getx()<tank.getx()) {
								  return o;
							  } // if 
							  break;
			case PlayerInput.DIRECTION_RIGHT:if (o.gety()+32>=tank.gety() && o.gety()<tank.gety()+32 &&
									 o.getx()>tank.getx()) {
								  return o;
							  } // if 
							  break;
			} // switch
		} // while 
		
		return null;
	}

	public void gameEnd() {		
	}

	public void gameStarts() {
	}

	public String getPlayerId() {
		return m_playerID;
	}
}
