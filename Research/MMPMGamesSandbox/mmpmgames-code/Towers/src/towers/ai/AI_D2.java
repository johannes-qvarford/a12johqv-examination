package towers.ai;

import gatech.mmpm.GameState;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import towers.Action;
import towers.Towers;
import towers.TowersApp;
import towers.mmpm.Game2D2Converter;


/**
 * IA class that uses D2 IA learnt before to decide its movements.
 *  
 * @author Marco Antonio Gómez Martín
 */
public class AI_D2 implements AI {


	public AI_D2(gatech.mmpm.learningengine.IMEExecutor d2AI, String name) {
		m_playerName = name;
		m_d2AI = d2AI;
	}
	
	public void cycle(int application_state, Towers game, List<Action> actions) throws ClassNotFoundException, IOException {
		switch(application_state) {
		case TowersApp.STATE_INIT:
			actions.add(new Action("Accept",m_playerName,""));
			break;
			
		case TowersApp.STATE_GAME:	{
			if (stateProvider == null) {
				stateProvider = new TowersGameStateProvider();
				stateProvider.game = game;
			}
			d2Actions.clear();
			m_d2AI.getActions(stateProvider, d2Actions);

			// Convert to game actions and return.
			for (gatech.mmpm.Action a : d2Actions)
				actions.add(Game2D2Converter.toGameAction(a));
			}
			break;
		case TowersApp.STATE_QUITTING:
			actions.add(new Action("Accept",m_playerName,""));
			break;
		} // switch
	}

	class TowersGameStateProvider implements gatech.mmpm.learningengine.IMEExecutor.IGameStateProvider {
		public Towers game;
		public GameState getGameState() {
			return Game2D2Converter.toGameState(game);
		}
		public int getGameCycle() {
			return game.getCycle();
		}
	}
	
	public String getPlayerId() {
		return m_playerName;
	}

	public void gameStarts() {
		m_d2AI.gameStart(m_playerName);
	}

	public void gameEnd() {
		m_d2AI.gameEnd();
	}
	
	// Attributes
	String m_playerName;
	
	gatech.mmpm.learningengine.IMEExecutor m_d2AI;
	
	TowersGameStateProvider stateProvider = null;
	
	List<gatech.mmpm.Action> d2Actions = new LinkedList<gatech.mmpm.Action>();
}
