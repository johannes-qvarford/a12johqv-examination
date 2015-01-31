/**
 * 
 */
package s2.actionControllers;

import java.util.Set;

import s2.entities.S2PhysicalEntity;
import s2.entities.WUnit;
import s2.entities.buildings.WBuilding;
import s2.entities.troops.WPeasant;
import s2.game.HUD;
import s2.game.S2;

/**
 * @author kane
 * 
 */
public class RepairActionController extends ActionController {

	private WBuilding target;

	/** the game overview. needed to get the unit that was clicked on */
	private S2 s2;

	/**
	 * @param boundary_y
	 */
	public RepairActionController(Set<WUnit> i_unit, int boundary_y, S2 s2) {
		super(i_unit, "graphics/actions/repair.png", boundary_y, "repair");
		this.s2 = s2;
	}

	public RepairActionController(Set<WUnit> i_unit, WBuilding a_target, S2 s2) {
		super(i_unit, "graphics/actions/repair.png", 0, "repair");
		this.s2 = s2;
		target = a_target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see entities.actionControllers.ActionController#clickForParams(int, int,
	 *      int, int, game.HUD)
	 */
	@Override
	public boolean clickForParams(int map_x, int map_y, int current_screen_x, int current_screen_y,
			HUD theHUD) {
		S2PhysicalEntity entity = s2.entityAt(map_x, map_y);
		if (entity instanceof WBuilding) {
			target = (WBuilding) entity;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see entities.actionControllers.ActionController#enableActionParameters(game.HUD)
	 */
	@Override
	protected void enableActionParameters(HUD theHUD) {
		// Do nothing as there are no Action parameters to be displayed
		theHUD.resetActionParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see entities.actionControllers.ActionController#paramsSatisfied()
	 */
	@Override
	public boolean paramsSatisfied() {
		// only want a target that isn't already dead or at max HP
		if (target != null && target.getCurrent_hitpoints() > 0
				&& target.getCurrent_hitpoints() != target.getMax_hitpoints()) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see entities.actionControllers.ActionController#performAction(entities.PhysicalEntity)
	 */
	@Override
	public void performAction() {
		// All validations have been performed, so you can directly cast here
		for (WUnit currentTroop : units) {
			((WPeasant) currentTroop).repair(target.entityID, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see entities.actionControllers.ActionController#reset()
	 */
	@Override
	public void reset() {
		x = -1;
		y = -1;
		target = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see entities.actionControllers.ActionController#scrollLock()
	 */
	@Override
	public boolean scrollLock() {
		return false;
	}

	public WUnit getTarget() {
		return target;
	}

	public Object clone() {
		return new RepairActionController(units, target, s2);
	}

}
