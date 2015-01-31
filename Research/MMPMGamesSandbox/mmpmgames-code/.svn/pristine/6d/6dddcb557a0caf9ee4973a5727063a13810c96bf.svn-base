/*********************************************************************************
 Organization 					: 				Georgia Institute of Technology
 Cognitive Computing Lab (CCL)
 Authors							: 				Jai Rad
 Santi Ontanon
 ****************************************************************************/
package s2.entities.troops;

import gatech.mmpm.Entity;

import java.util.ArrayList;
import java.util.List;

import s2.actionControllers.ActionController;
import s2.base.PlayerInput;
import s2.entities.S2PhysicalEntity;
import s2.entities.WPlayer;
import s2.entities.WUnit;
import s2.entities.buildings.WBuilding;
import s2.entities.buildings.WFortress;
import s2.entities.buildings.WGoldMine;
import s2.entities.buildings.WBarracks;
import s2.entities.buildings.WBlacksmith;
import s2.entities.buildings.WLumberMill;
import s2.entities.buildings.WStable;
import s2.entities.buildings.WTower;
import s2.entities.buildings.WTownhall;
import s2.entities.map.WOTree;
import s2.game.S2;
import s2.helpers.Pair;

public class WPeasant extends WTroop {

	/** which buildings can be built */
	private List<String> allowedUnits = new ArrayList<String>();

	private int carriedGold = 0;

	private int carriedWood = 0;

	public WPeasant() {
		setConstants();
	}

	public WPeasant(WPeasant incoming) {
		super(incoming);
		setConstants();
	}

	private void setConstants() {
		attack = 3;
		max_hitpoints = 30;
		width = 1;
		length = 1;
		cost_gold = 400;
		cost_wood = 0;
		this.spriteName = "graphics/peasant.png";

		// Add actions to the list
		actionList.add("Build");
		actionList.add("Harvest");
		actionList.add("Repair");

		allowedUnits.add("s2.entities.buildings.WTownHall");
	}

	public Object clone() {
		WUnit e = new WPeasant(this);
		return e;
	}

	public static boolean isActive() {
		return true;
	}

	/**
	 * orders the unit to build a building at location x,y
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 * @param building
	 *            the type of building to build
	 */
	public void build(int x, int y, int building, ActionController action) {
		cleanup();
		lastAction = action;
		status.removeAll(status);
		status.add(ACTION_BUILD);
		status.add(x);
		status.add(y);
		status.add(building);
	}

	/**
	 * orders the unit to repair the given unit
	 * 
	 * @param unit
	 *            the unit to repair
	 */
	public void repair(int unit, ActionController action) {
		cleanup();
		lastAction = action;
		status.removeAll(status);
		status.add(ACTION_REPAIR);
		status.add(unit);
	}

	/**
	 * orders the unit to harvest at the selected gold mine.
	 * 
	 * @param target
	 *            the tree or gold mine id.
	 */
	public void harvest(int target, ActionController action) {
		cleanup();
		lastAction = action;
		status.removeAll(status);
		status.add(ACTION_HARVEST);
		status.add(target);
	}

	/**
	 * orders the unit to harvest at the selected tree.
	 * 
	 * @param x
	 *            the xLoc of the tree.
	 * @param y
	 *            the yLoc of the tree.
	 */
	public void harvest(int x, int y, ActionController action) {
		cleanup();
		lastAction = action;
		status.removeAll(status);
		status.add(ACTION_HARVEST);
		status.add(x);
		status.add(y);
	}

	/**
	 * checks the status of the entity to see if there are any pending actions
	 * to be performed
	 */
	public void cycle(int a_cycle, S2 a_game, List<ActionController> failedActions) {
		super.cycle(a_cycle, a_game, failedActions);
		if (a_cycle % 25 == 0) {

			setAllowed(a_game);

			if (status.size() > 0) {
				switch (status.get(0)) {
				case ACTION_REPAIR:
					doRepair(a_game);
					break;
				case ACTION_HARVEST:
					doHarvest(a_game);
					break;
				case ACTION_BUILD:
					doBuild(a_cycle,a_game, failedActions);
					break;
				}
			}
		}
	}

