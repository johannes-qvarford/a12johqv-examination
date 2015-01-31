/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.plans;

import d2.execution.planbase.PlayerGameState;
import gatech.mmpm.Context;
import gatech.mmpm.GameState;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;

import org.jdom.Element;

abstract public class Plan implements Cloneable, java.io.Serializable {

    static final long serialVersionUID = 0x34124547;
    /**
     * This variable stores the state of the game state in the expert trace from
     * where this plan was learnt.
     */
    protected GameState m_originalGameState = null;
    protected String m_originalPlayer = null;

    public Plan() {
    }

    public GameState getOriginalGameState() {
        return m_originalGameState;
    }

    public void setOriginalGameState(GameState gs) {
        m_originalGameState = gs;
    }

    public String getOriginalPlayer() {
        return m_originalPlayer;
    }

    public void setOriginalPlayer(String p) {
        m_originalPlayer = p;
    }

    public abstract Sensor getPreCondition();

    public abstract Sensor getPreFailureCondition();

    public abstract Sensor getSuccessCondition();

    public abstract Sensor getFailureCondition();

    public abstract Sensor getPostCondition();

    public abstract boolean checkPreCondition(PlayerGameState pgs);

    public abstract boolean checkPreFailureCondition(PlayerGameState pgs);

    public abstract boolean checkSuccessCondition(PlayerGameState pgs);

    public abstract boolean checkFailureCondition(PlayerGameState pgs);

    public abstract boolean checkPostCondition(PlayerGameState pgs);

    public Context getContext(int cycle, GameState gameState, String player) {
        Context result = new Context();
        result.put("playerID", player);

        return result;
    } // getContext

    /**
     * Writes the plan onto an XMLWriter object
     *
     * @param PlanID PlanID being passed. This is needed to determine what is
     * the nextPlan, given a particular plan
     * @param w The XMLWriter object
     */
    public void writeToXML(String PlanID, XMLWriter w) {
        if (this instanceof PetriNetPlan) {
            PetriNetPlan pnp = (PetriNetPlan) this;
            pnp.writeToXML(PlanID, w);
        }
        if (this instanceof ActionPlan) {
            ActionPlan ap = (ActionPlan) this;
            ap.writeToXML(PlanID, w);
        }
        if (this instanceof GoalPlan) {
            GoalPlan gp = (GoalPlan) this;
            gp.writeToXML(PlanID, w);
        }
    }

    public void writeToXMLDifference(String PlanID, GameState gs, XMLWriter w) {
        if (this instanceof PetriNetPlan) {
            PetriNetPlan pnp = (PetriNetPlan) this;
            pnp.writeToXMLDifferenceInternal(PlanID, gs, w);
        }
        if (this instanceof ActionPlan) {
            ActionPlan ap = (ActionPlan) this;
            ap.writeToXMLDifferenceInternal(PlanID, gs, w);
        }
        if (this instanceof GoalPlan) {
            GoalPlan gp = (GoalPlan) this;
            gp.writeToXML(PlanID, w);
        }
    }

    public Object clone() {
        return clone(new HashMap<Object, Object>());
    }

    public abstract Object clone(HashMap<Object, Object> alreadyCloned);

    public abstract boolean isPlanCompletelyExecuted();

    public static Plan loadFromXML(Element xml, GameState referenceGameState) {
        return ParseLmxPlans.parse(xml, null, referenceGameState);
    }
}