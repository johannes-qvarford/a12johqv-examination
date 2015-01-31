package s2.actionControllers;

import java.util.Set;

import s2.entities.WUnit;
import s2.entities.troops.WTroop;
import s2.game.HUD;

public class MoveActionController extends ActionController {

	private int x = -1, y = -1;

	public MoveActionController(Set<WUnit> i_unit,int hudBoundary) {
		super(i_unit,"graphics/actions/move.png", hudBoundary, "move");
	}

	public MoveActionController(Set<WUnit> i_unit,int a_x,int a_y) {
		super(i_unit,"graphics/actions/move.png", 0, "move");
		x = a_x;
		y = a_y;
	}
	
	public int getMoveX() {
		return x;
	}

	public int getMoveY() {
		return y;
	}


	public boolean paramsSatisfied() {
		System.out.println("satisfied " + units.size());
		if (x >= 0 && y >= 0) {
			return true;
		}
		return false;
	}

	public boolean clickForParams(int map_x, int map_y, int current_screen_x, int current_screen_y,
			HUD theHUD) {
		// Ignore the current x and y's but check to see if the area clicked is
		// on screen or not
		// we check to see if the click was on the HUD or not.
		// if so, return false, and that would make the selectedAction in S2App
		// to null
		if (current_screen_y < hudBoundary_y) {
			x = map_x;
			y = map_y;
			return true;
		}
		return false;
	}

	public void performAction() {
		System.out.println("perform " + units.size());
		// All validations have been performed, so you can directly cast here
		for(WUnit currentTroop : units) {
			((WTroop) currentTroop).move(x, y, this);
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
		return new MoveActionController(units,x,y);
	}

}
