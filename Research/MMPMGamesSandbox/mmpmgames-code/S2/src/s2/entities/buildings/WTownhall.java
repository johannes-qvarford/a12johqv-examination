/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
 ****************************************************************************/
package s2.entities.buildings;

import gatech.mmpm.Entity;


public class WTownhall extends WBuilding {

	public WTownhall() {
		setConstants();
	}

	public WTownhall(WTownhall incoming) {
		super(incoming);
		setConstants();
	}

	private void setConstants() {
		max_hitpoints = 1200;
		width = 4;
		length = 4;
		cost_gold = 1200;
		cost_wood = 800;
		this.spriteName = "graphics/townhall.png";
		
		actionList.add("Train");
		allowedUnits.add("s2.entities.troops.WPeasant");
	}

	public Object clone() {
		WTownhall e = new WTownhall(this);
		return e;
	}

	public static boolean isActive() {
		return true;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WTownhall ret;
		ret = new s2.mmpm.entities.WTownhall(""+entityID, owner);
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