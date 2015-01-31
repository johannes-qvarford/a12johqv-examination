/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.plans;

import d2.execution.planbase.PlayerGameState;
import gatech.mmpm.Action;
import gatech.mmpm.Context;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;

import org.jdom.Element;

public class ActionPlan extends Plan {

    /**
     *
     */
    private static final long serialVersionUID = 4886421472226573900L;
    private Action action = null;

    public ActionPlan() {
    }

    public ActionPlan(Action a) {
        action = a;
    }

    public ActionPlan(Action a, GameState gs) {
        action = a;
        setOriginalGameState(gs);
    }

    public String toString() {
        return "ActionPlan(" + action.toSimpleString() + ")";
    }

    public Context getContext(int cycle, GameState gameState, String player) {
        Context result = null;
        if (action != null) {
            result = action.getContext();
        }
        if (result == null) {
            result = new Context();
        }
        result.put("playerID", player);

        return result;
    } // getContext

    public Sensor getPreCondition() {
        return action.getPreCondition();
    }

    public Sensor getSuccessCondition() {
        return action.getSuccessCondition();
    }

    public Sensor getFailureCondition() {
        return action.getFailureCondition();
    }

    public Sensor getPreFailureCondition() {
        return action.getPreFailureCondition();
    }

    public Sensor getPostCondition() {
        return action.getPostCondition();
    }

    public boolean checkPreCondition(PlayerGameState pgs) {
        return action.checkPreCondition(pgs.cycle, pgs.gs, pgs.player);
    }

    public boolean checkSuccessCondition(PlayerGameState pgs) {
        return action.checkSuccessCondition(pgs.cycle, pgs.gs, pgs.player);
    }

    public boolean checkFailureCondition(PlayerGameState pgs) {
        return action.checkFailureCondition(pgs.cycle, pgs.gs, pgs.player);
    }

    public boolean checkPreFailureCondition(PlayerGameState pgs) {
        return action.checkPreFailureCondition(pgs.cycle, pgs.gs, pgs.player);
    }

    public boolean checkPostCondition(PlayerGameState pgs) {
        return action.checkPostCondition(pgs.cycle, pgs.gs, pgs.player);
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void writeToXML(String planID, XMLWriter w) {
        w.tagWithAttributes("plan", "id = '" + planID + "' type='" + this.getClass().getSimpleName() + "'");

        action.writeToXML(w);

        if (m_originalGameState != null) {
            m_originalGameState.writeToXML(w);
        }

        w.tag("/plan");
    }

    public void writeToXMLDifferenceInternal(String planID, GameState oldGameState, XMLWriter w) {
        w.tagWithAttributes("plan", "id = '" + planID + "' type='" + this.getClass().getSimpleName() + "'");

        action.writeToXML(w);

        if (m_originalGameState != null) {
            m_originalGameState.writeToXMLDifference(w, oldGameState);
        }
        if (m_originalPlayer != null) {
            w.tag("player", m_originalPlayer);
        }

        w.tag("/plan");
    }

    public static Plan loadFromXMLInternal(Element xml, GameState referenceGameState) {
        ActionPlan ret = new ActionPlan();

        Element egp_e = xml.getChild("Action");
        if (egp_e != null) {
            ret.setAction(Action.loadFromXML(egp_e));
        } else {
            System.err.println("ActionPlan.loadFromXMLInternal: no action in plan!");
        }

        Element gs_e = xml.getChild("gamestate");
        if (gs_e != null) {
            if (referenceGameState == null) {
                GameState gs = GameState.loadFromXML(gs_e, d2.core.Config.getDomain());
                ret.setOriginalGameState(gs);
            } else {
                GameState gs = GameState.loadDifferenceFromXML(gs_e, referenceGameState, d2.core.Config.getDomain());
                ret.setOriginalGameState(gs);
            }
            /*
             * ret.action.initializeActionConditions(0, gs); } else {
             * ret.action.initializeActionConditions();
             */
        }
        Element p_e = xml.getChild("player");
        if (p_e != null) {
            ret.setOriginalPlayer(p_e.getValue());
        }

        return ret;
    }

    public Object clone(HashMap<Object, Object> alreadyCloned) {
        if (alreadyCloned.get(this) != null) {
            return alreadyCloned.get(this);
        }
        if (action == null) {
            ActionPlan cloneT = new ActionPlan();
            alreadyCloned.put(this, cloneT);
            return cloneT;
        }
        Action a = (Action) action.clone();
        //	a.initializeActionConditions(0, m_originalGameState);
        ActionPlan p = new ActionPlan(a);
        alreadyCloned.put(this, p);
        p.setOriginalGameState(m_originalGameState);
        p.setOriginalPlayer(m_originalPlayer);
        return p;
    }

    public boolean isPlanCompletelyExecuted() {
        return false;
    }

}
