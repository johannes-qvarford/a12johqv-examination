package bc.mmpm.d2;

import gatech.mmpm.Action;
import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.PhysicalEntity;
import gatech.mmpm.Trace;
import gatech.mmpm.util.XMLWriter;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.worldmodel.ActionsModel;

// import bc.d2.actions.*;
// import bc.d2.entities.*;



public class BattleCityActionsModel extends ActionsModel {
	
	// Variables for the simulation:
	int m_current_simulation_cycle = 0;

	public BattleCityActionsModel() {
		super();
	}
	
	public void learn(Trace trace) {
	}

	public String toString() {
		String out = "BCActionsModel.";
		return out;
	}

	public void savetoXML(XMLWriter w)
	{
		w.tagWithAttributes("actions-model","class='bc.d2.BCActionsModel'");
		w.tag("/actions-model");
	}


	public void loadfromXML(Element e)
	{
	}

	public void resetSimulation(GameState gs) {
		m_current_simulation_cycle = 0;
	}


	public GameState simulate(GameState working_gs,List<Action> newActions, int cycles) {
		try {
			List<Entity> todelete = new LinkedList<Entity>();
			List<Entity> toadd = new LinkedList<Entity>();
			Class FireClass = Class.forName("bc.d2.actions.Fire");
			Class MoveClass = Class.forName("bc.d2.actions.Move");
			Class BCOTankClass = Class.forName("bc.d2.actions.CBOTank");
			Class BCOBulletClass = Class.forName("bc.d2.entities.BCOBullet");
			Class BCOTankGeneratorClass = Class.forName("bc.d2.entities.BCOTankGenerator");
			Class BCOBaseClass = Class.forName("bc.d2.entities.BCOBase");
			Class BCOWallClass = Class.forName("bc.d2.entities.BCOWall");
			Class BCOBlockClass = Class.forName("bc.d2.entities.BCOBlock");
			Class BCOWaterClass = Class.forName("bc.d2.entities.BCOWater");
	
			m_current_simulation_cycle+=cycles;
							
			for(Action a:newActions) {
				if (FireClass.isInstance(a)) {
					PhysicalEntity t = (PhysicalEntity)working_gs.getEntity(a.getEntityID());
					if (t!=null) {
						PhysicalEntity b = (PhysicalEntity)BCOBulletClass.getConstructor(String.class,String.class).newInstance("BCOBullet-" + t.getowner() + "-" + m_current_simulation_cycle,t.getowner());
						b.setFeatureValue("direction","" + (Integer)t.featureValue("direction"));
						b.setFeatureValue("tank",t.getentityID());
						if ((Integer)t.featureValue("direction")==0) {
							b.setx(t.getx()+8);
							b.sety(t.gety());
						}
						if ((Integer)t.featureValue("direction")==1) {
							b.setx(t.getx()+16);
							b.sety(t.gety()+8);
						}
						if ((Integer)t.featureValue("direction")==2) {
							b.setx(t.getx()+8);
							b.sety(t.gety()+16);
						}
						if ((Integer)t.featureValue("direction")==3) {
							b.setx(t.getx());
							b.sety(t.gety()+8);
						}
						t.setFeatureValue("next_shot_delay","16");
						toadd.add(b);
					} else {
	//					System.err.println("Action Fire referred to unit " + a.getEntityID() + " which does not exist in game state");
	//					System.err.println("Cloned:");
	//					System.err.println(working_gs.toXMLString());
					}
				}
			}
			for(Action a:newActions) {
				if (MoveClass.isInstance(a)) {
	//				System.out.println("action: " + a);
	//				System.out.flush();
					PhysicalEntity t = (PhysicalEntity)working_gs.getEntity(a.getEntityID());
					if (t!=null) {
	//					m_tanksRemainingMovement.put(t.getentityID(), 8);
						t.setFeatureValue("direction",a.parameterStringValue("direction"));
						t.setFeatureValue("next_move_delay","8");
					} else {
	//					System.err.println("Action Move referred to unit " + a.getEntityID() + " which does not exist in game state");
	//					System.err.println("Cloned:");
	//					System.err.println(working_gs.toXMLString());
					}
				}
			}
			
			for(int i=0;i<cycles;i++) {
				for(Entity ee:working_gs.getAllEntities()) {
					if (ee instanceof PhysicalEntity) {
						PhysicalEntity e = (PhysicalEntity)ee;
						/* We'll ignore the tank generators for now:
						if (BCOTankGeneratorClass.isInstance(e)) {
							
							if (e.getRemaining_tanks() > 0) {
								if (e.getTime_for_next()<=0) {
									
									// Create a new tank:
									BCOEnemyTank t = new BCOEnemyTank(null,null);
									t.setx(e.getx());
									t.sety(e.gety());
									toadd.add(t);							
									e.setTime_for_next(e.getInterval());							
								} else {
									e.setTime_for_next(e.getTime_for_next()-1);
								}
							} else {
								todelete.add(e);
							}
						}
						*/
						
						if (BCOTankClass.isInstance(e)) {
							float oldx = e.getx(), oldy = e.gety();
							int nmd = (Integer)e.featureValue("next_move_delay");
							if (nmd>0l) {
								if (((Integer)e.featureValue("direction"))==0) e.sety(e.gety()-2);
								if (((Integer)e.featureValue("direction"))==1) e.setx(e.getx()+2);
								if (((Integer)e.featureValue("direction"))==2) e.sety(e.gety()+2);
								if (((Integer)e.featureValue("direction"))==3) e.setx(e.getx()-2);
								e.setFeatureValue("next_move_delay","" + (nmd-1));
							}
							
							if ((Integer)e.featureValue("next_shot_delay")>0) 
								e.setFeatureValue("next_shot_delay","" + ((Integer)e.featureValue("next_shot_delay")-1));
							
							for(PhysicalEntity pe2:working_gs.getCollisionsOf(e)) {
								if (BCOWallClass.isInstance(pe2) ||
									BCOBlockClass.isInstance(pe2) ||
									BCOWaterClass.isInstance(pe2)) {
									e.setx(oldx);
									e.sety(oldy);
								}
							}
						}
						if (BCOBulletClass.isInstance(e)) {
							if ((Integer)e.featureValue("direction")==0) e.sety(e.gety()-4);
							if ((Integer)e.featureValue("direction")==1) e.setx(e.getx()+4);
							if ((Integer)e.featureValue("direction")==2) e.sety(e.gety()+4);
							if ((Integer)e.featureValue("direction")==3) e.setx(e.getx()-4);	
							
							for(PhysicalEntity pe2:working_gs.getCollisionsOf(e)) {
								if (BCOBaseClass.isInstance(pe2)) {
									List<Entity> el = working_gs.getEntityByOwner(pe2.getowner());
									for(Entity e3:el) todelete.add(e3);										
									todelete.add(e);
								} else if (BCOTankClass.isInstance(pe2)) {
									if (!e.getentityID().equals(e.featureValue("tank"))) {
										todelete.add(pe2);			
										todelete.add(e);											
									}
								} else if (BCOWaterClass.isInstance(pe2)) {
									
								} else if (BCOWallClass.isInstance(pe2)) {
									todelete.add(e);
								} else {
									todelete.add(pe2);
									todelete.add(e);
								}
							}
						}
					}
				}
	
				while(!todelete.isEmpty()) {
					Entity e = todelete.remove(0);
					working_gs.deleteEntity(e);
				}
				while(!toadd.isEmpty()) working_gs.addEntity(toadd.remove(0));
	
			}
	
			return working_gs;
		} catch (Exception e) {
			System.err.println("BattleCityActionModel: exception while simulating");
			e.printStackTrace();			
		}
		return null;
	}

}
