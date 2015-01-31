/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.plans;

import d2.execution.planbase.PlayerGameState;
import gatech.mmpm.GameState;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gatech.mmpm.util.Pair;
import java.util.HashSet;
import java.util.LinkedList;

public class PetriNetPlan extends Plan {

    static final long serialVersionUID = 0x34124537;
    private ArrayList<PetriNetElement> petriNetElements;
    private Sensor PreCondition;
    private Sensor PreFailureCondition;
    private Sensor SuccessCondition;
    private Sensor FailureCondition;
    private Sensor PostCondition;

    public PetriNetPlan() {
        super();
        petriNetElements = new ArrayList<PetriNetElement>();
    }

    public ArrayList<PetriNetElement> getPetriNetElements() {
        return petriNetElements;
    }

    public void setPetriNetElements(ArrayList<PetriNetElement> petriNetElements) {
        this.petriNetElements = petriNetElements;
    }

    public void addPetriNetElement(PetriNetElement p) {
        if (!petriNetElements.contains(p)) {
            petriNetElements.add(p);
        }
    }

    public PetriNetElement getPetriNetElement(int i) {
        return petriNetElements.get(i);
        //return null;
    }

    public PetriNetElement getPetriNetElement(String id) {
        for (PetriNetElement pne : petriNetElements) {
            if (pne.getElementID().equals(id)) {
                return pne;
            }
        }
        return null;
    }


    public void writeToXML(String planID, XMLWriter w) {
        writeToXMLDifferenceInternal(planID, null, w);
    }

    public void writeToXMLDifferenceInternal(String planID, GameState oldGameState, XMLWriter w) {
        ArrayList<Pair<Plan, String>> nextPlans = new ArrayList<Pair<Plan, String>>();

        w.tagWithAttributes("plan", "id = '" + planID + "' type='" + this.getClass().getSimpleName() + "'>");

        w.tag("conditions");
        w.tag("preCondition");
        this.getPreCondition().writeToXML(w);
        w.tag("/preCondition");

        w.tag("successCondition");
        this.getSuccessCondition().writeToXML(w);
        w.tag("/successCondition");

        w.tag("failureCondition");
        this.getFailureCondition().writeToXML(w);
        w.tag("/failureCondition");
        w.tag("/conditions");
        w.flush();

        if (m_originalGameState != null) {
            m_originalGameState.writeToXMLDifference(w, oldGameState);
        }
        if (m_originalPlayer != null) {
            w.tag("player", m_originalPlayer);
        }

        for (PetriNetElement petriNetElement : petriNetElements) {
            if (petriNetElement instanceof DummyState) {
                DummyState dState = (DummyState) petriNetElement;
                dState.writeToXML(planID, w);
            }

            if (petriNetElement instanceof PlanState) {
                //generate a planID using the counter only if it's not present in the nextPlans list
                String nextPlanID = planIsNotInList(petriNetElement.getPlan(), nextPlans);
                if (nextPlanID == null) {
                    nextPlanID = "PLAN" + Counter.PlanCounter++;
                    nextPlans.add(new Pair<Plan, String>(petriNetElement.getPlan(), nextPlanID));
                }
                PlanState pState = (PlanState) petriNetElement;
                pState.writeToXML(nextPlanID, w);
            }

            if (petriNetElement instanceof PlanPreconditionFailedState) {
                //generate a planID using the counter only if it's not present in the nextPlans list
                String nextPlanID = planIsNotInList(petriNetElement.getPlan(), nextPlans);
                if (nextPlanID == null) {
                    nextPlanID = "PLAN" + Counter.PlanCounter++;
                    nextPlans.add(new Pair<Plan, String>(petriNetElement.getPlan(), nextPlanID));
                }
                PlanPreconditionFailedState pState = (PlanPreconditionFailedState) petriNetElement;
                pState.writeToXML(nextPlanID, w);
            }

            if (petriNetElement instanceof DummyTransition) {
                DummyTransition dTransition = (DummyTransition) petriNetElement;
                dTransition.writeToXML(planID, w);
            }

            if (petriNetElement instanceof PreTransition) {
                //generate a planID using the counter only if it's not present in the nextPlans list
                String nextPlanID = planIsNotInList(petriNetElement.getPlan(), nextPlans);
                if (nextPlanID == null) {
                    nextPlanID = "PLAN" + Counter.PlanCounter++;
                    nextPlans.add(new Pair<Plan, String>(petriNetElement.getPlan(), nextPlanID));
                }

                PreTransition pTransition = (PreTransition) petriNetElement;
                pTransition.writeToXML(nextPlanID, w);
            }

            if (petriNetElement instanceof PreFailureTransition) {
                //generate a planID using the counter only if it's not present in the nextPlans list
                String nextPlanID = planIsNotInList(petriNetElement.getPlan(), nextPlans);
                if (nextPlanID == null) {
                    nextPlanID = "PLAN" + Counter.PlanCounter++;
                    nextPlans.add(new Pair<Plan, String>(petriNetElement.getPlan(), nextPlanID));
                }

                PreFailureTransition pfTransition = (PreFailureTransition) petriNetElement;
                pfTransition.writeToXML(nextPlanID, w);
            }

            if (petriNetElement instanceof SuccessTransition) {
                //generate a planID using the counter only if it's not present in the nextPlans list
                String nextPlanID = planIsNotInList(petriNetElement.getPlan(), nextPlans);
                if (nextPlanID == null) {
                    nextPlanID = "PLAN" + Counter.PlanCounter++;
                    nextPlans.add(new Pair<Plan, String>(petriNetElement.getPlan(), nextPlanID));
                }

                SuccessTransition sTransition = (SuccessTransition) petriNetElement;
                sTransition.writeToXML(nextPlanID, w);
            }

            if (petriNetElement instanceof FailureTransition) {
                //generate a planID using the counter only if it's not present in the nextPlans list
                String nextPlanID = planIsNotInList(petriNetElement.getPlan(), nextPlans);
                if (nextPlanID == null) {
                    nextPlanID = "PLAN" + Counter.PlanCounter++;
                    nextPlans.add(new Pair<Plan, String>(petriNetElement.getPlan(), nextPlanID));
                }

                FailureTransition fTransition = (FailureTransition) petriNetElement;
                fTransition.writeToXML(nextPlanID, w);
            }
            w.flush();
        }

        for (Pair<Plan, String> psp : nextPlans) {
            psp._a.writeToXMLDifference(psp._b, oldGameState, w);
            w.flush();
        }

        w.flush();
        w.tag("/plan");
    }

