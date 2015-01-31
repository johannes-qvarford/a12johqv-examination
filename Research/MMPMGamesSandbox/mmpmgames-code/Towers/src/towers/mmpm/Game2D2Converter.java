package towers.mmpm;

import gatech.mmpm.ActionParameterType;
import gatech.mmpm.ParseLmxTrace;

import java.util.HashMap;
import java.util.List;

import towers.Action;
import towers.objects.Entity;

/**
 * Class used to convert elements between both domains: game and D2.
 * 
 * @author Marco Antonio Gómez Martín
 */
public class Game2D2Converter {

	public static towers.Action toGameAction(gatech.mmpm.Action d2Action) {

		HashMap<String,String> parameters = new HashMap<String,String>();
		String actionName;
		
		if (d2Action instanceof towers.mmpm.actions.Build) {
			
//			System.out.println("D2 wants to execute " + d2Action);
			
			actionName = "Build"; 
			
			towers.mmpm.actions.Build buildAction =
				(towers.mmpm.actions.Build)d2Action;
			
			parameters.put("type", ActionParameterType.ENTITY_TYPE.toString(buildAction.getType()));
			parameters.put("coor", ActionParameterType.COORDINATE.toString(buildAction.getCoor()) );
		} /* else if (d2action instanceof ...) {
		} */ else {
			return null;
		}
		
		Action a = new Action(actionName, 
							  d2Action.getEntityID(),
							  d2Action.getEntityID());
		for (String p : parameters.keySet())
			a.addParameter(p, parameters.get(p));

		return a;
	}

	public static gatech.mmpm.Action toD2Action(towers.Action action) {

		if (action.m_name.equals("Build")) {
			towers.mmpm.actions.Build ret = 
				new towers.mmpm.actions.Build(action.m_actor, action.m_unit_id);
			
			ret.setCoor(action.getParameter("coor"));
			ret.setType(action.getParameter("type"));
			return ret;
			
		} /* else if (action.m_name.equals("...")) {
		} */ else
			return null;
	}
	
	
	/**
	 * Converts from the Towers game state to the D2 game state.
	 *  
	 * @param game Current game
	 * @return GameState in
	 */
	public static gatech.mmpm.GameState toGameState(towers.Towers game) {
		gatech.mmpm.GameState ret = new gatech.mmpm.GameState();
		

		ret.addMap(game.getMap().toD2Map());
		// Add the entities stored in our TMap
		List<towers.objects.Entity> entities;

		entities = game.getEntityList();
		for (Entity e : entities) {
			gatech.mmpm.Entity d2Entity = e.toD2Entity();
			if ((d2Entity != null) &&
				!(d2Entity instanceof towers.mmpm.entities.TWall))
				ret.addEntity(d2Entity);
		}

		// Add the entities stored by the game itself
		List<towers.objects.PhysicalEntity> mapEntities;
		mapEntities = game.getMap().getMapEntities();
		for (Entity e : mapEntities) {
			gatech.mmpm.Entity d2Entity = e.toD2Entity();
			if ((d2Entity != null) &&
				!(d2Entity instanceof towers.mmpm.entities.TWall))
				ret.addEntity(d2Entity);
		}
		
		return ret;
		/*
		String s = game.saveToXML(0);
		//System.out.println(s);
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			doc = builder.build(new java.io.StringBufferInputStream(s));
			Element root = doc.getRootElement();
			GameState gs = parser.parseGameState(root,"dm");
			return gs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		*/
	}
	
	static ParseLmxTrace parser = new ParseLmxTrace(TowersDomain.getDomainName());
}
