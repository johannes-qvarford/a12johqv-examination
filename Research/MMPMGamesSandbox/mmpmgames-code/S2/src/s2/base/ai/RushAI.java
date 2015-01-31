/**
 * 
 */
package s2.base.ai;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import s2.actionControllers.ActionController;
import s2.actionControllers.AttackActionController;
import s2.actionControllers.BuildActionController;
import s2.actionControllers.HarvestActionController;
import s2.actionControllers.TrainActionController;
import s2.entities.WPlayer;
import s2.entities.WUnit;
import s2.entities.buildings.WFortress;
import s2.entities.buildings.WGoldMine;
import s2.entities.buildings.WBarracks;
import s2.entities.buildings.WBlacksmith;
import s2.entities.buildings.WLumberMill;
import s2.entities.buildings.WTownhall;
import s2.entities.troops.WArcher;
import s2.entities.troops.WCatapult;
import s2.entities.troops.WFootman;
import s2.entities.troops.WKnight;
import s2.entities.troops.WPeasant;
import s2.game.S2;
import s2.helpers.Pair;

/**
 * @author kane AI that builds a barracks and two footmen and ATTACKS! then
 *         harvests and trains footmen, attacking when there are two.
 */
public class RushAI implements AI {

	int DEBUG = 0;

	/**
	 * the player name represented by this AI.
	 */
	private String m_playerID;

	/**
	 * default constructor.
	 * 
	 * @param playerID
	 */
	public RushAI(String playerID) {
		m_playerID = playerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see base.ai.AI#gameEnd()
	 */
	public void gameEnd() {
		// do nothing, except maybe celebrate
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see base.ai.AI#gameStarts()
	 */
	public void gameStarts() {
		// start kicking ass
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see base.ai.AI#game_cycle(game.S2, entities.WPlayer, java.util.List)
	 */
	public void game_cycle(S2 game, WPlayer player, List<ActionController> actions)
			throws ClassNotFoundException, IOException {
		if (game.getCycle() % 25 != 0) {
			return;
		}
		if (checkTownhall(game, player, actions)) {
			return;
		}

		if (checkBarracks(game, player, actions)) {
			return;
		}

		checkPeasant(game, player, actions);

		buildFootmen(game, player, actions);

		attack(game, player, actions);
	}

	/**
	 * attacks the enemy!
	 * 
	 * @param game
	 * @param player
	 * @param actions
	 */
	private void attack(S2 game, WPlayer player, List<ActionController> actions) {
		List<WUnit> footmen = game.getUnitTypes(player, WFootman.class);
		WPlayer enemy = null;
		for (WPlayer entity : game.getPlayers()) {
			if (entity != player) {
				enemy = entity;
				break;
			}
		}
		WUnit enemyTroop = game.getUnitType(enemy, WFootman.class);
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WKnight.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WArcher.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WCatapult.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WPeasant.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WTownhall.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WBarracks.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WLumberMill.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WBlacksmith.class);
		}
		if (null == enemyTroop) {
			enemyTroop = game.getUnitType(enemy, WFortress.class);
		}
		if (null == enemyTroop) {
			// if enemyTroop is still null here, we should have won the game
			return;
		}

		Set<WUnit> troops = new TreeSet<WUnit>();
		troops.addAll(footmen);
		actions.add(new AttackActionController(troops, enemyTroop, game));

	}

	/**
	 * trains a footman if there is enough gold.
	 * 
	 * @param game
	 * @param player
	 * @param actions
	 */
	private void buildFootmen(S2 game, WPlayer player, List<ActionController> actions) {
		WBarracks barracks = (WBarracks) game.getUnitType(player, WBarracks.class);

		if (null == barracks) {
			return;
		}
		if (player.getGold() >= 600) {
			Set<WUnit> set = new TreeSet<WUnit>();
			set.add(barracks);
			actions.add(new TrainActionController(set, 1, true));
		}
	}

	/**
	 * Checks that there is at least one peasant mining gold
	 * 
	 * @param game
	 * @param player
	 * @param actions
	 */
	private void checkPeasant(S2 game, WPlayer player, List<ActionController> actions) {
		WPeasant peasant = (WPeasant) game.getUnitType(player, WPeasant.class);
		if (null == peasant) {
			// TODO train peasant
			return;
		}
		if (peasant.getStatus().size() != 0 && peasant.getStatus().get(0) != 0) {
			return;
		}

		List<WUnit> mines = game.getUnitTypes(null, WGoldMine.class);
		WGoldMine mine = null;
		int leastDist = 9999;
		for (WUnit unit : mines) {
			int dist = Math.abs(unit.getX() - peasant.getX())
					+ Math.abs(unit.getY() - peasant.getY());
			if (dist < leastDist) {
				leastDist = dist;
				mine = (WGoldMine) unit;
			}
		}

		Set<WUnit> set = new TreeSet<WUnit>();
		set.add(peasant);
		actions.add(new HarvestActionController(set, mine, game));
	}

	/**
	 * Checks that a barracks exists, and builds one if it doesn't
	 * 
	 * @param game
	 * @param player
	 * @param actions
	 */
	private boolean checkBarracks(S2 game, WPlayer player, List<ActionController> actions) {
		if (DEBUG >= 1)
			System.out.println("Rush-AI: checkBarracks");
		if (null == game.getUnitType(player, WBarracks.class)) {
			WPeasant peasant = (WPeasant) game.getUnitType(player, WPeasant.class);
			if (null == peasant) {
				// TODO train peasant
				return true;
			}
			Pair<Integer, Integer> loc = game.findFreeSpace(peasant.getX(), peasant.getY(), 3);
			if (null == loc) {
				// can't build anything.
				return true;
			}
			if (DEBUG >= 1)
				System.out.println("Rush-AI: building barracks at " + loc.m_a + " , " + loc.m_b);

			Set<WUnit> set = new TreeSet<WUnit>();
			set.add(peasant);
			actions.add(new BuildActionController(set, loc.m_a, loc.m_b, 1));
		}
		return false;
	}

	/**
	 * Checks that a townhall exists, and builds one if it doesn't.
	 * 
	 * @param game
	 * @param player
	 * @param actions
	 */
	private boolean checkTownhall(S2 game, WPlayer player, List<ActionController> actions) {
		if (DEBUG >= 1)
			System.out.println("Rush-AI: checkTownhall");
		if (null == game.getUnitType(player, WTownhall.class)) {
			WPeasant peasant = (WPeasant) game.getUnitType(player, WPeasant.class);
			if (null == peasant) {
				// we're screwed, can't build, can't harvest
				return true;
			}
			Pair<Integer, Integer> loc = game.findFreeSpace(peasant.getX(), peasant.getY(), 3);
			Set<WUnit> set = new TreeSet<WUnit>();
			set.add(peasant);
			actions.add(new BuildActionController(set, loc.m_a, loc.m_b, 0));
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see base.ai.AI#getPlayerId()
	 */
	public String getPlayerId() {
		return m_playerID;
	}

}
