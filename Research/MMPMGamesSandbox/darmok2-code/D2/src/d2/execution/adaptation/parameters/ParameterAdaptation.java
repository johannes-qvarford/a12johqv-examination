/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation.parameters;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.adaptation.PlanAdaptation;
import d2.execution.planbase.PlanBase;
import d2.plans.ActionPlan;
import d2.plans.Plan;

import gatech.mmpm.Action;
import gatech.mmpm.ActionParameter;
import gatech.mmpm.ActionParameterType;
import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.Map;
import gatech.mmpm.PhysicalEntity;
import gatech.mmpm.TwoDMap;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

public class ParameterAdaptation extends PlanAdaptation {

    public static final boolean INFORM_ERRORS = true;
    public static int DEBUG = 0;
    public static int DEBUG_COORDINATES = 0;
    public static int DEBUG_ENTITIES = 0;
    public static int DEBUG_DIRECTION = 0;

    /*
     * This class will only adapt ActionPlan objects. Any other class will be discarded.
     * It's up to the classes that call this one, to decide which actions have to be adapted, i.e.
     * it's up to the planner to implement "Delayed Adaptation" or "Immediate Adaptation"
     */
    public ParameterAdaptation() {
    }

    public void adaptToAllow(Plan original_plan, GameState original_gs, String original_player,
                             Plan planToAllow, int current_cycle, GameState current_gs, String current_player,
                             PlanBase pb, PlanAdaptation parameterAdapter,List<String> usedIDs) {
    }

    public Plan adapt(Plan original_plan,GameState original_gs, String original_player, Sensor original_goal,
                      int current_cycle, GameState current_gs, String current_player, Sensor current_goal,
                      List<String> usedIDs) {

        if (DEBUG >= 2) {
            System.out.println("ParameterAdaptation.adapt (" + current_player + "): " + original_plan);
        }

        if (original_plan instanceof ActionPlan) {
            ActionPlan adapted_plan = (ActionPlan) original_plan.clone();
            adapted_plan.setOriginalGameState(current_gs);
            Action a = adapted_plan.getAction();
            String originalPlayer = a.getPlayerID();
            String originalUnitID = a.getEntityID();

            boolean adaptCoordinates = false;
            ActionParameter coordinateParameter = null;

            List<String> alreadyAdapted = new LinkedList<String>();

            // Adapt player and entity ID first:
            a.setPlayerID(current_player);
            {
                String oldID = a.getEntityID();
                if (oldID != null) {
                    Entity e = adaptEntityID(oldID, a, original_gs, current_gs, originalPlayer, current_player, usedIDs);
                    if (e == null) {
                        System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since an appropriate uentity ID for parameter 'entityID' cannot be found.");
                        return null;
                    }
                    String newID = e.getentityID();
                    a.setEntityID(newID);
                }
            }

            alreadyAdapted.add("playerID");
            alreadyAdapted.add("entityID");

            for (ActionParameter ap : a.listOfParameters()) {
                if (alreadyAdapted.contains(ap.m_name)) continue;
                if (a.parameterValue(ap.m_name)==null) continue;
                switch (ap.m_type) {
                    case ENTITY_ID: {
                        String oldID = ((Entity) a.parameterValue(ap.m_name)).getentityID();
                        if (oldID != null) {
                            Entity newID = adaptEntityID(oldID, null, original_gs, current_gs,
                                    originalPlayer, current_player, null);
                            if (newID == null) {
                                System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since an appropriate uentity ID for parameter '" + ap.m_name + "'cannot be found.");
                                return null;
                            }
                            a.setParameterValue(ap.m_name, ActionParameterType.ENTITY_ID.toString(newID));
                        }
                    }
                    break;
                    case PLAYER: {
                        String playerID = adaptPlayerID(a.parameterValue(ap.m_name).toString(),
                                originalPlayer, original_gs, current_player, current_gs);
                        if (playerID == null) {
                            System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since an appropriate player ID for parameter '" + ap.m_name + "'cannot be found.");
                            return null;
                        }
                        a.setParameterValue(ap.m_name, playerID);
                    }
                    break;
                    case COORDINATE:
                        coordinateParameter = ap;
                        adaptCoordinates = true;
                        break;
                    case DIRECTION: {
                        String newDirection = adaptDirection(Integer.parseInt(
                                a.parameterValue(ap.m_name).toString()), originalUnitID,
                                original_gs, a.getEntityID(), current_gs, current_player);
                        if (newDirection == null) {
                            System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since an appropriate direction for parameter '" + ap.m_name + "'cannot be found.");
                            return null;
                        }
                        a.setParameterValue(ap.m_name, newDirection);
                    }
                    break;
                    case ENTITY_TYPE:
                    case INTEGER:
                    case STRING:
                        // Don't do anything
                        break;
                }
            }

            // Coordinate adaptations:
            if (adaptCoordinates) {
                float original_coords[] = (float[]) a.parameterValue(coordinateParameter.m_name);
                float new_coords[] = adaptCoordinates(original_coords, originalUnitID, original_gs, a.getEntityID(), current_cycle, current_gs, current_player, a, coordinateParameter);
                if (new_coords == null) {
                    System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since appropriate coordinates for parameter '" + coordinateParameter.m_name + "' cannot be found.");
                    return null;
                }

                a.setParameterValue(coordinateParameter.m_name, coordinateParameter.m_type.toString(new_coords));
            }

            //	adapted_plan.getAction().initializeActionConditions(current_cycle,current_gs);

            if (DEBUG >= 1) {
                System.out.println("ParameterAdaptation: original Plan " + original_plan);
                System.out.println("ParameterAdaptation: adapted Plan " + adapted_plan);
            }

            if (reasoningTraceWriter!=null) {
                reasoningTraceWriter.tagWithAttributes("adaptation-episode","cycle=\"" + current_cycle +"\"");
                reasoningTraceWriter.tag("original-action");
                Action oa = ((ActionPlan)original_plan).getAction();
                oa.writeToXML(reasoningTraceWriter);
                original_gs.writeToXML(reasoningTraceWriter);
                reasoningTraceWriter.tag("/original-action");
                reasoningTraceWriter.tag("adapted-action");
                Action aa = ((ActionPlan)adapted_plan).getAction();
                aa.writeToXML(reasoningTraceWriter);
                current_gs.writeToXML(reasoningTraceWriter);
                reasoningTraceWriter.tag("/adapted-action");
                reasoningTraceWriter.tag("/adaptation-episode");
                reasoningTraceWriter.flush();
            }

            return adapted_plan;
        }

        return original_plan;
    }

