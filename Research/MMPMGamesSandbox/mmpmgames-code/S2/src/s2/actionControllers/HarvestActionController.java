/**
 * 
 */
package s2.actionControllers;

import java.util.Set;

import s2.entities.S2PhysicalEntity;
import s2.entities.WUnit;
import s2.entities.buildings.WGoldMine;
import s2.entities.map.WOTree;
import s2.entities.troops.WPeasant;
import s2.game.HUD;
import s2.game.S2;

/**
 * @author kane
 * 
 */
public class HarvestActionController extends ActionController {

	private S2PhysicalEntity target;

	/** the game overview. needed to get the unit that was clicked on */
	private S2 s2;

	public HarvestActionController(Set<WUnit> i_unit, int hudBoundary, S2 s2) {
		super(i_unit, "graphics/actions/harvest.png", hudBoundary, "harvest");
		this.s2 = s2;
	}

	public HarvestActionController(Set<WUnit> i_unit, S2PhysicalEntity i_target, S2 s2) {
		super(i_unit, "graphics/actions/harvest.png", 0, "harvest");
		this.s2 = s2;
		target = i_target;
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
		if (entity instanceof WGoldMine) {
			target = entity;
			return true;
		}
		entity = s2.mapEntityAt(map_x, map_y);
		if (entity instanceof WOTree) {
			target = entity;
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
		if (target != null) {
			if (target instanceof WOTree) {
				return true;
			}
			if (target instanceof WGoldMine) {
				return ((WGoldMine) target).getRemaining_gold() > 0;
			}
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
		for (WUnit current : units) {
			WPeasant currentTroop = (WPeasant) current;
			if (target instanceof WOTree) {
				currentTroop.harvest(target.getX(), target.getY(), this);
			}
			if (target instanceof WGoldMine) {
				currentTroop.harvest(target.entityID, this);
			}
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

	public S2PhysicalEntity getTarget() {
		return target;
	}

	public Object clone() {
		return new HarvestActionController(units, target, s2);
	}

}
