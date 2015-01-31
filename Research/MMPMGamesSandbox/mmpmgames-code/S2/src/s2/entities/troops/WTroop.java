/*********************************************************************************
 Organization 					: 				Georgia Institute of Technology
 Cognitive Computing Lab (CCL)
 Authors							: 				Jai Rad
 Santi Ontanon
 ****************************************************************************/
package s2.entities.troops;

import java.util.List;

import s2.actionControllers.ActionController;
import s2.base.PlayerInput;
import s2.entities.S2PhysicalEntity;
import s2.entities.WPlayer;
import s2.entities.WUnit;
import s2.entities.map.WOGrass;
import s2.entities.pathing.AStar;
import s2.game.S2;
import s2.helpers.Pair;

public abstract class WTroop extends WUnit {
	protected int speed;

	protected AStar pathPlanner;

	protected List<Pair<Double, Double>> path;

	protected int pathIndex = -1;

	public WTroop() {
		setConstants();
	}

	private void setConstants() {
		width = 1;
		length = 1;
		range = 1;

		// Add actions to the list
		actionList.add("Move");
		actionList.add("Attack");
		actionList.add("StandGround");
	}

	public WTroop(WTroop incoming) {
		super(incoming);
		setConstants();
	}

	public static boolean isActive() {
		return true;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int incoming) {
		this.speed = incoming;
	}

	public void setSpeed(String incoming) {
		this.speed = Integer.parseInt(incoming);
	}

	/**
	 * orders the unit to move to location x,y
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 */
	public void move(int x, int y, ActionController action) {
		cleanup();
		lastAction = action;
		status.removeAll(status);
		status.add(ACTION_MOVE);
		status.add(x);
		status.add(y);
	}

	/**
	 * orders the unit to attack the given unit
	 * 
	 * @param unit
	 *            the unit to attack
	 */
	public void attack(int unit,ActionController action) {
		cleanup();
		lastAction = action;
		status.removeAll(status);
		status.add(ACTION_ATTACK);
		status.add(unit);
	}

	/**
	 * orders the unit to never move.
	 */
	public void standGround(ActionController action) {
		cleanup();
		lastAction = action;
		status.removeAll(status);
		status.add(ACTION_STAND_GROUND);
	}

	/**
	 * checks the status of the entity to see if there are any pending actions
	 * to be performed
	 */
	public void cycle(int a_cycle, S2 a_game, List<ActionController> failedActions) {
		super.cycle(a_cycle, a_game, failedActions);
		if (a_cycle % 25 == 0) {
			if (status.size() > 0) {
//				System.out.println("I'm unit " + getEntityID() + " a " + getClass().getSimpleName() + " with status " + status.get(0));
				switch (status.get(0)) {
				case ACTION_MOVE:
					moveTowardsTarget(a_game, status.get(1), status.get(2));
					break;
				case ACTION_ATTACK:
					doAttack(a_cycle,a_game);
					break;
				case ACTION_STAND_GROUND:
					doStandGround(a_game);
					break;
				default:
					doIdleAction(a_game);
				}
			} else {
				doIdleAction(a_game);
			}
		}
	}

	/**
	 * @param m_game
	 */
	private void doAttack(int a_cycle,S2 a_game) {
		int unitID = status.get(1);
		WUnit target = a_game.getUnit(unitID);
		
		if (target == null || target.getCurrent_hitpoints() <= 0) {
			cleanup();
			status.removeAll(status);
			status.add(ACTION_NULL);
		} else {
			if (inRange(target)) {
//				System.out.println("  My target is in range, so I'm attacking for " + attack);
				target.setCurrent_hitpoints(target.getCurrent_hitpoints() - attack);
				target.setLastAttackCycle(a_cycle);
			} else {
//				System.out.println("  My target out of range");
				moveTowardsTargetToAttack(a_game, target);
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
				unit.setCurrent_hitpoints(unit.getCurrent_hitpoints() - attack);
				return;
			}
		}
	}

	/**
	 * @param m_game
	 */
	private void doIdleAction(S2 m_game) {
		for (WUnit unit : m_game.getUnits()) {
			if (null == owner || null == unit.owner) {
				continue;
			}
			if (!unit.owner.equals(owner) && inRange(unit) && status.size() == 0) {
				attack(unit.entityID,null);
				return;
			}
		}
	}

