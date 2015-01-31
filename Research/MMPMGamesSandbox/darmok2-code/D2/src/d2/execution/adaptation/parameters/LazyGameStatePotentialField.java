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



public class LazyGameStatePotentialField extends GameStatePotentialField {
	/*
	 * GameStatePotentialField creates a collection of potential fields
	 * for each different entity type and owner (player) in the game.
	 */

	String m_player;
	boolean []computed = null;
	Vector<HashMap<String,float []>> m_potentialFields = new Vector<HashMap<String,float []>>();

	public LazyGameStatePotentialField() {
		super();
	}


	public LazyGameStatePotentialField(GameState gs,String player) {
		super();
		if (powCache==null) computeCache();
		
		m_gs = gs;
		m_player = player;

		size = gs.getMap().size();
		
		m_potentialFields.add(new HashMap<String,float []>());
		m_potentialFields.add(new HashMap<String,float []>());
		m_potentialFields.add(new HashMap<String,float []>());
		
		computed = new boolean[gs.getMap().size()];
				
		for(int i=0;i<gs.getMap().size();i++) computed[i] = false;		
		
		if (m_gs.getMap() instanceof TwoDMap){
			TwoDMap map = (TwoDMap)m_gs.getMap();
			float []borderField = new float[map.size()];
			int mapSize = map.size();
			for(int pos = 0; pos<mapSize ; pos++) borderField[pos]=0;
			EntityWeights.addFakeClass("*BORDER*",0.5);
			m_potentialFields.get(NEUTRAL_FIELDS).put("*BORDER*",borderField);
		}
		
		// This forces to compute at least one cell
		get(NEUTRAL_FIELDS,"*BORDER*",0);
		
	}

	
	void considerEntity(Entity e,Map map,String player,int pos) {
		String p;
		PhysicalEntity pe;
		String ec;
		float []potentialField;
		Class<?> c;
		

		if (e instanceof PhysicalEntity) {
			pe = (PhysicalEntity)e;
			c = pe.getClass();
			
			while(c!=null && c!=Entity.class) {
				ec = c.getName();
				p = pe.getowner();
				
				if (p==null) {
					// Neutral:
					potentialField = m_potentialFields.get(NEUTRAL_FIELDS).get(ec);
					if (potentialField==null) {
						potentialField = new float[map.size()];
						m_potentialFields.get(NEUTRAL_FIELDS).put(ec,potentialField);
					}
				} else if (p.equals(player)) {
					// Player:
					potentialField = m_potentialFields.get(PLAYER_FIELDS).get(ec);
					if (potentialField==null) {
						potentialField = new float[map.size()];
						m_potentialFields.get(PLAYER_FIELDS).put(ec,potentialField);
					}
				} else {
					// Enemy:
					potentialField = m_potentialFields.get(ENEMIES_FIELDS).get(ec);
					if (potentialField==null) {
						potentialField = new float[map.size()];
						m_potentialFields.get(ENEMIES_FIELDS).put(ec,potentialField);	
					}
				}
								
				if (map.getNumberOfDimensions()>0) {
					for(int w = 0;w<pe.getwidth();w+=map.getCellSizeInDimension(0)) {
						if (map.getNumberOfDimensions()>1) {
							for(int l = 0;l<pe.getlength();l+=map.getCellSizeInDimension(1)) {
								if (map.getNumberOfDimensions()>2) {
									for(int h = 0;h<pe.getheight();h+=map.getCellSizeInDimension(2)) {
										float coords[] = pe.get_Coords();
										coords[0]+=w;
										coords[1]+=l;
										coords[2]+=h;
										addToPotentialField(potentialField,map,coords,pos);							
										coords[0]-=w;
										coords[1]-=l;
										coords[2]-=h;
									}
								} else {
									float coords[] = pe.get_Coords();
									coords[0]+=w;
									coords[1]+=l;
									addToPotentialField(potentialField,map,coords,pos);									
									coords[0]-=w;
									coords[1]-=l;
								}
							}					
						} else {
							float coords[] = pe.get_Coords();
							coords[0]+=w;
							addToPotentialField(potentialField,map,coords,pos);
							coords[0]-=w;
						}
					}
				} else {
					float coords[] = pe.get_Coords();
					addToPotentialField(potentialField,map,coords,pos);												
				}
				c = c.getSuperclass();
			}
		}
		
	}
	
	
	void addToPotentialField(float []pf,Map map,float []coords,int pos) {
//		int size = map.size();
		int coords1[]={0,0,0};
		int coords2[]={0,0,0};
		map.toCellCoords(coords,coords2);
		map.toCellCoords(pos,coords1);
		float sqDistance = map.squareDistance(coords1, coords2);
		if (sqDistance<MAX_DISTANCE*MAX_DISTANCE) {
			pf[pos]+=powCache[(int) sqDistance];
			if (pf[pos]>maxValue) pf[pos]=maxValue;
		}
	}
	