    private String planIsNotInList(Plan plan, ArrayList<Pair<Plan, String>> nextPlans) {
        for (Pair<Plan, String> ps : nextPlans) {
            if (ps._a.equals(plan)) {
                return ps._b;
            }
        }
        return null;
    }

    public Object clone(HashMap<Object, Object> alreadyCloned) {
        if (alreadyCloned.get(this) != null) {
            return alreadyCloned.get(this);
        }

        PetriNetPlan cloneP = new PetriNetPlan();
        alreadyCloned.put(this, cloneP);

        for (PetriNetElement pne : petriNetElements) {
            cloneP.petriNetElements.add((PetriNetElement) pne.clone(alreadyCloned));
        }

        return cloneP;
    }

    public boolean isPlanCompletelyExecuted() {
        for (PetriNetElement p : getPetriNetElements()) {
            if (p instanceof State) {
                State s = (State) p;
                for (Pair<Integer, Transition> t_p : s.getNextTransitions()) {
                    if (s.getCurrentNumberOfTokens() >= t_p._a) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void replacePlan(Plan oldPlan, Plan adaptedPlan) {
        for (PetriNetElement pne : petriNetElements) {
            if (pne.getPlan() == oldPlan) {
                pne.setPlan(adaptedPlan);
            }
        }
    }

    public List<PetriNetElement> getAllElements() {
        return petriNetElements;
    }

    public List<PlanState> getAllPlanStates() {
        ArrayList<PlanState> plans = new ArrayList<PlanState>();

        for (int i = 0; i < this.getPetriNetElements().size(); i++) {
            if (this.getPetriNetElement(i) instanceof PlanState) {
                plans.add((PlanState) this.getPetriNetElement(i));
            }
        }

        return plans;
    }

    public ArrayList<PreTransition> getAllPreTransition() {
        ArrayList<PreTransition> plans = new ArrayList<PreTransition>();

        for (int i = 0; i < this.getPetriNetElements().size(); i++) {
            if (this.getPetriNetElement(i) instanceof PreTransition) {
                plans.add((PreTransition) this.getPetriNetElement(i));
            }
        }

        return plans;
    }

    public ArrayList<SuccessTransition> getAllSuccessTransition() {
        ArrayList<SuccessTransition> plans = new ArrayList<SuccessTransition>();

        for (int i = 0; i < this.getPetriNetElements().size(); i++) {
            if (this.getPetriNetElement(i) instanceof SuccessTransition) {
                plans.add((SuccessTransition) this.getPetriNetElement(i));
            }
        }

        return plans;
    }

    public ArrayList<FailureTransition> getAllFailureTransition() {
        ArrayList<FailureTransition> plans = new ArrayList<FailureTransition>();

        for (int i = 0; i < this.getPetriNetElements().size(); i++) {
            if (this.getPetriNetElement(i) instanceof FailureTransition) {
                plans.add((FailureTransition) this.getPetriNetElement(i));
            }
        }

        return plans;
    }

    public boolean checkFailureCondition(PlayerGameState pgs) {

//		if(!onFailureConditionCalled)
//			onFailureCondition();
        return ((Float) FailureCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player))
                >= Sensor.BOOLEAN_TRUE_THRESHOLD);
    }

    public boolean checkPostCondition(PlayerGameState pgs) {
//		if(!onPostConditionCalled)
//		onPostCondition();
        return ((Float) PostCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player))
                >= Sensor.BOOLEAN_TRUE_THRESHOLD);
    }

