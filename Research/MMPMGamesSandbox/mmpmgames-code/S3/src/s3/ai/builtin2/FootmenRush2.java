/**
 *
 */
package s3.ai.builtin2;

import s3.ai.builtin.*;
import java.io.IOException;
import java.util.*;

import s3.ai.AI;
import s3.ai.AStar;
import s3.base.S3;
import s3.base.S3Action;
import s3.entities.*;
import s3.util.Pair;

/**
 * @author Santi: footmen rush in waves
 *
 * This AI is the same as "FootmenRush" in the package "builtin" except for the
 * following improvements: 
 * - Better target selection (only consider reachable targets)
 * - If the AI has other attacking units, even if they are not of the primary type of this rush, they are also used for attack
 */

public class FootmenRush2 implements AI {
    
    // target classes sorted by priority:
    Class[] targetClasses = {WCatapult.class, WTower.class, WKnight.class, WArcher.class, WFootman.class, WPeasant.class,
                             WTownhall.class, WBarracks.class, WLumberMill.class, WBlacksmith.class,
                             WFortress.class, WStable.class};

    public class Request {

        public int priority;
        public int unitID;
        public int costGold, costWood;
        public S3Action action;

        public Request(int p, int ID, int g, int w, S3Action a) {
            priority = p;
            unitID = ID;
            costGold = g;
            costWood = w;
            action = a;
        }
        
        public String toString() {
            return "(" + priority + ")-" + action;
        }
    }

    
    public class RequestComparator implements Comparator<Request>{

        public int compare(Request o1, Request o2) {
            return (o1.priority>o2.priority ? -1 : (o1.priority==o2.priority ? 0 : 1));
        }
    }    
    

    Class troopClass = null;
    int DEBUG = 0;
    int nGoldPeasants = 2;
    int nWoodPeasants = 2;
    int nBarracks = 1;
    int nTrainedTroops = 0;
    int wave_size = 4;
    String m_playerID;
    
    // This stores all the units from previous waves, so that they continue attacking, while the 
    // current wave is being trained:
    HashSet<WUnit> troopFromPresiouvWaves = new HashSet<WUnit>();

    public FootmenRush2(String playerID) {
        m_playerID = playerID;
        troopClass = WFootman.class;
    }

    public void gameEnd() {
    }

    public void gameStarts() {
    }

    public void game_cycle(S3 game, WPlayer player, List<S3Action> actions)
            throws ClassNotFoundException, IOException {
        if (game.getCycle() % 25 != 0) {
            return;
        }

        List<Request> requests = new LinkedList<Request>();

        requests.addAll(checkTownhall(game, player));
        requests.addAll(checkBarracks(game, player));
        requests.addAll(checkPeasants(game, player));
        requests.addAll(buildTroops(game, player));
        requests.addAll(attack(game, player));

        executeRequests(requests, game, player, actions);
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
            System.out.println("-- Sorted Requests: ---");
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

            // count how many footmen we train:
            if (r.action.m_action == S3Action.ACTION_TRAIN
                    && r.action.m_parameters.get(0).equals(WFootman.class.getSimpleName())) {
                nTrainedTroops++;
                if (nTrainedTroops == 5) {
                    nGoldPeasants++;
                }
                if (nTrainedTroops == 10) {
                    nBarracks++;
                    nGoldPeasants++;
                }
                if (nTrainedTroops == 15) {
                    nGoldPeasants++;
                }
            }

        }
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

        WPlayer enemy = null;
        for (WPlayer entity : game.getPlayers()) {
            if (entity != player) {
                enemy = entity;
                break;
            }
        }

