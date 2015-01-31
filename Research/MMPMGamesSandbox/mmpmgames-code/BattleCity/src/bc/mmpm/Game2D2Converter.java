package bc.mmpm;

//import gatech.mmpm.ParseLmxTrace;
import bc.helpers.VirtualController;

/**
 * Class used to convert elements between both domains: game and D2.
 * 
 * @author Marco Antonio G�mez Mart�n
 */
public class Game2D2Converter {

	public static void toGameAction(gatech.mmpm.Action d2Action, VirtualController vc) {		
		
//		System.out.println("D2 executes " + d2Action);
		
		if (d2Action instanceof bc.mmpm.actions.Fire) {
			vc.m_button[0]=true;
		}  else if (d2Action instanceof bc.mmpm.actions.Move) {
			bc.mmpm.actions.Move ma = (bc.mmpm.actions.Move)d2Action;
			vc.m_joystick[ma.getDirection()] = true;
//			System.out.println("D2 moves " + ma.getdirection());
		}
	}
	
	
	public static gatech.mmpm.Action toD2Action(bc.Action action) {

		if (action.m_name.equals("Move")) {
			bc.mmpm.actions.Move ret = 
				new bc.mmpm.actions.Move(action.m_unit_id,action.m_actor);
			
			ret.setDirection(action.getParameter("direction"));
			return ret;
			
		} else if (action.m_name.equals("Fire")) {
			bc.mmpm.actions.Fire ret = 
				new bc.mmpm.actions.Fire(action.m_unit_id,action.m_actor);			
			return ret;
			
		} /* else if (action.m_name.equals("...")) {
		} */ else
			return null;
	}

	
	/**
	 * @param game Current game
	 * @return GameState in
	 */
	public static gatech.mmpm.GameState toGameState(bc.BattleCity game, gatech.mmpm.IDomain idomain) {
		return game.getMap().toGameState(idomain);
	}
	
//	static ParseLmxTrace parser = new ParseLmxTrace(BCDomain.getDomainName());
}