    private String adaptDirection(int originalDirection, String originalUnitID, GameState original_gs,
            String currentUnitID, GameState current_gs, String current_player) {

        PhysicalEntity originalUnit = (PhysicalEntity) original_gs.getEntity(originalUnitID);
        PhysicalEntity currentUnit = (PhysicalEntity) current_gs.getEntity(currentUnitID);
        String originalPlayer = originalUnit.getowner();
//        String currentPlayer = currentUnit.getowner();
        GameStatePotentialField original_pf = (GameStatePotentialField) original_gs.getMetaData("GameStatePotentialField-" + originalPlayer);
        GameStatePotentialField current_pf = (GameStatePotentialField) current_gs.getMetaData("GameStatePotentialField-" + current_player);

        if (original_pf == null) {
//			System.out.println("(***)");
//			original_pf = new LazyGameStatePotentialField(original_gs,originalPlayer);
            original_pf = new EagerGameStatePotentialField(original_gs, originalPlayer);
            original_gs.addMetaData("GameStatePotentialField-" + originalPlayer, original_pf);
        }
        if (current_pf == null) {
//			System.out.println("[***]");
//			current_pf = new LazyGameStatePotentialField(current_gs,currentPlayer);
            current_pf = new EagerGameStatePotentialField(current_gs, current_player);
            current_gs.addMetaData("GameStatePotentialField-" + current_player, current_pf);
        }

        int best = -1;
        double bestDistance = 0, distance;
        int window_size = 5;
        int directions_to_try[] = null;

        if (original_gs.getMap().getNumberOfDimensions() == 1) {
            directions_to_try = new int[2];
            directions_to_try[0] = Map.DIRECTION_X_NEG;
            directions_to_try[1] = Map.DIRECTION_X_POS;
        }
        if (original_gs.getMap().getNumberOfDimensions() == 2) {
            directions_to_try = new int[4];
            directions_to_try[0] = Map.DIRECTION_X_NEG;
            directions_to_try[1] = Map.DIRECTION_X_POS;
            directions_to_try[2] = Map.DIRECTION_Y_NEG;
            directions_to_try[3] = Map.DIRECTION_Y_POS;
        }
        if (original_gs.getMap().getNumberOfDimensions() == 3) {
            directions_to_try = new int[6];
            directions_to_try[0] = Map.DIRECTION_X_NEG;
            directions_to_try[1] = Map.DIRECTION_X_POS;
            directions_to_try[2] = Map.DIRECTION_Y_NEG;
            directions_to_try[3] = Map.DIRECTION_Y_POS;
            directions_to_try[4] = Map.DIRECTION_Z_NEG;
            directions_to_try[5] = Map.DIRECTION_Z_POS;
        }

        if (directions_to_try == null) {
            return null;
        }

        PotentialFieldWindow pfw_original = new PotentialFieldWindow(original_pf, original_gs.getMap(), original_gs.getMap().toCell(originalUnit.get_Coords()), window_size, originalDirection);
        if (DEBUG_DIRECTION >= 2) {
            System.out.println("Original Direction potential field window (" + originalDirection + "):");
            pfw_original.printFields();
        }

        for (int i = 0; i < directions_to_try.length; i++) {
            PotentialFieldWindow pfw = new PotentialFieldWindow(current_pf, current_gs.getMap(), current_gs.getMap().toCell(currentUnit.get_Coords()), window_size, directions_to_try[i]);
            if (DEBUG_DIRECTION >= 2) {
                System.out.println("Potential field window in direction " + directions_to_try[i] + ":");
                pfw.printFields();
            }

            distance = pfw_original.distance(pfw, null, null);
            if (DEBUG_DIRECTION >= 1) {
                System.out.println("adaptDirection: direction " + directions_to_try[i] + " : " + distance);
            }
            if (best == -1 || distance < bestDistance) {
                best = directions_to_try[i];
                bestDistance = distance;
            }
        }

        return "" + best;
    }

