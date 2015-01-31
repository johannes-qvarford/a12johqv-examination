/**
 *
 */
package s3.ai.builtin2;

import s3.ai.builtin.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import s3.ai.AStar;

import s3.base.S3;
import s3.base.S3Action;
import s3.entities.*;
import s3.util.Pair;

/**
 * @author Santi: AI that deploys a different strategy depending on the terrain and the opponent:
 */
public class AdaptiveDefensiveRush extends FootmenRush2 {

    static final HashMap<Class,Integer> targetPriority = new HashMap<Class,Integer>();
    static {
        targetPriority.put(WCatapult.class,1);
        targetPriority.put(WTower.class,1);
        targetPriority.put(WKnight.class,1);
        targetPriority.put(WArcher.class,1);
        targetPriority.put(WFootman.class,1);
        targetPriority.put(WPeasant.class,1);

        targetPriority.put(WTownhall.class,2);
        targetPriority.put(WBarracks.class,3);
        targetPriority.put(WStable.class,4);
        targetPriority.put(WLumberMill.class,5);
        targetPriority.put(WBlacksmith.class,6);
        targetPriority.put(WFortress.class,7);
    }
    
    int cycle = 0;
    int strategy = 0;

    // strategy variables (these default values are updated at the first cycle of the game):
    int gold_in_map = 100000;
    int wood_in_map = 100000;
    int distance_between_players = 32;
    int walking_distance_between_players = 32;
    
    Class[] buildOrder = {WBarracks.class};
    
    class FloodFillNode {
        public int x,y,value;
        
        public FloodFillNode(int a_x, int a_y, int a_v) {
            x = a_x;
            y = a_y;
            value = a_v;
        }
    }
    
    
    int nTowers = 4;

    public AdaptiveDefensiveRush(String playerID) {
        super(playerID);
        troopClass = WFootman.class;
        nGoldPeasants = 2;
        nWoodPeasants = 0;
        nBarracks = 1;
        nTowers = 0;
        wave_size = 2;
    }

