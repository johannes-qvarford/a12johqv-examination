/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.learn.plansimilarity;

import gatech.mmpm.Action;
import gatech.mmpm.ActionParameter;
import gatech.mmpm.ActionParameterType;

/**
 *
 * @author santi
 */
public class ActionSimilarity {
    public static double similarity(Action a1, Action a2) {
        // If they are different actions, 0 similarity:
        if (a1.getClass() != a2.getClass()) return 0;
        
        // If they are the same action, then compare their parameters:
        
        // Since they share the same action, they are already somewhat similar, add 20 points:
        double total = 20;
        double equal = 20;
        
        for(ActionParameter ap:a1.listOfParameters()) {
            Object o1 = a1.parameterValue(ap.getName());
            Object o2 = a2.parameterValue(ap.getName());
            
            // since this can be complicated, we use the simplification, of just compare them by turning them into strings:
            String o1s = (o1==null ? "":o1.toString());
            String o2s = (o2==null ? "":o2.toString());
            double points = 1;
            // some parameter types are more important than others:
            if (ap.m_type==ActionParameterType.ENTITY_TYPE) points = 10;
            if (ap.m_type==ActionParameterType.ENTITY_ID) points = 2;
            if (ap.m_type==ActionParameterType.PLAYER) points = 10;
            if (ap.m_type==ActionParameterType.COORDINATE) points = 1;
            if (ap.m_type==ActionParameterType.DIRECTION) points = 5;
            if (ap.m_type==ActionParameterType.INTEGER) points = 1;
            if (ap.m_type==ActionParameterType.BOOLEAN) points = 10;
            if (ap.m_type==ActionParameterType.FLOAT) points = 1;
            if (o1s.equals(o2s)) equal += points;
            total += points;
        }
        
        double similarity = equal/total;
        
        if (similarity<0 || similarity>1) {
            System.err.println("ActionSimilarity = " + similarity + " = " + equal + "/" + total);
        }
        
        return similarity;
    }
}
