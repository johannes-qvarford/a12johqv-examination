package bc.ai;

import bc.BCMap;
import java.io.IOException;
import java.util.List;

import bc.BattleCity;
import bc.PlayerInput;
import bc.helpers.Pair;
import bc.helpers.VirtualController;
import bc.mmpm.entities.BCOBullet;
import bc.objects.BCOBase;
import bc.objects.BCOTank;
import bc.objects.BCPhysicalEntity;
import gatech.mmpm.TwoDMap;
import java.util.LinkedList;

public class AIFollower extends AIRandom {

    public AIFollower(String playerID) {
        super(playerID);
    }

    public void game_cycle(VirtualController vc, BattleCity game, BCOTank tank) throws IOException, ClassNotFoundException {
        int move_command = -1;
        boolean fire_command = false;

        vc.m_joystick[PlayerInput.DIRECTION_UP] = false;
        vc.m_joystick[PlayerInput.DIRECTION_DOWN] = false;
        vc.m_joystick[PlayerInput.DIRECTION_LEFT] = false;
        vc.m_joystick[PlayerInput.DIRECTION_RIGHT] = false;
        vc.m_button[0] = false;

        if (tank.readyToFire()) {
            int dir = tank.getdirection();
            if (move_command != -1) {
                dir = move_command;
            }
            BCPhysicalEntity o = on_sight(dir, game.getMap(), "BCOBase", tank);

            if (o == null
                    || !((BCOBase) o).getowner().equals(m_playerID)) {
                fire_command = true;
            }

            if (fire_command) {
                vc.m_button[0] = true;
            }
        } // if     

        if (tank.readyToMove()) {
            List<BCPhysicalEntity> l = game.getMap().getObjects("BCOPlayerTank");
            BCPhysicalEntity closest = null;

            if (l == null || l.size() == 1) {
                l = game.getMap().getObjects("BCOEnemyTank");
            }

            int min_distance = -1;

            for (BCPhysicalEntity o : l) {
                if (o != tank) {
                    int d = (tank.getx() - o.getx()) * (tank.getx() - o.getx()) + (tank.gety() - o.gety()) * (tank.gety() - o.gety());
                    if (min_distance == -1 || d < min_distance) {
                        closest = o;
                        min_distance = d;
                    }
                }
            } // while
            
            
            // Use A* to find the closest path to a tank:
            List<Integer> path = shortestPathTo(game,tank,closest);
            
            if (path!=null && !path.isEmpty()) move_command = path.get(0);                
            if (move_command != -1) {
                vc.m_joystick[move_command] = true;
            }
        }
    }
    
    
    List<Integer> shortestPathTo(BattleCity game, BCOTank tank, BCPhysicalEntity destination) throws IOException, ClassNotFoundException {
        BCMap map = game.getMap();
        List<Pair<Integer,Integer>> open =  new LinkedList<Pair<Integer,Integer>>();
        
        int dx = map.getDx()/BCMap.TILE_WIDTH;
        int dy = map.getDy()/BCMap.TILE_HEIGHT;
        int grid[] = new int[dx*dy];
        for(int i = 0;i<dx*dy;i++) grid[i]=-1;
        
//        System.out.println("DX: " + dx);
        int start = (tank.getx() / BCMap.TILE_WIDTH) + (tank.gety() / BCMap.TILE_HEIGHT)*dx;
        int current = start;
        int target = (destination.getx() / BCMap.TILE_WIDTH) + (destination.gety() / BCMap.TILE_HEIGHT)*dx;
        open.add(new Pair<Integer,Integer>(current,current));
        
        while(!open.isEmpty()) {
            Pair<Integer,Integer> tmp = open.remove(0);
            current = tmp.m_a;
            grid[tmp.m_a] = tmp.m_b;
//            System.out.println("current: " + current + "(" + open.size() + ")");
            if (current==target) {
//                List<Integer> visited = new LinkedList<Integer>();
                List<Integer> path = new LinkedList<Integer>();
                current = target;
//                System.out.println("Tracing back:");
                while(current!=start) {
//                    visited.add(current);
//                    System.out.println(" - " + current);
                    int parent = grid[current];
                    if (parent+1 == current) path.add(0,PlayerInput.DIRECTION_RIGHT);
                    else if (parent-1 == current) path.add(0,PlayerInput.DIRECTION_LEFT);
                    else if (parent+dx == current) path.add(0,PlayerInput.DIRECTION_DOWN);
                    else if (parent-dx == current) path.add(0,PlayerInput.DIRECTION_UP);
                    else {
                        System.err.println("Difference between parent and current is: " + (current-parent));
                    }
                    current = parent;
                }
                /*
                {
                    for(int i = 0;i<dy;i++) {
                        for(int j = 0;j<dx;j++) {
                            if (grid[j+i*dx]==-1) System.out.print(".");
                            if (grid[j+i*dx]==-2) System.out.print("W");
                            if (grid[j+i*dx]>=0) {
                                if (visited.contains(j+i*dx)) System.out.print("+");
                                else System.out.print("*");
                                
                            }
                        }
                        System.out.println("");
                    }
                }
                */
                
                return path;
            }
            
            int xoff[] = {0, BCMap.TILE_WIDTH, 0, -BCMap.TILE_WIDTH};
            int yoff[] = {-BCMap.TILE_HEIGHT, 0, BCMap.TILE_HEIGHT, 0};
            int off[] = {-dx,1,dx,-1};
            for(int i = 0;i<4;i++) {
                if (grid[current+off[i]]==-1) {
                    int x = (current % dx) * BCMap.TILE_WIDTH + xoff[i];
                    int y = (current / dx) * BCMap.TILE_HEIGHT + yoff[i];
                    if (!map.collisionOnlyMap(tank, x - tank.getx(), y - tank.gety())) {
//                       System.out.println("next: " + (current+off[i]));
                       open.add(new Pair<Integer,Integer>(current+off[i],current));
                    }
                    grid[current+off[i]] = -2; // mark as seen
                }
            }
        }
               
        
        return null;
    }
}
