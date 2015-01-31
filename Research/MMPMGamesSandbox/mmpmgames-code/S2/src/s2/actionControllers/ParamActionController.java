package s2.actionControllers;

import java.util.Set;

import s2.entities.WUnit;
import s2.game.HUD;

public class ParamActionController extends ActionController {
	protected String paramName;
	public ParamActionController(Set<WUnit> i_unit,String spriteName, int hudBoundary, String i_paramName) {
		super(i_unit,spriteName,hudBoundary,i_paramName);
		paramName = i_paramName;
	}


	@Override
	public boolean clickForParams(int map_x, int map_y, int current_screen_x,
			int current_screen_y, HUD theHUD) {
		return false;
	}

	@Override
	protected void enableActionParameters(HUD theHUD) {

	}

	@Override
	public boolean paramsSatisfied() {
		return false;
	}

	@Override
	public void performAction() {

	}

	@Override
	public void reset() {

	}

	public String getParamName() {
		return paramName;
	}


	@Override
	public boolean scrollLock() {
		return false;
	}
	
	public Object clone() {
		// This should never be called...
		return null;
	}
	
}