        WUnit enemyTroop = null;
        for (Class c : targetClasses) {
            List<WUnit> el = game.getAllUnitsByType(enemy, c);
            for(WUnit e:el) {
                // check if there is a path from this unit to one of ours:
//                System.out.println("troops: " + troops.size());
                for(WTroop t:troops) {
                    Pair<Integer,Integer> pos = t.rangedLoc(e, game);
//                    System.out.println(t.getX() + "," + t.getY() + " : " + pos);
                    AStar pathPlanner = new AStar(t.getX(), t.getY(), pos.m_a, pos.m_b, t, game);
                    List<Pair<Integer, Integer>> path = pathPlanner.computePath();
                    if (path!=null) {
                        enemyTroop = e;
                        break;
//                    } else {
//                        System.out.println("  No path from " + t.getClass().getSimpleName() + " to " + e.getClass().getSimpleName());
                    }
                }
                if (enemyTroop!=null) break;
            }
            if (enemyTroop!=null) break;
        }

        if (enemyTroop == null) {
            return requests;
        }

        if (nt < wave_size) {
            for (WUnit u : troops) {
                if (troopFromPresiouvWaves.contains(u)) {
                    if (u.getStatus() == null ||
                        u.getStatus().m_action != S3Action.ACTION_ATTACK ||
                        u.getStatus().getIntParameter(0) != enemyTroop.entityID)
                        requests.add(new Request(100, u.entityID, 0, 0, new S3Action(u.entityID, S3Action.ACTION_ATTACK, enemyTroop.entityID)));
                }
            }
        } else {
            for (WUnit u : troops) {
                troopFromPresiouvWaves.add(u);
                if (u.getStatus() == null ||
                    u.getStatus().m_action != S3Action.ACTION_ATTACK ||
                    u.getStatus().getIntParameter(0) != enemyTroop.entityID)
                    requests.add(new Request(100, u.entityID, 0, 0, new S3Action(u.entityID, S3Action.ACTION_ATTACK, enemyTroop.entityID)));               
            }
        }
        return requests;
    }

    List<Request> buildTroops(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        List<WUnit> barrackss = game.getUnitTypes(player, WBarracks.class);
        WBarracks barracks = null;
        for (WUnit b : barrackss) {
            if (b.getStatus() == null || b.getStatus().m_action == S3Action.ACTION_STAND_GROUND) {
                barracks = (WBarracks) b;
            }
        }
        if (null == barracks) {
            return requests;
        }
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

    protected List<Request> checkPeasants(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        int gp = 0;
        int wp = 0;
        List<WPeasant> freePeasants = new LinkedList<WPeasant>();
        for (S3Entity e : game.getAllUnits()) {
            if (e instanceof WPeasant && e.getOwner().equals(m_playerID)) {
                WPeasant peasant = (WPeasant) e;
                if (peasant.getStatus() != null && peasant.getStatus().m_action == S3Action.ACTION_HARVEST) {
                    if (peasant.getStatus().m_parameters.size() == 1) {
                        gp++;
                    } else {
                        wp++;
                    }
                } else {
                    if (peasant.getStatus() == null) {
                        freePeasants.add(peasant);
                    }
                }
            }
        }

        if (gp < nGoldPeasants && freePeasants.size() > 0) {
            WPeasant peasant = freePeasants.get(0);
            List<WUnit> mines = game.getUnitTypes(null, WGoldMine.class);
            WGoldMine mine = null;
            int leastDist = 9999;
            for (WUnit unit : mines) {
                int dist = peasant.distance(unit, game);
                if (dist != -1 && dist < leastDist && ((WGoldMine) unit).getRemaining_gold() > 0) {
                    leastDist = dist;
                    mine = (WGoldMine) unit;
                }
            }

            if (mine != null) {
                requests.add(new Request(100, peasant.entityID, 0, 0, new S3Action(peasant.entityID, S3Action.ACTION_HARVEST, mine.entityID)));
                return requests;
            }
        }
        if (wp < nWoodPeasants && freePeasants.size() > 0) {
            WPeasant peasant = freePeasants.get(0);
            List<WOTree> trees = new LinkedList<WOTree>();
            for (int i = 0; i < game.getMap().getWidth(); i++) {
                for (int j = 0; j < game.getMap().getHeight(); j++) {
                    S3PhysicalEntity e = game.getMap().getEntity(i, j);
                    if (e instanceof WOTree) {
                        trees.add((WOTree) e);
                    }
                }
            }

            WOTree tree = null;
            int leastDist = 9999;
            for (WOTree unit : trees) {
                int dist = Math.abs(unit.getX() - peasant.getX())
                        + Math.abs(unit.getY() - peasant.getY());
                if (dist < leastDist) {
                    leastDist = dist;
                    tree = unit;
                }
            }

            if (tree != null) {
                requests.add(new Request(100, peasant.entityID, 0, 0, new S3Action(peasant.entityID, S3Action.ACTION_HARVEST, tree.getX(), tree.getY())));
                return requests;
            }
        }

        if ((gp < nGoldPeasants || wp < nWoodPeasants) && freePeasants.isEmpty()) {
            WTownhall th = (WTownhall) game.getUnitType(player, WTownhall.class);
            if (th != null && (th.getStatus() == null)) {
                requests.add(new Request(200, th.entityID, 400, 0, new S3Action(th.entityID, S3Action.ACTION_TRAIN, WPeasant.class.getSimpleName())));
            }
        }
        
//        System.out.println("npeasants: " + gp + "/" + wp);

        return requests;

    }

    List<Request> checkBarracks(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        if (DEBUG >= 1) {
            System.out.println("FootmenRush2: checkBarracks");
        }
        int nb = 0;
        for (S3Entity e : game.getAllUnits()) {
            if (e instanceof WBarracks && e.getOwner().equals(m_playerID)) {
                nb++;
            }
        }

        if (nb < nBarracks) {
            List<WUnit> peasants = game.getUnitTypes(player, WPeasant.class);
            WPeasant peasant = null;
            for (WUnit p : peasants) {
                if (p.getStatus() != null
                        && p.getStatus().m_action == S3Action.ACTION_BUILD
                        && p.getStatus().m_parameters.get(0).equals(WBarracks.class.getSimpleName())) {
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
            
            if (!peasant.isAllowedToBuild(WBarracks.class.getSimpleName())) return requests;
            
            // First try one locatino with space to walk around it:
            Pair<Integer, Integer> loc = game.findFreeSpace(peasant.getX(), peasant.getY(), 5);
            if (null == loc) {
                loc = game.findFreeSpace(peasant.getX(), peasant.getY(), 3);
                if (loc == null) {
                    return requests;
                }
            }
            if (DEBUG >= 1) {
                System.out.println("FootmenRush2: building barracks at " + loc.m_a + " , " + loc.m_b);
            }

            requests.add(new Request(150, peasant.entityID, 700, 450, new S3Action(peasant.entityID, S3Action.ACTION_BUILD, WBarracks.class.getSimpleName(), loc.m_a, loc.m_b)));
        }
        return requests;
    }

    protected List<Request> checkTownhall(S3 game, WPlayer player) {
        List<Request> requests = new LinkedList<Request>();
        if (DEBUG >= 1) {
            System.out.println("FootmenRush2: checkTownhall");
        }
        if (null == game.getUnitType(player, WTownhall.class)) {
            List<WUnit> peasants = game.getUnitTypes(player, WPeasant.class);
            WPeasant peasant = null;
            for (WUnit p : peasants) {
                if (p.getStatus() != null
                        && p.getStatus().m_action == S3Action.ACTION_BUILD
                        && p.getStatus().m_parameters.get(0).equals(WTownhall.class.getSimpleName())) {
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
            Pair<Integer, Integer> loc = game.findFreeSpace(peasant.getX(), peasant.getY(), 3);
            requests.add(new Request(200, peasant.entityID, 1200, 800, new S3Action(peasant.entityID, S3Action.ACTION_BUILD, WTownhall.class.getSimpleName(), loc.m_a, loc.m_b)));
        }
        return requests;
    }

    /*
     * (non-Javadoc)
     *
     * @see base.ai.AI#getPlayerId()
     */
    public String getPlayerId() {
        return m_playerID;
    }
}
