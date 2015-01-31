package s2.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import s2.actionControllers.ActionController;
import s2.actionControllers.AttackActionController;
import s2.actionControllers.BuildActionController;
import s2.actionControllers.HarvestActionController;
import s2.actionControllers.MoveActionController;
import s2.actionControllers.RepairActionController;
import s2.actionControllers.StandGroundActionController;
import s2.actionControllers.TrainActionController;
import s2.base.PlayerInput;
import s2.base.S2App;
import s2.entities.Sprite;
import s2.entities.SpriteStore;
import s2.entities.WPlayer;
import s2.entities.WUnit;
import s2.entities.buildings.WBuilding;
import s2.entities.troops.WPeasant;

public class HUD {
	private static final int PARAM_Y_LOC = S2App.SCREEN_Y * 7 / 8;

	public static final int HUD_Y_LOC = S2App.SCREEN_Y - S2App.SCREEN_Y / 8;

	protected static final String owner = "player1";

	private S2 s2;

	// protected WPlayer player;

	protected Sprite gold;

	protected Sprite wood;

	private HashMap<String, ActionController> actionMap;

	private HashMap<String, ActionController> actionParamMap;

	private final int thickness = 3;

	private int x_margin1 = 120;

	private int y_margin1 = HUD_Y_LOC + 6 * thickness;

	protected ActionController selectedHUDActionParam = null;

	private Set<WUnit> selectedUnits = null;

	public HUD(S2 game) {
		s2 = game;
		// player = game.getPlayers().get(0);
		// register sprite images for HUD
		gold = SpriteStore.get().getSprite("graphics/gold.png");
		wood = SpriteStore.get().getSprite("graphics/wood.png");

		addActionInfo();
		actionParamMap = new HashMap<String, ActionController>();
	}

	private void addActionInfo() {
		actionMap = new HashMap<String, ActionController>();
		// Create the action entities for
		// Move,Attack,Harvest,StandGround,Repair,Train,Build
		actionMap.put("Move", new MoveActionController(null, PARAM_Y_LOC));
		actionMap.put("Attack", new AttackActionController(null, PARAM_Y_LOC, s2));
		actionMap.put("Harvest", new HarvestActionController(null, PARAM_Y_LOC, s2));
		actionMap.put("StandGround", new StandGroundActionController(null, PARAM_Y_LOC));
		actionMap.put("Repair", new RepairActionController(null, PARAM_Y_LOC, s2));
		actionMap.put("Train", new TrainActionController(null, PARAM_Y_LOC));
		actionMap.put("Build", new BuildActionController(null, PARAM_Y_LOC));
	}

