/*********************************************************************************
 Organization 					: 				Georgia Institute of Technology
 Cognitive Computing Lab (CCL)
 Authors							: 				Jai Rad
 Santi Ontanon
 ****************************************************************************/
package s2.entities.buildings;

import java.util.ArrayList;
import java.util.List;

import s2.actionControllers.ActionController;
import s2.base.PlayerInput;
import s2.entities.WPlayer;
import s2.entities.WUnit;
import s2.entities.troops.WArcher;
import s2.entities.troops.WCatapult;
import s2.entities.troops.WFootman;
import s2.entities.troops.WKnight;
import s2.entities.troops.WPeasant;
import s2.game.S2;
import s2.helpers.Pair;

public abstract class WBuilding extends WUnit {

	/** which units this building can train */
	protected List<String> allowedUnits = new ArrayList<String>();

	public WBuilding() {
	}

	public WBuilding(WBuilding incoming) {
		super(incoming);
	}

	public static boolean isActive() {
		return true;
	}

	/**
	 * orders the unit to train the given unit
	 * 
	 * @param unit
	 *            the unit to train
	 */
	public void train(int unit, ActionController action) {
		lastAction = action;
		status.clear();
		status.add(ACTION_TRAIN);
		status.add(unit);
	}

	public List<String> getAllowedUnits() {
		return allowedUnits;
	}

	/**
	 * checks the status of the entity to see if there are any pending actions
	 * to be performed
	 */
	public void cycle(int m_cycle, S2 m_game, List<ActionController> failedActions) {
		super.cycle(m_cycle, m_game, failedActions);
		if (m_cycle % 25 == 0) {
			if (status.size() > 0) {
				switch (status.get(0)) {
				case ACTION_STAND_GROUND:
					doStandGround(m_game);
					break;
				case ACTION_TRAIN:
					WUnit newUnit = null;
					switch (status.get(1)) {
					case 0: // peasant
						newUnit = new WPeasant();
						break;
					case 1: // footman
						newUnit = new WFootman();
						break;
					case 2: // archer
						newUnit = new WArcher();
						break;
					case 3: // catapult
						newUnit = new WCatapult();
						break;
					case 4: // knight
						newUnit = new WKnight();
						break;
					}
					
					if (newUnit==null) {
						System.err.println("WBuilding.cycle: wrong type for train a unit! -> " + status.get(1));
						return;
					}

					newUnit.setCreator(this);
					newUnit.setCreatedCycle(m_cycle);
					findLocation(m_game, newUnit);

					WPlayer player = getPlayer(m_game);

					// check cost
					if (player.getGold() < newUnit.getCost_gold()
							|| player.getWood() < newUnit.getCost_wood()) {
						// can't afford building, stop

						// Failed Action!!!
						failedActions.add(lastAction);
						
						if (player.getInputType() == PlayerInput.INPUT_MOUSE) {
							m_game.setMessage("Can't afford that; Cost is "
									+ newUnit.getCost_gold() + " gold and "
									+ newUnit.getCost_wood() + " wood.");
						}
						status.clear();
						return;
					}
					// subtract cost
					player.setGold(player.getGold() - newUnit.getCost_gold());
					player.setWood(player.getWood() - newUnit.getCost_wood());

					// set unit attributes
					newUnit.setCurrent_hitpoints(newUnit.getMax_hitpoints());
					newUnit.setOwner(player.owner);
					newUnit.setEntityID(m_game.nextID());
					newUnit.setColor(player.getColor());
					m_game.addUnit(newUnit);

					status.clear();

					break;
				}
			}
		}
	}
	
	/**
	 * @param m_game
	 */
	private void doStandGround(S2 m_game) {
		for (WUnit unit : m_game.getUnits()) {
			if (null == owner || null == unit.owner) {
				continue;
			}
			if (!unit.owner.equals(owner) && inRange(unit)) {
				unit.setCurrent_hitpoints(unit
						.getCurrent_hitpoints()
						- attack);
//				target_x = unit.getX() + unit.getWidth()/2;
//				target_y = unit.getY() + unit.getLength()/2;

				return;
			}
		}
	}

	/**
	 * Decides if the target is within range of the unit's attack. Assumes units
	 * attack range is a square, not a circle.
	 * 
	 * @param target
	 *            the unit to be attacked.
	 * @return true if the unit is in range, false otherwise.
	 */
	protected boolean inRange(WUnit target) {
		if (target == null) {
			return false;
		}
		// check X
		if (x + range >= target.getX()
				&& x - range <= target.getX() + target.getWidth() - 1) {
			// check y
			if (y + range >= target.getY()
					&& y - range <= target.getY() + target.getLength() - 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * finds a free spot for the unit
	 * 
	 * @param m_game
	 * @param newUnit
	 */
	private void findLocation(S2 m_game, WUnit newUnit) {
		Pair<Integer, Integer> loc = m_game.findFreeSpace(getX()
				+ (getWidth() / 2), getY() + (getHeight() / 2), 1);
		// location is next to the building
		newUnit.setX(loc.m_a);
		newUnit.setY(loc.m_b);

	}

}