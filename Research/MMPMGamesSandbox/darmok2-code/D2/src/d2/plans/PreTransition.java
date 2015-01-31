/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.plans;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;

import gatech.mmpm.util.Pair;

public class PreTransition extends Transition {

    /**
     *
     */
    private static final long serialVersionUID = -2009119503434599352L;

    public PreTransition() {
        super();
        setElementID("PRE" + getElementID());
    }

    public PreTransition(Plan p) {
        super();
        setElementID("PRE" + getElementID());
        this.setPlan(p);
    }

    public Sensor getCondition() {
        return getPlan().getPreCondition();
    }

    public void writeToXML(String planID, XMLWriter w) {
        //System.out.println(getElementID());
        w.rawXML("<transition id='" + this.getElementID() + "' type=\"PreTransition\" nextPlan='" + planID + "'>");
        w.tag("conditions");
        getCondition().writeToXML(w);
        w.tag("/conditions");
        w.tag("nextStates");
        for (Pair<Integer, State> t : getNextStates()) {
            w.rawXML("<nextState id='" + t._b.getElementID() + "' tokens='" + t._a + "'/>");
        }
        //w.rawXML(nextStates);
        w.tag("/nextStates");
        w.rawXML("</transition>");
        w.flush();
    }

    public Object clone(HashMap<Object, Object> alreadyCloned) {
        if (alreadyCloned.get(this) != null) {
            return alreadyCloned.get(this);
        }

        PreTransition cloneT = new PreTransition();
        alreadyCloned.put(this, cloneT);

        cloneT.elementID = elementID;
        cloneT.plan = (plan == null ? null : (Plan) plan.clone(alreadyCloned));

        cloneT.c = (Sensor) c.clone();
        for (Pair<Integer, State> p : nextTokenStates) {
            cloneT.nextTokenStates.add(new Pair<Integer, State>(new Integer((int) p._a), (State) p._b.clone(alreadyCloned)));
        }
        for (Pair<Integer, State> p : previousStates) {
            cloneT.previousStates.add(new Pair<Integer, State>(new Integer((int) p._a), (State) p._b.clone(alreadyCloned)));
        }

        return cloneT;
    }
}
