/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package d2.learn.planlearning;

import gatech.mmpm.Action;
import gatech.mmpm.sensor.Sensor;

/**
 *
 * @author santi
 */
public class PlanSpan {
    
    Action m_action = null;
    Sensor m_goal = null;
    int m_start = 0;
    int m_end = 0;
    boolean m_succeeded = true;

    public PlanSpan(Action a, int start, int end, boolean succeeded) {
        m_action = a;
        m_start = start;
        m_end = end;
        m_succeeded = succeeded;
    }

    public PlanSpan(Sensor g, int start, int end, boolean succeeded) {
        m_goal = g;
        m_start = start;
        m_end = end;
        m_succeeded = succeeded;
    }
    
    public int getStart() {
        return m_start;
    }
    
    public int getEnd() {
        return m_end;
    }
    
    public Action getAction() {
        return m_action;
    }
            
    public Sensor getGoal() {
        return m_goal;
    }
}
