/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.execution.planbase;

/**
 *
 * @author santi
 */
public class FeatureStats {
    public boolean neverUpdated = true;   
            
    public double min = 0, max = 1;
    public double weight = 1;
    
    public void update(double value) {
        if (neverUpdated) {
            min = max = value;
            neverUpdated = false;
        } else {
            if (value<min) min = value;
            if (value>max) max = value;
        }
    }
}
