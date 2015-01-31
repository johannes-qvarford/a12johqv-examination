/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.learn.plansimilarity;

import d2.plans.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author santi
 */
public class EditDistancePlanSimilarity extends PlanSimilarity {

    public double similarity(PetriNetPlan p1, PetriNetPlan p2) {
        return 1.0 - distance(p1, p2);
    }
    
    public double similarity(List<Plan> subplans1, List<Plan> subplans2) {
        return 1.0 - distance(subplans1, subplans2);
    }
    
    public double distance(PetriNetPlan p1, PetriNetPlan p2) {
        List<Plan> subplans1 = new ArrayList<Plan>();
        List<Plan> subplans2 = new ArrayList<Plan>();
        for(PlanState ps:p1.getAllPlanStates()) subplans1.add(ps.getPlan());
        for(PlanState ps:p2.getAllPlanStates()) subplans2.add(ps.getPlan());
        return distance(subplans1, subplans2);
    }    
    
    
    public double distance(List<Plan> subplans1, List<Plan> subplans2) {
        // - This similarity first computes the matrix of similarities of the individual subplans in the plans
        // - Finally, it treats the plan as a set of actions (i.e. ignores the structure of the plan), and applies a standard edit distance
        //   with the following operators:
        //      - delete action:    cost 1
        //      - insert action:    cost 1
        //      - replace action:   cost (1 - similarity of actions)
        
        int np1 = subplans1.size();
        int np2 = subplans2.size();
        
        double planSimilarities[] = new double[np1*np2];
        for(int i1 = 0;i1<np1;i1++) {
            for(int i2 = 0;i2<np2;i2++) {
                Plan sp1 = subplans1.get(i1);
                Plan sp2 = subplans2.get(i2);
                
                if (sp1 instanceof ActionPlan && sp2  instanceof ActionPlan) {
                    planSimilarities[i1*np2+i2] = ActionSimilarity.similarity(((ActionPlan)sp1).getAction(),((ActionPlan)sp2).getAction());
                } else if (sp1 instanceof GoalPlan && sp2 instanceof GoalPlan) {
                    if (((GoalPlan)sp1).getGoal().getClass() == 
                        ((GoalPlan)sp2).getGoal().getClass()) {
                        planSimilarities[i1*np2+i2] = 1;
                    } else {
                        planSimilarities[i1*np2+i2] = 0;
                    }
                } else {
                    planSimilarities[i1*np2+i2] = 0;
                }
            }            
        }
        
        // Compute the similarity:
        double cost = 0;
        int assignments[] = new int[np1];
        for(int i = 0;i<np1;i++) {
            int best1 = -1;
            int best2 = -1;
            for(int n1 = 0;n1<np1;n1++) {
                for(int n2 = 0;n2<np2;n2++) {
                    if (planSimilarities[n1*np2+n2]>=0) {
                        if (best1==-1 ||
                            (planSimilarities[n1*np2+n2] >
                            planSimilarities[best1*np2+best2])) {
                            best1 = n1;
                            best2 = n2;
                        }
                    }
                }
            }
            
            // replace action:
            if (best1!=-1 && best2!=-1) {
                cost+= 1 - planSimilarities[best1*np2+best2];
                if (planSimilarities[best1*np2+best2]<0 || planSimilarities[best1*np2+best2]>1) {
                    System.err.println("Plan similarity: " + planSimilarities[best1*np2+best2] + " (for " + best1 + " and " + best2 + ")");
                }
                // invalidate the appropriate coordinates:
                for(int n1 = 0;n1<np1;n1++) planSimilarities[n1*np2+best2] = -1;
                for(int n2 = 0;n2<np2;n2++) planSimilarities[best1*np2+n2] = -1;
//            } else {
//                cost+= 1;
            }
                
        }
        
        // delete/insert action:
        cost += Math.abs(np1 - np2);
        
        double maximum = Math.max(np1, np2);

        return cost/maximum;
    }
    
}
