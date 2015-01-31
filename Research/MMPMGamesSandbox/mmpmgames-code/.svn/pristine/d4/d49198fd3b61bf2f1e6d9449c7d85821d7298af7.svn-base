package bc.ai;

import gatech.mmpm.GameState;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import bc.BattleCity;
import bc.BattleCityApp;
import bc.PlayerInput;
import bc.mmpm.Game2D2Converter;
import bc.helpers.VirtualController;


/**
 * IA class that uses D2 IA learnt before to decide its movements.
 *  
 * @author Marco Antonio G�mez Mart�n
 */
public class AI_D2 implements AI {


	public AI_D2(gatech.mmpm.learningengine.IMEExecutor d2AI, String name, gatech.mmpm.IDomain idomain) {
		this.idomain = idomain;
		m_playerName = name;
		m_d2AI = d2AI;
	}
	
	public void cycle(int application_state, BattleCity game, VirtualController vc) throws ClassNotFoundException, IOException {

		switch(application_state) {
		case BattleCityApp.STATE_INIT:
			vc.m_button[0]=true;
			break;
			
		case BattleCityApp.STATE_GAME:	{

			if (stateProvider == null) {
				stateProvider = new BCGameStateProvider();
				stateProvider.game = game;
				stateProvider.idomain = idomain;
			}
			d2Actions.clear();
			m_d2AI.getActions(stateProvider, d2Actions);
			
			vc.m_joystick[PlayerInput.DIRECTION_UP]=false;
			vc.m_joystick[PlayerInput.DIRECTION_RIGHT]=false;
			vc.m_joystick[PlayerInput.DIRECTION_DOWN]=false;
			vc.m_joystick[PlayerInput.DIRECTION_LEFT]=false;
			vc.m_button[0]=false;

			// Convert to game actions and return.
			for (gatech.mmpm.Action a : d2Actions)
				Game2D2Converter.toGameAction(a,vc);
			}
			break;
		case BattleCityApp.STATE_QUITTING:
			vc.m_button[0]=true;
			break;
		} // switch
	}

	class BCGameStateProvider implements gatech.mmpm.learningengine.IMEExecutor.IGameStateProvider {
		public BattleCity game;
		public gatech.mmpm.IDomain idomain;
		
		public GameState getGameState() {
			return Game2D2Converter.toGameState(game,idomain);
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
	
	gatech.mmpm.IDomain idomain = null;
	
	BCGameStateProvider stateProvider = null;
	
	List<gatech.mmpm.Action> d2Actions = new LinkedList<gatech.mmpm.Action>();
}
