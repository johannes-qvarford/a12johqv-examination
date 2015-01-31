/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation.parameters;

import gatech.mmpm.Map;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



public class PotentialFieldWindow extends EagerGameStatePotentialField {
	int m_window_size;

	// The direction rotates the potential field, 0 (Map.DIRECTION_Y_NEG) is the default direction
	public PotentialFieldWindow(GameStatePotentialField pf,Map map,int center,int window_size,int direction) {
		size = (int)Math.pow(window_size,map.getNumberOfDimensions());
		int coords[] = new int[3];
		int centerCoords[] = map.toCellCoords(center);
		int mapSize = map.size();
		
		m_window_size = window_size;
		
		for(int fields = 0;fields<3;fields++) {			
			HashMap<String,float []> lf = new HashMap<String,float []>();
			m_potentialFields.add(lf);
			
			// We create a duplicate of the list to prevent a concurrent modification exception:
			List<String> fieldNames = new LinkedList<String>();
			fieldNames.addAll(pf.getFieldNames(fields));
			
			for(String cn:fieldNames) {
				float []field_pf = pf.getField(fields, cn);
				float []lf_pf = new float[size];
				
				lf.put(cn, lf_pf);
				
				for(int i=0;i<size;i++) {
					
					if (map.getNumberOfDimensions()==1) {
						coords[0]=i;						
					} else if (map.getNumberOfDimensions()==2) {
						coords[0]=i%window_size;
						coords[1]=i/window_size;
					} else if (map.getNumberOfDimensions()==3) {
						coords[0]=i%window_size;
						coords[1]=(i%(window_size*window_size))/window_size;						
						coords[2]=i/(window_size*window_size);
					} else {
						coords[0]=0;
						coords[1]=0;
						coords[2]=0;
					}
	
					for(int j=0;j<map.getNumberOfDimensions();j++) {
						coords[j]-=window_size/2;
					}

					rotateCoords(coords,direction);
					
					for(int j=0;j<map.getNumberOfDimensions();j++) {
						coords[j]+=centerCoords[j];
					}
										
					int cell = map.toCell(coords);
					if (cell>=0 && cell<=mapSize) {
						lf_pf[i]=pf.get(field_pf,cell);
					} else {
						lf_pf[i]=0;
					}						
				}					
			}
			
		}
	}
	
	
	void rotateCoords(int []coords,int direction) {
		int tmp;
		switch(direction) {
		case Map.DIRECTION_Y_NEG:
				break;
		case Map.DIRECTION_Y_POS:
				coords[0]=-coords[0];
				coords[1]=-coords[1];
				break;
		case Map.DIRECTION_X_POS:
				tmp = coords[0];
				coords[0]=-coords[1];
				coords[1]=tmp;
				break;
		case Map.DIRECTION_X_NEG:
				tmp = coords[0];
				coords[0]=coords[1];
				coords[1]=-tmp;
				break;
		case Map.DIRECTION_Z_POS:
				tmp = coords[1];
				coords[1] = -coords[2];
				coords[2] = tmp;
				break;
		case Map.DIRECTION_Z_NEG:
				tmp = coords[1];
				coords[1] = coords[2];
				coords[2] = -tmp;
				break;
		}
	}
	
	
//	double similarity(PotentialFieldWindow pf,double []ownerWeights,HashMap<String,Double> entityWeights) {
//		return 1.0-distance(pf,ownerWeights,entityWeights);
//	}

	public double distance(PotentialFieldWindow pf,double []ownerWeights,HashMap<String,Double> entityWeights) {
		double accum_distance = 0,s;
		for(int i= 0;i<size;i++) {
			s = distance(i,pf,i,ownerWeights,entityWeights);
			accum_distance+=s*s;
		}
		
		return Math.sqrt(accum_distance)/Math.sqrt(size);
	}
	
	public void printFields() {
		String labels[]={"Player","Enemy","Neutral"};
		
		for(int i = 0;i<3;i++) {
			System.out.print("Fields " + i + ": " + m_potentialFields.get(i).keySet().size() + " ");
			for(String cn:m_potentialFields.get(i).keySet()) System.out.print(cn + " ");
			System.out.println("");
		}
		
		for(int i = 0;i<3;i++) {
			System.out.println(labels[i] + " fields:");
			HashMap<String,float []> fields = m_potentialFields.get(i);
			for(String cn:fields.keySet()) {
				System.out.println("Field for " + cn + " (weight: " + EntityWeights.getWeight(cn) + ")");
				
				float field[] = fields.get(cn);
				
				// Note: we only know how to pretty print the field for a 2D map...
				if (size == m_window_size * m_window_size) {
					for(int y=0;y<m_window_size;y++) {
						for(int x=0;x<m_window_size;x++) {
							System.out.format("%.2f  ",field[x+y*m_window_size]);
						}
						System.out.println("");
					}
				} else {
					for(int j=0;j<size;j++) {
						System.out.format("%.2f  ",field[j]);
					}
					System.out.println("");
					
				}
			}
		}

	}

	
}
