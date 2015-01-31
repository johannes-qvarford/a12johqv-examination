/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
****************************************************************************/
package s2.entities.buildings;

import gatech.mmpm.Entity;

public class WStable extends WBuilding {


	public WStable()
	{
		setConstants();
	}
	private void setConstants() {
		max_hitpoints = 500;
		width = 3;
		length = 3;
		cost_gold = 1000;
		cost_wood = 300;
		this.spriteName = "graphics/stables.png";
	}
	public WStable( WStable incoming )
	{
		super(incoming);
		setConstants();
	}
	public Object clone() {
		WStable e = new WStable(this);
		return e;
	}


	public static boolean isActive() 
	{
		return true;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WStable ret;
		ret = new s2.mmpm.entities.WStable(""+entityID, owner);
		ret.setx(x);
		ret.sety(y);
		ret.setCurrent_hitpoints(current_hitpoints);
		ret.setCycle_created(cycle_created);
		ret.setCycle_last_attacked(cycle_last_attacked);
		if (creator==null) ret.setCreator("");
		  else ret.setCreator(creator.getEntityID()+""); 
		if (status.size()==0) ret.setStatus("0");
		 else ret.setStatus(status.get(0)+"");
		return ret;
	}

}