    private Entity adaptEntityID(String originalID, Action a, GameState original_gs, GameState current_gs, String original_player, String player, List<String> usedIDs) {
        List<Entity> candidates = new LinkedList<Entity>();
        Entity originalEntity = null;

        if (DEBUG_ENTITIES >= 1) {
            System.out.println("ParameterAdaptation.adaptEntityID: start adapting " + a);
            System.out.println("ParameterAdaptation.adaptEntityID: Id to adapt " + originalID);
            System.out.println("ParameterAdaptation.adaptEntityID: usedIDs: " + usedIDs);
        }

        if (original_gs != null) {
            originalEntity = original_gs.getEntity(originalID);
        }

        if (originalEntity == null) {
            System.err.println(original_gs.toString());
            System.err.println(a);
            System.err.println("adaptEntityID: original entity cannot be found! -> " + originalID);
            return null;
        } else {
            if (DEBUG_ENTITIES >= 1) {
                System.out.println("ParameterAdaptation.adaptEntityID: original entity " + originalEntity);
            }
        }

        // Filter all the eligible candidates:
        for (Entity e : current_gs.getAllEntities()) {
            if (usedIDs==null || !usedIDs.contains(e.getentityID())) {
                if (a != null) {
                    // If the entity has to execute a particular action, check that it actually can:
                    if (e.getowner() != null &&
                        e.getowner().equals(player)) {
                        if (e.canExecute(a)) {
                            candidates.add(e);
                        } else {
                            if (DEBUG_ENTITIES >= 2) {
        			System.out.println(e.getClass().getSimpleName() + " cannot execute " + a);
                            }
                        }
                    }
                } else {
                    if (originalEntity.getowner() == null) {
//                        System.out.println("Considering: " + e);
                        if (e.getowner() == null) {
                            candidates.add(e);
                        }
                    } else if (originalEntity.getowner().equals(original_player)) {
                        if (e.getowner() != null && e.getowner().equals(player)) {
                            candidates.add(e);
                        }
                    } else {
                        if (e.getowner() != null && !e.getowner().equals(player)) {
                            candidates.add(e);
                        }
                    }
                }
            }
        }

        if (DEBUG_ENTITIES >= 1) {
            System.out.println("ParameterAdaptation.adaptEntityID: " + candidates.size() + " candidates.");
        }

        // Rank the units by similarity, and pick the best:
        {
            Entity best = null;
            double best_similarity = 0, similarity = 0;

            for (Entity candidate : candidates) {
                if (originalEntity != null) {
                    similarity = entitySimilarity(candidate, originalEntity, current_gs, original_gs, player, original_player);
                } else {
                    similarity = 0;
                }

                if (DEBUG_ENTITIES >= 1) {
                    System.out.println("ParameterAdaptation.adaptEntityID: " + candidate.getClass().getSimpleName() + " " + candidate.getentityID() + " : " + similarity);
                }

                if (best == null || similarity > best_similarity) {
                    best = candidate;
                    best_similarity = similarity;
                }
            }

            if (DEBUG_ENTITIES >= 2) {
                System.out.println("original entity:" + (originalEntity!=null ? originalEntity.toString():"---"));
                System.out.println("new entity:" + (best!=null ? best.toString():"---"));
            }
            if (DEBUG_ENTITIES >= 1) {
                System.out.println("ParameterAdaptation.adaptEntityID: end");
            }

            if (best == null) {
                return null;
            }
            return best;
        }
    }

