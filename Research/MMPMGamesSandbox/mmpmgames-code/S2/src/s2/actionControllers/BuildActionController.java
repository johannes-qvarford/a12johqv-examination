package s2.actionControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import s2.entities.WUnit;
import s2.entities.troops.WPeasant;
import s2.game.HUD;

public class BuildActionController extends ActionController {

	public static List<String> ALL_BUILDING_MAPPINGS = new ArrayList<String>();

	protected int x = -1, y = -1;

	protected int building = -1;

	public static final int START_STATE = 0;

	public static final int PARAM_SELECTED_STATE = 1;

	public static final int LOCATION_SELECTED_STATE = 2;

	protected int current_state = START_STATE;

	protected ParamActionController selectedActionParam = null;

	static {
		ALL_BUILDING_MAPPINGS.add(0, "s2.entities.buildings.WTownhall");
		ALL_BUILDING_MAPPINGS.add(1, "s2.entities.buildings.WBarracks");
		ALL_BUILDING_MAPPINGS.add(2, "s2.entities.buildings.WStable");
		ALL_BUILDING_MAPPINGS.add(3, "s2.entities.buildings.WFortress");
		ALL_BUILDING_MAPPINGS.add(4, "s2.entities.buildings.WLumberMill");
		ALL_BUILDING_MAPPINGS.add(5, "s2.entities.buildings.WBlacksmith");
		ALL_BUILDING_MAPPINGS.add(6, "s2.entities.buildings.WTower");
	}

	/**
	 * human action.
	 * 
	 * @param i_unit
	 * @param hudBoundary
	 */
	public BuildActionController(Set<WUnit> i_unit, int hudBoundary) {
		super(i_unit, "graphics/actions/build.png", hudBoundary, "build");
	}

	/**
	 * AI action.
	 * 
	 * @param i_unit
	 * @param x
	 * @param y
	 * @param building
	 */
	public BuildActionController(Set<WUnit> i_unit, int x, int y, int building) {
		super(i_unit, "graphics/actions/build.png", 0, "build");
		this.x = x;
		this.y = y;
		this.building = building;
		current_state = LOCATION_SELECTED_STATE;
	}

	public int getBuildX() {
		return x;
	}

	public int getBuildY() {
		return y;
	}

	@Override
	public boolean clickForParams(int map_x, int map_y, int current_screen_x, int current_screen_y,
			HUD theHUD) {
		// First click must be INSIDE the HUD checking for click on
		// ActionParameters
		// Second click must be OUTSIDE the HUD on the map to decide Building
		// location
		System.out.println("BuildActionController.clickForParams: current_state = " + current_state);
		if (current_state == START_STATE) {
			// check for click inside HUD
			selectedActionParam = (ParamActionController) theHUD.hudParamClick(current_screen_x,
					current_screen_y);
			if (selectedActionParam == null) {
				reset();
				return false;
			}
			building = ALL_BUILDING_MAPPINGS.indexOf(selectedActionParam.getParamName());
			current_state = PARAM_SELECTED_STATE;
			return true;
		}

		if (current_state == PARAM_SELECTED_STATE) {
			// check for click outside HUD
			if (current_screen_y < hudBoundary_y) {
				x = map_x;
				y = map_y;
				current_state = LOCATION_SELECTED_STATE;
				System.out.println("BuildActionController.clickForParams: ok!");
				return true;
			}
			System.out.println("BuildActionController.clickForParams: click outside boundary");
		}
		reset();
		return false;
	}

	@Override
	public boolean paramsSatisfied() {
		if (current_state == LOCATION_SELECTED_STATE)
			if (x >= 0 && y >= 0 && building >= 0)
				return true;
		return false;
	}

	@Override
	public void performAction() {
		// All validations have been performed, so you can directly cast here
		for (WUnit currentPeasant : units) {
			((WPeasant) currentPeasant).build(x, y, building, this);
		}
	}

	@Override
	protected void enableActionParameters(HUD theHUD) {
		theHUD.resetActionParams();
		theHUD.addActionParams("s2.entities.buildings.WTownHall", new ParamActionController(units,
				"graphics/townhall.png", hudBoundary_y, "s2.entities.buildings.WTownhall"));
		theHUD.addActionParams("s2.entities.buildings.WBarracks", new ParamActionController(units,
				"graphics/barracks.png", hudBoundary_y, "s2.entities.buildings.WBarracks"));
		theHUD.addActionParams("s2.entities.buildings.WStable", new ParamActionController(units, "graphics/stables.png",
				hudBoundary_y, "s2.entities.buildings.WStable"));
		theHUD.addActionParams("s2.entities.buildings.WFortress", new ParamActionController(units,
				"graphics/fortress.png", hudBoundary_y, "s2.entities.buildings.WFortress"));
		theHUD.addActionParams("s2.entities.buildings.WLumbermill", new ParamActionController(units,
				"graphics/lumbermill.png", hudBoundary_y, "s2.entities.buildings.WLumberMill"));
		theHUD.addActionParams("s2.entities.buildings.WBlacksmith", new ParamActionController(units,
				"graphics/blacksmith.png", hudBoundary_y, "s2.entities.buildings.WBlacksmith"));
		theHUD.addActionParams("s2.entities.buildings.WTower", new ParamActionController(units, "graphics/tower.png",
				hudBoundary_y, "s2.entities.buildings.WTower"));
	}

	@Override
	public void reset() {
		x = -1;
		y = -1;
		building = -1;
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
		return building;
	}

	public Object clone() {
		return new BuildActionController(units, x, y, building);
	}
}
