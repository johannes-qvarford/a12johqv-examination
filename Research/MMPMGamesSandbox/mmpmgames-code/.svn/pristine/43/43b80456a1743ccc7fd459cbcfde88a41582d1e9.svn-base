package towers;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import towers.helpers.Pair;
import towers.objects.PhysicalEntity;
import towers.objects.TBase;
import towers.objects.TTower;
import towers.objects.TUpgradeGold;
import towers.objects.TUpgradeUnits;
import towers.objects.TWall;

public class Paths {
	public static int DIRECTION_UP = 0;
	public static int DIRECTION_DOWN = 1;
	public static int DIRECTION_LEFT = 2;
	public static int DIRECTION_RIGHT = 3;
	
	int m_dx,m_dy;
	
	HashMap<String,Integer [][]> m_paths = new HashMap<String,Integer [][]>();
	
	public Paths() {
	}
	
	public boolean recomputePaths(TMap map) throws ClassNotFoundException {
		HashMap<String,Integer [][]> paths = new HashMap<String,Integer [][]>();
		List<PhysicalEntity> targets = map.getObjects(TBase.class);
		
		m_dx = map.m_dx/TMap.TILE_WIDTH;
		m_dy = map.m_dy/TMap.TILE_HEIGHT;
		boolean [][]obstacles = new boolean [m_dy][m_dx]; 
		for(int i=0;i<m_dy;i++)
			for(int j=0;j<m_dx;j++)
				obstacles[i][j]=false;
		for(PhysicalEntity pe:map.getAllObjects()) {
			if (pe instanceof TTower ||
				pe instanceof TWall ||
				pe instanceof TUpgradeGold ||
				pe instanceof TUpgradeUnits) {
				obstacles[pe.gety()/TMap.TILE_HEIGHT][pe.getx()/TMap.TILE_WIDTH] = true;
			}
		}
		
		for(PhysicalEntity target:targets) {
			Integer [][]directions = new Integer [m_dy][m_dx]; 
			for(int i=0;i<m_dy;i++)
				for(int j=0;j<m_dx;j++)
					directions[i][j]=null;
			
			List<Pair<Integer,Integer>> open = new LinkedList<Pair<Integer,Integer>>();
			open.add(new Pair<Integer,Integer>(target.getx()/TMap.TILE_WIDTH,target.gety()/TMap.TILE_HEIGHT));
			
			while(!open.isEmpty()) {
				Pair<Integer,Integer> point = open.remove(0);
				
				if (point.m_a>0) {
					if (directions[point.m_b][point.m_a-1]==null) {
						if (!obstacles[point.m_b][point.m_a-1]) {
							directions[point.m_b][point.m_a-1]=DIRECTION_RIGHT;
							open.add(new Pair<Integer,Integer>(point.m_a-1,point.m_b));
						}							
					}
				}
				if (point.m_a<m_dx-1) {
					if (directions[point.m_b][point.m_a+1]==null) {
						if (!obstacles[point.m_b][point.m_a+1]) {
							directions[point.m_b][point.m_a+1]=DIRECTION_LEFT;
							open.add(new Pair<Integer,Integer>(point.m_a+1,point.m_b));
						}							
					}
				}
				if (point.m_b>0) {
					if (directions[point.m_b-1][point.m_a]==null) {
						if (!obstacles[point.m_b-1][point.m_a]) {
							directions[point.m_b-1][point.m_a]=DIRECTION_DOWN;
							open.add(new Pair<Integer,Integer>(point.m_a,point.m_b-1));
						}							
					}
				}
				if (point.m_b<m_dy-1) {
					if (directions[point.m_b+1][point.m_a]==null) {
						if (!obstacles[point.m_b+1][point.m_a]) {
							directions[point.m_b+1][point.m_a]=DIRECTION_UP;
							open.add(new Pair<Integer,Integer>(point.m_a,point.m_b+1));
						}
					}
				}
			}
			
			for(PhysicalEntity t2:targets) {
				if (directions[t2.gety()/TMap.TILE_WIDTH][t2.getx()/TMap.TILE_HEIGHT]==null) return false;
			}
			
			/*
			System.out.println("Added paths for target " + target.getowner());
			for(int i=0;i<m_dy;i++) {
				for(int j=0;j<m_dx;j++) {
					System.out.print(directions[i][j] + "  ");
				}
				System.out.println();
			}
			*/
			paths.put(target.getowner(),directions);
		}

		m_paths = paths;
		
		return true;
	}
	
	public Integer direction(int x,int y,String target) {
		Integer directions[][] = m_paths.get(target);
		
		if (directions==null || x<0 || y<0 || x>=m_dx || y>=m_dy) {
			System.out.println("No directions for '" + target + "' at " + x + "," + y);
			return DIRECTION_RIGHT; 
		}

		return directions[y][x];
	}
}
