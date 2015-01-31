/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
 ****************************************************************************/
package s2.entities.buildings;

import gatech.mmpm.Entity;

public class WFortress extends WBuilding {

	public WFortress() {
		setConstants();
	}

	private void setConstants() {
		max_hitpoints = 1600;
		width = 4;
		length = 4;
		cost_gold = 2500;
		cost_wood = 1200;
		this.spriteName = "graphics/fortress.png";
	}

	public WFortress(WFortress incoming) {
		super(incoming);
		setConstants();
	}

	public Object clone() {
		WFortress e = new WFortress(this);
		return e;
	}

	public static boolean isActive() {
		return true;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WFortress ret;
		ret = new s2.mmpm.entities.WFortress(""+entityID, owner);
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