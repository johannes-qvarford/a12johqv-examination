package s2.base.ai;



import java.io.IOException;
import java.util.List;

import s2.actionControllers.ActionController;
import s2.entities.WPlayer;
import s2.game.S2;

public class AIEmpty implements AI {

	public String m_playerID;

	public void gameEnd() {
	}

	public void gameStarts() {
	}

	public AIEmpty(String playerID) {
		m_playerID = playerID;
	}
	
	public String getPlayerId() {
		return m_playerID;
	}
		
	public void game_cycle(S2 game,WPlayer player,List<ActionController> actions) throws IOException, ClassNotFoundException {

		// This AI is empty
	}
}
