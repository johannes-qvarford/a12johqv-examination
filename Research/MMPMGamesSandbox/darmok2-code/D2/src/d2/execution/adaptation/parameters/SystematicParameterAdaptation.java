/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation.parameters;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import d2.execution.adaptation.PlanAdaptation;
import d2.plans.ActionPlan;
import d2.plans.Plan;
import gatech.mmpm.Action;
import gatech.mmpm.ActionParameter;
import gatech.mmpm.ActionParameterType;
import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.Map;
import gatech.mmpm.PhysicalEntity;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

public class SystematicParameterAdaptation extends ParameterAdaptation {

    public static boolean INFORM_ERRORS = false;
    public static int DEBUG_COORDINATES = 0;
    public static int DEBUG_ENTITIES = 0;
    public static int DEBUG_DIRECTION = 0;
    public static int DEBUG = 0;
    public int MAX_CHOICES_PER_PARAMETER = 2;

    /*
     * This class will only adapt ActionPlan objects. Any other class will be discarded.
     * It's up to the classes that call this one, to decide which actions have to be adapted, i.e.
     * it's up to the planner to implement "Delayed Adaptation" or "Immediate Adaptation"
     */
    public SystematicParameterAdaptation(int a_MAX_CHOICES_PER_PARAMETER) {
        MAX_CHOICES_PER_PARAMETER = a_MAX_CHOICES_PER_PARAMETER;
    }

//    public Plan adapt(Plan original_plan, GameState original_gs,
//            Sensor original_goal, int current_cycle, GameState current_gs,
//            Sensor current_goal,List<String> usedIDs) {
    public Plan adapt(Plan original_plan,GameState original_gs, String original_player, Sensor original_goal,
                      int current_cycle, GameState current_gs, String current_player, Sensor current_goal,
                      List<String> usedIDs) {


        if (original_plan instanceof ActionPlan) {
            if (DEBUG >= 1) {
                System.out.println("SystematicParameterAdaptation.adapt (" + current_player + "): " + original_plan);
            }

            ActionPlan adapted_plan = (ActionPlan) original_plan.clone();
            adapted_plan.setOriginalGameState(current_gs);
            Action a = adapted_plan.getAction();
            String originalPlayer = a.getPlayerID();
            String originalUnitID = a.getEntityID();
            List<String> currentUnitIDCandidates = null;
            HashMap<String, List<String>> parameterValuesCandidates = new HashMap<String, List<String>>();

            boolean adaptCoordinates = false;
            ActionParameter coordinateParameter = null;

            List<String> alreadyAdapted = new LinkedList<String>();

            // Adapt player and entity ID first:
            a.setPlayerID(current_player);
            {
                String oldID = a.getEntityID();
                if (oldID != null) {
                    List<Entity> l = adaptEntityID(oldID, a, original_gs, current_gs, originalPlayer, current_player, MAX_CHOICES_PER_PARAMETER, usedIDs);
                    currentUnitIDCandidates = new LinkedList<String>();
                    for (Entity e : l) {
                        currentUnitIDCandidates.add(e.getentityID());
                    }
                    // If the entityID cannot be adapted, fail adaptation:
                    if (currentUnitIDCandidates.size() == 0) {
                        if (INFORM_ERRORS) {
                            System.err.println("ParameterAdaptation:adapt, action " + original_plan + " cannot be adapted, since an appropriate unit ID cannot be found, original: " + oldID);
                            System.err.println("Current Game State:\n" + current_gs);
                            System.err.println("Original Game State:\n" + original_gs);
                        }
                        return null;
                    }
                    a.setEntityID(currentUnitIDCandidates.get(0));
                }
            }

            alreadyAdapted.add("playerID");
            alreadyAdapted.add("entityID");

            for (ActionParameter ap : a.listOfParameters()) {
                if (alreadyAdapted.contains(ap.m_name)) {
                    continue;
                }
                switch (ap.m_type) {
                    case ENTITY_ID: {
                        String oldID = ((Entity) a.parameterValue(ap.m_name)).getentityID();
                        if (oldID != null) {
                            List<Entity> newEntities = adaptEntityID(oldID, null, original_gs, current_gs, originalPlayer, current_player, MAX_CHOICES_PER_PARAMETER,null);
                            if (newEntities.size() == 0) {
                                System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since an appropriate uentity ID for parameter '" + ap.m_name + "'cannot be found.");
                                return null;
                            }

                            List<String> newEntitiesString = new LinkedList<String>();
                            for (Entity e : newEntities) {
                                newEntitiesString.add(ActionParameterType.ENTITY_ID.toString(e));
                            }
                            a.setParameterValue(ap.m_name, newEntitiesString.get(0));


                            parameterValuesCandidates.put(ap.m_name, newEntitiesString);
                        }
                    }
                    break;
                    case PLAYER: {
                        List<String> playerIDs = adaptPlayerID(a.parameterValue(ap.m_name).toString(),
                                originalPlayer, original_gs, current_player, current_gs, MAX_CHOICES_PER_PARAMETER);
                        if (playerIDs.size() == 0) {
                            System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since an appropriate player ID for parameter '" + ap.m_name + "'cannot be found.");
                            return null;
                        }
                        a.setParameterValue(ap.m_name, playerIDs.get(0));
                    }
                    break;
                    case COORDINATE:
                        coordinateParameter = ap;
                        adaptCoordinates = true;
                        break;
                    case DIRECTION: {
                        List<String> newDirections = adaptDirection(
                                Integer.parseInt(a.parameterValue(ap.m_name).toString()),
                                originalUnitID, original_gs, a.getEntityID(), current_gs,
                                MAX_CHOICES_PER_PARAMETER);
                        if (newDirections.size() == 0) {
                            System.err.println("ParameterAdaptation:adapt, action " + adapted_plan.getAction().getClass().getSimpleName() + " cannot be adapted, since an appropriate direction for parameter '" + ap.m_name + "'cannot be found.");
                            return null;
                        }
                        a.setParameterValue(ap.m_name, newDirections.get(0));
                    }
                    break;
                    case ENTITY_TYPE:
                    case INTEGER:
                    case STRING:
                        // Don't do anything
                        break;
                }
            }

            // debug:
			/*
            {
            for(String pn:parameterValuesCandidates.keySet()) {
            System.out.println(pn + " -> " + parameterValuesCandidates.get(pn).size() + " candidates.");
            for(String v:parameterValuesCandidates.get(pn)) {
            System.out.println("    " + v);
            }
            }
            }
             */

            // Iterate over all the possibilities:
            {
                int current = 0;

                do {
                    a = getPossibility(a, current, currentUnitIDCandidates, parameterValuesCandidates, a.listOfParameters());
                    if (a != null) {
                        // Coordinate adaptations:
                        if (adaptCoordinates) {
                            float original_coords[] = (float[]) a.parameterValue(coordinateParameter.m_name);
                            float new_coords[] = adaptCoordinates(original_coords, originalUnitID, original_gs, a.getEntityID(), current_cycle, current_gs, current_player, a, coordinateParameter);
                            if (new_coords != null) {
                                a.setParameterValue(coordinateParameter.m_name, coordinateParameter.m_type.toString(new_coords));
                                adapted_plan.setAction(a);
                                return adapted_plan;
                            }
                        } else {
                            Action ca = (Action) a.clone();
                            //	ca.initializeActionConditions();
                            if (DEBUG>=1) System.out.print("checking valid conditions: " + ca.getValidCondition());
                            if (ca.checkValidCondition(current_cycle, current_gs, ca.getPlayerID())) {
                                if (DEBUG>=1) System.out.print("Valid conditions satisfied...");
                                adapted_plan.setAction(a);
                                return adapted_plan;
                            }
                        }
                    }
                    current++;
                } while (a != null);
            }

            System.err.println("Plan " + original_plan + " cannot be adapted!!!");
            return null;
        }

        return original_plan;
    }

