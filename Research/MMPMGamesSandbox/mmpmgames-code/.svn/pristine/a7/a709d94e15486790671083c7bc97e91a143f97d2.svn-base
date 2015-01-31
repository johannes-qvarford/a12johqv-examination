package towers.ai;

import java.io.IOException;
import java.util.List;

import towers.Action;
import towers.Towers;

/**
 * Interface of the towers AIs.
 * 
 * @author Marco Antonio
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
	public void cycle(int application_state, Towers game,
			List<Action> actions) throws ClassNotFoundException, IOException;

	/**
	 * Method called by the game when it ends.
	 */
	public void gameEnd();
	
	/**
	 * @return The name of the AI.
	 */
	public String getPlayerId();
}
