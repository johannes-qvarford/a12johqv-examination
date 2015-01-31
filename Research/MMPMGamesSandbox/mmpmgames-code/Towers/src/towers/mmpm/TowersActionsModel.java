/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.domainspecific;

import gatech.mmpm.Action;
import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.PhysicalEntity;
import gatech.mmpm.Trace;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jdom.Element;

import d2.worldmodel.ActionsModel;


public class TowersActionsModel extends ActionsModel {

	int m_current_simulation_cycle = 0;

	public TowersActionsModel() {
		super();
	}
	
	public String toString() {
		String out = "TowersActionsModel.";
		return out;
	}

	public void learn(Trace trace) {
	}

	public void loadfromXML(Element e) {
	}

	public void savetoXML(XMLWriter w) {
		w.tagWithAttributes("actions-model","class='towers.d2.TowersActionsModel'");
		w.tag("/actions-model");
	}
	
	public void resetSimulation(GameState gs) {
		m_current_simulation_cycle = 0;
	}

	/*
	 * This simulator makes several simplifications to make it run faster:
	 * 	- The timers are not kept strictly
	 *  - The units advance using a simple greedy path planning instead of the full path-planning of the real game
	 *  - The check for proximity in building is not performed (it assumes that the actions are valid)
	 */
	public GameState simulate(GameState working_gs, List<Action> newActions, int cycles) {
		try {
			List<Entity> todelete = new LinkedList<Entity>();
			List<Entity> toadd = new LinkedList<Entity>();	
			List<Entity> bases = working_gs.getEntityByType(Class.forName("towers.mmpm.entities.TBase"));
			List<String> players = new LinkedList<String>();		
			players.addAll(working_gs.getAllPlayers());
			Random rg = new Random();
			HashMap<String,Integer> playerGoldUpgrades = new HashMap<String,Integer>();
			Class BuildClass = Class.forName("towers.mmpm.actions.Build");
			Class TUnitClass = Class.forName("towers.mmpm.entities.TUnit");
			Class TBaseClass = Class.forName("towers.mmpm.entities.TBase");
			Class TTowerClass = Class.forName("towers.mmpm.entities.TTower");
			Class TUpgradeGoldClass = Class.forName("towers.mmpm.entities.TUpgradeGold");
			Class TUpgradeUnitsClass = Class.forName("towers.mmpm.entities.TUpgradeUnits");
			Class TPlayerClass = Class.forName("towers.mmpm.wntities.TPlayer");
			
			for(String player:players) {
				int n = 0;
				List<Entity> l = working_gs.getEntityByType(Class.forName("towers.mmpm.entities.TUpgradeUnits"));
				for(Entity entity:l) {
					if (entity.getowner().equals(player)) n++;
				}
				playerGoldUpgrades.put(player,n);
			}
			
			for(Action a:newActions) {
				if (BuildClass.isInstance(a)) {
					PhysicalEntity pe = null;
					if (a.parameterStringValue("type").equals("TTower")) {
						pe = (PhysicalEntity)TTowerClass.getConstructor(String.class,String.class).newInstance("",a.getPlayerID());
						pe.setx(((float[])(a.parameterValue("coor")))[0]);
						pe.sety(((float[])(a.parameterValue("coor")))[1]);	
						working_gs.addEntity(pe);
					}
					if (a.parameterStringValue("type").equals("TUpgradeGold")) {
						pe = (PhysicalEntity)TUpgradeGoldClass.getConstructor(String.class,String.class).newInstance("",a.getPlayerID());
						pe.setx(((float[])(a.parameterValue("coor")))[0]);
						pe.sety(((float[])(a.parameterValue("coor")))[1]);	
						working_gs.addEntity(pe);
						playerGoldUpgrades.put(a.getPlayerID(),playerGoldUpgrades.get(a.getPlayerID())+1);
					}
					if (a.parameterStringValue("type").equals("TUpgradeUnits")) {
						pe = (PhysicalEntity)TUpgradeUnitsClass.getConstructor(String.class,String.class).newInstance("",a.getPlayerID());
						pe.setx(((float[])(a.parameterValue("coor")))[0]);
						pe.sety(((float[])(a.parameterValue("coor")))[1]);	
						working_gs.addEntity(pe);
					}
					if (a.parameterStringValue("type").equals("TWall")) {
						working_gs.getMap().setCellLocation('w', 
									working_gs.getMap().toCellCoords(((float[])(a.parameterValue("coor")))),
									d2.core.Config.getDomain());
					}
				}
			}
			
			for(int i = 0;i<cycles;i++) {
				for(Entity ee:working_gs.getAllEntities()) {
					if (ee instanceof PhysicalEntity) {
						PhysicalEntity e = (PhysicalEntity)ee; 
						if (TUnitClass.isInstance(ee) && (m_current_simulation_cycle%25)==0) {
							PhysicalEntity target = null;
							
							for(Entity pb:bases) if (pb.getowner().equals(e.featureValue("target"))) target = (PhysicalEntity)pb;
							
							if (target!=null) {
								float dx = target.getx()-e.getx();
								float dy = target.gety()-e.gety();
								
								if (dy>0) {
									e.sety(e.gety()+16);
								} else if (dy<0) {
									e.sety(e.gety()-16);								
								} else {
									if (dx>0) {
										e.setx(e.getx()+16);
									} else if (dx<0) {
										e.setx(e.getx()-16);								
									} else {
										target.setFeatureValue("hitpoints", "" + (((Integer)target.featureValue("hitpoints"))-1));
										if (((Integer)target.featureValue("hitpoints"))<=0) {
											todelete.add(target);
											players.remove(target.getowner());
										}
										todelete.add(e);
									}
								}
							}
						}
						
						if (TBaseClass.isInstance(e) && players.size()>1 && (m_current_simulation_cycle%100)==0) {					
							PhysicalEntity u = (PhysicalEntity)TUpgradeUnitsClass.getConstructor(String.class,String.class).newInstance("",e.getowner());
							u.setx(e.getx());
							u.sety(e.gety());
							{
								String target = null;
								// Find target:
								while(target == null) {
									String tmp = players.get(rg.nextInt(players.size()));
									if (!tmp.equals(e.getowner())) target = tmp;
								}
								u.setFeatureValue("target",target);
							}
							{
								int n = 0;
								List<Entity> l = working_gs.getEntityByType(TUpgradeUnitsClass);
								for(Entity entity:l) {
									if (entity.getowner().equals(e.getowner())) n++;
								}
								u.setFeatureValue("hitpoints","" + ((int)(5*Math.pow(1.5, n))));
							}
							toadd.add(u);	
						}
						
						if (TTowerClass.isInstance(e) && (m_current_simulation_cycle%20)==0) {						
							PhysicalEntity closest = null;
							float distance = 0,d;
							// Look for the closest unit:
							for(Entity e2:working_gs.getAllEntities()) {
								if (TUnitClass.isInstance(e2)) {
									PhysicalEntity u = (PhysicalEntity)e2;
																	
									if (!u.getowner().equals(e.getowner())) {
										d = (u.getx()-e.getx())*(u.getx()-e.getx())+(u.gety()-e.gety())*(u.gety()-e.gety());
										if (closest == null || d<distance) {
											closest = u;
											distance = d;
										}
									}
								}
							}
							if (closest!=null && distance<(16*5)*(16*5)) {
								closest.setFeatureValue("hitpoints","" + ((int) (closest.getheight()-1)));
								if (((Integer)closest.featureValue("hitpoints"))<=0) {
									todelete.add(closest);
								}
							}						
						}
		
						if (TPlayerClass.isInstance(e)) {
							int n = playerGoldUpgrades.get(e.getowner());
							if (m_current_simulation_cycle%(100*Math.pow(0.8, n))==0) {
								e.setFeatureValue("gold", "" + (((Integer)e.featureValue("gold"))+1));
							}
						}
					}
				}
				
				while(!todelete.isEmpty()) {
					Entity e = todelete.remove(0);
					working_gs.deleteEntity(e);
				}
				while(!toadd.isEmpty()) working_gs.addEntity(toadd.remove(0));	
				
				m_current_simulation_cycle++;
			}
			
			return working_gs;
		} catch (Exception e) {
			System.err.println("TowersActionModel: exception while simulating");
			e.printStackTrace();
		}
		return null;
	}

}