	public float get(int type, String field, int pos) {
//		System.out.print("[" + pos + "]");
		if (computed[pos]) {
			float[] f = m_potentialFields.get(type).get(field);
			if (f==null) return 0;
			return f[pos];
		} else {
//			System.out.print("{" + pos + "}");
			
			computed[pos] = true;
			
			for(Entity e:m_gs.getAllEntities()) considerEntity(e,m_gs.getMap(),m_player,pos);
			
			for(int i=0;i<m_gs.getMap().size();i++) {
				Entity e = m_gs.getMap().get(i);
//				System.out.println(i + " -> " + e.getClass().getSimpleName());
				if (e!=null) considerEntity(e,m_gs.getMap(),m_player,pos);
			}
			
			// ADd the borders for TwoDMaps:
			if (m_gs.getMap() instanceof TwoDMap){
				TwoDMap map = (TwoDMap)m_gs.getMap();
				float []borderField = m_potentialFields.get(NEUTRAL_FIELDS).get("*BORDER*");
				int d1,d2,d;
				int x = pos % map.getSizeInDimension(0);
				int y = pos / map.getSizeInDimension(0);
				d1 = Math.min(x,y);
				d2 = Math.min((map.getSizeInDimension(0)-1)-x,(map.getSizeInDimension(1)-1)-y);
				d = Math.min(d1,d2);
				d=d*d;
				if (d<MAX_DISTANCE*MAX_DISTANCE) {
					borderField[pos]=powCache[d];
				} else {
					borderField[pos]=0;
				}
				EntityWeights.addFakeClass("*BORDER*",BORDER_WEIGHT);
				m_potentialFields.get(NEUTRAL_FIELDS).put("*BORDER*",borderField);
			}			
			return m_potentialFields.get(type).get(field)[pos];
		}
	}


	public float get(float[] field, int pos) {
//		System.out.print("[" + pos + "]");
		if (computed[pos]) {
			return field[pos];
		} else {
//			System.out.print("{" + pos + "}");
			
			computed[pos] = true;
			
			for(Entity e:m_gs.getAllEntities()) considerEntity(e,m_gs.getMap(),m_player,pos);
			
			for(int i=0;i<m_gs.getMap().size();i++) {
				Entity e = m_gs.getMap().get(i);
//				System.out.println(i + " -> " + e.getClass().getSimpleName());
				if (e!=null) considerEntity(e,m_gs.getMap(),m_player,pos);
			}
			
			// ADd the borders for TwoDMaps:
			if (m_gs.getMap() instanceof TwoDMap){
				TwoDMap map = (TwoDMap)m_gs.getMap();
				float []borderField = m_potentialFields.get(NEUTRAL_FIELDS).get("*BORDER*");
				int d1,d2,d;
				int x = pos % map.getSizeInDimension(0);
				int y = pos / map.getSizeInDimension(0);
//				System.out.println(pos + " -> (" + x + "," + y + ")");
				d1 = Math.min(x,y);
				d2 = Math.min((map.getSizeInDimension(0)-1)-x,(map.getSizeInDimension(1)-1)-y);
				d = Math.min(d1,d2);
				d=d*d;
				if (d<MAX_DISTANCE*MAX_DISTANCE) {
					borderField[pos]=powCache[d];
				} else {
					borderField[pos]=0;
				}
				EntityWeights.addFakeClass("*BORDER*",0.5);
				m_potentialFields.get(NEUTRAL_FIELDS).put("*BORDER*",borderField);
			}			
			return field[pos];
		}		
	}


	public float[] getField(int type, String field) {
		return m_potentialFields.get(type).get(field);
	}


	public Collection<String> getFieldNames(int type) {
		return m_potentialFields.get(type).keySet();
	}
	
}