	/**
	 * plans and executes a path to a goal.
	 * 
	 * @param game
	 */
	protected void moveTowardsTarget(S2 game, int x, int y) {
		if (getX() == x && getY() == y) {
			cleanup();
			status.removeAll(status);
			status.add(ACTION_NULL);
		} else {
			// init planner
			if (pathPlanner == null) {
				pathPlanner = new AStar(getX(), getY(), x, y, this, game);
				path = pathPlanner.computePath();
				pathIndex = 0;				
			}

			// check that a path exists
			if (null == path) {
				WPlayer player = getPlayer(game);
				if (player.getInputType() == PlayerInput.INPUT_MOUSE) {
					game.setMessage("Can't get to that location.");
				}
				cleanup();
				return;
			}

			// take step
			int oldX = getX();
			int oldY = getY();

			if (path.size() > pathIndex) {
				setX(path.get(pathIndex).m_a.intValue());
				setY(path.get(pathIndex).m_b.intValue());
				pathIndex++;
			} else {
				cleanup();
				return;
			}

			// check for collision, replan if necessary
			if (game.anyLevelCollision(this)) {
				pathPlanner = null;
				setX(oldX);
				setY(oldY);
				moveTowardsTarget(game, x, y);
			}
		}
	}

	/**
	 * cleans up unit items after finishing an action.
	 */
	protected void cleanup() {
		pathPlanner = null;
		path = null;
		pathIndex = -1;
		target_x = -1;
		target_y = -1;
		lastAction = null;
	}

	/**
	 * move towards the given unit.
	 * 
	 * @param m_game
	 * @param target
	 */
	protected void moveTowardsTargetToAttack(S2 m_game, WUnit target) {
		Pair<Integer, Integer> loc = rangedLoc(target, m_game);
//		System.out.println("  My target is in " + target.getX() + "," + target.getY() + " but I want to go to " + loc.m_a + "," + loc.m_b + " I am at " + getX() + "," + getY());
		moveTowardsTarget(m_game, loc.m_a, loc.m_b);
	}
	
	protected void moveTowardsTarget(S2 m_game, WUnit target) {
		Pair<Integer, Integer> loc = rangedLoc(target, m_game);
		moveTowardsTarget(m_game, loc.m_a, loc.m_b);
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
	 * Decides if the target location is within range of the unit's attack.
	 * Assumes units attack range is a square, not a circle.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return true if the unit is in range, false otherwise.
	 */
	protected boolean inRange(int xLoc, int yLoc) {
		// check X
		if (x + range >= xLoc && x - range <= xLoc) {
			// check y
			if (y + range >= yLoc && y - range <= yLoc) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Decides if the target is within range of the unit's attack. Assumes units
	 * attack range is a square, not a circle.
	 * 
	 * @param target
	 *            the unit to be attacked.
	 * @return true if the unit is in range, false otherwise.
	 */
	protected Pair<Integer, Integer> rangedLoc(WUnit target, S2 game) {
		if (target == null) {
			return new Pair<Integer, Integer>(-1, -1);
		}
		int bestx = -1;
		int besty = -1;
		double bestdistance = -1;

		for (int xLoc = target.getX() - range; xLoc < target.getX()
				+ target.getWidth() + range; xLoc++) {
			for (int yLoc = target.getY() - range; yLoc < target.getY()
					+ target.getLength() + range; yLoc++) {
				S2PhysicalEntity pe = game.getEntity(xLoc, yLoc);
				if (pe instanceof WOGrass) {
					double distance = Math.sqrt((xLoc - x) * (xLoc - x)
							+ (yLoc - y) * (yLoc - y));
					if (bestdistance == -1 || distance < bestdistance) {
						bestx = xLoc;
						besty = yLoc;
						bestdistance = distance;
					}
				}
			}

		}
		return new Pair<Integer, Integer>(bestx, besty);
	}

	/**
	 * Decides if the target is within range of the unit's attack. Assumes units
	 * attack range is a square, not a circle.
	 * 
	 * @param target
	 *            the unit to be attacked.
	 * @return true if the unit is in range, false otherwise.
	 */
	protected Pair<Integer, Integer> rangedLoc(int goalx, int goaly, S2 game) {
		int bestx = -1;
		int besty = -1;
		double bestdistance = -1;

		for (int xLoc = goalx - range; xLoc < goalx + 1 + range; xLoc++) {
			for (int yLoc = goaly - range; yLoc < goaly + 1 + range; yLoc++) {
				S2PhysicalEntity pe = game.getEntity(xLoc, yLoc);
				if (pe instanceof WOGrass) {
					double distance = Math.sqrt((xLoc - x) * (xLoc - x)
							+ (yLoc - y) * (yLoc - y));
					if (bestdistance == -1 || distance < bestdistance) {
						bestx = xLoc;
						besty = yLoc;
						bestdistance = distance;
					}
				}
			}

		}
		return new Pair<Integer, Integer>(bestx, besty);
	}
}