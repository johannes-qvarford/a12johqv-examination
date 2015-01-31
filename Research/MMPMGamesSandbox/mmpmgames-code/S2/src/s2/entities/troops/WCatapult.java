/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
 ****************************************************************************/
package s2.entities.troops;

import gatech.mmpm.Entity;

public class WCatapult extends WTroop {

	public WCatapult() {
		setConstants();
	}

	public WCatapult(WCatapult incoming) {
		super(incoming);
		setConstants();
	}

	private void setConstants() {
		attack = 80;
		range = 8;
		max_hitpoints = 110;
		width = 1;
		length = 1;
		cost_gold = 900;
		cost_wood = 300;
		this.spriteName = "graphics/catapult.png";
	}

	public Object clone() {
		WCatapult e = new WCatapult(this);
		return e;
	}

	public static boolean isActive() {
		return true;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WCatapult ret;
		ret = new s2.mmpm.entities.WCatapult(""+entityID, owner);
		ret.setx(x);
		ret.sety(y);
		ret.setCurrent_hitpoints(current_hitpoints);
		ret.setCycle_created(cycle_created);
		ret.setCycle_last_attacked(cycle_last_attacked);
		ret.setRange(range);
		ret.setAttack(attack);
		if (creator==null) ret.setCreator("");
		  else ret.setCreator(creator.getEntityID()+""); 
		if (status.size()==0) ret.setStatus("0");
		 else ret.setStatus(status.get(0)+"");
		return ret;
	}

}