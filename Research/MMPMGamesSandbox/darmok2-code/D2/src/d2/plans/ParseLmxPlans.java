/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.plans;

import gatech.mmpm.GameState;
import gatech.mmpm.sensor.Sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import gatech.mmpm.util.Pair;

@SuppressWarnings("unchecked")
public class ParseLmxPlans {

    Document doc;
    Element root;
    ArrayList<PetriNetElement> dummyList;
    public HashMap<String, Plan> planList = new HashMap<String, Plan>();

    public ParseLmxPlans() {
    }

    public List<Plan> parse(GameState referenceGameState) {

        List<Plan> l = new LinkedList<Plan>();

        if (doc == null) {
            return new LinkedList<Plan>();
        }

        root = doc.getRootElement();
        List<Element> plans = root.getChildren("plan");

        for (Element plan : plans) {
            String planType = plan.getAttributeValue("type");
            if (planType.equals("PetriNetPlan")) {
                l.add(getPetrinetPlan(plan, referenceGameState));
            }

            if (planType.equals("SubGoalPlan")) {
                l.add(GoalPlan.loadFromXMLInternal(plan, referenceGameState));
            }

            if (planType.equals("ActionPlan")) {
                l.add(ActionPlan.loadFromXMLInternal(plan, referenceGameState));
            }
        }

        return l;
    }

    public Plan parse(int traceCount, String planID, GameState referenceGameState) {
        if (doc == null) {
            return null;
        }

        root = doc.getRootElement();

        List<Element> plans = root.getChildren("plan");

        for (Element plan : plans) {
            // Search for the plan with the ID planID
            if (plan.getAttributeValue("id").equals(planID)) {
                String planType = plan.getAttributeValue("type");
                // System.out.println("Processing plan " + planID + " of type "
                // + planType);
                if (planType.equals("PetriNetPlan")) {
                    return getPetrinetPlan(plan, referenceGameState);
                }

                if (planType.equals("SubGoalPlan")) {
                    return GoalPlan.loadFromXMLInternal(plan, referenceGameState);
                }

                if (planType.equals("ActionPlan")) {
                    return ActionPlan.loadFromXMLInternal(plan, referenceGameState);
                }
            }
        }
        // System.out.println("Plan with ID " + planID + " not found!!");
        return null;
    }

    public static Plan parse(Element xml, ParseLmxPlans parser, GameState referenceGameState) {
        if (parser == null) {
            parser = new ParseLmxPlans();
        }
        String planType = xml.getAttributeValue("type");
        Plan ret = null;
        if (planType.equals("PetriNetPlan")) {
            ret = parser.getPetrinetPlan(xml, referenceGameState);
        }

        if (planType.equals("GoalPlan")) {
            ret = GoalPlan.loadFromXMLInternal(xml, referenceGameState);
        }

        if (planType.equals("ActionPlan")) {
            ret = ActionPlan.loadFromXMLInternal(xml, referenceGameState);
        }

        if (ret == null) {
            System.err.println("parse: ret is null!!, planType = " + planType);
        }

        parser.planList.put(xml.getAttributeValue("id"), ret);

        return ret;
    }

