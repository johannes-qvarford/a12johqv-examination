/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation.parameters;

import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.Map;
import gatech.mmpm.PhysicalEntity;
import gatech.mmpm.TwoDMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;



public class EagerGameStatePotentialField extends GameStatePotentialField {
	/*
	 * GameStatePotentialField creates a collection of potential fields
	 * for each different entity type and owner (player) in the game.
	 */
	
	Vector<HashMap<String,float []>> m_potentialFields = new Vector<HashMap<String,float []>>();

	public EagerGameStatePotentialField() {
		super();
	}


	public EagerGameStatePotentialField(GameState gs,String player) {
		super();
		if (powCache==null) computeCache();
		
		m_gs = gs;

		size = gs.getMap().size();
		
		m_potentialFields.add(new HashMap<String,float []>());
		m_potentialFields.add(new HashMap<String,float []>());
		m_potentialFields.add(new HashMap<String,float []>());
	
//		System.out.println("Considering entities...");
		for(Entity e:gs.getAllEntities()) considerEntity(e,gs.getMap(),player);

//		System.out.println("Considering map entities...");
		for(int i=0;i<gs.getMap().size();i++) {
			Entity e = gs.getMap().get(i);
//			System.out.println(i + " -> " + e.getClass().getSimpleName());
			if (e!=null) considerEntity(e,gs.getMap(),player);
		}
	
//		System.out.println("Considering borders...");
		// Add the borders for TwoDMaps:
		if (gs.getMap() instanceof TwoDMap){
			TwoDMap map = (TwoDMap)gs.getMap();
			float []borderField = new float[map.size()];
			int pos = 0, d1,d2,d;
			for(int y=0;y<map.getSizeInDimension(1);y++) {
				for(int x=0;x<map.getSizeInDimension(0);x++) {
					d1 = Math.min(x,y);
					d2 = Math.min((map.getSizeInDimension(0)-1)-x,(map.getSizeInDimension(1)-1)-y);
					d = Math.min(d1,d2);
					d=d*d;
					if (d<MAX_DISTANCE*MAX_DISTANCE) {
						borderField[pos]=powCache[d];
					} else {
						borderField[pos]=0;
					}
					pos++;
				}				
			}
			EntityWeights.addFakeClass("*BORDER*",BORDER_WEIGHT);
			m_potentialFields.get(NEUTRAL_FIELDS).put("*BORDER*",borderField);
		}

	}

	
	void considerEntity(Entity e,Map map,String player) {
		String p;
		PhysicalEntity pe;
		String ec;
		float []potentialField;
		Class<?> c;
		

		if (e instanceof PhysicalEntity) {
			int ms = map.size();
			pe = (PhysicalEntity)e;
			c = pe.getClass();
			
			while(c!=null && c!=Entity.class) {
				ec = c.getName();
				p = pe.getowner();
				
				if (p==null) {
					// Neutral:
					potentialField = m_potentialFields.get(NEUTRAL_FIELDS).get(ec);
					if (potentialField==null) {
						potentialField = new float[ms];
						m_potentialFields.get(NEUTRAL_FIELDS).put(ec,potentialField);
					}
				} else if (p.equals(player)) {
					// Player:
					potentialField = m_potentialFields.get(PLAYER_FIELDS).get(ec);
					if (potentialField==null) {
						potentialField = new float[ms];
						m_potentialFields.get(PLAYER_FIELDS).put(ec,potentialField);
					}
				} else {
					// Enemy:
					potentialField = m_potentialFields.get(ENEMIES_FIELDS).get(ec);
					if (potentialField==null) {
						potentialField = new float[ms];
						m_potentialFields.get(ENEMIES_FIELDS).put(ec,potentialField);	
					}
				}
				
				if (map.getNumberOfDimensions()==3) {
					float coords[];
					coords = pe.get_Coords();
					for(int w = 0;w<pe.getwidth();w+=map.getCellSizeInDimension(0)) {
						for(int l = 0;l<pe.getlength();l+=map.getCellSizeInDimension(1)) {
							for(int h = 0;h<pe.getheight();h+=map.getCellSizeInDimension(2)) {
								coords[0]+=w;
								coords[1]+=l;
								coords[2]+=h;
								addToPotentialField(potentialField,map,coords);							
								coords[0]-=w;
								coords[1]-=l;
								coords[2]-=h;
							}
						}					
					}					
				} else if (map.getNumberOfDimensions()==2) {
					float coords[];
					coords = pe.get_Coords();
					for(int w = 0;w<pe.getwidth();w+=map.getCellSizeInDimension(0)) {
						for(int l = 0;l<pe.getlength();l+=map.getCellSizeInDimension(1)) {
							coords[0]+=w;
							coords[1]+=l;
							addToPotentialField(potentialField,map,coords);							
							coords[0]-=w;
							coords[1]-=l;
						}					
					}										
				} else if (map.getNumberOfDimensions()==1) {
					float coords[];
					coords = pe.get_Coords();
					for(int w = 0;w<pe.getwidth();w+=map.getCellSizeInDimension(0)) {
						coords[0]+=w;
						addToPotentialField(potentialField,map,coords);							
						coords[0]-=w;
					}															
				} else {
					float coords[] = pe.get_Coords();
					addToPotentialField(potentialField,map,coords);					
				}
								
				if (map.getNumberOfDimensions()>0) {

				} else {
				}
				c = c.getSuperclass();
			}
		}
		
	}
	
	
	void addToPotentialField(float []pf,Map map,float []coords) {
		int coords1[]={0,0,0};
		int coords2[]={0,0,0};
		map.toCellCoords(coords,coords2);
		if (map instanceof TwoDMap) {
			TwoDMap tdm = (TwoDMap)map;
			int minx = Math.max(0,coords2[0]-(MAX_DISTANCE-1));
			int maxx = Math.min((int)(tdm.getSizeInDimension(0)/tdm.getCellSizeInDimension(0)),coords2[0]+(MAX_DISTANCE-1));
			int miny = Math.max(0,coords2[1]-(MAX_DISTANCE-1));;
			int maxy = Math.min((int)(tdm.getSizeInDimension(1)/tdm.getCellSizeInDimension(1)),coords2[1]+(MAX_DISTANCE-1));
			int dx,dy;
			for(int y = miny;y<maxy;y++) {
				coords1[1]=y;
				dy = Math.abs(y-coords2[1]);
				for(int x = minx;x<maxx;x++) {
					coords1[0]=x;
					dx = Math.abs(x-coords2[0]);
					int i = tdm.toCell(coords1);
					pf[i]+=gridCache[dx][dy];
					if (pf[i]>maxValue) pf[i]=maxValue;
				}			
			}
		} else {
			int size = map.size();
			for(int i = 0;i<size;i++) {
				map.toCellCoords(i,coords1);
				float sqDistance = map.squareDistance(coords1, coords2);
				if (sqDistance<MAX_DISTANCE*MAX_DISTANCE) {
					pf[i]+=powCache[(int) sqDistance];
					if (pf[i]>maxValue) pf[i]=maxValue;
				}
			}
		}
	}
	
	
	public float get(int type, String field, int pos) {
		float[] f = m_potentialFields.get(type).get(field);
		if (f==null) return 0;
		return f[pos];
	}