    private String adaptPlayerID(String player, String originalPlayer,
            GameState original_gs, String currentPlayer, GameState current_gs) {

        List<Entity> candidates = new LinkedList<Entity>();
        Entity originalEntity = null;

        if (player.equals(originalPlayer)) {
            return currentPlayer;
        }

        if (original_gs != null) {
            originalEntity = original_gs.getEntity(player);
        }

        // Filter all the eligible candidates:
        for (String p : current_gs.getAllPlayers()) {
            if (!p.equals(currentPlayer)) {
                candidates.add(current_gs.getEntity(p));
            }
        }

        // Rank the players by similarity, and pick the best:
        {
            Entity best = null;
            double best_similarity = 0, similarity = 0;

            for (Entity candidate : candidates) {
                if (originalEntity != null) {
                    similarity = entitySimilarity(candidate, originalEntity, current_gs, original_gs, player, originalPlayer);
                } else {
                    similarity = 0;
                }

                if (best == null || similarity > best_similarity) {
                    best = candidate;
                    best_similarity = similarity;
                }
            }

//			System.out.println("ParameterAdaptation.adaptPlayerID: end");

            if (best == null) {
                return null;
            }
            return best.getentityID();
        }
    }

    protected float[] adaptCoordinates(float original_coords[], String originalUnitID, GameState original_gs, String currentUnitID, int current_cycle, GameState current_gs, String current_player, Action a, ActionParameter actionParameter) {
        Entity originalUnit = original_gs.getEntity(originalUnitID);
        Entity currentUnit = current_gs.getEntity(currentUnitID);
        String originalPlayer = originalUnit.getowner();
//        String currentPlayer = currentUnit.getowner();
        GameStatePotentialField original_pf = (GameStatePotentialField) original_gs.getMetaData("GameStatePotentialField-" + originalPlayer);
        GameStatePotentialField current_pf = (GameStatePotentialField) current_gs.getMetaData("GameStatePotentialField-" + current_player);
        int mapSize = current_gs.getMap().size();
        int bestPos = -1;
        double bestDistance = 0, distance;
        int pos1 = original_gs.getMap().toCell(original_coords);

        if (original_pf == null) {
            original_pf = new EagerGameStatePotentialField(original_gs, originalPlayer);
            original_gs.addMetaData("GameStatePotentialField-" + originalPlayer, original_pf);
        }
        if (current_pf == null) {
            current_pf = new EagerGameStatePotentialField(current_gs, current_player);
            current_gs.addMetaData("GameStatePotentialField-" + current_player, current_pf);
        }

        if (DEBUG_COORDINATES >= 1) {
            System.out.println("\n--------------------------------------------------------------------");
        }
        if (DEBUG_COORDINATES >= 1) {
            System.out.print("Original: " + pos1 + "/" + original_pf.size + " -> ");
            for (int i = 0; i < original_coords.length; i++) {
                System.out.print(original_coords[i] + " ");
            }
            System.out.println("");
        }
        if (DEBUG_COORDINATES >= 1) {
            original_pf.printFields(pos1);
        }

        if (DEBUG_COORDINATES >= 1) {
            System.out.println("Current: " + current_pf.size);
        }
        if (DEBUG_COORDINATES >= 3) {
            current_pf.printFields();
        }

        if (DEBUG_COORDINATES >= 2) {
            // Print valid coordinates:
            int width = -1;
            int pos = 0;
            if (current_gs.getMap() instanceof TwoDMap) {
                width = ((TwoDMap)current_gs.getMap()).getSizeInDimension(0);
            }
            for (int pos2 = 0; pos2 < mapSize; pos2++) {
                float new_coords[] = current_gs.getMap().toCoords(pos2);
                Action ca = (Action) a.clone();
                ca.setParameterValue(actionParameter.m_name, actionParameter.m_type.toString(new_coords));
                if (ca.checkValidCondition(current_cycle, current_gs, ca.getPlayerID())) {
                    System.out.print(".");
                } else {
                    System.out.print("X");
                }
                pos++;
                if (width>0 && pos>=width) {
                    pos-=width;
                    System.out.println("");
                }
            }
            System.out.println("");
        }

        for (int pos2 = 0; pos2 < mapSize; pos2++) {
            distance = original_pf.distance(pos1, current_pf, pos2, null, m_d2.getWorldModel().getEntityWeights());
            if (DEBUG_COORDINATES >= 2) {
                float tmp_coords[] = current_gs.getMap().toCoords(pos2);
                for (int i = 0; i < tmp_coords.length; i++) {
                    System.out.print(tmp_coords[i] + ",");
                }
                System.out.println(": " + distance);
            }

            if (bestPos == -1 || distance < bestDistance) {

//				if (DEBUG>=2) System.out.println(pos2 + ": " + distance);
//				if (DEBUG>=2) current_pf.printFields(pos2);

                // Test if it's valid:
                {
                    float new_coords[] = current_gs.getMap().toCoords(pos2);
                    Action ca = (Action) a.clone();
                    /*	if (coordinateParameters[0].m_type==ActionParameterType.CELL_COORDINATE_X ||
                    coordinateParameters[0].m_type==ActionParameterType.CELL_COORDINATE_Y ||
                    coordinateParameters[0].m_type==ActionParameterType.CELL_COORDINATE_Z) {
                    new_coords = original_gs.getMap().toCellCoords(new_coords);
                    }*/
                    ca.setParameterValue(actionParameter.m_name, actionParameter.m_type.toString(new_coords));
                    //	ca.initializeActionConditions();
//					System.out.print("checking: (" + pos2 + ") "); for(int c:new_coords) System.out.print(c/16 + " "); System.out.println("");
                    if (ca.checkValidCondition(current_cycle, current_gs, ca.getPlayerID())) {
                        bestPos = pos2;
                        bestDistance = distance;
                        if (DEBUG_COORDINATES >= 3) {
                            System.out.println(" valid!");
                        }
                    } else {
                        if (DEBUG_COORDINATES >= 3) {
                            System.out.println("The coordinates were not valid! (" + ca.getValidCondition() + ")");
                        }
                    }
                }
            }
        }

        if (bestPos == -1) {
            return null;
        }
        return current_gs.getMap().toCoords(bestPos);
    }