    private Transition getTransition(Element transitionElement, GameState referenceGameState) {
        Transition newTransition = null;
        if (transitionElement.getAttribute("type") == null) {
            newTransition = new DummyTransition();
        } else {
            String transitionType = transitionElement.getAttributeValue("type");
            if (transitionType.equals("PreTransition")) {
                newTransition = new PreTransition();
                String nextPlan = transitionElement.getAttributeValue("nextPlan");

                // System.out.println("planList " + planList.size());
                if (planList.containsKey(nextPlan)) {
                    newTransition.setPlan(planList.get(nextPlan));
                    if (planList.get(nextPlan) == null) {
                        System.err.println("getTransition (PreTransition): nextPlan found but is null!! ->"
                                + nextPlan);
                    }
                } else {
                    System.err.println("getTransition (PreTransition): nextPlan not found!");
                    newTransition.setPlan(parse(0, nextPlan, referenceGameState));
                }
            } else if (transitionType.equals("PreFailureTransition")) {
                newTransition = new PreFailureTransition();
                String nextPlan = transitionElement.getAttributeValue("nextPlan");
                if (planList.containsKey(nextPlan)) {
                    newTransition.setPlan(planList.get(nextPlan));
                    if (planList.get(nextPlan) == null) {
                        System.err.println("getTransition (PreFaliureTransition): nextPlan found but is null!! ->"
                                + nextPlan);
                    }
                } else {
                    System.err.println("getTransition (PreFaliureTransition): nextPlan not found!");
                    newTransition.setPlan(parse(0, nextPlan, referenceGameState));
                }
            } else if (transitionType.equals("SuccessTransition")) {
                newTransition = new SuccessTransition();
                String nextPlan = transitionElement.getAttributeValue("nextPlan");
                if (planList.containsKey(nextPlan)) {
                    // System.out.println("Found the plan " + nextPlan + "
                    // inside transition!");
                    newTransition.setPlan(planList.get(nextPlan));
                    if (planList.get(nextPlan) == null) {
                        System.err.println("getTransition (SuccessTransition): nextPlan found but is null!! ->"
                                + nextPlan);
                    }
                } else {
                    System.err.println("getTransition (SuccessTransition): nextPlan not found!");
                    newTransition.setPlan(parse(0, nextPlan, referenceGameState));
                }
            } else if (transitionType.equals("FailureTransition")) {
                newTransition = new FailureTransition();
                String nextPlan = transitionElement.getAttributeValue("nextPlan");
                if (planList.containsKey(nextPlan)) {
                    // System.out.println("Found the plan " + nextPlan + "
                    // inside transition!");
                    newTransition.setPlan(planList.get(nextPlan));
                    if (planList.get(nextPlan) == null) {
                        System.err.println("getTransition (FailureTransition): nextPlan found but is null!! ->"
                                + nextPlan);
                    }
                } else {
                    newTransition.setPlan(parse(0, nextPlan, referenceGameState));
                    System.err.println("getTransition (FailureTransition): nextPlan not found!");
                }
            } else if (transitionType.equals("DummyTransition")) {
                newTransition = new DummyTransition();
            } else {
                System.err.println("Wrong transition type!! " + transitionType);
                return null; // WRONG TRANSITION TYPE
            }
        }

        // get the generic components for transitions
        newTransition.setElementID(transitionElement.getAttributeValue("id"));

        Element cond_e = transitionElement.getChild("conditions");

        if (cond_e != null) {
            Sensor cond = Sensor.loadFromXML(cond_e.getChild("Sensor"));
            newTransition.setCondition(cond);
        }

        return newTransition;

    }

    private State getState(Element stateElement, GameState referenceGameState) {
        State newState = null;
        if (stateElement.getAttribute("type") == null) {
            newState = new DummyState();
        } else {
            String stateType = stateElement.getAttributeValue("type");
            if (stateType.equals("PlanState")) {
                newState = new PlanState();
                String nextPlan = stateElement.getAttributeValue("nextPlan");
                // System.out.println("\n");

                if (planList.containsKey(nextPlan)) {
                    // System.out.println("Found the plan " + nextPlan + "
                    // inside state!");
                    newState.setPlan(planList.get(nextPlan));
                    if (planList.get(nextPlan) == null) {
                        System.err.println("getState (" + stateType
                                + "): nextPlan found but is null!! ->"
                                + nextPlan);
                    }
                } else {
                    newState.setPlan(parse(0, nextPlan, referenceGameState));
                    System.err.println("getState (" + stateType
                            + "): nextPlan not found!");
                }
            } else if (stateType.equals("PlanPreconditionFailedState")) {
                newState = new PlanPreconditionFailedState();
                String nextPlan = stateElement.getAttributeValue("nextPlan");
                // System.out.println("\n");

                if (planList.containsKey(nextPlan)) {
                    // System.out.println("Found the plan " + nextPlan + "
                    // inside state!");
                    newState.setPlan(planList.get(nextPlan));
                    if (planList.get(nextPlan) == null) {
                        System.err.println("getState (" + stateType
                                + "): nextPlan found but is null!! ->"
                                + nextPlan);
                    }
                } else {
                    newState.setPlan(parse(0, nextPlan, referenceGameState));
                    System.err.println("getState (" + stateType
                            + "): nextPlan not found!");
                }
            } else if (stateType.equals("DummyState")) {
                newState = new DummyState();
            } else {
                System.err.println("Wrong state type!! " + stateType);
                return null; // WRONG STATE TYPE
            }
        }

        // get the generic components for states
        newState.setElementID(stateElement.getAttributeValue("id"));
        if (stateElement.getAttribute("initTokens") != null) {
            newState.setCurrentNumberOfTokens(stateElement.getAttributeValue("initTokens"));
        }

        return newState;

    }

