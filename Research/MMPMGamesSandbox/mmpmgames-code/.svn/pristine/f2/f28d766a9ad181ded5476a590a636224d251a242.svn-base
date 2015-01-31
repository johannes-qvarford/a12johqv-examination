/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Prafulla Mahindrakar
												Santi Ontanon
 ****************************************************************************/
package bc.objects;


import java.io.IOException;
import java.util.List;

import bc.Action;
import bc.BCMap;
import bc.BattleCity;
import bc.PlayerInput;
import bc.helpers.VirtualController;

public class BCOTankGenerator extends BCPhysicalEntity {
	public int time_for_next;
	public int remaining_tanks;
	public int interval;
	public int tank_type;

	public BCOTankGenerator(String id,int ax, int ay, int atime_for_next, int ainterval, int aremaining_tanks, int atank_type)
	{
		entityID = id;
		x = ax;
		y = ay;
		width = 0;
		height = 0;
		this.time_for_next = atime_for_next;
		this.remaining_tanks = aremaining_tanks;
		this.interval = ainterval;
		this.tank_type = atank_type;
	}

	public BCOTankGenerator()
	{
		width = 0;
		height = 0;
	}

	public BCOTankGenerator( BCOTankGenerator incoming )
	{
		super(incoming);
		width = 0;
		height = 0;
		this.time_for_next = incoming.time_for_next;
		this.remaining_tanks = incoming.remaining_tanks;
		this.interval = incoming.interval;
		this.tank_type = incoming.tank_type;
	}
	public Object clone() {
		BCOTankGenerator e = new BCOTankGenerator(this);
		return e;
	}


	public static boolean isActive() 
	{
		return false;
	}

	public int gettime_for_next()
	{
		return time_for_next;
	}

	public int getremaining_tanks()
	{
		return remaining_tanks;
	}

	public int getinterval()
	{
		return interval;
	}

	public int gettank_type()
	{
		return tank_type;
	}

	public void settime_for_next( int incoming )
	{
		this.time_for_next = incoming;
	}

	public void settime_for_next( String incoming )
	{
		this.time_for_next = Integer.parseInt(incoming);
	}

	public void setremaining_tanks( int incoming )
	{
		this.remaining_tanks = incoming;
	}

	public void setremaining_tanks( String incoming )
	{
		this.remaining_tanks = Integer.parseInt(incoming);
	}

	public void setinterval( int incoming )
	{
		this.interval = incoming;
	}

	public void setinterval( String incoming )
	{
		this.interval = Integer.parseInt(incoming);
	}

	public void settank_type( String incoming )
	{
		this.tank_type = Integer.parseInt(incoming);
	}


	public boolean cycle(List<VirtualController> l_vc, BCMap map, BattleCity game, List<Action> actions) throws IOException, ClassNotFoundException {
		if (!super.cycle(l_vc,map,game,actions)) return false;

		if (remaining_tanks<=0) return false;

		time_for_next--;
		if (time_for_next<=0) {
			BCOTank t = new BCOEnemyTank(x,y,PlayerInput.DIRECTION_UP,tank_type);

			if (!map.collision(t)) {
				map.addObject(t);

				remaining_tanks--;
				time_for_next = interval;
			} // if 
		} // if 

		return true;
	}
}