    public void saveToXML(XMLWriter w) {
        w.tag("type", this.getClass().getName());
    }

    public static PlanAdaptation loadFromXMLInternal(Element xml) {
        return new ParameterAdaptation();
    }

    protected static double entityClassSimilarity(Entity e1, Entity e2) {
        // The similarity computation is:
        // a: distance from root (Entity) to antiunification
        // b: distance from antiunificaiton to c1
        // c: distance from antiunification to c2
        // the similarity is: sym = a*2/(a*2+b+c)

        double a = 0, b = 0, c = 0;
        Class<?> antiunification = null, c1, c2;

        c1 = e1.getClass();
        c2 = e2.getClass();

        antiunification = c1;

//		System.out.println("entityClassSimilarity, c1: " + c1.getName());
//		System.out.println("entityClassSimilarity, c2: " + c2.getName());

//		System.out.println("entityClassSimilarity, au: " + antiunification.getName());
        while (!antiunification.isInstance(e2)) {
            antiunification = antiunification.getSuperclass();
//			System.out.println("entityClassSimilarity, au: " + antiunification.getName());
            b++;
        }
        while (c2 != antiunification) {
            c2 = c2.getSuperclass();
            c++;
        }

        while (antiunification != Entity.class) {
            antiunification = antiunification.getSuperclass();
            a++;
        }

//		System.out.println("entityClassSimilarity, c1: " + c1.getName()+ " c2: " + c2.getName() + ", a: " + a + " b: " + b + " c: " + c + " similarity: " + ((2*a)/(2*a + b + c)));

        return (2 * a) / (2 * a + b + c);
    }

