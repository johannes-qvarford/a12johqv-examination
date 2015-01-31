/**
 * 
 */
package s2.actionControllers;

import java.util.Set;

import s2.entities.WUnit;
import s2.entities.troops.WTroop;
import s2.game.HUD;

/**
 * @author kane
 * 
 */
public class StandGroundActionController extends ActionController {

	public StandGroundActionController(Set<WUnit> i_unit, int hudBoundary) {
		super(i_unit, "graphics/actions/standGround.png", hudBoundary, "standGround");
	}

	public StandGroundActionController(Set<WUnit> i_unit) {
		super(i_unit, "graphics/actions/standGround.png", 0, "standGround");
	}

	public boolean paramsSatisfied() {
		return true;
	}

	public boolean clickForParams(int map_x, int map_y, int current_screen_x, int current_screen_y,
			HUD theHUD) {
		return true;
	}

	public void performAction() {
		// All validations have been performed, so you can directly cast here
		for (WUnit currentTroop : units) {
			((WTroop) currentTroop).standGround(this);
		}
	}

	protected void enableActionParameters(HUD theHUD) {
		// Do nothing as there are no Action parameters to be displayed
		theHUD.resetActionParams();
	}

	public void reset() {
		x = -1;
		y = -1;
	}

	public boolean scrollLock() {
		return false;
	}

	public Object clone() {
		return new StandGroundActionController(units);
	}
}