    public boolean checkPreCondition(PlayerGameState pgs) {
//		if(!onPreConditionCalled)
//		onPreCondition();
        return ((Float) PreCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player))
                >= Sensor.BOOLEAN_TRUE_THRESHOLD);
    }

    public boolean checkPreFailureCondition(PlayerGameState pgs) {
//		if(!onPreFailureConditionCalled)
//		onPreFailureCondition();
        return ((Float) PreFailureCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player))
                >= Sensor.BOOLEAN_TRUE_THRESHOLD);
    }

    public boolean checkSuccessCondition(PlayerGameState pgs) {
//		if(!onSuccessConditionCalled)
//		onSuccessCondition();
        return ((Float) SuccessCondition.evaluate(pgs.cycle, pgs.gs, pgs.player, getContext(pgs.cycle, pgs.gs, pgs.player))
                >= Sensor.BOOLEAN_TRUE_THRESHOLD);
    }

    public Sensor getFailureCondition() {
        return FailureCondition;
    }

    public Sensor getPostCondition() {
        return PostCondition;
    }

    public Sensor getPreCondition() {
        return PreCondition;
    }

    public Sensor getPreFailureCondition() {
        return PreFailureCondition;
    }

    public Sensor getSuccessCondition() {
        return SuccessCondition;
    }

    public void setFailureCondition(Sensor s) {
        FailureCondition = s;
    }

    public void setPostCondition(Sensor s) {
        PostCondition = s;
    }

    public void setPreCondition(Sensor s) {
        PreCondition = s;
    }

    public void setPreFailureCondition(Sensor s) {
        PreFailureCondition = s;
    }

    public void setSuccessCondition(Sensor s) {
        SuccessCondition = s;
    }

    // This method returns true if there is a direct path from s0 to s1
    // that doesn't go through any other PlanState
    public boolean planDirectlyPrecedes(PlanState s0, PlanState s1) {
        List<State> open = new LinkedList<State>();
        HashSet<State> closed = new HashSet<State>();

        open.add(s0);
        while (!open.isEmpty()) {
            State s = open.remove(0);
            closed.add(s);
            for (Pair<Integer, Transition> next_pair : s.getNextTransitions()) {
                for (Pair<Integer, State> next_pair2 : next_pair.getSecond().getNextStates()) {
                    State next_state = next_pair2.getSecond();
                    if (next_state == s1) {
                        return true;
                    }
                    if (!(next_state instanceof PlanState) && !closed.contains(next_state) && !open.contains(next_state)) {
                        open.add(next_state);
                    }
                }
            }
        }

//        System.out.println(closed.size() + "States reachable from s0 = " + s0.getElementID());
//        for(State s:closed) {
//            System.out.println("    " + s.getElementID() + "(" + s.getClass().getSimpleName() + ")");
//        }

        return false;
    }
    
    public String toString() {
        String tmp = "";
        for(PlanState ps:getAllPlanStates()) {
            Plan p = ps.getPlan();
            tmp += p.toString() + "\n";
        }
        return tmp;
    }
    
}
