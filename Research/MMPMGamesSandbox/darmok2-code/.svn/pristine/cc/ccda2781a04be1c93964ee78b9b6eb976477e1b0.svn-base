/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;

import d2.plans.Plan;
import gatech.mmpm.Context;
import gatech.mmpm.GameState;



/*******************************************************************************
 * 
 * @author Santi Ontañón and Rushabh Shah. This class is used for Condition
 *         Matching which is used for Plan Adaptation and Retrieval
 */
public class ConditionMatcher {

	protected static final int DEBUG = 0;

	public ConditionMatcher() {
	}


    /* Returns a double between 0 and 1 (0 meaning false and 1 meaning true).
     * It returns "true" when the "postCondition" can satisfy the "preCondition"
     */
    public double match(Context postContext, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Context preContext, Sensor preCondition, int cycle2, GameState gs2, String player2) {
		if (DEBUG >= 1)
			System.out.println("AtomicMatching: " + postCondition + " with " + preCondition);

        if (postCondition==null || preCondition==null) return 0.0;

		// Rule 2: exact identical conditions match
		if (postCondition.equivalents(postContext, cycle1, gs1, player1,
                                      preCondition, preContext, cycle2, gs2, player2)) {
			// Rule 1: primitive values do not match:
			if (!(preCondition instanceof False) &&
			    !(preCondition instanceof True))
				return 1.0;
		}

        return 0.0;    
    }


    /* Same as the previous method, but it can take into account what plans do the conditions
     * belong to in order to do a finer grained matching.
     */
	public double match(Plan first, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Plan second, Sensor preCondition, int cycle2, GameState gs2, String player2) {
        return match(first.getContext(cycle1, gs1, player1),postCondition, cycle1, gs1, player1,
                     second.getContext(cycle2, gs2, player2),preCondition, cycle2, gs2, player2);
	}
}
