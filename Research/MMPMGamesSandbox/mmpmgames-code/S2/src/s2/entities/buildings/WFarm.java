/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
****************************************************************************/
package s2.entities.buildings;

import gatech.mmpm.Entity;

public class WFarm extends WBuilding {


	public WFarm()
	{
		max_hitpoints = 400;
		width = 2;
		length = 2;
		cost_gold = 500;
		cost_wood = 250;
		this.spriteName = "graphics/farm.png";
	}
	public WFarm( WFarm incoming )
	{
		super(incoming);
		max_hitpoints = 400;
		width = 2;
		length = 2;
		cost_gold = 500;
		cost_wood = 250;
		this.spriteName = "graphics/farm.png";
	}
	public Object clone() {
		WFarm e = new WFarm(this);
		return e;
	}


	public static boolean isActive() 
	{
		return true;
	}

	public Entity toD2Entity() {
		s2.mmpm.entities.WFarm ret;
		ret = new s2.mmpm.entities.WFarm(""+entityID, owner);
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