	/**
	 * changes which buildings can be built
	 * 
	 * @param m_game
	 */
	private void setAllowed(S2 m_game) {
		WPlayer player = m_game.getPlayer(owner);
		if (null == m_game.getUnitType(player, WTownhall.class)) {
			allowedUnits.remove("s2.entities.buildings.WLumbermill");
			allowedUnits.remove("s2.entities.buildings.WBarracks");
			allowedUnits.remove("s2.entities.buildings.WBlacksmith");
		} else {
			allowedUnits.add("s2.entities.buildings.WLumbermill");
			allowedUnits.add("s2.entities.buildings.WBarracks");
			allowedUnits.add("s2.entities.buildings.WBlacksmith");
		}

		if (null == m_game.getUnitType(player, WLumberMill.class)) {
			allowedUnits.remove("s2.entities.buildings.WTower");
		} else {
			allowedUnits.add("s2.entities.buildings.WTower");
		}

		if (null == m_game.getUnitType(player, WBarracks.class)
				|| null == m_game.getUnitType(player, WLumberMill.class)
				|| null == m_game.getUnitType(player, WBlacksmith.class)) {
			allowedUnits.remove("s2.entities.buildings.WFortress");
		} else {
			allowedUnits.add("s2.entities.buildings.WFortress");
		}

		if (null == m_game.getUnitType(player, WFortress.class)) {
			allowedUnits.remove("s2.entities.buildings.WStable");
		} else {
			allowedUnits.add("s2.entities.buildings.WStable");
		}

	}

	private void doBuild(int cycle, S2 game, List<ActionController> failedActions) {
		int xLoc = status.get(1);
		int yLoc = status.get(2);
		int type = status.get(3);

		if (inRange(xLoc, yLoc)) {
			// TODO make reflective
			WBuilding building = null;
			WPlayer player = getPlayer(game);
			switch (type) {
			case 0: // townhall
				building = new WTownhall();
				break;
			case 1: // barracks
				building = new WBarracks();
				break;
			case 2: // stable
				building = new WStable();
				break;
			case 3: // fortress
				building = new WFortress();
				break;
			case 4: // lumbermill
				building = new WLumberMill();
				break;
			case 5: // blacksmith
				building = new WBlacksmith();
				break;
			case 6: // tower
				building = new WTower();
				break;

			}
			
			building.setCreator(this);
			building.setCreatedCycle(cycle);
			
			building.setX(xLoc);
			building.setY(yLoc);
			
			// Temporary remove the peasant:
			game.removeUnit(this);

			if (game.anyLevelCollision(building)) {
				game.addUnit(this);	// Add the peasant again

				// Add the failed action to the list:
//				failedActions.add(lastAction);
				
				// can't build here d00dz
				if (player.getInputType() == PlayerInput.INPUT_MOUSE) {
					game.setMessage("Building location needs to be cleared.");
				}
//				status.removeAll(status);
//				status.add(ACTION_NULL);
				return;
			}

			building.setCurrent_hitpoints(building.getMax_hitpoints());
			building.setOwner(player.owner);
			building.setEntityID(game.nextID());
			building.setColor(player.getColor());

			if (player.getGold() < building.getCost_gold() || 
				player.getWood() < building.getCost_wood()) {
				game.addUnit(this);	// Add the peasant again
				
				// Add the failed action to the list:
//				failedActions.add(lastAction);

				// can't afford building, stop
				if (player.getInputType() == PlayerInput.INPUT_MOUSE) {
					game.setMessage("Can't afford that building; Cost is "
							+ building.getCost_gold() + " gold and " + building.getCost_wood()
							+ " wood.");
				}
//				status.removeAll(status);
//				status.add(ACTION_NULL);
			} else {
				player.setGold(player.getGold() - building.getCost_gold());
				player.setWood(player.getWood() - building.getCost_wood());
				// System.out.println(building);
				game.addUnit(building);
				status.removeAll(status);
				status.add(ACTION_NULL);
				
				// Find a free square around the new building:
				Pair<Integer, Integer> loc = game.findFreeSpace(getX(), getY(), 1);
				setX(loc.m_a);
				setY(loc.m_b);
				
				game.addUnit(this);	// Add the peasant again
			}
		} else {
			moveTowardsTarget(game, xLoc, yLoc);
		}
	}

