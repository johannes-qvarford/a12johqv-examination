package s2.base.ai;


import java.io.IOException;
import java.util.List;

import s2.actionControllers.ActionController;
import s2.entities.WPlayer;
import s2.game.S2;

/**
 * Interface of the towers AIs.
 * 
 * @author Santi Ontañón
 * adapted from the same class for Towers by Marco Antonio
 */
public interface AI {

	/**
	 * Method called by the game when it starts.
	 */
	public void gameStarts();
	
	/**
	 * Execute the 'tick()' of the AI. 
	 * @param application_state State of the application.
	 * @param game Game where the AI is running.
	 * @param actions List of actions that the game should execute.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void game_cycle(S2 game, WPlayer player, List<ActionController> actions) throws ClassNotFoundException, IOException;

	/**
	 * Method called by the game when it ends.
	 */
	public void gameEnd();
	
	/**
	 * @return The name of the AI.
	 */
	public String getPlayerId();
}
