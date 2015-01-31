/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
 ****************************************************************************/
package s2.entities.buildings;

import gatech.mmpm.Entity;

import java.util.List;

import s2.actionControllers.ActionController;
import s2.entities.WPlayer;
import s2.game.S2;

public class WBarracks extends WBuilding {

	public WBarracks() {
		setConstants();
	}

	private void setConstants() {
		max_hitpoints = 800;
		width = 3;
		length = 3;
		cost_gold = 700;
		cost_wood = 450;
		this.spriteName = "graphics/barracks.png";

		actionList.add("Train");
		allowedUnits.add("s2.entities.troops.WFootman");
	}

	public WBarracks(WBarracks incoming) {
		super(incoming);
		setConstants();
	}

	public Object clone() {
		WBarracks e = new WBarracks(this);
		return e;
	}

	public static boolean isActive() {
		return true;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WBarracks ret;
		ret = new s2.mmpm.entities.WBarracks(""+entityID, owner);
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

	/**
	 * calls super and checks for training dependencies
	 */
	public void cycle(int m_cycle, S2 m_game, List<ActionController> failedActions) {
		super.cycle(m_cycle, m_game, failedActions);
		if (m_cycle % 25 == 0) {
			WPlayer player = m_game.getPlayer(owner);
			if (null == m_game.getUnitType(player, WStable.class)) {
				allowedUnits.remove("s2.entities.troops.WKnight");
			} else {
				allowedUnits.add("s2.entities.troops.WKnight");
			}
			
			if (null == m_game.getUnitType(player, WBlacksmith.class)) {
				allowedUnits.remove("s2.entities.troops.WCatapult");
			} else {
				allowedUnits.add("s2.entities.troops.WCatapult");
			}
			
			if (null == m_game.getUnitType(player, WLumberMill.class)) {
				allowedUnits.remove("s2.entities.troops.WArcher");
			} else {
				allowedUnits.add("s2.entities.troops.WArcher");
			}
		}
	}
	
}