    private PetriNetPlan getPetrinetPlan(Element plan, GameState referenceGameState) {
        PetriNetPlan pnp = new PetriNetPlan();
        dummyList = new ArrayList<PetriNetElement>();

        // Get all children
        List<Element> planElements = plan.getChildren();

        for (Element planElement : planElements) {
            if (planElement.getName().equals("plan")) {
                parse(planElement, this, referenceGameState);
            }
        }

        for (Element planElement : planElements) {
            if (planElement.getName().equals("conditions")) {
                Element prec_e = planElement.getChild("preCondition");
                if (prec_e != null) {
                    Sensor prec = Sensor.loadFromXML(prec_e.getChild("Sensor"));
                    pnp.setPreCondition(prec);
                }
                Element prefc_e = planElement.getChild("preFailureCondition");
                if (prefc_e != null) {
                    Sensor prefc = Sensor.loadFromXML(prefc_e.getChild("Sensor"));
                    pnp.setPreFailureCondition(prefc);
                }
                Element succ_e = planElement.getChild("successCondition");
                if (succ_e != null) {
                    Sensor succ = Sensor.loadFromXML(succ_e.getChild("Sensor"));
                    pnp.setSuccessCondition(succ);
                }
                Element fail_e = planElement.getChild("failureCondition");
                if (fail_e != null) {
                    Sensor fail = Sensor.loadFromXML(fail_e.getChild("Sensor"));
                    pnp.setFailureCondition(fail);
                }
            } else {
                if (planElement.getName().equals("state")) {
                    State newState = getState(planElement, referenceGameState);
                    dummyList.add(newState);
                }
                if (planElement.getName().equals("transition")) {
                    Transition newTran = getTransition(planElement, referenceGameState);
                    dummyList.add(newTran);
                }
                if (planElement.getName().equals("gamestate")) {
                    pnp.setOriginalGameState(GameState.loadDifferenceFromXML(planElement, referenceGameState, d2.core.Config.getDomain()));
                }
                if (planElement.getName().equals("player")) {
                    pnp.setOriginalPlayer(planElement.getValue());
                }

            }
        }

        for (Element planElement : planElements) {
            if (planElement.getName().equals("conditions")) {
                // Processed these already
            } else {
                if (planElement.getName().equals("state")) {
                    String stateID = planElement.getAttributeValue("id");
                    State stateElement = (State) getElementFromID(stateID,
                            dummyList);

                    List<Element> nextTransitions = planElement.getChild(
                            "nextTransitions").getChildren("nextTransition");
                    for (Element transition : nextTransitions) {
                        String transitionID = transition.getAttributeValue("id");
                        int transitionTokensRequired = Integer.parseInt(transition.getAttributeValue("tokens"));
                        Transition tranElement = (Transition) getElementFromID(
                                transitionID, dummyList);
                        stateElement.addNextTransition(
                                transitionTokensRequired, tranElement);
                    }
                    pnp.addPetriNetElement(stateElement);
                }
                if (planElement.getName().equals("transition")) {
                    String transitionID = planElement.getAttributeValue("id");
                    Transition tranElement = (Transition) getElementFromID(
                            transitionID, dummyList);

                    List<Element> nextStates = planElement.getChild(
                            "nextStates").getChildren("nextState");
                    for (Element state : nextStates) {
                        String stateID = state.getAttributeValue("id");
                        int tokensReq = Integer.parseInt(state.getAttributeValue("tokens"));
                        State stateElement = (State) getElementFromID(stateID,
                                dummyList);
                        tranElement.addNextState(tokensReq, stateElement);
                    }
                    pnp.addPetriNetElement(tranElement);

                }

            }

        }
        planList.put(plan.getAttributeValue("id"), pnp);
        return pnp;

    }

    private PetriNetElement getElementFromID(String id,
            ArrayList<PetriNetElement> dummyList) {

        // System.out.println("Searching for " + id);
        for (PetriNetElement p : dummyList) {
            // System.out.println(p);
            // System.out.println(p.getClass().getSimpleName() + " -> " +
            // p.getElementID());

            if (p.getElementID().equals(id)) {
                // System.out.println("FOUND!!! " + id);
                return p;
            }
        }

        System.err.println("FUBARED while searching for " + id);
        return null;
    }

    public boolean initializeDOMParser(String fileName) {
        try {
            SAXBuilder builder = new SAXBuilder();
            File file = new File(fileName);
            doc = builder.build(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            doc = null;
            return false;
        }

    }

    public int printPNP(PetriNetElement p, String indentation) {
        indentation = indentation + "\t";
        if (p == null) {
            return 0;
        }
        if (p instanceof State) {
            State sp = (State) p;
            System.out.println(indentation + "State :" + sp.getElementID());
            for (Pair<Integer, Transition> t : sp.getNextTransitions()) {
                printPNP(t._b, indentation);
            }
        }
        if (p instanceof Transition) {
            Transition tp = (Transition) p;
            System.out.println(indentation + "Transition :" + tp.getElementID());
            for (Pair<Integer, State> s : tp.getNextStates()) {
                printPNP(s._b, indentation);
            }
        }

        return 0;

    }
}
