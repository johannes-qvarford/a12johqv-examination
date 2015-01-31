package towers.ai;


import gatech.mmpm.ActionParameterType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import towers.Action;
import towers.TMap;
import towers.Towers;
import towers.TowersApp;
import towers.objects.Entity;
import towers.objects.PhysicalEntity;
import towers.objects.TBase;
import towers.objects.TPlayer;
import towers.objects.TTower;
import towers.objects.TUpgradeGold;
import towers.objects.TUpgradeUnits;

public class AIRandom implements AI {
	
	public void gameEnd() {
	}

	public void gameStarts() {
	}

	static Random rg = new Random(1);
	public String m_playerID;
	int m_buildingToBuild = -1;

	public AIRandom(String playerID) {
		m_playerID = playerID;
	}
	
	public String getPlayerId() {
		return m_playerID;
	}
	
	/* (non-Javadoc)
	 * @see towersreloaded.ai.IIA#cycle(int, towersreloaded.Towers, java.util.List)
	 */
	public void cycle(int application_state,Towers game, List<Action> actions) throws ClassNotFoundException, IOException {
		switch(application_state) {
		case TowersApp.STATE_INIT:	actions.add(new Action("Accept",m_playerID,""));
									break;
		case TowersApp.STATE_GAME:	{
										List<Entity> l = game.getObjects("TPlayer");
										Entity player=null;

										for(Entity o:l) 
											if (((TPlayer)o).getowner().equals(m_playerID)) player=o;
										if (player!=null) game_cycle(game,(TPlayer)player,actions);
									}
									
									break;
		case TowersApp.STATE_QUITTING:	actions.add(new Action("Accept",m_playerID,""));
										break;
		} // switch

	}
	
	public void game_cycle(Towers game,TPlayer player,List<Action> actions) throws IOException, ClassNotFoundException {
		// Find the base and towers and upgrades:
		if (game.getCycle()%5==0) {
			List<PhysicalEntity> basesAndTowers = new LinkedList<PhysicalEntity>();
			int n_upgradeGold = 0;
			int n_upgradeUnit = 0;
			List<PhysicalEntity> l = game.getMap().getObjects(TBase.class);
			for(PhysicalEntity pe:l) if (pe.getowner().equals(player.getowner())) basesAndTowers.add(pe);
			l = game.getMap().getObjects(TTower.class);
			for(PhysicalEntity pe:l) if (pe.getowner().equals(player.getowner())) basesAndTowers.add(pe);
			l = game.getMap().getObjects(TUpgradeGold.class);
			for(PhysicalEntity pe:l) if (pe.getowner().equals(player.getowner())) n_upgradeGold++;
			l = game.getMap().getObjects(TUpgradeUnits.class);
			for(PhysicalEntity pe:l) if (pe.getowner().equals(player.getowner())) n_upgradeUnit++;
						
			if (m_buildingToBuild==-1) {
				// Decide what to build:
				if (basesAndTowers.size()<2) {
					m_buildingToBuild = 0;
				} else {
					int t = rg.nextInt(100);
					
					if (t<30) m_buildingToBuild = 0;
					else if (t<60) m_buildingToBuild = 1;
					else if (t<80) m_buildingToBuild = 2;
					else m_buildingToBuild = 3;
				}				
				
			}
			
			// Build it:
			if (m_buildingToBuild!=-1) {
				String buildingName[]={"towers.mmpm.entities.TTower",
									   "towers.mmpm.entities.TWall",
									   "towers.mmpm.entities.TUpgradeGold",
									   "towers.mmpm.entities.TUpgradeUnits"};					

				// Compute cost:
				int cost = game.cost(buildingName[m_buildingToBuild], player.getentityID());
				
				if (cost<=player.getGold() && basesAndTowers.size()>0) {					// Find a location:
					PhysicalEntity location = basesAndTowers.get(rg.nextInt(basesAndTowers.size()));
					int lx = location.getx();
					int ly = location.gety();
					
					lx+=(rg.nextInt(9)-4)*TMap.TILE_WIDTH;
					ly+=(rg.nextInt(9)-4)*TMap.TILE_HEIGHT;
					
					if (lx<0) lx = 0;
					if (lx>=game.getMap().getDx()) lx=game.getMap().getDx()-TMap.TILE_WIDTH;
					if (ly<0) ly = 0;
					if (ly>=game.getMap().getDy()) ly=game.getMap().getDy()-TMap.TILE_HEIGHT;
					lx/=TMap.TILE_WIDTH;
					ly/=TMap.TILE_HEIGHT;
					
					Action a = new Action("Build",player.getentityID(),player.getentityID());
					a.addParameter("type", buildingName[m_buildingToBuild]);
					float coor[] = new float[]{lx*TMap.TILE_WIDTH,ly*TMap.TILE_HEIGHT,0};
					a.addParameter("coor", ActionParameterType.COORDINATE.toString(coor));
					actions.add(a);
					m_buildingToBuild=-1;					
				}
			}
		}
		
	}
}