	private void drawPlayerInfo(Graphics2D g, List<PlayerInput> pi_l) {
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(0, HUD_Y_LOC, S2App.SCREEN_X, S2App.SCREEN_Y / 8, 10, 10);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(0 + thickness, HUD_Y_LOC + thickness, S2App.SCREEN_X - 2 * thickness,
				S2App.SCREEN_Y / 8 - thickness, 10, 10);

		// Player1 information
		int x_margin1 = 5;
		int y_margin1 = HUD_Y_LOC + 6 * thickness;
		for (WPlayer player : s2.getPlayers()) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 12));

			// gold
			gold.draw(g, x_margin1, y_margin1 - 10);
			g.drawString(player.getGold() + "", x_margin1 + 25, y_margin1 + 5);
			// wood
			wood.draw(g, x_margin1, y_margin1 + 8);
			g.drawString(player.getWood() + "", x_margin1 + 25, y_margin1 + 25);

			{
				for (PlayerInput pi : pi_l) {
					if (pi.m_playerID.equals(player.getOwner())) {
						g.drawString(pi.m_playerName, x_margin1 + 25, y_margin1 + 50);
					}
				}
			}

			x_margin1 += 80;
		}

		g.drawString(s2.getCycle() + "", S2App.SCREEN_X - 50, y_margin1 + 50);
	}

	/**
	 * draw the HUD on the screen.
	 * 
	 * @param g
	 * @param selectedUnits
	 * @param selectedAction
	 */
	public void draw(Graphics2D g, Set<WUnit> units, ActionController selectedAction,
			List<PlayerInput> pi_l) {
		selectedUnits = units;
		drawMessage(g);
		drawPlayerInfo(g, pi_l);
		drawSelectedUnit(g, selectedAction);
	}

	private void drawMessage(Graphics2D g) {
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Arial", Font.PLAIN, 16));
		g.drawString(s2.getMessage(), 10, HUD_Y_LOC - 10);
	}

	/**
	 * draw the unit image, name, hit points, and available actions.
	 * 
	 * @param g
	 * @param selectedAction
	 */
	private void drawSelectedUnit(Graphics2D g, ActionController selectedAction) {
		WUnit selected = null;
		
		//get selected unit
		if (selectedUnits.size() == 1) {
			selected = selectedUnits.iterator().next();
		}

		//select sprite
		if (selected != null && selected.spriteName != null && selected.sprite == null)
			selected.setSprite();

		//display unit info
		if (selected != null && selected.getCurrent_hitpoints() > 0) {
			g.setColor(Color.RED);
			g.drawLine(x_margin1 + 40, y_margin1 - 5, x_margin1 + 40, y_margin1 + 50);

			// need to scale the unit image
			if (selected.getWidth() > 1) {
				selected.sprite.draw(g, x_margin1 + 45, y_margin1 - 5, 40, 40);
			} else {
				selected.sprite.draw(g, x_margin1 + 45, y_margin1 - 5);
			}
			g.drawString(selected.featureValue("type").toString(), x_margin1 + 85, y_margin1 + 5);
			g.drawString(selected.getCurrent_hitpoints() + " HP", x_margin1 + 85, y_margin1 + 25);
		}

		// get list of actions allowed by all selected units
		List<String> actions = new LinkedList<String>();
		if (selectedUnits.size() > 0) {
			actions = selectedUnits.iterator().next().getActionList();
		}
		for (WUnit u : selectedUnits) {
			List<String> toKeep = new LinkedList<String>();
			for (String s : u.getActionList()) {
				if (actions.contains(s)) {
					toKeep.add(s);
				}
			}
			actions = toKeep;
		}
		
		
		//display actions
		int x_offset = 0;
		x_offset = drawActions(g, selectedAction, actions, x_offset);
		// Display action parameters (if any)
		x_offset += 40;
		drawActionParams(g, x_offset);

	}

	/**
	 * Draws the available actions for the selected unit.
	 * 
	 * @param g
	 * @param selectedAction
	 * @param actions
	 * @param x_offset
	 * @return
	 */
	private int drawActions(Graphics2D g, ActionController selectedAction, List<String> actions,
			int x_offset) {
		for (String action : actions) {
			actionMap.get(action).setx((x_margin1 + 155 + x_offset) / 8);
			actionMap.get(action).sety((y_margin1 + 10) / 8);
			actionMap.get(action).draw(g, 0, 0);
			// System.out.println(action);
			// Mark the action, if selected
			if (selectedAction != null) {
				if (selectedAction.getActionName().equalsIgnoreCase(action)) {
					g.setColor(Color.RED);
					g.setStroke(new BasicStroke(2));
					g.drawRect(((x_margin1 + 155 + x_offset) / 8) * ActionController.CELL_WIDTH,
							((y_margin1 + 10) / 8) * ActionController.CELL_HEIGHT, 32, 32);
				}
			}
			x_offset += 40;
		}
		return x_offset;
	}

	/**
	 * draws parameters for the action for the selected unit.
	 * 
	 * @param g
	 *            the graphics context.
	 * @param x_offset
	 *            the offset at which to draw the parameters.
	 */
	private void drawActionParams(Graphics2D g, int x_offset) {
		if (selectedUnits.size() == 0) {
			return;
		}
		WUnit unit = selectedUnits.iterator().next();
		Set<Entry<String, ActionController>> tmp = new HashSet<Entry<String,ActionController>>();
		tmp.addAll(actionParamMap.entrySet());
		for (Entry<String, ActionController> entry : tmp) {

			// if it's a building that can't train this unit, continue
			if (unit instanceof WBuilding
					&& (!((WBuilding) unit).getAllowedUnits().contains(entry.getKey()))) {
				continue;
			} else if (unit instanceof WPeasant
					&& (!((WPeasant) unit).getAllowedUnits().contains(entry.getKey()))) {
				continue;
			}
			ActionController ac = entry.getValue();

			ac.setx((x_margin1 + 155 + x_offset) / 8);
			ac.sety((y_margin1 + 10) / 8);
			ac.draw(g, 0, 0);
			if (selectedHUDActionParam != null) {
				if (selectedHUDActionParam.getActionName().equals(ac.getActionName())) {
					g.setColor(Color.RED);
					g.setStroke(new BasicStroke(2));
					g.drawRect(((x_margin1 + 155 + x_offset) / 8) * ActionController.CELL_WIDTH,
							((y_margin1 + 10) / 8) * ActionController.CELL_HEIGHT, 32, 32);
				}
			}
			x_offset += 40;
		}
	}

	/**
	 * Method for any click on the HUD for ACTIONS only
	 * 
	 * @param map_x
	 *            x location
	 * @param map_y
	 *            y location
	 */
	public ActionController hudClick(int map_x, int map_y) {
		for (Entry<String, ActionController> entry : actionMap.entrySet()) {
			ActionController ac = entry.getValue();
			// System.out.println("\t" + ac.getActionName());
			if (ac.isEntityAt(map_x, map_y, this)) {
				return ac;
			}
		}
		return null;
	}

	/**
	 * Method for any click on the HUD for ACTION_PARAM only
	 * 
	 * @param map_x
	 *            x location
	 * @param map_y
	 *            y location
	 */
	public ActionController hudParamClick(int map_x, int map_y) {
		for (Entry<String, ActionController> entry : actionParamMap.entrySet()) {
			ActionController ac = entry.getValue();
			// System.out.println("\t\t" + ac.getActionName());
			if (ac.isEntityAt(map_x, map_y, this)) {
				selectedHUDActionParam = ac;
				return ac;
			}
		}
		return null;
	}

	public void addActionParams(String paramName, ActionController actionParamController) {
		actionParamMap.put(paramName, actionParamController);
	}

	public void resetActionParams() {
		actionParamMap.clear();
		selectedHUDActionParam = null;
	}

}
