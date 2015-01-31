/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package s2.mmpm.d2;

import d2.plans.ActionPlan;
import d2.plans.Plan;
import d2.worldmodel.ConditionMatcher;
import gatech.mmpm.Context;
import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.PhysicalEntity;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.builtin.EntityTypeExists;
import gatech.mmpm.sensor.composite.Invocation;
import s2.mmpm.actions.Attack;
import s2.mmpm.entities.WGoldMine;
import s2.mmpm.entities.WOTree;
import s2.mmpm.actions.Build;
import s2.mmpm.actions.ResourceLocation;
import s2.mmpm.actions.Train;
import s2.mmpm.entities.WBarracks;
import s2.mmpm.entities.WPeasant;
import s2.mmpm.entities.WTownhall;
import s2.mmpm.sensors.GoldCondition;
import s2.mmpm.sensors.WoodCondition;

/**
 *
 * @author santi
 */
public class S2ConditionMatcher extends ConditionMatcher {
    ConditionMatcher base = new ConditionMatcher();

    public double match(Context postContext, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Context preContext, Sensor preCondition, int cycle2, GameState gs2, String player2) {
        if (base.match(postContext, postCondition, cycle1, gs1, player1,
                       preContext, preCondition, cycle2, gs2, player2)>=1.0) return 1.0;

        // Specific S2 rules:

        return 0.0;
    }


    public double match(Plan first, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Plan second, Sensor preCondition, int cycle2, GameState gs2, String player2) {
        if (base.match(first, postCondition, cycle1, gs1, player1,
                       second, preCondition, cycle2, gs2, player2)>=1.0) return 1.0;

        if (second instanceof ActionPlan &&
                   ((ActionPlan)second).getAction() instanceof Build) {
            if (first instanceof ActionPlan &&
                ((ActionPlan)first).getAction() instanceof Train &&
                ((Train)(((ActionPlan)first).getAction())).getType() == WPeasant.class) {
                if (preCondition instanceof Invocation &&
                    ((Invocation)preCondition).getSensor() instanceof EntityTypeExists)
                    return 1.0;
            }
            if (first instanceof ActionPlan &&
                ((ActionPlan)first).getAction() instanceof ResourceLocation) {
                ResourceLocation a = (ResourceLocation)((ActionPlan)first).getAction();
                PhysicalEntity e = gs1.getEntityAt(a.getCoor());
                if (preCondition instanceof Invocation &&
                    ((Invocation)preCondition).getSensor() instanceof GoldCondition &&
                    e != null &&
                    e instanceof WGoldMine)
                    return 1.0;
                if (preCondition instanceof Invocation &&
                    ((Invocation)preCondition).getSensor() instanceof WoodCondition &&
                    e != null &&
                    e instanceof WOTree)
                    return 1.0;
            }
        } else if (second instanceof ActionPlan &&
                   ((ActionPlan)second).getAction() instanceof Train) {
            Train a2 = (Train)((ActionPlan)second).getAction();
            if (a2.getType() == WPeasant.class) {
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Build &&
                    ((Build)(((ActionPlan)first).getAction())).getType() == WBarracks.class)
                    return 1.0;
            } else {
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Build &&
                    ((Build)(((ActionPlan)first).getAction())).getType() == WTownhall.class)
                    return 1.0;
            }
            if (first instanceof ActionPlan &&
                ((ActionPlan)first).getAction() instanceof ResourceLocation) {
                ResourceLocation a = (ResourceLocation)((ActionPlan)first).getAction();
                PhysicalEntity e = gs1.getEntityAt(a.getCoor());
                if (preCondition instanceof Invocation &&
                    ((Invocation)preCondition).getSensor() instanceof GoldCondition &&
                    e != null &&
                    e instanceof WGoldMine)
                    return 1.0;
                if (preCondition instanceof Invocation &&
                    ((Invocation)preCondition).getSensor() instanceof WoodCondition &&
                    e != null &&
                    e instanceof WOTree)
                    return 1.0;
            }
        } else if (second instanceof ActionPlan &&
                   ((ActionPlan)second).getAction() instanceof ResourceLocation) {
            if (first instanceof ActionPlan &&
                ((ActionPlan)first).getAction() instanceof Train &&
                ((Train)(((ActionPlan)first).getAction())).getType() == WPeasant.class) {
                return 1.0;
            }
        } else if (second instanceof ActionPlan &&
                   ((ActionPlan)second).getAction() instanceof Attack) {
            Attack a2 = (Attack)((ActionPlan)second).getAction();
            Entity e2 = gs2.getEntity(a2.getEntityID());
            if (e2!=null &&
                first instanceof ActionPlan &&
                ((ActionPlan)first).getAction() instanceof Train &&
                ((Train)(((ActionPlan)first).getAction())).getType() == e2.getClass()) {
                return 1.0;
            }
        }

        return 0.0;
	}

}