    public void game_cycle(S3 game, WPlayer player, List<S3Action> actions)
            throws ClassNotFoundException, IOException {
        if (game.getCycle() % 25 != 0) {
            return;
        }

        List<Request> requests = new LinkedList<Request>();

        checkStrategy(game, player);
        
        requests.addAll(checkTownhall(game, player));
        requests.addAll(checkBuildings(game, player));
        requests.addAll(checkPeasants(game, player));
        requests.addAll(checkTowers(game, player));
        requests.addAll(buildTroops(game, player));
        requests.addAll(attack(game, player));

        executeRequests(requests, game, player, actions);
        cycle++;
    }
    
    
    public void checkStrategy(S3 game, WPlayer player) {
        // In the first cycle compute map features:
        {
            // one unit at random from each player:
            WUnit player1_unit = null;
            WUnit player2_unit = null;
            
            gold_in_map = 0;
            wood_in_map = 0;
            
            // Compute gold in map:
            for(S3Entity e:game.getAllUnits()) {
                if (e instanceof WGoldMine) {
                    gold_in_map += ((WGoldMine)e).getRemaining_gold();
                }
                
                if (e instanceof WUnit) {
                    if (e.getOwner()!=null) {
                        if (e.getOwner().equals(player.owner)) {
                            if (player1_unit==null) {
                                player1_unit = (WUnit)e;
//                                System.out.println("player 1: " + e);
                            }
                        } else {
                            if (player2_unit==null) {
                                player2_unit = (WUnit)e;
//                                System.out.println("player 2: " + e);
                            }
                        }
                    }
                }
            }
            // Compute wood in map:
            for(int y = 0;y<game.getMap().getHeight();y++) {
                for(int x = 0;x<game.getMap().getWidth();x++) {
                    S3PhysicalEntity pe = game.getMap().getEntity(x, y);
                    if (pe != null && 
                        pe instanceof WOTree) {
                        wood_in_map += 100;
                    }
                }
            }
            if (cycle==0) {
                // Compute player distances:
                if (player1_unit!=null && player2_unit!=null) {
                    int x1 = player1_unit.getX();
                    int y1 = player1_unit.getY();
                    int x2 = player2_unit.getX();
                    int y2 = player2_unit.getY();

                    distance_between_players = (int)Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));

                    // compute the walking distance:
                    game.removeUnit(player1_unit);
                    game.removeUnit(player2_unit);
                    WUnit tmp_u = new WPeasant();
                    tmp_u.setX(x1);
                    tmp_u.setX(x2);
                    AStar pathPlanner = new AStar(x1, y1, x2, y2, tmp_u, game);
                    List<Pair<Integer, Integer>> path = pathPlanner.computePath();
    //                pathPlanner.printVisitedNodes();
                    game.addUnitDirectly(player1_unit);
                    game.addUnitDirectly(player2_unit);
                    if (path!=null) {
                        walking_distance_between_players = path.size();
                    } else {
                        walking_distance_between_players = -1;
                    }
                }
            }
/*
            System.out.println("Strategy variables:");
            System.out.println("Gold in map: " + gold_in_map);
            System.out.println("Wood in map: " + wood_in_map);
            System.out.println("distance: " + distance_between_players);
            System.out.println("walking distance: " + walking_distance_between_players);
*/
        }
        
        // histogram of enemy units:
        HashMap<String, Integer> enemyUnits = new HashMap<String, Integer>();
        for(S3Entity e:game.getAllUnits()) {
            if (e instanceof WUnit) {
                if (e.getOwner()!=null) {
                    if (!e.getOwner().equals(player.owner)) {
                        String type = e.getClass().getSimpleName();
                        Integer n = enemyUnits.get(type);
                        if (n==null) n = 0;
                        enemyUnits.put(type, n+1);
                    }
                }
            }
        }
    
        switch(strategy) {
            case 0: 
                    troopClass = WFootman.class;
                    nGoldPeasants = 2;
                    nWoodPeasants = 0;
                    nBarracks = 1;
                    nTowers = 0;
                    wave_size = 2;
                    buildOrder = new Class[]{WBarracks.class};
                
                    strategy = 2;
                    break;
            case 1: {
                        troopClass = WFootman.class;
                        nGoldPeasants = 2;
                        nWoodPeasants = 0;
                        nBarracks = 1;
                        wave_size = 4;
                        
                        if (distance_between_players>40) nGoldPeasants+=2;
                        if (walking_distance_between_players>100) nGoldPeasants++;
                        
                        if (nTrainedTroops>2*wave_size) {
                            nGoldPeasants++;
                            if (nTrainedTroops>4*wave_size) nGoldPeasants = 4;
                            if (player.getGold()>1000) {
                                nWoodPeasants = 1;
                                nBarracks = 1;
                                // add lumbermill, so that we can build towers!
                                buildOrder = new Class[]{WBarracks.class, WLumberMill.class};
                                if (nTrainedTroops>2*wave_size) {
                                    nGoldPeasants++;
                                    nBarracks = 2;
                                }
                            }
                        } else {
                            buildOrder = new Class[]{WBarracks.class};                            
                        }
                        
                        Integer net = enemyUnits.get("WTower");
                        Integer nef = enemyUnits.get("WFortress");
                        if (net!=null && net>0) {
                            // enemy has towers!
                            if (nef!=null && nef>0 && 
                                gold_in_map + player.getGold()>10000 &&
                                wood_in_map + player.getWood()>5000) {
                                // knights!
                                strategy = 4;
                            } else {
                                if (gold_in_map + player.getGold()>5000 &&
                                    wood_in_map + player.getWood()>2000 &&
                                    walking_distance_between_players<80) {  // catapults are slow, if it's too far, they won't work!
                                    // catapults is possible:
                                    strategy = 3;
                                } else {
                                    // no better strategy is possible...
                                    // ...
                                }
                                
                            }
                        }
                    }
                    break;
            case 2: {
                        troopClass = WArcher.class;
                        nGoldPeasants = 2;
                        nWoodPeasants = 1;
                        nBarracks = 1;
                        wave_size = 4;
                        buildOrder = new Class[]{WBarracks.class, WLumberMill.class};

                        if (distance_between_players>40) nGoldPeasants+=2;
                        if (walking_distance_between_players>100) nGoldPeasants++;
                
                        if (nTrainedTroops>2*wave_size) {
                            nGoldPeasants = 4;
                            if (nTrainedTroops>5*wave_size) nGoldPeasants = 5;
                            if (player.getGold()>1000) {
                                nBarracks = 2;                        
                            }
                        }
                        
                        Integer net = enemyUnits.get("WTower");
                        Integer nef = enemyUnits.get("WFortress");
                        if (net!=null && net>0) {
                            // enemy has towers!
                            if (nef!=null && nef>0 && 
                                gold_in_map + player.getGold() > 10000 &&
                                wood_in_map + player.getWood() > 5000) {
                                // knights!
                                strategy = 4;
                            } else {
                                if (gold_in_map + player.getGold()>5000 &&
                                    wood_in_map + player.getWood()>2000 &&
                                    walking_distance_between_players<80) {  // catapults are slow, if it's too far, they won't work!
                                    // catapults is possible:
                                    strategy = 3;
                                } else {
                                    // no better strategy is possible...
                                    // ...
                                }
                                
                            }
                        }                        
                    }
                    break;
            case 3: {
                        troopClass = WCatapult.class;
                        nGoldPeasants = 3;
                        nWoodPeasants = 1;
                        nBarracks = 1;
                        wave_size = 4;
                        buildOrder = new Class[]{WBarracks.class, WLumberMill.class, WBlacksmith.class};

                        if (distance_between_players>40) nGoldPeasants+=2;
                        if (walking_distance_between_players>100) nGoldPeasants++;                        
                        
                        if (nTrainedTroops>2*wave_size && player.getGold()>1000) {
                            nGoldPeasants = 5;
                            nBarracks = 2;
                        }

                        Integer net = enemyUnits.get("WTower");
                        Integer nef = enemyUnits.get("WFortress");
                        if (net!=null && net>0 &&
                            nef!=null && nef>0 && 
                            gold_in_map + player.getGold() > 10000 &&
                            wood_in_map + player.getWood() > 5000) {
                            // knights!
                            strategy = 4;
                        }   
                    }
                    break;
            case 4: {
                        troopClass = WKnight.class;
                        nGoldPeasants = 4;
                        nWoodPeasants = 2;
                        nBarracks = 1;
                        wave_size = 4;
                        buildOrder = new Class[]{WBarracks.class, WLumberMill.class, WBlacksmith.class, WFortress.class, WStable.class};

                        if (distance_between_players>40) nGoldPeasants+=2;
                        if (walking_distance_between_players>100) nGoldPeasants++;
                        
                        if (nTrainedTroops>2*wave_size && player.getGold()>1000) {
                            nGoldPeasants = 6;
                            nBarracks = 2;
                        }
                    }   
                    break;
        }
        
        // common techniques:
        if (nTrainedTroops>=wave_size*2) {
            if (player.getGold()<600) {
                nGoldPeasants++;
                nTowers = 0;
            } else {
                nTowers = 2;
            }
            if (nTrainedTroops>=wave_size*4) {
                if (player.getGold()<600) {
                    nGoldPeasants++;
                    nTowers = 0;
                } else {
                    nTowers = 4;
                }
            }
        }         
        
        if (walking_distance_between_players==-1) nWoodPeasants=2;
        if (gold_in_map==0) nGoldPeasants=0;
        if (wood_in_map==0) nWoodPeasants=0;
        
        
