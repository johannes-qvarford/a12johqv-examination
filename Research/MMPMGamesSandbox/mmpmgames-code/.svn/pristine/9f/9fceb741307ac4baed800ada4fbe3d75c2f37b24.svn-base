/**
 * 
 */
package s2.actionControllers;

import java.util.Set;

import s2.entities.S2PhysicalEntity;
import s2.entities.WUnit;
import s2.entities.troops.WTroop;
import s2.game.HUD;
import s2.game.S2;

/**
 * @author kane
 * 
 */
public class AttackActionController extends ActionController {

	private WUnit target;

	/** the game overview. needed to get the unit that was clicked on */
	private S2 s2;

	/**
	 * human controller
	 * 
	 * @param boundary_y
	 */
	public AttackActionController(Set<WUnit> i_unit, int boundary_y, S2 s2) {
		super(i_unit, "graphics/actions/attack.png", boundary_y, "attack");
		this.s2 = s2;
	}

	/**
	 * ai controller
	 * 
	 * @param boundary_y
	 */
	public AttackActionController(Set<WUnit> i_unit, WUnit target, S2 s2) {
		super(i_unit, "graphics/actions/attack.png", 0, "attack");
		this.s2 = s2;
		this.target = target;
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
		if (entity instanceof WUnit) {
			target = (WUnit) entity;
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
		// only want a target that isn't already dead
		if (target != null && target.getCurrent_hitpoints() > 0) {
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
		if (target != null) {
			for (WUnit currentTroop : units) {
				((WTroop) currentTroop).attack(target.entityID, this);
			}
		} else {
			System.err.println("Target unit does not exist!!!");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see entities.actionControllers.ActionController#reset()
	 */
	@Override
	public void reset() {
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
		return new AttackActionController(units, target, s2);
	}

}