    @SuppressWarnings("unchecked")
    static class ParameterValueComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
            double o0 = ((Pair<Object, Double>) arg0)._b;
            double o1 = ((Pair<Object, Double>) arg1)._b;

            if (o0 > o1) {
                return -1;
            } else if (o0 < o1) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private List<Entity> adaptEntityID(String originalID, Action a, GameState original_gs, GameState current_gs, String original_player, String player, int MAX, List<String> usedIDs) {
        List<Pair<Entity, Double>> candidates = new LinkedList<Pair<Entity, Double>>();
        List<Entity> ret = new LinkedList<Entity>();
        Entity originalEntity = null;

        if (DEBUG_ENTITIES >= 1) {
            System.out.println("ParameterAdaptation.adaptEntityID: start adapting " + a);
        }

        if (original_gs != null) {
            originalEntity = original_gs.getEntity(originalID);
        }

        if (originalEntity == null) {
            System.err.println("adaptEntityID: original entity cannot be found! -> " + originalID);
        }

        // Filter all the eligible candidates:
        for (Entity e : current_gs.getAllEntities()) {
            if (usedIDs==null || !usedIDs.contains(e.getentityID())) {
                if (a != null) {
                    // If the entity has to execute a particular action, check that it actually can:
                    if (e.getowner() != null
                            && e.getowner().equals(a.getPlayerID())) {
                        if (e.canExecute(a)) {
                            candidates.add(new Pair<Entity, Double>(e, 0.0));
                        } else {
                            //						System.out.println(e.getClass().getSimpleName() + " cannot execute " + a);
                        }
                    }
                } else {
                    if (originalEntity.getowner() == null) {
                        if (e.getowner() == null) {
                            candidates.add(new Pair<Entity, Double>(e, 0.0));
                        }
                    } else if (originalEntity.getowner().equals(original_player)) {
                        if (e.getowner() != null && e.getowner().equals(player)) {
                            candidates.add(new Pair<Entity, Double>(e, 0.0));
                        }
                    } else {
                        if (e.getowner() != null && !e.getowner().equals(player)) {
                            candidates.add(new Pair<Entity, Double>(e, 0.0));
                        }
                    }
                }
            }
        }

        if (DEBUG_ENTITIES >= 1) {
            System.out.println("ParameterAdaptation.adaptEntityID: " + candidates.size() + " candidates.");
        }

        // Rank the units by similarity, and pick the best MAX units:
        {
            for (Pair<Entity, Double> candidate : candidates) {
                if (originalEntity != null) {
                    candidate._b = entitySimilarity(candidate._a, originalEntity, current_gs, original_gs, player, original_player);
                }

                if (DEBUG_ENTITIES >= 1) {
                    System.out.println("ParameterAdaptation.adaptEntityID: " + candidate._a + " : " + candidate._b);
                }
            }

            Collections.sort(candidates, new ParameterValueComparator());

            if (MAX > 0) {
                while (candidates.size() > MAX) {
                    candidates.remove(MAX);
                }
            }
            for (Pair<Entity, Double> c : candidates) {
                if (DEBUG_ENTITIES >= 1) {
                    System.out.println("ParameterAdaptation.adaptEntityID: selected candidate: " + c._a.getentityID() + " with " + c._b);
                }
                ret.add(c._a);
            }

            // debug:
			/*
            if (ret.get(0) instanceof PhysicalEntity &&
            originalEntity instanceof PhysicalEntity) {
            System.out.println("Top candidate: " + ret.get(0));
            Entity e = ret.get(0);
            System.out.println("Original entity PF");
            GameStatePotentialField.printPotentialFields((PhysicalEntity)originalEntity,original_gs,"GameStatePotentialField-" + original_player);
            System.out.println("Selected entity PF");
            GameStatePotentialField.printPotentialFields((PhysicalEntity)e,current_gs,"GameStatePotentialField-" + player);
            }*/

            if (DEBUG_ENTITIES >= 1) {
                System.out.println("ParameterAdaptation.adaptEntityID: end");
            }

            return ret;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> adaptPlayerID(String player, String originalPlayer,
            GameState original_gs, String currentPlayer, GameState current_gs, int MAX) {

        List<String> ret = new LinkedList<String>();
        List<Pair<Entity, Double>> candidates = new LinkedList<Pair<Entity, Double>>();
        Entity originalEntity = null;

        if (player.equals(originalPlayer)) {
            ret.add(currentPlayer);
            return ret;
        }

        if (original_gs != null) {
            originalEntity = original_gs.getEntity(player);
        }

        // Filter all the eligible candidates:
        for (String p : current_gs.getAllPlayers()) {
            if (!p.equals(currentPlayer)) {
                candidates.add(new Pair<Entity, Double>(current_gs.getEntity(p), 0.0));
            }
        }

        // Rank the players by similarity, and pick the best:
        {
            double similarity = 0;

            for (Pair<Entity, Double> candidate : candidates) {
                if (originalEntity != null) {
                    similarity = entitySimilarity(candidate._a, originalEntity, current_gs, original_gs, player, originalPlayer);
                } else {
                    similarity = 0;
                }
                candidate._b = similarity;
            }

            //			System.out.println("ParameterAdaptation.adaptPlayerID: end");
        }

        Collections.sort(candidates, new ParameterValueComparator());

        if (MAX > 0) {
            while (candidates.size() > MAX) {
                candidates.remove(MAX);
            }
        }
        for (Pair<Entity, Double> c : candidates) {
            if (DEBUG_ENTITIES >= 1) {
                System.out.println("ParameterAdaptation.adaptPlayerID: selected candidate: " + c._a.getentityID() + " with " + c._b);
            }
            ret.add(c._a.getentityID());
        }

        if (DEBUG_ENTITIES >= 1) {
            System.out.println("ParameterAdaptation.adaptPlayerID: end");
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    private List<String> adaptDirection(int originalDirection, String originalUnitID, GameState original_gs,
            String currentUnitID, GameState current_gs, int MAX) {
        List<String> ret = new LinkedList<String>();
        List<Pair<String, Double>> candidates = new LinkedList<Pair<String, Double>>();

        PhysicalEntity originalUnit = (PhysicalEntity) original_gs.getEntity(originalUnitID);
        PhysicalEntity currentUnit = (PhysicalEntity) current_gs.getEntity(currentUnitID);
        String originalPlayer = originalUnit.getowner();
        String currentPlayer = currentUnit.getowner();
        GameStatePotentialField original_pf = (GameStatePotentialField) original_gs.getMetaData("GameStatePotentialField-" + originalPlayer);
        GameStatePotentialField current_pf = (GameStatePotentialField) current_gs.getMetaData("GameStatePotentialField-" + currentPlayer);

        if (original_pf == null) {
//			original_pf = new LazyGameStatePotentialField(original_gs,originalPlayer);
            original_pf = new EagerGameStatePotentialField(original_gs, originalPlayer);
            original_gs.addMetaData("GameStatePotentialField-" + originalPlayer, original_pf);
        }
        if (current_pf == null) {
//			current_pf = new LazyGameStatePotentialField(current_gs,currentPlayer);
            current_pf = new EagerGameStatePotentialField(current_gs, currentPlayer);
            current_gs.addMetaData("GameStatePotentialField-" + currentPlayer, current_pf);
        }

        double similarity;
        int window_size = 3;
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
        //pfw_original.printFields();

        for (int i = 0; i < directions_to_try.length; i++) {
            PotentialFieldWindow pfw = new PotentialFieldWindow(current_pf, current_gs.getMap(), current_gs.getMap().toCell(currentUnit.get_Coords()), window_size, directions_to_try[i]);

            similarity = 1.0 - pfw_original.distance(pfw, null, null);
            if (DEBUG_DIRECTION >= 2) {
                System.out.println("adaptDirection: direction " + directions_to_try[i] + " : " + similarity);
            }

            candidates.add(new Pair<String, Double>("" + directions_to_try[i], similarity));
        }
        Collections.sort(candidates, new ParameterValueComparator());

        if (MAX > 0) {
            while (candidates.size() > MAX) {
                candidates.remove(MAX);
            }
        }
        for (Pair<String, Double> c : candidates) {
            if (DEBUG_DIRECTION >= 1) {
                System.out.println("ParameterAdaptation.adaptDirection: selected candidate: " + c._a + " with " + c._b);
            }
            ret.add(c._a);
        }

        if (DEBUG_DIRECTION >= 1) {
            System.out.println("ParameterAdaptation.adaptDirection: end");
        }

        return ret;
    }

    Action getPossibility(Action plan, int count, List<String> currentUnitIDCandidates, HashMap<String, List<String>> parameterValuesCandidates, List<ActionParameter> parameters) {

        int current, remainder = count;

        current = remainder % currentUnitIDCandidates.size();
        remainder = remainder / currentUnitIDCandidates.size();

        plan.setEntityID(currentUnitIDCandidates.get(current));
        for (ActionParameter a : parameters) {
            List<String> candidates = parameterValuesCandidates.get(a.m_name);

            if (candidates != null) {
                current = remainder % candidates.size();
                remainder = remainder / candidates.size();

                plan.setParameterValue(a.m_name, candidates.get(current));
            }
        }

        if (remainder == 0) {
            if (DEBUG >= 1) {
                System.out.println("SystematicParameterAdaptation.getPossibility " + count + " -> " + plan);
            }
            return plan;
        }
        return null;
    }

    public void saveToXML(XMLWriter w) {
        w.tag("type", this.getClass().getName());
        w.tag("max-choices-per-parameter", MAX_CHOICES_PER_PARAMETER);
    }

    public static PlanAdaptation loadFromXMLInternal(Element xml) {

        return new SystematicParameterAdaptation(Integer.parseInt(xml.getChildText("max-choices-per-parameter")));
    }
}

