/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
 ****************************************************************************/
package s2.entities.buildings;

import gatech.mmpm.Entity;


public class WTower extends WBuilding {

	public WTower() {
		setConstants();
	}

	public WTower(WTower incoming) {
		super(incoming);
		setConstants();
	}

	private void setConstants() {
		attack = 3;
		range = 8;
		max_hitpoints = 110;
		width = 2;
		length = 2;
		cost_gold = 900;
		cost_wood = 300;
		this.spriteName = "graphics/tower.png";
		//Always stand ground!
		status.add(ACTION_STAND_GROUND);
	}

	public Object clone() {
		WTower e = new WTower(this);
		return e;
	}

	public static boolean isActive() {
		return true;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WTower ret;
		ret = new s2.mmpm.entities.WTower(""+entityID, owner);
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