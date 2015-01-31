/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.learn.plansimilarity;

import d2.plans.PetriNetPlan;

/**
 *
 * @author santi
 */
public abstract class PlanSimilarity {
    public abstract double similarity(PetriNetPlan p1, PetriNetPlan p2);
}