	public float get(float[] field, int pos) {
        if (pos<0 || pos>=field.length) {
            System.err.println("EagerGameStatePotentialField: getting field from outside of the map!!!");
            return 0;
        }
		return field[pos];
	}


	public float[] getField(int type, String field) {
		return m_potentialFields.get(type).get(field);
	}


	public Collection<String> getFieldNames(int type) {
		return m_potentialFields.get(type).keySet();
	}
	
/*
	// This is just for testing purposes:
	public static void main(String args[]) {
		String playerName = "player1";		
		
		PlanBase pb = new SimilarityRetrieval();
		Properties conf = new Properties();
		conf.setProperty("domain", "bc");
		// Initialize The Entities:
		
		try {
			String cn = "domain." + conf.getProperty("domain") + ".Index";
			Class.forName(cn).getDeclaredMethod("initialize", null).invoke(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PlanLearning pl = new MonolithicPlanLearning(pb, conf.getProperty("domain"), new WinGameGoal());				
		ParseLmxTrace p = new ParseLmxTrace(conf.getProperty("domain"));
		String filename = "traces/bc/trace-0.xml";
		
		if (p.initializeDOMParser(filename)) {
			try {
				Trace trace = p.parse(0);
				System.out.println("Trace loaded...");
				
				EntityHierarchy.printWeights(Entity.class);
				
				int count  = 0;
				long st1 = System.currentTimeMillis();
//				for(Entry e:trace.getEntries())
				{
					Entry e = trace.getEntries().get(0);
					long st2 = System.currentTimeMillis();
					GameStatePotentialField gspf = new GameStatePotentialField(e.getGameState(),playerName);
					gspf.printFields();
					System.out.println("Time taken: " + (System.currentTimeMillis()-st2));
					count++;
					// visualize the potential fields
				}
				System.out.println("Total Time taken (" + count + "): " + (System.currentTimeMillis()-st1));
				
			} catch(Exception e) {
				e.printStackTrace();				
			}
		}		
	}
*/

	
}