    // player1 represents the point of view in the GameState from where e1 was drawn
    // player2 represents the point of view in the GameState from where e2 was drawn
    protected static double entitySimilarity(Entity e1, Entity e2, GameState gs1, GameState gs2, String player1, String player2) {
        double baseSimilarity = entityClassSimilarity(e1, e2);
        List<String> fl1 = e1.listOfFeatures();
        List<String> fl2 = e2.listOfFeatures();
        double featureSimilarity = 0;
        int nCommonFeatures = 0;
        double sameOwner = 0;	// This feature counts as much as the rest

        for (String f : fl1) {
            if (f.equals("entityID")) {
                // ignore
            } else if (f.equals("owner")) {
                if (e1.getowner() != null && e2.getowner() != null) {
                    boolean p1 = e1.getowner().equals(player1);
                    boolean p2 = e2.getowner().equals(player2);
                    if (p1 == p2) {
                        sameOwner = 1;
                    }
                } else {
                    if (e1.getowner() == null && e2.getowner() == null) {
                        sameOwner = 1;
                    }
                }
            } else if (f.equals("x") || f.equals("y") || f.equals("z")) {
                // ignore (since we will use the coordinates later)
            } else if (fl2.contains(f)) {
                Object fv1 = e1.featureValue(f);
                Object fv2 = e2.featureValue(f);

                if (fv1 == null || fv2 == null) {
                    // we don't know, so we don't count this feature
                } else {
                    if (fv1.equals(fv2)) {
                        featureSimilarity++;
//						System.out.println("entitySimilarity: f: " + f + " fv1: " + fv1 + " fv2: " + fv2 + " OK");
                    } else {
//						System.out.println("entitySimilarity: f: " + f + " fv1: " + fv1 + " fv2: " + fv2 + " X");						
                    }
                    nCommonFeatures++;
                }
            }
        }

        if (e1 instanceof PhysicalEntity
                && e2 instanceof PhysicalEntity) {
            // Potential field comparison:
            float[] coors1 = ((PhysicalEntity) e1).get_Coords();
            float[] coors2 = ((PhysicalEntity) e2).get_Coords();
            GameStatePotentialField pf1 = (GameStatePotentialField) gs1.getMetaData("GameStatePotentialField-" + player1);
            GameStatePotentialField pf2 = (GameStatePotentialField) gs2.getMetaData("GameStatePotentialField-" + player2);
            int pos1 = gs1.getMap().toCell(coors1);
            int pos2 = gs2.getMap().toCell(coors2);

            if (pf1 == null) {
                pf1 = new EagerGameStatePotentialField(gs1, player1);
                gs1.addMetaData("GameStatePotentialField-" + player1, pf1);
            }
            if (pf2 == null) {
                pf2 = new EagerGameStatePotentialField(gs2, player2);
                gs2.addMetaData("GameStatePotentialField-" + player2, pf2);
            }

            double d = Math.sqrt(pf1.distance(pos1, pf2, pos2, null, null)) / GameStatePotentialField.maxValue;
            if (Double.isNaN(d)) {
                System.err.println("We've got a NaN!!!");
            }
            featureSimilarity += 1.0 - d;
            nCommonFeatures++;
        }

//		System.out.println("entitySimilarity: " + featureSimilarity + " / " + nCommonFeatures);						

        if (nCommonFeatures == 0) {
            featureSimilarity = sameOwner;
        } else {
            featureSimilarity = ((featureSimilarity / nCommonFeatures) + sameOwner) / 2;
        }

//		System.out.println("entitySimilarity: " + baseSimilarity*featureSimilarity);

        return (baseSimilarity + featureSimilarity)/2;
    }
}
