package towers.objects;

import gatech.mmpm.ActionParameterType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import towers.Action;
import towers.TMap;
import towers.Towers;
import towers.helpers.Pair;

public class TPlayer extends Entity {

	Pair<Integer,Action> m_pendingAction = null;  
	
	float m_r,m_g,m_b;
	int gold = 0;

	int cycle = 0;

	public TPlayer(String owner,int a_gold,int ar,int ag,int ab) {
		super(owner,owner);
		gold = a_gold;
		m_r=ar/255.0f;
		m_g=ag/255.0f;
		m_b=ab/255.0f;
	}

	public Object clone() {
		return new TPlayer(owner,gold,(int)(m_r*255),(int)(m_g*255),(int)(m_b*255));
	}

	public int getGold() {
		return gold;
	}	

	public float get_r() {
		return m_r;
	}

	public float get_g() {
		return m_g;
	}

	public float get_b() {
		return m_b;
	}

	public boolean cycle(TMap map, Towers game, List<Action> actions) throws IOException, ClassNotFoundException {
		cycle++;

		List<Action> todelete = new LinkedList<Action>();
		int n_gold_upgrades = 0;
		List<PhysicalEntity> upgradeGold_l = map.getObjects(TUpgradeGold.class);
		int n_unit_upgrades = 0;
		List<PhysicalEntity> upgradeUnits_l = map.getObjects(TUpgradeUnits.class);
		List<PhysicalEntity> bases = map.getObjects(TBase.class);
		PhysicalEntity base = null;
		List<Action> actionsToAttempt = new LinkedList<Action>();

		for(PhysicalEntity pe:bases) if (pe.getowner().equals(this.getentityID())) base=pe;
		for(PhysicalEntity pe:upgradeGold_l) if (pe.getowner().equals(this.getentityID())) n_gold_upgrades++;
		for(PhysicalEntity pe:upgradeUnits_l) if (pe.getowner().equals(this.getentityID())) n_unit_upgrades++;

		// If our base has been destroyed, end of game:
		if (base==null) return false;
		
		if (cycle>=100*Math.pow(0.8, n_gold_upgrades)) {
			cycle=0;
			gold++;
		}

		if (m_pendingAction!=null && m_pendingAction.m_a<50) {
			actionsToAttempt.add(m_pendingAction.m_b);
			for(Action a:actions) {
				if (a.m_actor.equals(getentityID())) {
					todelete.add(a);
				}
			}
		} else {
			for(Action a:actions) {
				if (a.m_actor.equals(getentityID())) {
					actionsToAttempt.add(a);
				}
			}
		}
		
		for(Action a:actionsToAttempt) {
			if (a.m_name.equals("Build")) {
				PhysicalEntity toAdd = null;
				int spent = 0;

				float[] coor = (float[])
					ActionParameterType.COORDINATE.fromString(a.getParameter("coor"));
				int x = (int)(coor[0])/TMap.TILE_WIDTH;
				int y = (int)(coor[1])/TMap.TILE_HEIGHT;
				String type = a.getParameter("type");

				
				if (map.getObjectAt(x, y)==null) {
					int cost = game.cost(type,owner);
					
//						System.out.println("Building a " + type + " at " + x + "," + y);

					if (type.equals("towers.mmpm.entities.TTower")) {						
						if (gold>=cost) {
							spent = cost;
							TTower t = new TTower(owner + "-" + type + "-" + game.getCycle(),owner,x*TMap.TILE_WIDTH,y*TMap.TILE_HEIGHT);
							toAdd = t;
						} else {
							todelete.add(a);
						}
					}
					if (type.equals("towers.mmpm.entities.TWall")) {
						if (gold>=cost) {
							spent = cost;
							TWall t = new TWall(owner + "-" + type + "-" + game.getCycle(),owner,x*TMap.TILE_WIDTH,y*TMap.TILE_HEIGHT);
							toAdd = t;
						} else {
							todelete.add(a);
						}
					}
					if (type.equals("towers.mmpm.entities.TUpgradeGold")) {
						if (gold>=cost) {
							spent = cost;
							TUpgradeGold t = new TUpgradeGold(owner + "-" + type + "-" + game.getCycle(),owner,x*TMap.TILE_WIDTH,y*TMap.TILE_HEIGHT);
							toAdd = t;
						} else {
							todelete.add(a);
						}
					}
					if (type.equals("towers.mmpm.entities.TUpgradeUnits")) {
						if (gold>=cost) {
							spent = cost;
							TUpgradeUnits t = new TUpgradeUnits(owner + "-" + type + "-" + game.getCycle(),owner,x*TMap.TILE_WIDTH,y*TMap.TILE_HEIGHT);
							toAdd = t;
						} else {
							todelete.add(a);
						}
					}
					
					if (toAdd!=null) {
						map.addObject(toAdd);
						
						if (!map.getPaths().recomputePaths(map)) {
							map.removeObject(toAdd);
							todelete.add(a);
							System.out.println("You are blocking the way!");
						} else {
							gold -= spent;
						}
					}
					m_pendingAction = null;
				} else {
					// There is some object in the location where the building has to be built
					if (m_pendingAction==null) {
						m_pendingAction = new Pair<Integer,Action>(0,a);
					} else {
						if (m_pendingAction.m_b==a) {
							m_pendingAction.m_a++;
						} else {
							m_pendingAction = new Pair<Integer,Action>(0,a);							
						}
					}
				}
			}
		}
		
		actions.removeAll(todelete);
		return true;
	}
	
	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
					 "  <owner>" + owner + "</owner>\n" +			
					 "  <type>" + getClass().getSimpleName() + "</type>\n" +			
					 "  <gold>" + gold + "</gold>\n" +			
					 "  <color>" + rgbStringColor() + "</color>\n";	
		return out + "</entity>";
	}	
	
	public gatech.mmpm.Entity toD2Entity() {
		towers.mmpm.entities.TPlayer ret;
		
		ret = new towers.mmpm.entities.TPlayer(entityID, owner);
		ret.setGold(gold);
		ret.setColor(rgbStringColor());
		return ret;
	}
	
	
	String rgbStringColor() {
		String out="";
		char hex[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		int v,v1,v2;
		
		v = (int)(m_r*255);
		v1 = v/16;
		v2 = v%16;		
		out+=hex[v1] + "" +  hex[v2];

		v = (int)(m_g*255);
		v1 = v/16;
		v2 = v%16;		
		out+=hex[v1] + "" +  hex[v2];
		
		v = (int)(m_b*255);
		v1 = v/16;
		v2 = v%16;		
		out+=hex[v1] + "" +  hex[v2];
		
		return out;
	}



}