	/**
	 * @param game
	 */
	private void doRepair(S2 game) {
		int unitID = status.get(1);
		WPlayer player = getPlayer(game);
		WUnit target = game.getUnit(unitID);
		if (target == null || target.getCurrent_hitpoints() <= 0
				|| target.getCurrent_hitpoints() == target.getMax_hitpoints()) {
			if (player.getInputType() == PlayerInput.INPUT_MOUSE) {
				game.setMessage("Repairs finished.");
			}

			status.removeAll(status);
			status.add(ACTION_NULL);
		} else {
			if (inRange(target)) {
				target.setCurrent_hitpoints(target.getCurrent_hitpoints() + attack);
			} else {
				moveTowardsTarget(game, target);
			}
		}
	}

	/**
	 * executes the harvest action.
	 * 
	 * @param game
	 */
	private void doHarvest(S2 game) {
		
		if (status.size()==2) {
			int unitID = status.get(1);
			WUnit target = game.getUnit(unitID);
	
			if (target != null && target instanceof WGoldMine) {
				mine((WGoldMine) target, game);
			}
		} else {
			chop(status.get(1), status.get(2), game);			
		}

	}

	/**
	 * harvest wood from location x, y
	 * 
	 * @param x
	 * @param y
	 * @param game
	 */
	private void chop(int x, int y, S2 game) {
		if (carriedWood == 100) {
			WPlayer player = getPlayer(game);
			WTownhall townhall = (WTownhall) game.getUnitType(player, WTownhall.class);
			if (inRange(townhall)) {
				player.setWood(player.getWood() + 100);
				carriedWood = 0;
				cleanup();
			} else {
				// move towards townhall
				moveTowardsTarget(game, townhall);
			}
		} else {
			if (inRange(x, y)) {
				S2PhysicalEntity wood = game.mapEntityAt(x, y);
				if (!(wood instanceof WOTree)) {
					WPlayer player = getPlayer(game);
					// already harvested
					
					WTownhall townhall = (WTownhall) game.getUnitType(player, WTownhall.class);
					S2PhysicalEntity nextWood = game.locateNearestMapEntity(x, y, WOTree.class,townhall);
					
					if (nextWood != null) {
						// System.out.println("Here");
						harvest(nextWood.getX(), nextWood.getY(), lastAction);
						return;
					}
					if (player.getInputType() == PlayerInput.INPUT_MOUSE) {
						game.setMessage("There is no wood at that location.");
					}
					status.removeAll(status);
					status.add(ACTION_NULL);
					return;
				}
				game.clearMapEntity(x, y);
				carriedWood = 100;
				cleanup();
			} else {
				// move towards wood
				Pair<Integer, Integer> loc = rangedLoc(x, y,game);
				moveTowardsTarget(game, loc.m_a, loc.m_b);
			}
		}
	}

	/**
	 * harvest gold from the target mine
	 * 
	 * @param target
	 * @param game
	 */
	private void mine(WGoldMine target, S2 game) {
		if (target == null || target.getRemaining_gold() <= 0) {
			status.removeAll(status);
			status.add(ACTION_NULL);
		} else {
			if (carriedGold == 100) {
				WPlayer player = getPlayer(game);
				WTownhall townhall = (WTownhall) game.getUnitType(player, WTownhall.class);
				if (inRange(townhall)) {
					player.setGold(player.getGold() + 100);
					carriedGold = 0;
					cleanup();
				} else {
					// move towards townhall
					moveTowardsTarget(game, townhall);
				}
			} else {
				if (inRange(target)) {
					target.setRemaining_gold(target.getRemaining_gold() - 100);
					carriedGold = 100;
					cleanup();
				} else {
					// move towards mine
					moveTowardsTarget(game, target);
				}
			}
		}
	}

	public Entity toD2Entity() {
		s2.mmpm.entities.WPeasant ret;
		ret = new s2.mmpm.entities.WPeasant("" + entityID, owner);
		ret.setx(x);
		ret.sety(y);
		ret.setCurrent_hitpoints(current_hitpoints);
		ret.setCycle_created(cycle_created);
		ret.setCycle_last_attacked(cycle_last_attacked);
		ret.setRange(range);
		ret.setAttack(attack);
		ret.setCarriedGold(carriedGold);
		ret.setCarriedWood(carriedWood);
		if (creator==null) ret.setCreator("");
					  else ret.setCreator(creator.getEntityID()+""); 
		if (status.size()==0) ret.setStatus("0");
						 else ret.setStatus(status.get(0)+"");
		
//		System.out.println("*** Peasant " + entityID + " -> status: " + status);
		
		return ret;
	}

	public List<String> getAllowedUnits() {
		return allowedUnits;
	}

}