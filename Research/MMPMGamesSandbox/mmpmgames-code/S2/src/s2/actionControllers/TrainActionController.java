package s2.actionControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import s2.entities.WUnit;
import s2.entities.buildings.WBuilding;
import s2.game.HUD;

public class TrainActionController extends ActionController {

	public static List<String> ALL_UNIT_MAPPINGS = new ArrayList<String>();

	protected int unitType = -1;

	public static final int START_STATE = 0;

	public static final int PARAM_SELECTED_STATE = 1;

	protected int current_state = START_STATE;

	protected ParamActionController selectedActionParam = null;

	static {
		ALL_UNIT_MAPPINGS.add(0, "s2.entities.troops.WPeasant");
		ALL_UNIT_MAPPINGS.add(1, "s2.entities.troops.WFootman");
		ALL_UNIT_MAPPINGS.add(2, "s2.entities.troops.WArcher");
		ALL_UNIT_MAPPINGS.add(3, "s2.entities.troops.WCatapult");
		ALL_UNIT_MAPPINGS.add(4, "s2.entities.troops.WKnight");
	}

	/**
	 * human constructor.
	 * 
	 * @param boundary_y
	 */
	public TrainActionController(Set<WUnit> i_unit, int boundary_y) {
		super(i_unit, "graphics/actions/train.png", boundary_y, "train");
	}

	/**
	 * ai constructor
	 * 
	 * @param i_unit
	 * @param unitType
	 * @param no
	 *            don't use this, differentiating from other constructor TODO
	 *            fix param hack
	 */
	public TrainActionController(Set<WUnit> i_unit, int unitType, boolean no) {
		super(i_unit, "graphics/actions/train.png", 0, "train");
		this.unitType = unitType;
		current_state = PARAM_SELECTED_STATE;
	}

	@Override
	public boolean clickForParams(int map_x, int map_y, int current_screen_x, int current_screen_y,
			HUD theHUD) {
		// First click must be INSIDE the HUD checking for click on
		// ActionParameters
		if (current_state == START_STATE) {
			// check for click inside HUD
			selectedActionParam = (ParamActionController) theHUD.hudParamClick(current_screen_x,
					current_screen_y);
			if (selectedActionParam == null) {
				reset();
				return false;
			}
			unitType = ALL_UNIT_MAPPINGS.indexOf(selectedActionParam.getParamName());
			current_state = PARAM_SELECTED_STATE;
			return true;
		}

		reset();
		return false;
	}

	@Override
	public boolean paramsSatisfied() {
		if (current_state == PARAM_SELECTED_STATE && unitType >= 0) {
			return true;
		}

		return false;
	}

	@Override
	public void performAction() {

		// All validations have been performed, so you can directly cast here
		for (WUnit currentBuilding : units) {
			((WBuilding) currentBuilding).train(unitType, this);
		}
	}

	@Override
	protected void enableActionParameters(HUD theHUD) {
		theHUD.resetActionParams();
		theHUD.addActionParams("s2.entities.troops.WPeasant", new ParamActionController(units, "graphics/peasant.png",
				hudBoundary_y, "s2.entities.troops.WPeasant"));
		theHUD.addActionParams("s2.entities.troops.WFootman", new ParamActionController(units, "graphics/footman.png",
				hudBoundary_y, "s2.entities.troops.WFootman"));
		theHUD.addActionParams("s2.entities.troops.WArcher", new ParamActionController(units, "graphics/archer.png",
				hudBoundary_y, "s2.entities.troops.WArcher"));
		theHUD.addActionParams("s2.entities.troops.WCatapult", new ParamActionController(units,
				"graphics/catapult.png", hudBoundary_y, "s2.entities.troops.WCatapult"));
		theHUD.addActionParams("s2.entities.troops.WKnight", new ParamActionController(units, "graphics/knight.png",
				hudBoundary_y, "s2.entities.troops.WKnight"));
	}

	@Override
	public void reset() {
		x = -1;
		y = -1;
		unitType = -1;
		current_state = START_STATE;
		selectedActionParam = null;

	}

	@Override
	public boolean scrollLock() {
		if (selectedActionParam == null)
			return true;
		return false;
	}

	public int getType() {
		return unitType;
	}

	public Object clone() {
		return new TrainActionController(units, unitType, true);
	}

}
