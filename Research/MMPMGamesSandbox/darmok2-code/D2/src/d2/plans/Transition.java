/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.plans;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.True;

import java.util.ArrayList;
import java.util.List;

import gatech.mmpm.util.Pair;

abstract public class Transition extends PetriNetElement implements Cloneable, java.io.Serializable {

    static final long serialVersionUID = 0x34363547;
    protected Sensor c = null;
    protected ArrayList<Pair<Integer, State>> previousStates = null;
    protected ArrayList<Pair<Integer, State>> nextTokenStates = null;

    public Transition() {
        previousStates = new ArrayList<Pair<Integer, State>>();
        nextTokenStates = new ArrayList<Pair<Integer, State>>();
        c = new True();
    }
    
    public void addPreviousState(int tokenReq, State s) {
        Pair<Integer, State> newPair = new Pair<Integer, State>(tokenReq, s);
        if (tokenReq<=0) System.err.println("Transition.addPreviousState: tokens required is " + tokenReq);
        this.previousStates.add(newPair);
    }
    
    public List<Pair<Integer, State>> getPreviousStates() {
        return previousStates;
    }

    public List<Pair<Integer, State>> getNextStates() {
        return nextTokenStates;
    }

    public void addNextState(int tokenReq, State nextState) {
        Pair<Integer, State> newPair = new Pair<Integer, State>(tokenReq, nextState);
        if (tokenReq<=0) System.err.println("Transition.addNextState: tokens required is " + tokenReq);
        this.nextTokenStates.add(newPair);
    }

    public void transferTokens() {
    }

    public void printContent() {
        System.out.println("Transition ---> " + getElementID());
//		for (State s : nextStates)
//		for(int i=0; i<nextTokenStates.size(); i++)
//		{
//			System.out.println("\t\t" + nextTokenStates.get(i).m_b.getElementID());
//		}

    }

    public void setCondition(Sensor a_c) {
        c = a_c;
    }

    abstract public Sensor getCondition();
    //abstract public void writeToXML(String planID, Writer fw) throws IOException;
}