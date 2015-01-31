/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation.parameters;

import gatech.mmpm.PhysicalEntity;
import gatech.mmpm.GameState;
import gatech.mmpm.TwoDMap;

import java.util.Collection;
import java.util.HashMap;



public abstract class GameStatePotentialField {
	/*
	 * GameStatePotentialField creates a collection of potential fields
	 * for each different entity type and owner (player) in the game.
	 */

	public static final int PLAYER_FIELDS = 0;
	public static final int ENEMIES_FIELDS = 1;
	public static final int NEUTRAL_FIELDS = 2;
	
	public static final float discountFactor = 0.75f;
	public static final float maxValue = 10.0f;
	
	public static final int MAX_DISTANCE = 16;
	
	public static final float BORDER_WEIGHT = 0.25f;
	
	static float powCache[] = null;
	static float gridCache[][] = null;
	int size;
	GameState m_gs;

	public GameStatePotentialField() {
		m_gs = null;
	}
	
	abstract public Collection<String> getFieldNames(int type);
	abstract public float [] getField(int type,String field);
	abstract public float get(int type,String field,int pos);
	abstract public float get(float []field,int pos);
	
	// Output range is 0 - GameStatePotentialField.maxvalue
	// Only those entities for which both have a field!=0 are considered:
	public float distance(int pos1, GameStatePotentialField gspf, int pos2,double []ownerWeights,HashMap<String,Double> entityWeights) {
		// TODO: consider these weights
//		double []ow ={0.4,0.3,0.3};
		float pf1[],pf2[];
		float v1,v2;
		Double w;
		float value[]={0,0,0},weight[]={0,0,0};
		
//		if (ownerWeights!=null) ow = ownerWeights;
				
		for(int fields = 0;fields<3;fields++) {
			
			for(String cn:getFieldNames(fields)) {
				pf1 = getField(fields,cn);
				pf2 = gspf.getField(fields,cn);

                v1 = get(pf1,pos1);
				if (pf2!=null) {
					v2 = gspf.get(pf2,pos2);
				} else {
					v2 = 0;
				}
				if (entityWeights!=null) {
					w = entityWeights.get(cn);
					if (w==null) w = EntityWeights.getWeight(cn);
				} else {
					w = EntityWeights.getWeight(cn);
				}
				if (v1>0 || v2>0) {
					value[fields]+=(v1-v2)*(v1-v2)*w;
					weight[fields]+=w;
				}
			}
			for(String cn:gspf.getFieldNames(fields)) {
				pf1 = getField(fields,cn);
				pf2 = gspf.getField(fields,cn);
				
				v2 = gspf.get(pf2,pos2);
				if (pf1==null) {
					v1 = 0;
					if (entityWeights!=null) {
						w = entityWeights.get(cn);
						if (w==null) w = EntityWeights.getWeight(cn);
					} else {
						w = EntityWeights.getWeight(cn);
					}
					if (v1>0 || v2>0) {
						value[fields]+=(v1-v2)*(v1-v2)*w;
						weight[fields]+=w;
					}
				} else {
					// Already considered
				}
			}
		}
		
//		return Math.sqrt(value/weight);
		float total = 0;
		
		if (weight[0]!=0) total+=value[0]/weight[0];
		if (weight[1]!=0) total+=value[1]/weight[1];
		if (weight[2]!=0) total+=value[2]/weight[2];
		return total;
	}
	
	public void computeCache() {
		int i,j;
		powCache = new float[MAX_DISTANCE*MAX_DISTANCE];
		for(i=0;i<MAX_DISTANCE*MAX_DISTANCE;i++) {
			powCache[i]=(float)Math.pow(0.75f, Math.sqrt(i));
		}
		gridCache = new float[MAX_DISTANCE][MAX_DISTANCE];
		for(i=0;i<MAX_DISTANCE;i++) {
			for(j=0;j<MAX_DISTANCE;j++) {
				int tmp = Math.min(MAX_DISTANCE*MAX_DISTANCE-1,i*i+j*j);
				gridCache[i][j] = powCache[tmp];
			}
		}		
	}

	public void printFieldSummary() {
		String labels[]={"Player","Enemy","Neutral"};
				
		System.out.println("----------------------");
		for(int i = 0;i<3;i++) {
			System.out.println(labels[i] + " fields:");
			for(String cn:getFieldNames(i)) {
				System.out.println(cn + " -> weight: " + EntityWeights.getWeight(cn) + "");
			}
		}

	}

	
	public void printFields() {
		String labels[]={"Player","Enemy","Neutral"};
		
		for(int i = 0;i<3;i++) {
			System.out.print("Fields " + i + ": " + getFieldNames(i).size() + " ");
			for(String cn:getFieldNames(i)) System.out.print(cn + " ");
			System.out.println("");
		}
		
		for(int i = 0;i<3;i++) {
			System.out.println(labels[i] + " fields:");
			for(String cn:getFieldNames(i)) {
				System.out.println("Field for " + cn + " (weight: " + EntityWeights.getWeight(cn) + ")");
				
				float field[] = getField(i,cn);
				
				// Note: we only know how to pretty print the field for a 2D map...
				if (m_gs!=null && m_gs.getMap() instanceof TwoDMap) {
					TwoDMap m = (TwoDMap)m_gs.getMap();
					for(int y=0;y<m.getSizeInDimension(1);y++) {
						for(int x=0;x<m.getSizeInDimension(0);x++) {
							System.out.format("%.2f  ",get(field,x+y*m.getSizeInDimension(0)));
						}
						System.out.println("");
					}
				} else {
					for(int j=0;j<size;j++) {
						System.out.format("%.2f  ",get(field,j));
					}
					System.out.println("");
					
				}
			}
		}

	}
	
	public void printField(String cn) {
		String labels[]={"Player","Enemy","Neutral"};
				
		for(int i = 0;i<3;i++) {
			System.out.println(labels[i] + " fields:");
			{
				System.out.println("Field for " + cn + " (weight: " + EntityWeights.getWeight(cn) + ")");
				
				float field[] = getField(i,cn);
			
				if (field!=null) {
					// Note: we only know how to pretty print the field for a 2D map...
					if (m_gs!=null && m_gs.getMap() instanceof TwoDMap) {
						TwoDMap m = (TwoDMap)m_gs.getMap();
						for(int y=0;y<m.getSizeInDimension(1);y++) {
							for(int x=0;x<m.getSizeInDimension(0);x++) {
								System.out.format("%.2f  ",field[x+y*m.getSizeInDimension(0)]);
							}
							System.out.println("");
						}
					} else {
						for(int j=0;j<size;j++) {
							System.out.format("%.2f  ",get(field,j));
						}
						System.out.println("");
						
					}
				}
			}
		}

	}
		
	public void printFields(int position) {
		String labels[]={"Player","Enemy","Neutral"};
				
		for(int i = 0;i<3;i++) {
			System.out.println(labels[i] + " fields:");
			for(String cn:getFieldNames(i)) {
				float field[] = getField(i,cn);
				if (get(field,position)!=0) {
					System.out.print("Field for " + cn + " (weight: " + EntityWeights.getWeight(cn) + ")");
					System.out.format("  %.2f\n",get(field,position));
				}
			}
		}
	}
	
	public static void printPotentialFields(PhysicalEntity e,GameState gs, String ID) {
		GameStatePotentialField pf = (GameStatePotentialField) gs.getMetaData(ID);
		pf.printFields(gs.getMap().toCell(e.get_Coords()));
	}

}