//        System.out.println(nTrainedTroops + " - Strategy: " + strategy + ", towers: " + nTowers + ", gp/wp: " + nGoldPeasants + "/" + nWoodPeasants);
    }
        

    List<Request> checkTowers(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        boolean lumberMill = false;
        if (DEBUG >= 1) {
            System.out.println("AdaptiveDefensiveRush: checkTowers");
        }
        int nt = 0;
        for (S3Entity e : game.getAllUnits()) {
            if (e instanceof WTower && e.getOwner().equals(m_playerID)) {
                nt++;
            }
            if (e instanceof WLumberMill && e.getOwner().equals(m_playerID)) {
                lumberMill = true;
            }
        }

        if (!lumberMill) {
            // Sine the 'checkBarracks' method already builds the lumber mill, just wait
            return requests;
        }

        if (nt < nTowers) {
            List<WUnit> peasants = game.getUnitTypes(player, WPeasant.class);
            WPeasant peasant = null;
            for (WUnit p : peasants) {
                if (p.getStatus() != null
                        && p.getStatus().m_action == S3Action.ACTION_BUILD
                        && p.getStatus().m_parameters.get(0).equals(WTower.class.getSimpleName())) {
                    // There is already a peasant building a barracks:
                    return requests;
                }
            }
            for (WUnit p : peasants) {
                if (p.getStatus() == null
                        || p.getStatus().m_action != S3Action.ACTION_BUILD) {
                    peasant = (WPeasant) p;
                }
            }
            if (null == peasant) {
                return requests;
            }
            // First try one location with space to walk around it:
            Pair<Integer, Integer> loc = findBestTowerLocation(game,player);
            if (loc==null) {
                loc = game.findFreeSpace(peasant.getX(), peasant.getY(), 4);
                if (loc == null) {
                    loc = game.findFreeSpace(peasant.getX(), peasant.getY(), 2);
                    if (loc == null) {
                        return requests;
                    }
                } else {
                    // build in the center:
                    loc.m_a++;
                    loc.m_b++;
                }
            }
            if (DEBUG >= 1) {
                System.out.println("AdaptiveDefensiveRush: building tower at " + loc.m_a + " , " + loc.m_b);
            }

            requests.add(new Request(125, peasant.entityID, 900, 300, new S3Action(peasant.entityID, S3Action.ACTION_BUILD, WTower.class.getSimpleName(), loc.m_a, loc.m_b)));
        }
        return requests;
    }

    List<Request> checkBuildings(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        if (DEBUG >= 1) {
            System.out.println("AdaptiveDefensiveRush: checkBuildings");
        }

        for (int i = 0; i < buildOrder.length; i++) {
            int target = 1;
            int n = 0;
            if (buildOrder[i] == WBarracks.class) {
                target = nBarracks;
            }
            for (S3Entity e : game.getAllUnits()) {
                if (buildOrder[i].isInstance(e) && e.getOwner().equals(m_playerID)) {
                    n++;
                    if (n >= target) {
                        break;
                    }
                }
            }
            if (n < target) {
                List<WUnit> peasants = game.getUnitTypes(player, WPeasant.class);
                WPeasant peasant = null;
                for (WUnit p : peasants) {
                    if (p.getStatus() != null
                            && p.getStatus().m_action == S3Action.ACTION_BUILD
                            && p.getStatus().m_parameters.get(0).equals(buildOrder[i].getSimpleName())) {
                        return requests;
                    }
                }
                for (WUnit p : peasants) {
                    if (p.getStatus() == null
                            || p.getStatus().m_action != S3Action.ACTION_BUILD) {
                        peasant = (WPeasant) p;
                    }
                }
                if (null == peasant) return requests;
                
                if (!peasant.isAllowedToBuild(buildOrder[i].getSimpleName())) return requests;
                
                // First try one locatino with space to walk around it:
                WBuilding b = null;
                try {
                    b = (WBuilding) buildOrder[i].newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (b == null) return requests;

                Pair<Integer, Integer> loc = game.findFreeSpace(peasant.getX(), peasant.getY(), b.getWidth() + 2);
                if (null == loc) {
                    loc = game.findFreeSpace(peasant.getX(), peasant.getY(), b.getWidth());
                    if (loc == null) {
                        return requests;
                    }
                } else {
                    loc.m_a++;
                    loc.m_b++;
                }

                if (DEBUG >= 1) {
                    System.out.println("AdaptiveDefensiveRush: building " + buildOrder[i].getSimpleName() + " at " + loc.m_a + " , " + loc.m_b);
                }

                requests.add(new Request(150, peasant.entityID, b.getCost_gold(), b.getCost_wood(), new S3Action(peasant.entityID, S3Action.ACTION_BUILD, buildOrder[i].getSimpleName(), loc.m_a, loc.m_b)));
                break;
            }
        }

        return requests;
    }

    List<Request> buildTroops(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        List<WUnit> barrackss = game.getUnitTypes(player, WBarracks.class);
        List<WUnit> stables = game.getUnitTypes(player, WStable.class);
        WBarracks barracks = null;
        for (WUnit b : barrackss) {
            if (b.getStatus() == null || b.getStatus().m_action == S3Action.ACTION_STAND_GROUND) {
                barracks = (WBarracks) b;
            }
        }
        
        if (null == barracks) {
            return requests;
        }
        
        if (!barracks.isAllowedToTrain(troopClass.getSimpleName())) return requests;
        
        WUnit troop = null;
        try {
            troop = (WUnit) troopClass.newInstance();
        } catch (Exception e) {
        }
        if (troop != null && barracks.getStatus() == null && player.getGold() >= troop.getCost_gold() && player.getWood() >= troop.getCost_wood()) {
            S3Action a = new S3Action(barracks.entityID, S3Action.ACTION_TRAIN, troopClass.getSimpleName());
            requests.add(new Request(100, barracks.entityID, troop.getCost_gold(), troop.getCost_wood(), a));
        }
        return requests;
    }
    
    
    Pair<Integer,Integer> findBestTowerLocation(S3 game, WPlayer player) {
        int dx = game.getMap().getWidth();
        int dy = game.getMap().getHeight();
        boolean walkable[] = new boolean[dx*dy];
        
        List<FloodFillNode> seedsBuildings = new LinkedList<FloodFillNode>();
        int distanceFromBuildings[] = new int[dx*dy];
        
        List<FloodFillNode> seedsEnemy = new LinkedList<FloodFillNode>();
        int distanceFromEnemy[] = new int[dx*dy];
        
        List<FloodFillNode> seedsTowers = new LinkedList<FloodFillNode>();
        int distanceFromTowers[] = new int[dx*dy];
        
        for(int y = 0, offs = 0; y<dy; y++) {
            for(int x = 0;x<dx;x++, offs++) {
                if (game.isSpaceFree(1,x, y)) {    
                    walkable[offs]=true;
                } else {
                    walkable[offs]=false;
                }
                distanceFromBuildings[offs] = -1;
                distanceFromEnemy[offs] = -1;
                distanceFromTowers[offs] = -1;
            }
        }
        
        for(WUnit u:game.getUnits()) {
            if (u.getOwner()!=null) {
                if (u.getOwner().equals(player.owner)) {
                    // friendly:
                    if (u instanceof WBuilding) {                        
                        if (u instanceof WTower) {
                            for(int x = 0;x<u.getWidth();x++) {
                                for(int y = 0;y<u.getLength();y++) {
                                    seedsTowers.add(new FloodFillNode(u.getX()+x, u.getY()+y, 0));
                                }
                            }
                        } else {
                            for(int x = 0;x<u.getWidth();x++) {
                                for(int y = 0;y<u.getLength();y++) {
                                    seedsBuildings.add(new FloodFillNode(u.getX()+x, u.getY()+y, 0));
                                }
                            }                            
                        }
                    }
                } else {
                    // unfriendly:
                   if (u instanceof WBuilding) {                        
                        for(int x = 0;x<u.getWidth();x++) {
                            for(int y = 0;y<u.getLength();y++) {
                                seedsEnemy.add(new FloodFillNode(u.getX()+x, u.getY()+y, 0));
                            }
                        }                            
                   }
                }
            }
        }
        
        floodFill(distanceFromBuildings, walkable, dx, dy, seedsBuildings);
        floodFill(distanceFromTowers, walkable, dx, dy, seedsTowers);
        floodFill(distanceFromEnemy, walkable, dx, dy, seedsEnemy);
        
        // Now select a location that maximizes:
        // d_enemy - 100 * d_buildings + 0.5 * d_towers
        // subject to the constraint: d_buildings > 2
        Pair<Integer,Integer> best = null;
        float best_score = 0;
        for(int y = 0; y<dy; y++) {
            for(int x = 0;x<dx;x++) {
                int offs = x + y*dx;
                if (distanceFromBuildings[offs]>2) {
                    float f1 = -100 * distanceFromBuildings[offs];
                    float f2 = -1 * distanceFromEnemy[offs];
                    float f3 = 0.5f * distanceFromTowers[offs];
                    float score = f1 + f2 + f3;
                    
                    if (best == null || score>best_score) {
                        if (game.isSpaceFree(2,x, y)) {
                            best = new Pair<Integer,Integer>(x,y);
                            best_score = score;
                        }
                    }
                }
            }
        }
        
//        System.out.println("best " + best + " with " + best_score);
        
        return best;
    }
        
    void floodFill(int []matrix, boolean []walkable, int dx, int dy, List<FloodFillNode> open) {
        int nextx[] = {1,0,-1, 0};
        int nexty[] = {0,1, 0,-1};
        
//        System.out.println("Size of seeds: " + open.size());
        
        while(!open.isEmpty()) {
            FloodFillNode n = open.remove(0);
            if (matrix[n.x + n.y*dx]!=-1) continue;
            
            matrix[n.x + n.y*dx] = n.value;
            for(int i = 0;i<nextx.length;i++) {
                int nx = n.x + nextx[i];
                int ny = n.y + nexty[i];
                
                if (nx>=0 && nx<dx && ny>=0 && ny<dy &&
                    walkable[nx+ny*dx] &&
                    matrix[nx+ny*dx]==-1) {
                    open.add(new FloodFillNode(nx, ny, n.value+1));
                }
            }
        }
        
        /*
        System.out.println("Matrix result:");
        for(int y = 0;y<dy;y++) {
            for(int x = 0;x<dx;x++) {
                System.out.print(" " + matrix[x+y*dx]);
            }
            System.out.println("");
        }
        */
    }
    
    
    protected List<Request> attack(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        List<WTroop> troops = new LinkedList<WTroop>();
        int nt = 0;        
        
        for(WUnit u:game.getUnits()) {
            if (u.getOwner()!=null && u.getOwner().equals(m_playerID)) {
                if ((u instanceof WTroop) &&
                    !(u instanceof WPeasant)) troops.add((WTroop)u);
            }
        }
        
        for (WUnit fm : troops) {
            if (fm.getStatus() == null || 
                fm.getStatus().m_action == S3Action.ACTION_STAND_GROUND) {
                nt++;
            }
        }

        if (nt < wave_size) {
            for (WTroop u : troops) {
                if (troopFromPresiouvWaves.contains(u)) {
                    requests.addAll(attack(game,player,u));
                }
            }
        } else {
            for (WTroop u : troops) {
                troopFromPresiouvWaves.add(u);
                if (u.getStatus() == null || u.getStatus().m_action != S3Action.ACTION_ATTACK) {
                    requests.addAll(attack(game,player,u));
                }
            }
        }
        return requests;
    }    
    
    
    public List<Request> attack(S3 game, WPlayer player, WTroop troop) {
        List<Request> requests = new LinkedList<Request>();        
        WUnit enemyTroop = null;
        int bestPriority = 0;
        int bestPath = 0;
        for(WUnit e:game.getUnits()) {
            // check if there is a path from this unit to one of ours:
            if (e.getOwner()!=null && !e.getOwner().equals(player.owner)) {
                int priority = targetPriority.get(e.getClass());
                if (enemyTroop==null || priority<=bestPriority) {
                    Pair<Integer,Integer> pos = troop.rangedLoc(e, game);
                    AStar pathPlanner = new AStar(troop.getX(), troop.getY(), pos.m_a, pos.m_b, troop, game);
                    List<Pair<Integer, Integer>> path = pathPlanner.computePath();
                    if (path!=null) {
                        if (enemyTroop==null ||
                            priority<bestPriority ||
                            (priority==bestPriority && path.size()<bestPath)) {
                            enemyTroop = e;
                            bestPriority = priority;
                            bestPath = path.size();
                        }
                    }
                }
            }
        }
        
        /*
        if (enemyTroop!=null) {
            System.out.println("target: " + enemyTroop.getClass() + " (" + bestPriority + ") at distane " + bestPath);
        }
        */
        
        if (enemyTroop!=null) {
            if (troop.getStatus() == null ||
                troop.getStatus().m_action != S3Action.ACTION_ATTACK ||
                troop.getStatus().getIntParameter(0) != enemyTroop.entityID)
                requests.add(new Request(100, troop.entityID, 0, 0, new S3Action(troop.entityID, S3Action.ACTION_ATTACK, enemyTroop.entityID)));
        }
        return requests;
    }
    
    
    protected void executeRequests(List<Request> requests, S3 game, WPlayer player, List<S3Action> actions) {
                
        // also if any does not use any resources, execute it: (like attacks)
        List<Request> toExecute = new LinkedList<Request>();

        // sort requests, and if the one with highest priority can be satisfied, do it:
        {
            int committed_gold = 0;
            int committed_wood = 0;
            Request highest = null;
            
            Collections.sort(requests, new RequestComparator());
            
/*            
            System.out.println("-- " + game.getCycle() + " -- Sorted Requests: ---");
            for (Request r : requests) {
                System.out.println("  " + r);
            }
  */          
                        
            for (Request r : requests) {                
                if ((r.costGold + committed_gold <= player.getGold() &&
                     r.costWood + committed_wood <= player.getWood()) ||
                    (r.costWood == 0 &&
                     r.costGold + committed_gold <= player.getGold())) {
                    boolean valid = true;
                    if (r.action.m_action == S3Action.ACTION_BUILD) {
                        WPeasant u = (WPeasant)game.getUnit(r.unitID);
                        if (!u.isAllowedToBuild((String)r.action.m_parameters.get(0))) valid = false;
                    } else if (r.action.m_action == S3Action.ACTION_TRAIN) {
                        WBuilding u = (WBuilding)game.getUnit(r.unitID);
                        if (!u.isAllowedToTrain((String)r.action.m_parameters.get(0))) valid = false;
                    }
                    
                    if (valid) {
                        toExecute.add(r);
                        committed_gold += r.costGold;
                        committed_wood += r.costWood;
//                    } else {
//                        System.out.println("REMOVED FOR DEPENDENCIES (" + ((String)r.action.m_parameters.get(0)) + ") " + r);
                    }
                }
            }
        }

        // Filter the toExecute list for actions to the same peasant:
        List<Request> toDelete = new LinkedList<Request>();
        for(int i = 0;i<toExecute.size();i++) {
            for(int j = i+1;j<toExecute.size();j++) {
                Request r1 = toExecute.get(i);
                Request r2 = toExecute.get(j);
                if (r1.unitID == r2.unitID) {
                    if (r1.priority >= r2.priority && !toDelete.contains(r2)) {
                        toDelete.add(r2);

//                        System.out.println("REMOVING: " + r2);
                    }
                }
            }
        }
                
        toExecute.removeAll(toDelete);
/*
        System.out.println("-- To Execute: ---");
        for(Request r:toExecute) {
            System.out.println("  " + r);
        }
*/      
        
        for (Request r : toExecute) {
            actions.add(r.action);
            if (r.action.m_action == S3Action.ACTION_TRAIN && 
                !r.action.m_parameters.get(0).equals(WPeasant.class.getSimpleName())) {
                nTrainedTroops++;
            }
        }
    }    
}
