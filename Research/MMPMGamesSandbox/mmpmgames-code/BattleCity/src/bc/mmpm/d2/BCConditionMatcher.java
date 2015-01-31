/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.mmpm.d2;

import bc.mmpm.goals.DestroyEnemies;
import bc.mmpm.goals.DestroyEnemyBase;
import bc.mmpm.goals.GetInLineWithEnemy;
import bc.mmpm.goals.GetInLineWithEnemyBase;
import bc.mmpm.goals.WinGoal;
import d2.plans.Plan;
import d2.worldmodel.ConditionMatcher;
import gatech.mmpm.Context;
import gatech.mmpm.GameState;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;

/**
 *
 * @author santi
 */
public class BCConditionMatcher extends ConditionMatcher {
    ConditionMatcher base = new ConditionMatcher();

    public double match(Context postContext, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Context preContext, Sensor preCondition, int cycle2, GameState gs2, String player2) {
        if (base.match(postContext, postCondition, cycle1, gs1, player1,
                       preContext, preCondition, cycle2, gs2, player2)>=1.0) return 1.0;

        if (preCondition instanceof DestroyEnemies) {

        } else if (preCondition instanceof DestroyEnemyBase) {

        } else if (preCondition instanceof GetInLineWithEnemy) {

        } else if (preCondition instanceof GetInLineWithEnemyBase) {

        } else if (preCondition instanceof WinGoal) {
            if (postCondition instanceof DestroyEnemies ||
                postCondition instanceof DestroyEnemyBase) return 1.0;
        }

        return 0.0;
    }

    /* In BattleCity, all actions depend on each other (no chance of eliminating actions)
     */
    public double match(Plan first, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Plan second, Sensor preCondition, int cycle2, GameState gs2, String player2) {
        return 1.0;
	}

}
