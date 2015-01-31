package bc.ai;


import java.io.IOException;

import bc.BattleCity;
import bc.PlayerInput;
import bc.helpers.VirtualController;
import bc.mmpm.entities.BCOBullet;
import bc.objects.BCOBase;
import bc.objects.BCOTank;
import bc.objects.BCPhysicalEntity;

public class AIMeek extends AIRandom {

	public AIMeek(String playerID) {
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


		// RANDOM-CONTINUOUS MOVEMENT - FIRE WHEN THEY SEE PLAYER TANK
		if (tank.readyToFire()) {
			int dir = tank.getdirection();
			if (move_command!=-1) dir = move_command;
			
			if (on_sight(dir,game.getMap(),"BCOPlayerTank",tank)!=null) fire_command = true;
			if (on_sight(dir,game.getMap(),"BCOBase",tank)!=null) fire_command = true;

			BCPhysicalEntity o=on_sight(dir,game.getMap(),"BCOBase",tank);			
			if (o!=null && ((BCOBase)o).getowner().equals(m_playerID)) fire_command=false;
			
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

}
